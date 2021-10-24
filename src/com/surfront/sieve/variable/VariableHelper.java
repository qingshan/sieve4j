package com.surfront.sieve.variable;

import com.surfront.sieve.Argument;
import com.surfront.sieve.StringArgument;

public class VariableHelper {
    public static boolean containsVariable(Argument[] arguments) {
        for (Argument argument : arguments) {
            if (argument instanceof StringArgument && containsVariable((StringArgument) argument)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsVariable(StringArgument argument) {
        return containsVariable((String[]) argument.getValue());
    }

    public static boolean containsVariable(String[] patterns) {
        for (String value : patterns) {
            if (containsVariable(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsVariable(String pattern) {
        if (pattern == null || pattern.length() == 0) {
            return false;
        }
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
                    if (c != '$') {
                        k = 0;
                    }
                    break;
                case 2: // '\002'
                    if (Character.isLetterOrDigit(c)
                            || c == '_' || c == '.' || c == '-'
                            || c == '\'' || c == '"'
                            || c == '[' || c == ']'
                            ) {
                        k = 2;
                        property.append(c);
                        break;
                    }
                    k = 0;
                    if (c == '}') {
                        return true;
                    }
                    break;

                case 0: // '\0'
                    if (c == '$') {
                        k = 1;
                    }
                    break;

                default:
                    break;
            }
        }
        return false;
    }
}
