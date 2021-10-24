package com.surfront.sieve.matcher;

/**
 * The ":matches" version specifies a wildcard match using the
 * characters "*" and "?".  "*" matches zero or more characters, and "?"
 * matches a single character.  "?" and "*" may be escaped as "\\?" and
 * "\\*" in strings to match against themselves.  The first backslash
 * escapes the second backslash; together, they escape the "*".  This is
 * awkward, but it is commonplace in several programming languages that
 * use globs and regular expressions.
 */
public class MatchesMatcher extends CharArrayMatcher {
    private char[] pattern;

    public MatchesMatcher(String pattern, boolean caseSensitive) {
        super(caseSensitive);
        this.pattern = toCharArray(pattern, caseSensitive);
    }

    public boolean match(char[] text) {
        return matches(pattern, 0, text, 0);
    }

    public final static boolean matches(char[] pattern, int pindex, char[] text, int tindex) {
        if (text == null) {
            return false;
        }
        for (; ;) {
            if (pindex == pattern.length) {
                /* ran out of pattern */
                return (tindex == text.length);
            }
            char pc1 = pattern[pindex++];
            switch (pc1) {
                case '?':
                    if (tindex == text.length) {
                        return false;
                    }
                    tindex++;
                    break;
                case '*':
                    if (pindex == pattern.length) {
                        return true;
                    }
                    char pc2;
                    while ((pc2 = pattern[pindex]) == '*' || pc2 == '?') {
                        if (pc2 == '?') {
                            /* eat the character now */
                            if (tindex == text.length) {
                                return false;
                            }
                            tindex++;
                        }
                        /* coalesce into a single wildcard */
                        pindex++;
                        if (pindex >= pattern.length) {
                            break;
                        }
                    }
                    if (pindex == pattern.length) {
                        /* wildcard at end of string, any remaining text is ok */
                        return true;
                    }
                    while (tindex < text.length) {
                        /* recurse */
                        if (matches(pattern, pindex, text, tindex)) {
                            return true;
                        }
                        tindex++;
                    }
                case '\\':
                    //Escape character
                    pc1 = pattern[pindex++];
                default:
                    if (tindex == text.length) {
                        return false;
                    }
                    char tc = text[tindex];
                    if (pc1 == tc) {
                        tindex++;
                    } else {
                        /* literal char doesn't match */
                        return false;
                    }
            }
        }
        /* never reaches */
    }

    public static String escape(String pattern) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '*') {
                sb.append("\\*");
            } else if (c == '?') {
                sb.append("\\?");
            } else if (c == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String unescape(String pattern) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (c == '\\') {
                if (i < pattern.length() - 1) {
                    char c2 = pattern.charAt(i + 1);
                    if (c2 == '*' || c2 == '?' || c2 == '\\') {
                        sb.append(c2);
                        i++;
                        continue;
                    }
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}
