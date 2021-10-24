package com.surfront.sieve;

/**
 * A control command is an identifier followed by a test as an argument,
 * and ends with a block.
 *
 * @see IfControlCommand
 * @see ElsifControlCommand
 * @see ElseControlCommand
 */
public abstract class ControlCommand<E> implements Command<E> {
    protected Test<E> test;
    protected BlockCommand<E> block = new BlockCommand<E>();
    protected boolean illegal;

    public Test<E> getTest() {
        return test;
    }

    public void setTest(Test<E> test) {
        if (IllegalTest.isIllegal(test)) {
            illegal = true;
        }
        this.test = test;
    }

    public Command<E>[] getCommands() {
        return block.getCommands();
    }

    public void setCommands(Command<E>[] commands) {
        if (IllegalCommand.isIllegal(commands)) {
            illegal = true;
        }
        block.setCommands(commands);
    }

    public void compile(SieveContext<E> context) {
        if (illegal) {
            return;
        }
        try {
            test.compile(context);
        } catch (Exception e) {
            if (SieveContext.DEBUG) {
                System.out.println("Invalid Test: " + test);
                e.printStackTrace();
            }
            illegal = true;
        }
        try {
            block.compile(context);
        } catch (Exception e) {
            if (SieveContext.DEBUG) {
                System.out.println("Invalid Test: " + test);
                e.printStackTrace();
            }
            illegal = true;
        }
    }

    public int execute(E data) {
        if (!illegal && test(data)) {
            if (SieveContext.DEBUG) {
                System.out.println("Test: " + test);
            }
            if (block.execute(data) == STOP) {
                return STOP;
            } else {
                return ENDIF;
            }
        }
        return CONTINUE;
    }

    private boolean test(E data) {
        try {
            return test == null || test.test(data);
        } catch (Exception e) {
            return false;
        }
    }

    public String toString() {
        return toString("");
    }

    protected String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(getType());
        if (test != null) {
            sb.append(' ');
            sb.append(test.toString());
        }
        sb.append("\r\n");
        sb.append(indent);
        sb.append("{\r\n");
        sb.append(block.toString(indent + "\t"));
        sb.append(indent);
        sb.append('}');
        return sb.toString();
    }
}
