package com.surfront.sieve.transformer;

public interface Transformer {
    public String getType();

    public String transform(String value);
}
