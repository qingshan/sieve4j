package com.surfront.sieve;

/**
 * The super class of multiple logic test
 *
 * @see AllofTest
 * @see AnyofTest
 * @see SomeofTest
 */
public abstract class MultipleTest<E> extends LogicTest<E> {
    protected Test<E>[] tests;

    public Test<E>[] getTests() {
        return tests;
    }

    public void setTests(Test<E>[] tests) {
        this.tests = tests;
    }

    public void compile(SieveContext<E> context) {
        for (Test<E> test : tests) {
            test.compile(context);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType());
        sb.append(' ');
        sb.append('(');
        Test<E>[] tests = getTests();
        sb.append(tests[0].toString());
        for (int i = 1; i < tests.length; i++) {
            sb.append(',');
            sb.append(' ');
            sb.append(tests[i].toString());
        }
        sb.append(')');
        return sb.toString();
    }

}
