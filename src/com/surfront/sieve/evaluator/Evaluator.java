package com.surfront.sieve.evaluator;

public interface Evaluator<E> {
    public <T> T evaluate(E data, Class<T> type);
}
