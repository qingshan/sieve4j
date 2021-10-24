package com.surfront.sieve;

import java.lang.reflect.Array;

public class BlockCommand<E> implements Command<E> {
    public static final String TYPE = "block";
    protected Command<E>[] commands;

    public BlockCommand() {
    }

    public String getType() {
        return TYPE;
    }

    public BlockCommand(Command<E>[] commands) {
        this.commands = commands;
    }

    public Command<E>[] getCommands() {
        return commands;
    }

    public void setCommands(Command<E>[] commands) {
        this.commands = commands;
    }

    public void addCommand(Command<E> command) {
        if (command instanceof BlockCommand) {
            addCommands(((BlockCommand<E>) command).getCommands());
        } else {
            addCommands(command);
        }
    }

    public void addCommands(Command<E>... commands) {
        if (this.commands == null) {
            this.commands = commands;
        } else {
            Command<E>[] newCommands = (Command<E>[]) Array.newInstance(commands.getClass().getComponentType(), this.commands.length + commands.length);
            System.arraycopy(this.commands, 0, newCommands, 0, this.commands.length);
            System.arraycopy(commands, 0, newCommands, this.commands.length, commands.length);
            this.commands = newCommands;
        }
    }

    public void compile(SieveContext<E> context) {
        for (Command<E> command : commands) {
            command.compile(context);
        }
    }

    public int execute(E data) {
        int status = CONTINUE;
        for (Command<E> command : commands) {
            //Check last command's status
            if (status == STOP) {
                //Stop execute
                break;
            }
            if (status == ENDIF && (command instanceof ElsifControlCommand || command instanceof ElseControlCommand)) {
                //Skip elsif and else command
                continue;
            }
            status = command.execute(data);
        }
        if (status == ENDIF) {
            status = CONTINUE;
        }
        return status;
    }

    public String toString() {
        return toString("");
    }

    protected String toString(String indent) {
        StringBuilder sb = new StringBuilder();
        for (Command<E> command : commands) {
            sb.append(indent);
            if (command instanceof ControlCommand) {
                sb.append(((ControlCommand) command).toString(indent));
            } else if (command instanceof CommentCommand) {
                sb.append(((CommentCommand) command).toString(indent));
            } else {
                sb.append(command.toString());
            }
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
