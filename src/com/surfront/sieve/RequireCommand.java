package com.surfront.sieve;

import com.surfront.sieve.evaluator.Evaluator;
import com.surfront.sieve.evaluator.EvaluatorFactory;

public class RequireCommand<E> extends ActionCommand<E> {
    public static final String TYPE = "require";
    protected Argument[] arguments;

    public String getType() {
        return TYPE;
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public void setArguments(Argument[] arguments) {
        this.arguments = arguments;
    }

    public void compile(SieveContext<E> context) {
        for (int i = 0; i < arguments.length; i++) {
            Argument argument = arguments[i];
            if (argument instanceof TagArgument) {
                String value = (String) argument.getValue();
                if ("action".equalsIgnoreCase(value)) {
                    //require :action "className" "requires"
                    //requires as action type
                    String className = ((String[]) arguments[++i].getValue())[0];
                    String[] requires = (String[]) arguments[++i].getValue();
                    try {
                        defineAction(context, className, requires);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("ClassNotFoundException: " + e.getMessage());
                    }
                } else if ("test".equalsIgnoreCase(value)) {
                    //require :test "className" "requires"
                    //requires as test type
                    String className = ((String[]) arguments[++i].getValue())[0];
                    String[] requires = (String[]) arguments[++i].getValue();
                    try {
                        defineTest(context, className, requires);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("ClassNotFoundException: " + e.getMessage());
                    }
                } else if ("eval".equalsIgnoreCase(value)) {
                    //require :eval "className" "requires"
                    //requires as eval type
                    String className = ((String[]) arguments[++i].getValue())[0];
                    String[] requires = (String[]) arguments[++i].getValue();
                    try {
                        defineEval(context, className, requires);
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException("ClassNotFoundException: " + e.getMessage());
                    }
                } else {
                    throw new SyntaxException("No such tag " + value);
                }
            } else {
                //Ignore it
            }
        }
    }

    public int execute(E data) {
        return CONTINUE;
    }

    protected void defineAction(SieveContext<E> context, String className, String[] requires) throws ClassNotFoundException {
        Class<ActionCommand<E>> clazz = loadClass(context.getClassLoader(), className);
        CommandFactory<E> commandFactory = context.getCommandFactory();
        for (String require : requires) {
            commandFactory.defineAction(require, clazz);
        }
    }

    protected void defineTest(SieveContext<E> context, String className, String[] requires) throws ClassNotFoundException {
        Class<Test<E>> clazz = loadClass(context.getClassLoader(), className);
        TestFactory<E> testFactory = context.getTestFactory();
        for (String require : requires) {
            testFactory.defineTest(require, clazz);
        }
    }

    protected void defineEval(SieveContext<E> context, String className, String[] requires) throws ClassNotFoundException {
        Class<Evaluator<E>> clazz = loadClass(context.getClassLoader(), className);
        EvaluatorFactory<E> testFactory = context.getEvaluatorFactory();
        for (String require : requires) {
            testFactory.defineEvaluator(require, clazz);
        }
    }

    protected static <T> Class<T> loadClass(ClassLoader classLoader, String className) throws ClassNotFoundException {
        return (Class<T>) classLoader.loadClass(className);
    }
}
