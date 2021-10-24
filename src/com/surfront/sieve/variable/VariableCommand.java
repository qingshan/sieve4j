package com.surfront.sieve.variable;

import com.surfront.sieve.*;

public class VariableCommand<E> extends ActionCommand<E> {
    protected final ActionCommand<E> command;
    protected Argument[] arguments;
    protected transient SieveContext<E> context;

    public VariableCommand(ActionCommand<E> command) {
        this.command = command;
    }

    public String getType() {
        return command.getType();
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public void setArguments(Argument[] arguments) {
        this.arguments = arguments;
    }

    public ActionCommand<E> getCommand() {
        return command;
    }

    public void compile(SieveContext<E> context) {
        this.context = context;
    }

    public int execute(E data) {
        ActionCommand<E> command = evaluate(data);
        if (command == null) {
            return CONTINUE;
        }
        command.compile(context);
        return command.execute(data);
    }

    protected ActionCommand<E> evaluate(E data) {
        VariableExpander<E> expander = new VariableExpander<E>(context);
        Argument[] arguments = expander.evaluate(this.arguments, data);
        CommandFactory<E> commandFactory = context.getCommandFactory();
        return commandFactory.getActionCommand((Class<? extends ActionCommand<E>>) command.getClass(), command.getType(), arguments);
    }
}
