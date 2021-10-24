package com.surfront.sieve;

/**
 * Else control command
 */
public class ElseControlCommand<E> extends ControlCommand<E> {
    public static final String TYPE = "else";

    public String getType() {
        return TYPE;
    }
}
