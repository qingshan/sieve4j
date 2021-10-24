package com.surfront.sieve.evaluator;

import com.surfront.sieve.ActionCommand;
import com.surfront.sieve.Argument;
import com.surfront.sieve.Compilable;
import com.surfront.sieve.SieveContext;

public class EvaluatorCommand<E> extends ActionCommand<E> {
    protected final String type;
    protected Argument[] arguments;
    protected transient Evaluator<E> evaluator;

    public EvaluatorCommand(String type) {
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
        EvaluatorFactory<E> evaluatorFactory = context.getEvaluatorFactory();
        Evaluator<E> evaluator = evaluatorFactory.getEvaluator(type, arguments);
        if (evaluator instanceof Compilable) {
            ((Compilable<E>) evaluator).compile(context);
        }
        this.evaluator = evaluator;
    }

    public int execute(E data) {
        if (evaluator == null) {
            return CONTINUE;
        }
        Boolean result = evaluator.evaluate(data, Boolean.class);
        if (result != null && result) {
            return STOP;
        }
        return CONTINUE;
    }
}
