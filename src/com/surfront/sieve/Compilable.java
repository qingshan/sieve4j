package com.surfront.sieve;

public interface Compilable<T> {
    public void compile(SieveContext<T> context);
}
