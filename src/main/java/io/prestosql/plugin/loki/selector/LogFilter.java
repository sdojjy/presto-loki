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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public interface LogFilter {
    Map<String, String> a = new HashMap<>();
    boolean eval(String log);

    class Contains implements LogFilter {
        private String expr;

        public Contains(String expr) {
            this.expr = expr;
        }

        @Override
        public boolean eval(String log) {
            return log.contains(expr);
        }
    }

    class NotContains implements LogFilter {
        private String expr;

        public NotContains(String expr) {
            this.expr = expr;
        }

        @Override
        public boolean eval(String log) {
            return !log.contains(expr);
        }
    }

    class Match implements LogFilter {
        private Pattern expr;

        public Match(String expr) {
            CharSequence input;
            this.expr = Pattern.compile(expr);
        }

        @Override
        public boolean eval(String log) {
            return expr.matcher(log).matches();
        }
    }

    class NotMatch implements LogFilter {
        private Pattern expr;

        public NotMatch(String expr) {
            CharSequence input;
            this.expr = Pattern.compile(expr);
        }

        @Override
        public boolean eval(String log) {
            return expr.matcher(log).matches();
        }
    }

    class And implements LogFilter{
        List<LogFilter> filters = new ArrayList<>();

        public And() {

        }

        @Override
        public boolean eval(String log) {
            for(LogFilter filter: filters){
                if(!filter.eval(log)){
                    return false;
                }
            }
            return true;
        }

        public void addFilter(String op, String value){
            if ("|=".equals(op)) {
                filters.add(new Contains(value));
            } else if ("!=".equals(op)) {
                filters.add(new NotContains(value));
            } else if ("|~".equals(op)) {
                filters.add(new Match(value));
            } else {//!~
                filters.add(new NotMatch(value));
            }
        }
    }
}
