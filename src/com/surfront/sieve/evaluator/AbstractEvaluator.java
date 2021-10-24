package com.surfront.sieve.evaluator;

import com.surfront.sieve.Compilable;
import com.surfront.sieve.SieveContext;

public abstract class AbstractEvaluator<E> implements Evaluator<E>, Compilable<E> {
    protected String type;
    protected String name;
    protected String[] arguments;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getArguments() {
        return arguments;
    }

    public void setArguments(String[] arguments) {
        this.arguments = arguments;
    }

    public void compile(SieveContext<E> context) {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType());
        sb.append(" :eval \"");
        sb.append(getName());
        sb.append('(');
        String[] arguments = getArguments();
        if (arguments.length > 0) {
            sb.append('\'');
            sb.append(arguments[0]);
            sb.append('\'');
            for (int i = 1; i < arguments.length; i++) {
                sb.append(',');
                sb.append('\'');
                sb.append(arguments[i]);
                sb.append('\'');
            }
        }
        sb.append(')');
        sb.append('"');
        return sb.toString();
    }
}

