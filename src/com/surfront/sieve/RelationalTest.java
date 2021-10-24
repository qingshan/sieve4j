package com.surfront.sieve;

import com.surfront.sieve.matcher.RelationalMatcher;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public abstract class RelationalTest<E> extends AbstractTest<E> {
    public static final String LT = "lt";
    public static final String LE = "le";
    public static final String EQ = "eq";
    public static final String GT = "gt";
    public static final String GE = "ge";
    public static final String NE = "ne";

    protected String type;
    protected String operator;
    protected String comparator;
    protected String[] headers;
    protected String[] operands;

    protected transient RelationalMatcher relationalMatcher;

    protected RelationalTest(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public abstract String getRelationalType();

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String[] getOperands() {
        return operands;
    }

    public void setOperands(String[] operands) {
        this.operands = operands;
    }

    public Argument[] getArguments() {
        List<Argument> arguments = new ArrayList<Argument>();
        arguments.add(new TagArgument(getRelationalType()));
        arguments.add(new StringArgument(operator));
        if (comparator != null) {
            arguments.add(new TagArgument("comparator"));
            arguments.add(new StringArgument(comparator));
        }
        if (headers != null && headers.length > 0) {
            arguments.add(new StringArgument(headers));
        }
        arguments.add(new StringArgument(operands));
        return arguments.toArray(new Argument[0]);
    }

    public void setArguments(Argument[] arguments) {
        if (arguments.length < 3) {
            throw new SyntaxException("Invalid Arguments in " + type + " test command");
        }
        int i;
        for (i = 0; i < arguments.length - 2; i++) {
            Argument argument = arguments[i];
            if (argument instanceof TagArgument) {
                String value = (String) argument.getValue();
                if (value.equalsIgnoreCase("comparator")) {
                    setComparator(((String[]) arguments[++i].getValue())[0]);
                } else if (value.equalsIgnoreCase(getRelationalType())) {
                    setOperator(((String[]) arguments[++i].getValue())[0]);
                } else {
                    throw new SyntaxException("Unsupported tag argument: " + value);
                }
            } else {
                throw new SyntaxException("Invalid Argument: " + argument);
            }
        }
        if (i == arguments.length - 2) {
            setHeaders((String[]) arguments[i++].getValue());
        }
        setOperands((String[]) arguments[i++].getValue());
    }

    public void compile(SieveContext<E> context) {
        this.relationalMatcher = new RelationalMatcher(operator, comparator, operands);
    }

    protected boolean genericTest(Object value) {
        if (value instanceof String) {
            return test((String) value);
        } else if (value instanceof Date) {
            return test((Date) value);
        } else if (value instanceof Number) {
            if (Comparator.COMPARATOR_NUMERIC.equals(comparator)) {
                if (value instanceof Double) {
                    return test(((Double) value).doubleValue());
                } else if (value instanceof Float) {
                    return test(((Float) value).doubleValue());
                } else if (value instanceof Long) {
                    return test(((Long) value).longValue());
                } else {
                    return test(((Number) value).intValue());
                }
            } else {
                return test(String.valueOf(((Number) value).intValue()));
            }
        } else if (value instanceof Collection) {
            for (Object valuei : (Collection) value) {
                if (genericTest(valuei)) {
                    return true;
                }
            }
            return false;
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object valuei = Array.get(value, i);
                if (genericTest(valuei)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected boolean test(String value) {
        return match(relationalMatcher, value);
    }

    protected boolean test(int value) {
        return match(relationalMatcher, value);
    }

    protected boolean test(long value) {
        return match(relationalMatcher, value);
    }

    protected boolean test(double value) {
        return match(relationalMatcher, value);
    }

    protected boolean test(Date date) {
        return match(relationalMatcher, date);
    }

    protected static boolean match(RelationalMatcher matcher, int value) {
        return matcher != null && matcher.match(value);
    }

    protected static boolean match(RelationalMatcher matcher, long value) {
        return matcher != null && matcher.match(value);
    }

    protected static boolean match(RelationalMatcher matcher, double value) {
        return matcher != null && matcher.match(value);
    }

    protected static boolean match(RelationalMatcher matcher, String value) {
        return matcher != null && matcher.match(value);
    }

    protected static boolean match(RelationalMatcher matcher, Date value) {
        return matcher != null && matcher.match(value);
    }
}
