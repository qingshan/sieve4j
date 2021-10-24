package com.surfront.sieve.matcher;

public class FalseMatcher implements Matcher {
    public boolean match(String text) {
        return false;
    }
}
