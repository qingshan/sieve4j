package com.surfront.sieve.matcher;

public class StartsWithMatcher extends CharArrayMatcher {
    protected char[] pattern;

    public StartsWithMatcher(String pattern, boolean caseSensitive) {
        super(caseSensitive);
        this.pattern = toCharArray(pattern, caseSensitive);
    }

    public boolean match(char[] text) {
        return startsWith(text, pattern);
    }

    public static boolean startsWith(char[] text, char[] pattern) {
        return startsWith(text, pattern, 0);
    }

    public static boolean startsWith(char[] text, char[] pattern, int toffset) {
        int to = toffset;
        int po = 0;
        int pc = pattern.length;
        // Note: toffset might be near -1>>>1.
        if ((toffset < 0) || (toffset > text.length - pc)) {
            return false;
        }
        while (--pc >= 0) {
            if (text[to++] != pattern[po++]) {
                return false;
            }
        }
        return true;
    }
}
