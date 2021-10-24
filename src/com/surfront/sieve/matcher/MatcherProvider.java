package com.surfront.sieve.matcher;

import java.util.Map;

public interface MatcherProvider {
    public Map<String, Class<? extends Matcher>> getClasses();
}
