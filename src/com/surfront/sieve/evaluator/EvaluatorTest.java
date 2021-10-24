package com.surfront.sieve.evaluator;

import com.surfront.sieve.AbstractTest;
import com.surfront.sieve.Argument;
import com.surfront.sieve.Compilable;
import com.surfront.sieve.SieveContext;

public class EvaluatorTest<E> extends AbstractTest<E> {
    protected final String type;
    protected Argument[] arguments;
    protected transient Evaluator<E> evaluator;

    public EvaluatorTest(String type) {
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

    public boolean test(E data) {
        if (evaluator == null) {
            return false;
        }
        Boolean result = evaluator.evaluate(data, Boolean.class);
        return result != null && result;
    }
}
