package com.surfront.sieve;

/**
 * Test not
 * <p/>
 * Syntax: not <test>
 * <p/>
 * The "not" test takes some other test as an argument, and yields the
 * opposite result.  "not false" evaluates to "true" and "not true"
 * evaluates to "false".
 */
public class NotTest<E> extends LogicTest<E> {
    public static final String TYPE = "not";
    protected Test<E> test;

    public String getType() {
        return TYPE;
    }

    public Test<E> getTest() {
        return test;
    }

    public void setTest(Test<E> test) {
        this.test = test;
    }

    public void compile(SieveContext<E> context) {
        test.compile(context);
    }

    public boolean test(E data) {
        return !test.test(data);
    }

    public String toString() {
        return TYPE + " " + test.toString();
    }

    public static <T> NotTest<T> newTest(Test<T> test) {
        NotTest<T> not = new NotTest<T>();
        not.setTest(test);
        return not;
    }
}
