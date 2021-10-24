package com.surfront.sieve.matcher;

import com.surfront.sieve.transformer.Transformer;

public class TransformerMatcher implements Matcher {
    private final Transformer transformer;
    private final Matcher matcher;

    public TransformerMatcher(Transformer transformer, Matcher matcher) {
        this.transformer = transformer;
        this.matcher = matcher;
    }

    public Transformer getTransformer() {
        return transformer;
    }

    public Matcher getMatcher() {
        return matcher;
    }

    public boolean match(String text) {
        return matcher.match(transformer.transform(text));
    }
}
