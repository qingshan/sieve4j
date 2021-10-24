package com.surfront.sieve.matcher;

public class MultiCharArrayMatcher extends CharArrayMatcher {
    protected CharArrayMatcher[] matchers;

    public MultiCharArrayMatcher(CharArrayMatcher[] matchers, boolean caseSensitive) {
        super(caseSensitive);
        this.matchers = matchers;
    }

    public CharArrayMatcher[] getMatchers() {
        return matchers;
    }

    public boolean match(char[] text) {
        for (CharArrayMatcher matcher : matchers) {
            if (matcher.match(text)) {
                return true;
            }
        }
        return false;
    }
}
