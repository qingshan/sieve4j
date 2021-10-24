package com.surfront.sieve;

import java.io.Serializable;

/**
 * There are three types of arguments:
 * Tagged argument, (String/String list) argument, Number argument
 *
 * @see TagArgument
 * @see StringArgument
 * @see NumberArgument
 */
public interface Argument extends Serializable {
    public Object getValue();
}
