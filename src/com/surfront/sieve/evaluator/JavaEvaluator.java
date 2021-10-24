package com.surfront.sieve.evaluator;

import com.surfront.sieve.SieveContext;
import com.surfront.sieve.Test;

public abstract class JavaEvaluator<E> extends AbstractEvaluator<E> implements Test<E> {
    public void compile(SieveContext<E> context) {
    }

    public <T> T evaluate(E data, Class<T> type) {
        Boolean result = test(data);
        return (T) result;
    }
}
