package com.surfront.sieve.matcher;

public class NullMatcher implements Matcher {
    public boolean match(String text) {
        return false;
    }
}
