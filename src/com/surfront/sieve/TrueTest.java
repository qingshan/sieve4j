package com.surfront.sieve;

/**
 * Test true
 * <p/>
 * Syntax: true
 * <p/>
 * The "true" test always evaluates to true.
 */
public class TrueTest<E> extends LogicTest<E> {
    public static final String TYPE = "true";

    public String getType() {
        return TYPE;
    }

    public void compile(SieveContext<E> context) {
    }

    public boolean test(E data) {
        //always return true
        return true;
    }

    public String toString() {
        return TYPE;
    }

    public static <T> TrueTest<T> newTest() {
        return new TrueTest<T>();
    }
}
