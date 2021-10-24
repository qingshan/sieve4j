package com.surfront.sieve;

public class IllegalCommand<E> implements Command<E> {
    protected String type;
    protected Argument[] arguments;

    public IllegalCommand(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public void setArguments(Argument[] arguments) {
        this.arguments = arguments;
    }

    public void compile(SieveContext<E> context) {
    }

    public int execute(E data) {
        return CONTINUE;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType());
        for (Argument argument : getArguments()) {
            sb.append(' ');
            sb.append(argument.toString());
        }
        sb.append(';');
        return sb.toString();
    }

    public static boolean isIllegal(Command command) {
        if (command instanceof BlockCommand) {
            return isIllegal(((BlockCommand) command).getCommands());
        } else if (command instanceof ControlCommand) {
            Test test = ((ControlCommand) command).getTest();
            if (IllegalTest.isIllegal(test)) {
                return true;
            }
            return isIllegal(((ControlCommand) command).getCommands());
        } else if (command instanceof IllegalCommand) {
            return true;
        }
        return false;
    }

    public static boolean isIllegal(Command[] commands) {
        for (Command command : commands) {
            if (isIllegal(command)) {
                return true;
            }
        }
        return false;
    }
}
