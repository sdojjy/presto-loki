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

import io.airlift.configuration.Config;
import io.airlift.configuration.ConfigDescription;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.net.URI;
import io.airlift.units.Duration;
import io.airlift.units.MinDuration;
import java.util.concurrent.TimeUnit;

public class LokiConnectorConfig
{
    private URI lokiURI = URI.create("http://localhost:3100");
    private Duration queryChunkSizeDuration = new Duration(1, TimeUnit.HOURS);

    @NotNull
    public URI getLokiURI()
    {
        return lokiURI;
    }

    @Config("loki.uri")
    @ConfigDescription("Where to find Loki coordinator host")
    public LokiConnectorConfig setLokiURI(URI lokiURI)
    {
        this.lokiURI = lokiURI;
        return this;
    }

    @MinDuration("1ms")
    public Duration getQueryChunkSizeDuration()
    {
        return queryChunkSizeDuration;
    }

    @Config("loki.query.chunk.size.duration")
    @ConfigDescription("The duration of each query to loki")
    public LokiConnectorConfig setQueryChunkSizeDuration(Duration queryChunkSizeDuration)
    {
        this.queryChunkSizeDuration = queryChunkSizeDuration;
        return this;
    }

    @PostConstruct
    public void checkConfig()
    {
    }
}
