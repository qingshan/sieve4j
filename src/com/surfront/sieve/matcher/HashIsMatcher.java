package com.surfront.sieve.matcher;

import java.util.HashSet;
import java.util.Set;

public class HashIsMatcher implements Matcher {
    public static final String TYPE = "hash_is";
    private Set<String> patterns;

    public HashIsMatcher(String[] patterns) {
        this.patterns = toSet(patterns);
    }

    public HashIsMatcher(Set<String> patterns) {
        this.patterns = patterns;
    }

    public boolean match(String text) {
        return patterns.contains(text);
    }

    private static Set<String> toSet(String[] patterns) {
        Set<String> set = new HashSet<String>();
        if (patterns != null) {
            for (String pattern : patterns) {
                set.add(pattern);
            }
        }
        return set;
    }
}
