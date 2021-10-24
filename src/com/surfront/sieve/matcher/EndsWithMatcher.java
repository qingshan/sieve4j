package com.surfront.sieve.matcher;

public class EndsWithMatcher extends CharArrayMatcher {
    protected char[] pattern;

    public EndsWithMatcher(String pattern, boolean caseSensitive) {
        super(caseSensitive);
        this.pattern = toCharArray(pattern, caseSensitive);
    }

    public boolean match(char[] text) {
        return endsWith(text, pattern);
    }

    public static boolean endsWith(char[] text, char[] pattern) {
        return StartsWithMatcher.startsWith(text, pattern, text.length - pattern.length);
    }
}
