package com.surfront.sieve;

/**
 * Test false
 * <p/>
 * Syntax: false
 * <p/>
 * The "false" test always evaluates to false.
 */
public class FalseTest<E> extends LogicTest<E> {
    public static final String TYPE = "false";

    public String getType() {
        return FalseTest.TYPE;
    }

    public void compile(SieveContext<E> context) {
    }

    public boolean test(E data) {
        //always return false
        return false;
    }

    public String toString() {
        return TYPE;
    }

    public static <T> FalseTest<T> newTest() {
        return new FalseTest<T>();
    }
}
