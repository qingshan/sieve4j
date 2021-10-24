package com.surfront.sieve.matcher;

/**
 * The ":is" match type describes an absolute match; if the contents of
 * the first string are absolutely the same as the contents of the
 * second string, they match.  Only the string "frobnitzm" is the string
 * "frobnitzm".  The null key ":is" and only ":is" the null value.
 */
public class IsMatcher extends CharArrayMatcher {
    protected char[] pattern;

    public IsMatcher(String pattern, boolean caseSensitive) {
        super(caseSensitive);
        this.pattern = toCharArray(pattern, caseSensitive);
    }

    public boolean match(char[] text) {
        return is(pattern, text);
    }

    public final static boolean is(char[] pattern, char[] text) {
        if (text == null) {
            return false;
        }
        if (text.length != pattern.length) {
            return false;
        }
        for (int i = 0; i < text.length; i++) {
            if (text[i] != pattern[i]) {
                return false;
            }
        }
        return true;
    }
}
