/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.plugin.loki;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.airlift.log.Logger;
import io.airlift.slice.Slice;
import io.prestosql.spi.connector.*;
import io.prestosql.spi.predicate.Domain;
import io.prestosql.spi.predicate.Marker;
import io.prestosql.spi.predicate.TupleDomain;
import io.prestosql.spi.type.IntegerType;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class LokiSplitManager
        implements ConnectorSplitManager {
    private static final Logger log = Logger.get(LokiSplitManager.class);

    private final LokiClient lokiClient;
    private final LokiClock lokiClock;

    private final URI lokiURI;

    @Inject
    public LokiSplitManager(LokiClient lokiClient, LokiClock lokiClock, LokiConnectorConfig config) {
        this.lokiClient = requireNonNull(lokiClient, "client is null");
        this.lokiClock = requireNonNull(lokiClock, "lokiClock is null");

        requireNonNull(config, "config is null");
        this.lokiURI = config.getLokiURI();
    }

    @Override
    public ConnectorSplitSource getSplits(
            ConnectorTransactionHandle transaction,
            ConnectorSession session,
            ConnectorTableHandle connectorTableHandle,
            SplitSchedulingStrategy splitSchedulingStrategy,
            DynamicFilter dynamicFilter) {
        LokiTableHandle tableHandle = (LokiTableHandle) connectorTableHandle;
        LokiTable table = lokiClient.getTable(tableHandle.getSchemaName(), tableHandle.getTableName());

        // this can happen if table is removed during a query
        if (table == null) {
            throw new TableNotFoundException(tableHandle.toSchemaTableName());
        }
        List<Long> times = generateTimesForSplits(session, lokiClock.now(), tableHandle);
        String query = "";
        if (tableHandle.getPredicate().isPresent()) {
            query = determinePredicateLabel(tableHandle.getPredicate().get()).orElse("");
        }
        List<ConnectorSplit> splits = null;
        try {
            splits = ImmutableList.of(
                    new LokiSplit(buildQuery(
                            lokiURI,
                            times.get(0),
                            times.get(1),
                            query)));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new FixedSplitSource(splits);
    }

    // URIBuilder handles URI encode
    private static URI buildQuery(URI baseURI, Long start, Long end, String queryStr)
            throws URISyntaxException {
        List<NameValuePair> nameValuePairs = new ArrayList<>(3);
        if (queryStr.trim().length() != 0) {
            nameValuePairs.add(new BasicNameValuePair("query", queryStr));
        } else {
            nameValuePairs.add(new BasicNameValuePair("query", "{notExistDeff !=\"/var/losswfefef\"}"));
        }
        nameValuePairs.add(new BasicNameValuePair("start", start + ""));
        nameValuePairs.add(new BasicNameValuePair("end", end + ""));
        return new URIBuilder(baseURI.toString())
                .setPath("/loki/api/v1/query_range")
                .setParameters(nameValuePairs)
                .build();
    }


    protected static List<Long> generateTimesForSplits(ConnectorSession session, Instant defaultUpperBound,
                                                       LokiTableHandle tableHandle) {
        Optional<LokiPredicateTimeInfo> predicateRange = tableHandle.getPredicate()
                .flatMap(LokiSplitManager::determinePredicateTimes);

        if (tableHandle.getPredicate().isPresent()) {
            log.info("push down predicate" + tableHandle.getPredicate().get().toString(session));
        }

        EffectiveLimits effectiveLimits = new EffectiveLimits(defaultUpperBound, predicateRange);
        Long upperBound = effectiveLimits.getUpperBound();
        Long lowerBound = effectiveLimits.getLowerBound();
        return ImmutableList.of(lowerBound, upperBound);
    }

    protected static Optional<LokiPredicateTimeInfo> determinePredicateTimes(TupleDomain<ColumnHandle> predicate) {
        Optional<Map<ColumnHandle, Domain>> maybeColumnHandleDomainMap = predicate.getDomains();
        Optional<Set<ColumnHandle>> maybeKeySet = maybeColumnHandleDomainMap.map(Map::keySet);
        Optional<Set<ColumnHandle>> maybeOnlyLokiColHandles = maybeKeySet.map(keySet -> keySet.stream()
                .filter(LokiColumnHandle.class::isInstance)
                .collect(Collectors.toSet()));
        Optional<Set<LokiColumnHandle>> maybeOnlyTimeStampColumnHandles = maybeOnlyLokiColHandles.map(handles -> handles.stream()
                .map(LokiColumnHandle.class::cast)
                .filter(handle -> handle.getColumnType().equals(IntegerType.INTEGER))
                .filter(handle -> handle.getColumnName().equals("timestamp"))
                .collect(Collectors.toSet()));

        // below we have a set of ColumnHandle that are all LokiColumnHandle AND of TimestampType wrapped in Optional: maybeOnlyTimeStampColumnHandles
        // the ColumnHandles in maybeOnlyTimeStampColumnHandles are keys to the map maybeColumnHandleDomainMap
        // and the values in that map are Domains which hold the timestamp predicate range info
        Map<ColumnHandle, Domain> columnHandleDomainMap = maybeColumnHandleDomainMap.orElse(ImmutableMap.of());
        Optional<Set<Domain>> maybeTimeDomains = maybeOnlyTimeStampColumnHandles.map(columnHandles -> columnHandles.stream()
                .map(columnHandleDomainMap::get)
                .collect(Collectors.toSet()));
        return processTimeDomains(maybeTimeDomains);
    }

    protected static Optional<String> determinePredicateLabel(TupleDomain<ColumnHandle> predicate) {
        Optional<Map<ColumnHandle, Domain>> maybeColumnHandleDomainMap = predicate.getDomains();
        Optional<Set<ColumnHandle>> maybeKeySet = maybeColumnHandleDomainMap.map(Map::keySet);
        Optional<Set<ColumnHandle>> maybeOnlyLokiColHandles = maybeKeySet.map(keySet -> keySet.stream()
                .filter(LokiColumnHandle.class::isInstance)
                .collect(Collectors.toSet()));
        Optional<Set<LokiColumnHandle>> maybeOnlyTimeStampColumnHandles = maybeOnlyLokiColHandles.map(handles -> handles.stream()
                .map(LokiColumnHandle.class::cast)
                .filter(handle -> handle.getColumnType().equals(LabelType.LABEL))
                .filter(handle -> handle.getColumnName().equals("labels"))
                .collect(Collectors.toSet()));

        // below we have a set of ColumnHandle that are all LokiColumnHandle AND of TimestampType wrapped in Optional: maybeOnlyTimeStampColumnHandles
        // the ColumnHandles in maybeOnlyTimeStampColumnHandles are keys to the map maybeColumnHandleDomainMap
        // and the values in that map are Domains which hold the timestamp predicate range info
        Map<ColumnHandle, Domain> columnHandleDomainMap = maybeColumnHandleDomainMap.orElse(ImmutableMap.of());
        Optional<Set<Domain>> maybeTimeDomains = maybeOnlyTimeStampColumnHandles.map(columnHandles -> columnHandles.stream()
                .map(columnHandleDomainMap::get)
                .collect(Collectors.toSet()));
        log.info(maybeTimeDomains.toString());
        return maybeTimeDomains.map(timeDomains -> {
            StringBuilder a = new StringBuilder();
            timeDomains.forEach(domain -> {
                if (!domain.getValues().getRanges().getSpan().includes(Marker.upperUnbounded(LabelType.LABEL))) {
                    a.append(((Slice) domain.getValues().getRanges().getSpan().getLow().getValue()).toStringUtf8());
                }
                if (!domain.getValues().getRanges().getSpan().includes(Marker.upperUnbounded(LabelType.LABEL))) {
                    if (a.length() <= 0) {
                        a.append(((Slice) domain.getValues().getRanges().getSpan().getHigh().getValue()).toStringUtf8());
                    }
                }
            });
            return a.toString();
        });
    }

    private static Optional<LokiPredicateTimeInfo> processTimeDomains(Optional<Set<Domain>> maybeTimeDomains) {
        return maybeTimeDomains.map(timeDomains -> {
            LokiPredicateTimeInfo.Builder timeInfoBuilder = LokiPredicateTimeInfo.builder();
            timeDomains.forEach(domain -> {
                if (!domain.getValues().getRanges().getSpan().includes(Marker.lowerUnbounded(IntegerType.INTEGER))) {
                    long packedValue = (long) domain.getValues().getRanges().getSpan().getLow().getValue();
                    timeInfoBuilder.setPredicateLowerTimeBound(Optional.of(packedValue));
                }
                if (!domain.getValues().getRanges().getSpan().includes(Marker.upperUnbounded(IntegerType.INTEGER))) {
                    long packedValue = (long) domain.getValues().getRanges().getSpan().getHigh().getValue();
                    timeInfoBuilder.setPredicateUpperTimeBound(Optional.of(packedValue));
                }
            });
            return timeInfoBuilder.build();
        });
    }

    private static class EffectiveLimits {
        private final Long upperBound;
        private final Long lowerBound;

        /**
         * If no upper bound is specified by the predicate, we use the time now() as the defaultUpperBound
         * if predicate LOWER bound is set AND predicate UPPER bound is set:
         * max duration          = upper bound - lower bound
         * effective upper bound = predicate upper bound
         * if predicate LOWER bound is NOT set AND predicate UPPER bound is set:
         * max duration          = config max duration
         * effective upper bound = predicate upper bound
         * if predicate LOWER bound is set AND predicate UPPER bound is NOT set:
         * max duration          = defaultUpperBound - lower bound
         * effective upper bound = defaultUpperBound
         * if predicate LOWER bound is NOT set AND predicate UPPER bound is NOT set:
         * max duration          = config max duration
         * effective upper bound = defaultUpperBound
         *
         * @param defaultUpperBound   If no upper bound is specified by the predicate, we use the time now() as the defaultUpperBound
         * @param maybePredicateRange Optional of pushed down predicate values for high and low timestamp values
         */
        public EffectiveLimits(Instant defaultUpperBound, Optional<LokiPredicateTimeInfo> maybePredicateRange) {
            if (maybePredicateRange.isPresent()) {
                if (maybePredicateRange.get().getPredicateUpperTimeBound().isPresent()) {
                    // predicate upper bound set
                    upperBound = maybePredicateRange.get().getPredicateUpperTimeBound().get();
                } else {
                    // predicate upper bound NOT set
                    upperBound = defaultUpperBound.getEpochSecond();
                }
                // here we're just working out the max duration using the above upperBound for upper bound
                if (maybePredicateRange.get().getPredicateLowerTimeBound().isPresent()) {
                    // predicate lower bound set
                    lowerBound = maybePredicateRange.get().getPredicateLowerTimeBound().get();
                } else {
                    // predicate lower bound NOT set
                    lowerBound = upperBound - 3600;
                }
            } else {
                // no predicate set, so no predicate value for upper bound, use defaultUpperBound (possibly now()) for upper bound and config for max durations
                upperBound = defaultUpperBound.getEpochSecond();
                lowerBound = upperBound - 3600;
            }
        }

        public Long getUpperBound() {
            return upperBound;
        }

        public Long getLowerBound() {
            return lowerBound;
        }
    }
}
