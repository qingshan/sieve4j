package com.surfront.sieve;

/**
 * Test allof
 * <p/>
 * Syntax: allof <tests: test-list>
 * <p/>
 * The allof test performs a logical AND on the tests supplied to it.
 * <p/>
 * Example: allof (false, false)  =>   false
 * allof (false, true)   =>   false
 * allof (true,  true)   =>   true
 * <p/>
 * The allof test takes as its argument a test-list.
 */
public class AllofTest<E> extends MultipleTest<E> {
    public static final String TYPE = "allof";

    public String getType() {
        return TYPE;
    }

    public boolean test(E data) {
        for (Test<E> test : tests) {
            if (!test.test(data)) {
                return false;
            }
        }
        return true;
    }

    public static <T> AllofTest<T> newTest(Test<T>... tests) {
        AllofTest<T> test = new AllofTest<T>();
        test.setTests(tests);
        return test;
    }
}
