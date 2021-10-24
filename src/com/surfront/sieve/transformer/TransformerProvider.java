package com.surfront.sieve.transformer;

import java.util.Map;

public interface TransformerProvider {
    public Map<String, Class<? extends Transformer>> getClasses();
}
