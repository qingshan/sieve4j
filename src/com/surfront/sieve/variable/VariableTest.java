package com.surfront.sieve.variable;

import com.surfront.sieve.*;

public class VariableTest<E> extends AbstractTest<E> {
    protected final AbstractTest<E> test;
    protected Argument[] arguments;
    protected transient SieveContext<E> context;

    public VariableTest(AbstractTest<E> test) {
        this.test = test;
    }

    public String getType() {
        return test.getType();
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public void setArguments(Argument[] arguments) {
        this.arguments = arguments;
    }

    public AbstractTest<E> getTest() {
        return test;
    }

    public void compile(SieveContext<E> context) {
        this.context = context;
    }

    public boolean test(E data) {
        Test<E> test = evaluate(data);
        if (test == null) {
            return false;
        }
        test.compile(context);
        return test.test(data);
    }

    protected AbstractTest<E> evaluate(E data) {
        VariableExpander<E> expander = new VariableExpander<E>(context);
        Argument[] arguments = expander.evaluate(this.arguments, data);
        TestFactory<E> testFactory = context.getTestFactory();
        return testFactory.getAbstractTest((Class<? extends AbstractTest<E>>) test.getClass(), test.getType(), arguments);
    }
}
