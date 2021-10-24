package com.surfront.sieve;

/**
 * Test anyof
 * <p/>
 * Syntax: anyof <tests: test-list>
 * <p/>
 * The anyof test performs a logical OR on the tests supplied to it.
 * <p/>
 * Example: anyof (false, false)  =>   false
 * anyof (false, true)   =>   true
 * anyof (true,  true)   =>   true
 * <p/>
 * The anyof test takes as its argument a test-list.
 */
public class AnyofTest<E> extends MultipleTest<E> {
    public static final String TYPE = "anyof";

    public String getType() {
        return TYPE;
    }

    public boolean test(E data) {
        for (Test<E> test : tests) {
            if (test.test(data)) {
                return true;
            }
        }
        return false;
    }

    public static <T> AnyofTest<T> newTest(Test<T>... tests) {
        AnyofTest<T> test = new AnyofTest<T>();
        test.setTests(tests);
        return test;
    }
}
