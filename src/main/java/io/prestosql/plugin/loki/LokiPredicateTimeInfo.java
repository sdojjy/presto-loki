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

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class LokiPredicateTimeInfo
{
    private final Optional<Long> predicateLowerTimeBound;
    private final Optional<Long> predicateUpperTimeBound;

    private LokiPredicateTimeInfo(Optional<Long> predicateLowerTimeBound, Optional<Long> predicateUpperTimeBound)
    {
        this.predicateLowerTimeBound = requireNonNull(predicateLowerTimeBound, "predicateLowerTimeBound is null");
        this.predicateUpperTimeBound = requireNonNull(predicateUpperTimeBound, "predicateUpperTimeBound is null");
    }

    public Optional<Long> getPredicateLowerTimeBound()
    {
        return predicateLowerTimeBound;
    }

    public Optional<Long> getPredicateUpperTimeBound()
    {
        return predicateUpperTimeBound;
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Optional<Long> predicateLowerTimeBound = Optional.empty();
        private Optional<Long> predicateUpperTimeBound = Optional.empty();

        private Builder() {}

        public void setPredicateLowerTimeBound(Optional<Long> predicateLowerTimeBound)
        {
            this.predicateLowerTimeBound = predicateLowerTimeBound;
        }

        public void setPredicateUpperTimeBound(Optional<Long> predicateUpperTimeBound)
        {
            this.predicateUpperTimeBound = predicateUpperTimeBound;
        }

        public LokiPredicateTimeInfo build()
        {
            return new LokiPredicateTimeInfo(predicateLowerTimeBound, predicateUpperTimeBound);
        }
    }
}
