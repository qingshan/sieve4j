package com.surfront.sieve.transformer;

import com.surfront.sieve.SyntaxException;

import java.lang.reflect.Constructor;
import java.util.*;

public class TransformerFactory {
    protected final Map<String, Class<? extends Transformer>> transformerClasses;

    public TransformerFactory(ClassLoader classLoader) {
        this.transformerClasses = getTransformerClasses(classLoader);
    }

    public Transformer getTransformer(String[] expressions) {
        if (expressions == null || expressions.length == 0) {
            return null;
        } else if (expressions.length == 1) {
            return getTransformer(expressions[0]);
        } else {
            List<Transformer> transformers = new ArrayList<Transformer>();
            for (String expression : expressions) {
                transformers.add(getTransformer(expression));
            }
            return new PipelineTransformer(transformers.toArray(new Transformer[0]));
        }
    }

    public Transformer getTransformer(String expression) {
        int index1 = expression.indexOf('(');
        int index2 = expression.indexOf(')');
        String name = expression;
        String[] arguments = null;
        if (index1 != -1 && index2 != -1) {
            name = expression.substring(0, index1);
            arguments = parseArguments(expression.substring(index1 + 1, index2));
        }
        return getTransformer(name, arguments);
    }

    protected Transformer getTransformer(String type, String[] arguments) {
        Class<? extends Transformer> transformerClass = transformerClasses.get(type);
        if (transformerClass == null) {
            throw new SyntaxException("No such transformer: " + type);
        }
        try {
            if (arguments == null || arguments.length == 0) {
                Constructor<? extends Transformer> constructor = transformerClass.getConstructor();
                return constructor.newInstance();
            } else {
                Constructor<? extends Transformer> constructor = transformerClass.getConstructor(String[].class);
                return constructor.newInstance(new Object[] {arguments});
            }
        } catch (Exception e) {
            throw new SyntaxException("Invalid transformer: " + type);
        }
    }

    protected String[] parseArguments(String arguments) {
        List<String> list = new ArrayList<String>();
        int start = -1;
        for (int i = 0; i < arguments.length(); i++) {
            char c = arguments.charAt(i);
            if (start == -1) {
                if (c == '\'') {
                    start = i + 1;
                }
            } else {
                if (c == '\'') {
                    list.add(arguments.substring(start, i));
                    start = -1;
                }
            }
        }
        return list.toArray(new String[0]);
    }

    private static Map<String, Class<? extends Transformer>> getTransformerClasses(ClassLoader classLoader) {
        Map<String, Class<? extends Transformer>> transformerClasses = new HashMap<String, Class<? extends Transformer>>();
        for (TransformerProvider provider : ServiceLoader.load(TransformerProvider.class, classLoader)) {
            transformerClasses.putAll(provider.getClasses());
        }
        return transformerClasses;
    }
}
