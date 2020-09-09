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
package io.prestosql.plugin.loki.selector;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Map;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Filters.AndFilter.class, name = "and"),
        @JsonSubTypes.Type(value = Filters.OrFilter.class, name = "or"),
        @JsonSubTypes.Type(value = Filters.EqualsFilter.class, name = "eq"),
        @JsonSubTypes.Type(value = Filters.GlobMatchFilter.class, name = "glob"),
        @JsonSubTypes.Type(value = Filters.TrueFilter.class, name = "true"),
        @JsonSubTypes.Type(value = Filters.FalseFilter.class, name = "false"),
        @JsonSubTypes.Type(value = Filters.NotFilter.class, name = "not")

})
public interface TagFilter {
    boolean eval(Map<String, String> tags);
}
