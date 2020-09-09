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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GlobMatcher {
    private final String _expr;
    private final List<GlobMatcher.SimpleGlobExpr> _simpleExprs;
    private boolean _negative;

    public GlobMatcher(String expr) {
        this(expr, true, true);
    }

    public GlobMatcher(String expr, boolean from_head, boolean to_tail) {
        this._simpleExprs = new ArrayList();
        this._negative = false;
        if (expr == null) {
            throw new NullPointerException();
        } else {
            this._expr = expr.trim();
            ArrayList<GlobMatcher.Token> tokens = new ArrayList();
            GlobMatcher.Token[] var5 = this._tokenize();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                GlobMatcher.Token token = var5[var7];
                if (token.code == 2) {
                    if (tokens.size() == 0) {
                        throw new IllegalArgumentException("Empty simple globbing expression");
                    }

                    this._adjustSimpleExpr(tokens, from_head, to_tail);
                    this._simpleExprs.add(new GlobMatcher.SimpleGlobExpr((GlobMatcher.Token[])tokens.toArray(new GlobMatcher.Token[0])));
                    tokens.clear();
                } else if (token.code == 11) {
                    this._negative = true;
                } else {
                    tokens.add(token);
                }
            }

            if (tokens.size() >= 0) {
                this._adjustSimpleExpr(tokens, from_head, to_tail);
                this._simpleExprs.add(new GlobMatcher.SimpleGlobExpr((GlobMatcher.Token[])tokens.toArray(new GlobMatcher.Token[0])));
            } else {
                throw new IllegalArgumentException("Empty simple globbing expression");
            }
        }
    }

    String matchAndCapture(String s) {
        if (!this._negative && this._simpleExprs.size() <= 1) {
            return ((GlobMatcher.SimpleGlobExpr)this._simpleExprs.get(0)).testAndCapture(s);
        } else {
            throw new IllegalArgumentException("Don't support capture for negative or composed glob expression");
        }
    }

    boolean isNegative() {
        return this._negative;
    }

    private void _adjustSimpleExpr(ArrayList<GlobMatcher.Token> tokens, boolean from_head, boolean to_tail) {
        if (tokens.size() > 0) {
            GlobMatcher.Token add_token;
            if (!from_head && ((GlobMatcher.Token)tokens.get(0)).code != 5) {
                add_token = new GlobMatcher.Token(5);
                add_token._do_capture = false;
                tokens.add(0, add_token);
            }

            if (!to_tail && ((GlobMatcher.Token)tokens.get(tokens.size() - 1)).code != 5) {
                add_token = new GlobMatcher.Token(5);
                add_token._do_capture = false;
                tokens.add(add_token);
            }
        }

    }

    private GlobMatcher.Token[] _tokenize() {
        ArrayList<GlobMatcher.Token> tokens = new ArrayList();
        int i = 0;

        while(true) {
            while(i < this._expr.length()) {
                char ch = this._expr.charAt(i);
                if (ch == '\\') {
                    ++i;
                    if (i == this._expr.length()) {
                        throw new IllegalArgumentException("Invalid escape character");
                    }

                    tokens.add(new GlobMatcher.Token("\\" + this._expr.charAt(i)));
                    ++i;
                } else if (ch != '[') {
                    if (ch == '!') {
                        if (i == 0) {
                            tokens.add(new GlobMatcher.Token(11));
                        } else {
                            tokens.add(new GlobMatcher.Token(ch + ""));
                        }

                        ++i;
                    } else {
                        tokens.add(new GlobMatcher.Token(ch + ""));
                        ++i;
                    }
                } else {
                    StringBuilder tmp = new StringBuilder("[");

                    for(int j = i + 1; j < this._expr.length(); ++j) {
                        char ch1 = this._expr.charAt(j);
                        tmp.append(ch1);
                        if (ch1 == ']') {
                            tokens.add(new GlobMatcher.Token(tmp.toString()));
                            i = j + 1;
                            break;
                        }
                    }

                    if (tmp.charAt(tmp.length() - 1) != ']') {
                        throw new IllegalArgumentException("No close ]");
                    }
                }
            }

            return (GlobMatcher.Token[])tokens.toArray(new GlobMatcher.Token[0]);
        }
    }

    public boolean test(String s) {
        boolean r = this._test(s);
        return this._negative != r;
    }

    private boolean _test(String s) {
        if (s == null) {
            throw new NullPointerException();
        } else {
            Iterator var2 = this._simpleExprs.iterator();

            GlobMatcher.SimpleGlobExpr sge;
            do {
                if (!var2.hasNext()) {
                    return false;
                }

                sge = (GlobMatcher.SimpleGlobExpr)var2.next();
            } while(!sge.test(s));

            return true;
        }
    }

    private static class SimpleGlobExpr {
        private final GlobMatcher.Token[] _tokens;

        public SimpleGlobExpr(GlobMatcher.Token[] tokens) {
            this._tokens = tokens;
        }

        public boolean test(String s) {
            if (s == null) {
                throw new NullPointerException();
            } else {
                return this._test(this._tokens, 0, s, 0, (StringBuilder)null);
            }
        }

        public String testAndCapture(String s) {
            StringBuilder buf = new StringBuilder();
            return this._test(this._tokens, 0, s, 0, buf) ? buf.toString() : null;
        }

        private boolean _test(GlobMatcher.Token[] tokens, int tk_p, String text, int str_p, StringBuilder buf) {
            while(tk_p < tokens.length && str_p <= text.length()) {
                GlobMatcher.Token token = tokens[tk_p];
                if (token.code != 5) {
                    if (text.length() <= str_p || !token.matches(text.charAt(str_p))) {
                        return false;
                    }
                } else {
                    if (tk_p + 1 == tokens.length) {
                        if (buf != null && token._do_capture) {
                            buf.insert(0, text.substring(str_p));
                        }

                        return true;
                    }

                    for(int k = 0; str_p + k < text.length(); ++k) {
                        if (this._test(tokens, tk_p + 1, text, str_p + k, buf)) {
                            if (buf != null && token._do_capture) {
                                buf.insert(0, text.substring(str_p, str_p + k));
                            }

                            return true;
                        }
                    }
                }

                ++str_p;
                ++tk_p;
            }

            return tk_p == tokens.length && str_p == text.length();
        }
    }

    static class MultiChoiceToken {
        boolean negative = false;
        boolean range = false;
        char bch;
        char ech;
        Set<Character> chs = new HashSet();

        public MultiChoiceToken(String s) {
            this.negative = s.charAt(0) == '^';
            if (this.negative) {
                s = s.substring(1);
            }

            if (s.length() == 0) {
                throw new IllegalArgumentException("Invalid token " + s);
            } else {
                int pos = s.indexOf(45);
                if (pos == 1 && s.length() == 3) {
                    this.range = true;
                    this.bch = s.charAt(0);
                    this.ech = s.charAt(2);
                } else {
                    for(int i = 0; i < s.length(); ++i) {
                        this.chs.add(s.charAt(i));
                    }
                }

            }
        }

        public boolean matches(char c) {
            boolean included = false;
            if (this.range) {
                included = c >= this.bch && c <= this.ech;
            } else {
                included = this.chs.contains(c);
            }

            return this.negative ? !included : included;
        }
    }

    private static class Token {
        public static final int CODE_OR = 2;
        public static final int CODE_SPACE = 3;
        public static final int CODE_QUESTION = 4;
        public static final int CODE_STAR = 5;
        public static final int CODE_CHAR = 6;
        public static final int CODE_ALPHANUMERIC = 7;
        public static final int CODE_NONSPACE = 8;
        public static final int CODE_DIGIT = 9;
        public static final int CODE_MULTICHOICE = 10;
        public static final int CODE_NEGATIVE = 11;
        int code;
        char ch;
        GlobMatcher.MultiChoiceToken _multiChoiceToken;
        boolean _do_capture;

        public Token(int code) {
            this.code = code;
            this._do_capture = false;
            if (code == 5) {
                this._do_capture = true;
            }

        }

        public Token(String s) {
            this._do_capture = false;
            if (s.length() == 1) {
                if (s.equals("*")) {
                    this.code = 5;
                    this._do_capture = true;
                } else if (s.equals("|")) {
                    this.code = 2;
                } else if (s.equals("?")) {
                    this.code = 4;
                } else {
                    this.code = 6;
                    this.ch = s.charAt(0);
                }
            } else if (s.length() == 2) {
                char ch0 = s.charAt(0);
                char ch1 = s.charAt(1);
                if (ch0 != '\\') {
                    throw new IllegalArgumentException("Invalid token " + s);
                }

                switch(ch1) {
                    case 'S':
                        this.code = 8;
                        break;
                    case '\\':
                        this.code = 6;
                        this.ch = '\\';
                        break;
                    case 'd':
                        this.code = 9;
                        break;
                    case 's':
                        this.code = 3;
                        break;
                    case 'w':
                        this.code = 7;
                        break;
                    case '|':
                        this.code = 6;
                        this.ch = '|';
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid token " + s);
                }
            } else {
                if (s.charAt(0) != '[' || s.length() < 3 || s.charAt(s.length() - 1) != ']') {
                    throw new IllegalArgumentException("Invalid token " + s);
                }

                this._multiChoiceToken = new GlobMatcher.MultiChoiceToken(s.substring(1, s.length() - 1));
                this.code = 10;
            }

        }

        public boolean matches(char c) {
            switch(this.code) {
                case 2:
                    throw new IllegalStateException("| is illegal");
                case 3:
                    return c == ' ' || this.ch == '\t';
                case 4:
                    return true;
                case 5:
                    return true;
                case 6:
                    return c == this.ch;
                case 7:
                    return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || c >= '0' && c <= '9';
                case 8:
                    return c != ' ' && c != '\t';
                case 9:
                    return c >= '0' && c <= '9';
                case 10:
                    return this._multiChoiceToken.matches(c);
                default:
                    throw new IllegalStateException("Invalid code " + this.code);
            }
        }
    }
}
