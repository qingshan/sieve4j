package com.surfront.sieve.evaluator;

import java.io.IOException;
import java.io.Serializable;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class Function implements Serializable {
   protected final String name;
   protected final Object[] arguments;

    public Function(String name, Object[] arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("(");
        for (int i = 0; i < arguments.length; i++) {
            Object argument = arguments[i];
            if (i > 0) {
                sb.append(", ");
            }
            if (argument instanceof Number) {
                sb.append(((Number) argument).intValue());
            } else {
                sb.append("'");
                sb.append(argument.toString());
                sb.append("'");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static Function parse(String text) throws IOException {
        StreamTokenizer st = new StreamTokenizer(new StringReader(text));
        st.resetSyntax();
        st.wordChars('a', 'z');
        st.wordChars('A', 'Z');
        st.wordChars(128 + 32, 255);
        st.wordChars('_', '_');
        st.whitespaceChars(0, ' ');
        st.ordinaryChar('-');
        st.ordinaryChar('/');
        st.quoteChar('"');
        st.quoteChar('\'');
        st.parseNumbers();
        if (st.nextToken() != StreamTokenizer.TT_WORD) {
            throw new IllegalArgumentException("Invalid argument: " + text);
        }
        String name = st.sval;
        if (st.nextToken() != '(') {
            throw new IllegalArgumentException("Invalid argument: " + text);
        }
        List<Object> arguments = new ArrayList<Object>();
        int token;
        while ((token = st.nextToken()) != StreamTokenizer.TT_EOF) {
            switch (token) {
                case StreamTokenizer.TT_NUMBER:
                    arguments.add((int) st.nval);
                    break;
                case StreamTokenizer.TT_WORD:
                    if ("true".equals(st.sval) || "false".equals(st.sval)) {
                        arguments.add(Boolean.valueOf(st.sval));
                    } else if ("null".equals(st.sval)) {
                        arguments.add(null);
                    }
                case '\'':
                case '"':
                    arguments.add(st.sval);
                    break;
                case ',':
                case ')':
                    break;
                default:
                    throw new IllegalArgumentException("Invalid argument: " + text);
            }
        }
        return new Function(name, arguments.toArray(new Object[0]));
    }
}
