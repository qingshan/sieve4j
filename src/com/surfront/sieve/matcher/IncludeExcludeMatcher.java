package com.surfront.sieve.matcher;

public class IncludeExcludeMatcher implements Matcher {
    private final Matcher includeMatcher;
    private final Matcher excludeMatcher;

    public IncludeExcludeMatcher(Matcher includeMatcher, Matcher excludeMatcher) {
        this.includeMatcher = includeMatcher;
        this.excludeMatcher = excludeMatcher;
    }

    public boolean match(String text) {
        return includeMatcher.match(text) && !excludeMatcher.match(text);
    }
}
