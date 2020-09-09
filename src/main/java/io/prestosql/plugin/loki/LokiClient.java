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
import com.google.common.collect.ImmutableSet;
import io.airlift.json.JsonCodec;
import io.prestosql.spi.PrestoException;
import io.prestosql.spi.type.TypeManager;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;

import javax.inject.Inject;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;

import static io.prestosql.plugin.loki.LokiErrorCode.LOKI_UNKNOWN_ERROR;
import static io.prestosql.spi.type.IntegerType.INTEGER;
import static io.prestosql.spi.type.VarcharType.VARCHAR;
import static java.util.Objects.requireNonNull;

public class LokiClient
{
    private static final OkHttpClient httpClient = new Builder().build();

    @Inject
    public LokiClient(LokiConnectorConfig config, JsonCodec<Map<String, Object>> metricCodec, TypeManager typeManager)
    {
        requireNonNull(config, "config is null");
        requireNonNull(metricCodec, "metricCodec is null");
        requireNonNull(typeManager, "typeManager is null");
    }

    public Set<String> getTableNames(String schema)
    {
        requireNonNull(schema, "schema is null");
        return ImmutableSet.of("loki");
    }

    public LokiTable getTable(String schema, String tableName)
    {
        requireNonNull(schema, "schema is null");
        requireNonNull(tableName, "tableName is null");
        if (!schema.equals("default")) {
            return null;
        }
        return new LokiTable(
                tableName,
                ImmutableList.of(
                        new LokiColumn("labels", LabelType.LABEL),
                        new LokiColumn("timestamp", INTEGER),
                        new LokiColumn("value", VARCHAR)));
    }

    private Map<String, Object> fetchMetrics(JsonCodec<Map<String, Object>> metricsCodec, URI metadataUri)
    {
        return metricsCodec.fromJson(fetchUri(metadataUri));
    }

    public byte[] fetchUri(URI uri)
    {
        Request.Builder requestBuilder = new Request.Builder().url(uri.toString());
        Response response;
        try {
            response = httpClient.newCall(requestBuilder.build()).execute();
            if (response.isSuccessful() && response.body() != null) {
                return response.body().bytes();
            }
        }
        catch (IOException e) {
            throw new PrestoException(LOKI_UNKNOWN_ERROR, "Error reading metrics", e);
        }

        throw new PrestoException(LOKI_UNKNOWN_ERROR, "URL " + requestBuilder.build().toString() + "Bad response " + response.code() + " " + response.message());
    }
}
