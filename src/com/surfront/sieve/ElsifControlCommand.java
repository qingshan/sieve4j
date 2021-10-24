package com.surfront.sieve;

/**
 * Elsif control command
 */
public class ElsifControlCommand<E> extends ControlCommand<E> {
    public static final String TYPE = "elsif";

    public String getType() {
        return TYPE;
    }
}
