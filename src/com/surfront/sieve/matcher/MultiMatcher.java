package com.surfront.sieve.matcher;

public class MultiMatcher implements Matcher {
    protected Matcher[] matchers;

    public MultiMatcher(Matcher[] matchers) {
        this.matchers = matchers;
    }

    public Matcher[] getMatchers() {
        return matchers;
    }

    public boolean match(String text) {
        for (Matcher matcher : matchers) {
            if (matcher.match(text)) {
                return true;
            }
        }
        return false;
    }
}
