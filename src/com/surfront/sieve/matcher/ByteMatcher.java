package com.surfront.sieve.matcher;

public interface ByteMatcher {
    public boolean match(byte[] bytes, int offset, int len);
}
