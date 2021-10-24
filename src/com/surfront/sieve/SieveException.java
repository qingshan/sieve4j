package com.surfront.sieve;

public class SieveException extends RuntimeException {
    public SieveException() {
    }

    public SieveException(String message) {
        super(message);
    }

    public SieveException(String message, Throwable cause) {
        super(message, cause);
    }

    public SieveException(Throwable cause) {
        super(cause);
    }
}
