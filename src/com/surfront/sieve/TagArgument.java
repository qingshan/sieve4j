package com.surfront.sieve;

/**
 * Tagged argument
 * A tagged argument is an argument for a command that begins with ":"
 * followed by a tag naming the argument, such as ":contains".
 */
public class TagArgument implements Argument {
    protected String value;

    public TagArgument(String value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return ":" + value;
    }
}
