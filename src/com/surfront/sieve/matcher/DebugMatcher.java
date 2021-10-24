package com.surfront.sieve.matcher;

public class DebugMatcher implements Matcher {
    protected final Matcher matcher;
    protected final String pattern;

    public DebugMatcher(Matcher matcher, String pattern) {
        this.matcher = matcher;
        this.pattern = pattern;
    }

    public boolean match(String text) {
        if (matcher.match(text)) {
            System.out.println("Pattern: " + pattern);
            return true;
        }
        return false;
    }
}
