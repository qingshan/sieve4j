package com.surfront.sieve;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class CommandFactory<E> {
    protected final Map<String, Class<? extends ActionCommand>> actionClasses = new HashMap<String, Class<? extends ActionCommand>>();

    public Map<String, Class<? extends ActionCommand>> getActionClasses() {
        return actionClasses;
    }

    public CommandFactory() {
        defineAction(RequireCommand.TYPE, RequireCommand.class);
        defineAction(StopCommand.TYPE, StopCommand.class);
    }

    public void defineAction(String type, Class<? extends ActionCommand> clazz) {
        defineAction(type, null, clazz);
    }

    public void defineAction(String type, String tag, Class<? extends ActionCommand> clazz) {
        if (tag == null) {
            actionClasses.put(type, clazz);
        } else {
            if (type == null) {
                actionClasses.put(":" + tag, clazz);
            } else {
                actionClasses.put(type + ":" + tag, clazz);
            }
        }
    }

    public ControlCommand<E> getCommand(String type, Test<E> test, Command<E>[] commands) {
        ControlCommand<E> control;
        if (type.equalsIgnoreCase(IfControlCommand.TYPE)) {
            control = new IfControlCommand<E>();
        } else if (type.equalsIgnoreCase(ElsifControlCommand.TYPE)) {
            control = new ElsifControlCommand<E>();
        } else {
            control = new ElseControlCommand<E>();
        }
        control.setTest(test);
        control.setCommands(commands);
        return control;
    }

    public ActionCommand<E> getCommand(String type, Argument[] arguments) {
        Class<? extends ActionCommand> commandClass = null;
        if (arguments.length > 0 && arguments[0] instanceof TagArgument) {
            String tag = (String) arguments[0].getValue();
            if (actionClasses.containsKey(type + ":" + tag)) {
                commandClass = actionClasses.get(type + ":" + tag);
            } else if (actionClasses.containsKey(":" + tag)) {
                commandClass = actionClasses.get(":" + tag);
            }
        }
        if (commandClass == null) {
            commandClass = actionClasses.get(type);
        }
        return getActionCommand(commandClass, type, arguments);
    }

    public <T> ActionCommand<T> getActionCommand(Class<? extends ActionCommand> commandClass, String type, Argument[] arguments) {
        ActionCommand<T> action = null;
        if (commandClass != null) {
            try {
                Constructor<? extends ActionCommand> constructor;
                try {
                    constructor = commandClass.getConstructor(String.class);
                    action = constructor.newInstance(type);
                } catch (NoSuchMethodException e) {
                    constructor = commandClass.getConstructor();
                    action = constructor.newInstance();
                }
            } catch (Exception e) {
                throw new SyntaxException("Invalid action command: " + type);
            }
        }
        if (action == null) {
            throw new SyntaxException("No such action command: " + type);
        }
        try {
            action.setArguments(arguments);
        } catch (RuntimeException e) {
            throw new SyntaxException("Illegal arguments: " + type);
        }
        return action;
    }
}
