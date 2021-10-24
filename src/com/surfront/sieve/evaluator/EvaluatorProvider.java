package com.surfront.sieve.evaluator;

import java.util.Map;

public interface EvaluatorProvider {
    public Map<String, Class<? extends Evaluator>> getClasses();
}
