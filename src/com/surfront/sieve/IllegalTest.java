package com.surfront.sieve;

public class IllegalTest<E> extends AbstractTest<E> {
    protected String type;
    protected Argument[] arguments;

    public IllegalTest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public void setArguments(Argument[] arguments) {
        this.arguments = arguments;
    }

    public boolean test(E data) {
        return false;
    }

    public static boolean isIllegal(Test test) {
        if (test instanceof MultipleTest) {
            return isIllegal(((MultipleTest) test).getTests());
        } else if (test instanceof NotTest) {
            return IllegalTest.isIllegal(((NotTest) test).getTest());
        } else {
            return test instanceof IllegalTest;
        }
    }

    public static boolean isIllegal(Test[] tests) {
        for (Test test : tests) {
            if (IllegalTest.isIllegal(test)) {
                return true;
            }
        }
        return false;
    }
}
