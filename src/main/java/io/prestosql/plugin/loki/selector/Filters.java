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


import io.prestosql.plugin.loki.ast.LabelSelectorLexer;
import io.prestosql.plugin.loki.ast.LabelSelectorParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Filters {

    public static class AndFilter implements TagFilter {
        private String type = "and";

        protected List<TagFilter> filters = new ArrayList<>();

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

    public static class RegexMatchFilter implements TagFilter {
        private String type = "regex";
        private String key;
        private Pattern pattern;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }


        public RegexMatchFilter(String key, String matcher) {
            this.key = key;
            this.pattern = Pattern.compile(matcher);
        }

        @Override
        public boolean eval(Map<String, String> tags) {
            return tags.containsKey(key) && pattern.matcher(tags.get(key)).find();
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

    public static TagFilter parse(String query) {
        LabelSelectorLexer lexer = new LabelSelectorLexer(CharStreams.fromString(query));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        LabelSelectorParser parser = new LabelSelectorParser(tokens);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new LabelSelectorError());
        LabelSelectorParser.BasequeryContext ctx = parser.basequery();

        ParseTreeWalker walker = new ParseTreeWalker();
        BaseQueryListener baseQuery = new BaseQueryListener();
        walker.walk(baseQuery, ctx);

        TagFilter expr = baseQuery.getTagFilter();
        if (expr == null) {
            throw new IllegalArgumentException("invalid query");
        }
        return expr;
    }


    public static void main(String[] args) {
        parse("{aa =~ \"xxx\", bb!= \"sss\"}");
    }

}
