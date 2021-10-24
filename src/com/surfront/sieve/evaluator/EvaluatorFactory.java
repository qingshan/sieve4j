package com.surfront.sieve.evaluator;

import com.surfront.sieve.Argument;
import com.surfront.sieve.SyntaxException;
import com.surfront.sieve.TagArgument;
import com.surfront.sieve.script.Language;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class EvaluatorFactory<E> {
    //Evaluator mappings
    protected final Map<String, Class<? extends Evaluator>> evaluatorClasses;
    protected final Map<String, Language<E>> languages = new HashMap<String, Language<E>>();

    public EvaluatorFactory(ClassLoader classLoader) {
        this.evaluatorClasses = getEvaluatorClasses(classLoader);
    }

    public Map<String, Class<? extends Evaluator>> getEvaluatorClasses() {
        return evaluatorClasses;
    }

    public void defineEvaluator(String type, Class<? extends Evaluator<E>> clazz) {
        defineEvaluator(type, null, clazz);
    }

    public void defineEvaluator(String type, String name, Class<? extends Evaluator<E>> clazz) {
        if (name == null) {
            evaluatorClasses.put(type, clazz);
        } else {
            if (type == null) {
                evaluatorClasses.put("." + name, clazz);
            } else {
                evaluatorClasses.put(type + "." + name, clazz);
            }
        }
        evaluatorClasses.put(type, clazz);
    }

    public void defineLanguage(String type, Language<E> language) {
        languages.put(type, language);
    }

    public Language<E> getLanguage(String type) {
        return languages.get(type);
    }

    public Evaluator<E> getEvaluator(String type, Argument[] arguments) {
        if (arguments.length > 0 && arguments[0] instanceof TagArgument) {
            String matchType = (String) arguments[0].getValue();
            if (!"eval".equalsIgnoreCase(matchType)) {
                throw new SyntaxException("No such match type: " + matchType);
            }
            String expression = ((String[]) arguments[1].getValue())[0];
            return getEvaluator(type, expression);
        }
        throw new SyntaxException("No such evaluator: " + type);
    }

    public Evaluator<E> getEvaluator(String type, String expression) {
        Language<E> language = getLanguage(type);
        if (language == null) {
            try {
                Function function = Function.parse(expression);
                return getAbstractEvaluator(type, function.getName(), toStrings(function.getArguments()));
            } catch (IOException e) {
                throw new SyntaxException("Invalid evaluator function: " + type + "." + expression);
            }
        } else {
            return language.getScript(expression);
        }
    }

    public AbstractEvaluator<E> getAbstractEvaluator(String type, String name, String[] arguments) {
        try {
            Class<? extends Evaluator> clazz = getEvaluatorClass(type, name);
            if (clazz == null) {
                throw new ClassNotFoundException("No evaluator class for: " + name);
            }
            AbstractEvaluator<E> evaluator = (AbstractEvaluator<E>) clazz.newInstance();
            evaluator.setType(type);
            evaluator.setName(name);
            evaluator.setArguments(toStrings(arguments));
            return evaluator;
        } catch (Exception e) {
            throw new SyntaxException("No such evaluator: " + type + "." + name);
        }
    }

    protected Class<? extends Evaluator> getEvaluatorClass(String type, String name) throws SyntaxException {
        Class<? extends Evaluator> clazz = evaluatorClasses.get(type + "." + name);
        if (clazz == null) {
            clazz = evaluatorClasses.get("." + name);
        }
        if (clazz == null) {
            clazz = evaluatorClasses.get(type);
        }
        if (clazz == null) {
            throw new SyntaxException("No such evaluator: " + name);
        }
        return clazz;
    }

    private static String[] toStrings(Object[] arguments) {
        String[] results = new String[arguments.length];
        for (int i = 0; i < results.length; i++) {
            results[i] = arguments[i].toString();
        }
        return results;
    }

    private static Map<String, Class<? extends Evaluator>> getEvaluatorClasses(ClassLoader classLoader) {
        Map<String, Class<? extends Evaluator>> evaluatorClasses = new HashMap<String, Class<? extends Evaluator>>();
        for (EvaluatorProvider provider : ServiceLoader.load(EvaluatorProvider.class, classLoader)) {
            evaluatorClasses.putAll(provider.getClasses());
        }
        return evaluatorClasses;
    }
}
