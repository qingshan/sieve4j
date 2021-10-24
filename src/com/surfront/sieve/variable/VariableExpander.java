package com.surfront.sieve.variable;

import com.surfront.sieve.Argument;
import com.surfront.sieve.SieveContext;
import com.surfront.sieve.StringArgument;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class VariableExpander<E> {
    protected SieveContext<E> context;

    public VariableExpander(SieveContext<E> context) {
        this.context = context;
    }

    public Argument[] evaluate(Argument[] arguments, E data) {
        Argument[] results = new Argument[arguments.length];
        for (int i = 0; i < results.length; i++) {
            Argument result = evaluate(arguments[i], data);
            if (result == null) {
                return null;
            }
            results[i] = result;
        }
        return results;
    }

    public Argument evaluate(Argument argument, E data) {
        if (argument instanceof StringArgument) {
            String[] values = evaluate((String[]) argument.getValue(), data);
            if (values.length == 0) {
                return null;
            }
            return new StringArgument(values);
        } else {
            return argument;
        }
    }

    public String[] evaluate(String[] patterns, E data) {
        List<String> results = new ArrayList<String>();
        for (String pattern : patterns) {
            Object result = evaluate(pattern, data);
            add(results, result);
        }
        return results.toArray(new String[0]);
    }

    protected void add(List<String> results, Object result) {
        if (result == null) {
            return;
        }
        if (result instanceof String) {
            results.add((String) result);
        } else if (result instanceof Collection) {
            for (Object item : (Collection) result) {
                add(results, item);
            }
        } else if (result.getClass().isArray()) {
            int length = Array.getLength(result);
            for (int i = 0; i < length; i++) {
                add(results, Array.get(result, i));
            }
        } else {
            results.add(String.valueOf(result));
        }
    }

    public Object evaluate(String pattern, E data) {
        StringBuilder result = new StringBuilder();
        StringBuffer property = new StringBuffer();
        int j = pattern.length();
        int k = 0;
        for (int i = 0; i < j; i++) {
            char c = pattern.charAt(i);
            switch (k) {
                case 1: // '\001'
                    if (c == '{') {
                        k = 2;
                        property = new StringBuffer();
                        break;
                    }
                    if (c == '$') {
                        result.append('$');
                    } else {
                        k = 0;
                        result.append('$');
                        result.append(c);
                    }
                    break;

                case 2: // '\002'
                    if (Character.isLetterOrDigit(c) || c == '_' || c == '.') {
                        k = 2;
                        property.append(c);
                        break;
                    }
                    k = 0;
                    if (c == '}') {
                        Object value = context.resolveVariable(data, property.toString());
                        if (value != null) {
                            result.append(value);
                            break;
                        }
                    }
                    result.append("${");
                    result.append(property);
                    result.append(c);
                    break;

                case 0: // '\0'
                    if (c == '$')
                        k = 1;
                    else
                        result.append(c);
                    break;

                default:
                    break;
            }
        }

        switch (k) {
            case 1: // '\001'
                result.append('$');
                break;

            case 2: // '\002'
                result.append("${");
                result.append(property.toString());
                break;
        }
        return result.toString();
    }
}
