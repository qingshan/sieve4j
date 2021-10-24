package com.surfront.sieve.matcher;

public class MultiByteMatcher implements ByteMatcher {
    protected ByteMatcher[] matchers;

    public MultiByteMatcher(ByteMatcher[] matchers) {
        this.matchers = matchers;
    }

    public boolean match(byte[] bytes, int offset, int len) {
        for (ByteMatcher matcher : matchers) {
            if (matcher.match(bytes, offset, len)) {
                return true;
            }
        }
        return false;
    }
}
