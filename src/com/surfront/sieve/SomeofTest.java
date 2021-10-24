package com.surfront.sieve;

public class SomeofTest<E> extends MultipleTest<E> {
    public static final String TYPE = "someof";
    protected int count;

    public SomeofTest(int count) {
        this.count = count;
    }

    public String getType() {
        return TYPE + "_" + count;
    }

    public int getCount() {
        return count;
    }

    public boolean test(E data) {
        int count = 0;
        for (Test<E> test : tests) {
            if (test.test(data)) {
                count++;
                if (count >= this.count) {
                    return true;
                }
            }
        }
        return false;
    }

    public static <T> SomeofTest<T> newTest(int count, Test<T>[] tests) {
        SomeofTest<T> test = new SomeofTest<T>(count);
        test.setTests(tests);
        return test;
    }
}
