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


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Filters {

    public static class AndFilter implements TagFilter {
        private String type = "and";

        protected List<TagFilter> filters = new ArrayList<>();

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        public List<TagFilter> getFilters() {
            return filters;
        }

        public void setFilters(List<TagFilter> filters) {
            this.filters = filters;
        }

        public AndFilter() {
        }

        public AndFilter(List<TagFilter> filters) {
            this.filters = filters;
        }

        public void AddFilter(TagFilter filter) {
            this.filters.add(filter);
        }

        @Override
        public boolean eval(Map<String, String> tags) {
            for (TagFilter filter : filters) {
                if (!filter.eval(tags)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class OrFilter extends AndFilter {
        private String type = "or";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        @Override
        public boolean eval(Map<String, String> tags) {
            for (TagFilter filter : filters) {
                if (filter.eval(tags)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class NotFilter implements TagFilter {

        private String type = "not";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        protected TagFilter filter;

        public TagFilter getFilter() {
            return filter;
        }

        public void setFilter(TagFilter filter) {
            this.filter = filter;
        }

        public NotFilter() {
        }

        public NotFilter(TagFilter filter) {
            this.filter = filter;
        }

        @Override
        public boolean eval(Map<String, String> tags) {
            return !filter.eval(tags);
        }
    }

    public static class EqualsFilter implements TagFilter {
        private String type = "eq";
        private String key;
        private String value;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public EqualsFilter() {
        }

        public EqualsFilter(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean eval(Map<String, String> tags) {
            return tags.containsKey(key) && value.equals(tags.get(key));
        }
    }

    public static class GlobMatchFilter implements TagFilter {
        private String type = "glob";
        private String key;
        private String expr;
        private GlobMatcher matcher;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getExpr() {
            return expr;
        }

        public void setExpr(String expr) {
            this.expr = expr;
        }

        public GlobMatcher getMatcher() {
            return matcher;
        }

        public void setMatcher(GlobMatcher matcher) {
            this.matcher = matcher;
        }

        public GlobMatchFilter() {
        }

        public GlobMatchFilter(String key, String matcher) {
            this.key = key;
            this.expr = matcher;
        }

        @Override
        public boolean eval(Map<String, String> tags) {
            if (matcher == null) {
                matcher = new GlobMatcher(expr);
            }
            return tags.containsKey(key) && matcher.test(tags.get(key));
        }
    }

    public static class TrueFilter implements TagFilter {
        private String type = "true";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public boolean eval(Map<String, String> tags) {
            return true;
        }
    }

    public static class FalseFilter extends TrueFilter {
        private String type = "false";

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public boolean eval(Map<String, String> tags) {
            return !super.eval(tags);
        }
    }


    public static TagFilter parse(JSONObject json) {
        return parse(json.toString());
    }

    public static TagFilter parse(String jsonStr) {
        if (jsonStr == null) {
            return new TrueFilter();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.reader().forType(TagFilter.class).readValue(jsonStr);
        } catch (IOException e) {
            return new TrueFilter();
        }
    }

    public static TagFilter parseLokiStyle(String lokiExpr) {
        if (Strings.isNullOrEmpty(lokiExpr)) {
            return new TrueFilter();
        }

        lokiExpr = lokiExpr.trim();
        if (!lokiExpr.startsWith("{") || !lokiExpr.endsWith("}")) {
            return new TrueFilter();
        }
        lokiExpr = lokiExpr.trim();
        lokiExpr = lokiExpr.substring(1, lokiExpr.length() - 1);
        Filters.AndFilter tagFilter = new Filters.AndFilter();
        String[] parts = lokiExpr.split(",");
        for (String part : parts) {

            String key = "";
            String op = "";
            String value = "";
            if (part.contains("!=")) {
                op = "!=";
                key = part.substring(0, part.indexOf("!=")).trim();
                value = part.substring(part.indexOf("!=") + 2).trim();
                if (value.startsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
            } else if (part.contains("!~")) {
                op = "!~";
                key = part.substring(0, part.indexOf("!~")).trim();
                value = part.substring(part.indexOf("!~") + 2).trim();
                if (value.startsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
            } else if (part.contains("=~")) {
                op = "=~";
                key = part.substring(0, part.indexOf("=~")).trim();
                value = part.substring(part.indexOf("=~") + 2).trim();
                if (value.startsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
            } else if (part.contains("=")) {
                op = "=";
                key = part.substring(0, part.indexOf("=")).trim();
                value = part.substring(part.indexOf("=") + 1).trim();
                if (value.startsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
            }


            if ("=".equals(op)) {
                tagFilter.AddFilter(new Filters.EqualsFilter(key, value));
            } else if ("!=".equals(op)) {
                tagFilter.AddFilter(new Filters.NotFilter(new Filters.EqualsFilter(key, value)));
            } else if ("=~".equals(op)) {
                tagFilter.AddFilter(new Filters.GlobMatchFilter(key, value));
            } else {//!~
                tagFilter.AddFilter(new Filters.NotFilter(new Filters.GlobMatchFilter(key, value)));
            }
        }
        return tagFilter;
    }

    public static void main(String[] args) {
        parseLokiStyle("{aa =~ \"xxx\", bb!= sss}");
    }

}
