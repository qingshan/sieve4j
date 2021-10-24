package com.surfront.sieve;

import java.io.Serializable;

/**
 * Sieve scripts are sequences of commands.  Commands can take any of
 * the tokens above as arguments, and arguments may be either tagged or
 * positional arguments.  Not all commands take all arguments.
 * <p/>
 * There are three kinds of commands: test commands, action commands,
 * and control commands.
 * <p/>
 * Test commands are not inherited from this interface, Because test
 * commands are special type which are only using in control commands.
 */
public interface Command<T> extends Compilable<T>, Serializable {
    public static final int STOP = -1;
    public static final int CONTINUE = 0;
    public static final int ENDIF = 1;

    public String getType();

    public int execute(T data);
}
