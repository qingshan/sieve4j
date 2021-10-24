package com.surfront.sieve;

import java.io.Serializable;

/**
 * Test
 * Test are given as arguments to commands in order to control their
 * actions. That is. test are given to if/elsif/else to decide which
 * block of code is run.
 */
public interface Test<T> extends Compilable<T>, Serializable {
    public String getType();

    public boolean test(T data);
}
