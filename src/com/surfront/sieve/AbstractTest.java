package com.surfront.sieve;

public abstract class AbstractTest<E> implements Test<E> {

    public abstract Argument[] getArguments();

    public abstract void setArguments(Argument[] arguments);

    public void compile(SieveContext<E> context) {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType());
        for (Argument argument : getArguments()) {
            sb.append(' ');
            sb.append(argument.toString());
        }
        return sb.toString();
    }
}
