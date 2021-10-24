package com.surfront.sieve;

/**
 * Syntax: stop
 * The "stop" action ends all processing.
 */
public class StopCommand<E> extends ActionCommand<E> {
    public static final String TYPE = "stop";

    public String getType() {
        return TYPE;
    }

    public Argument[] getArguments() {
        return new Argument[0];
    }

    public void setArguments(Argument[] arguments) {
        if (arguments.length != 0) {
            throw new SyntaxException("Wrong arguments in " + getType() + " command");
        }
    }

    public int execute(E data) {
        return STOP;
    }
}
