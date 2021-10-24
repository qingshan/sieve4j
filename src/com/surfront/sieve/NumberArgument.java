package com.surfront.sieve;

/**
 * Number argument
 * Numbers are given as ordinary decimal numbers.  However, those
 * numbers that have a tendency to be fairly large, such as message
 * sizes, MAY have a "K", "M", or "G" appended to indicate a multiple of
 * a power of two.
 * Only positive integers are permitted by this specification.
 */
public class NumberArgument implements Argument {
    protected String value;

    public NumberArgument(int value) {
        this(String.valueOf(value));
    }

    public NumberArgument(String value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return value;
    }
}
