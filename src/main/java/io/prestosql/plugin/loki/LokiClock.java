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

import javax.inject.Inject;

import java.time.Clock;
import java.time.Instant;

import static java.time.ZoneOffset.UTC;

/**
 * allow for settable Clock for testing
 */
public final class LokiClock
{
    private final Clock clock;

    @Inject
    public LokiClock()
    {
        this(Clock.systemUTC());
    }

    private LokiClock(Clock clock)
    {
        this.clock = clock;
    }

    public Instant now()
    {
        return clock.instant();
    }

    public static LokiClock fixedClockAt(Instant fixedInstant)
    {
        return new LokiClock(Clock.fixed(fixedInstant, UTC));
    }
}
