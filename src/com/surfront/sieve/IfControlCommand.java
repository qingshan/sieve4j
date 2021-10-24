package com.surfront.sieve;

/**
 * If control command
 */
public class IfControlCommand<E> extends ControlCommand<E> {
    public static final String TYPE = "if";

    public String getType() {
        return TYPE;
    }
}
