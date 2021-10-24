package com.surfront.sieve.matcher;

import java.util.regex.Pattern;

public class RegexMatcher implements Matcher {
    protected Pattern pattern;

    public RegexMatcher(String pattern, boolean caseSensitive) {
        try {
            this.pattern = Pattern.compile(pattern, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
        } catch (Exception e) {
        }
    }

    public boolean match(String text) {
        if (pattern == null) {
            return false;
        }
        if (text == null) {
            return false;
        }
        return pattern.matcher(text).find();
    }
}
