package com.surfront.sieve.matcher;

import com.surfront.sieve.Comparator;
import com.surfront.sieve.RelationalTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RelationalMatcher implements Matcher {
    protected final String operator;
    protected final String comparator;
    protected final String[] operands;
    protected int[] intOperands;
    protected long[] longOperands;
    protected double[] doubleOperands;

    public RelationalMatcher(String operator, String comparator, String[] operands) {
        this.operator = operator;
        this.comparator = comparator;
        this.operands = operands;
    }

    public boolean match(String[] values) {
        if (values == null) {
            return false;
        }
        for (String value : values) {
            if (match(value)) {
                return true;
            }
        }
        return false;
    }

    public boolean match(String text) {
        if (text == null) {
            return false;
        }
        text = text.trim();
        if (Comparator.COMPARATOR_OCTET.equalsIgnoreCase(comparator)) {
            return match(text, true);
        } else if (Comparator.COMPARATOR_NUMERIC.equalsIgnoreCase(comparator)) {
            try {
                return match(Integer.parseInt(text));
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            return match(text, false);
        }
    }

    protected boolean match(String value, boolean caseSensitive) {
        if (value == null) {
            return false;
        }
        boolean match = false;
        for (String pattern : operands) {
            int code = caseSensitive ? value.compareTo(pattern) : value.compareToIgnoreCase(pattern);
            if (RelationalTest.LE.equalsIgnoreCase(operator)) {
                match |= code <= 0;
            } else if (RelationalTest.LT.equalsIgnoreCase(operator)) {
                match |= code < 0;
            } else if (RelationalTest.EQ.equalsIgnoreCase(operator)) {
                match |= code == 0;
            } else if (RelationalTest.NE.equalsIgnoreCase(operator)) {
                match |= code != 0;
            } else if (RelationalTest.GT.equalsIgnoreCase(operator)) {
                match |= code > 0;
            } else if (RelationalTest.GE.equalsIgnoreCase(operator)) {
                match |= code >= 0;
            }
        }
        return match;
    }

    public boolean match(int count) {
        if (intOperands == null) {
            intOperands = toInts(operands);
        }
        boolean match = false;
        for (double pattern : intOperands) {
            if (RelationalTest.LE.equalsIgnoreCase(operator)) {
                match |= count <= pattern;
            } else if (RelationalTest.LT.equalsIgnoreCase(operator)) {
                match |= count < pattern;
            } else if (RelationalTest.EQ.equalsIgnoreCase(operator)) {
                match |= count == pattern;
            } else if (RelationalTest.NE.equalsIgnoreCase(operator)) {
                match |= count != pattern;
            } else if (RelationalTest.GT.equalsIgnoreCase(operator)) {
                match |= count > pattern;
            } else if (RelationalTest.GE.equalsIgnoreCase(operator)) {
                match |= count >= pattern;
            }
        }
        return match;
    }

    public boolean match(double count) {
        if (doubleOperands == null) {
            doubleOperands = toDoubles(operands);
        }
        boolean match = false;
        for (double pattern : doubleOperands) {
            if (RelationalTest.LE.equalsIgnoreCase(operator)) {
                match |= count <= pattern;
            } else if (RelationalTest.LT.equalsIgnoreCase(operator)) {
                match |= count < pattern;
            } else if (RelationalTest.EQ.equalsIgnoreCase(operator)) {
                match |= count == pattern;
            } else if (RelationalTest.NE.equalsIgnoreCase(operator)) {
                match |= count != pattern;
            } else if (RelationalTest.GT.equalsIgnoreCase(operator)) {
                match |= count > pattern;
            } else if (RelationalTest.GE.equalsIgnoreCase(operator)) {
                match |= count >= pattern;
            }
        }
        return match;
    }

    public boolean match(long count) {
        if (longOperands == null) {
            longOperands = toLongs(operands);
        }
        boolean match = false;
        for (long pattern : longOperands) {
            if (RelationalTest.LE.equalsIgnoreCase(operator)) {
                match |= count <= pattern;
            } else if (RelationalTest.LT.equalsIgnoreCase(operator)) {
                match |= count < pattern;
            } else if (RelationalTest.EQ.equalsIgnoreCase(operator)) {
                match |= count == pattern;
            } else if (RelationalTest.NE.equalsIgnoreCase(operator)) {
                match |= count != pattern;
            } else if (RelationalTest.GT.equalsIgnoreCase(operator)) {
                match |= count > pattern;
            } else if (RelationalTest.GE.equalsIgnoreCase(operator)) {
                match |= count >= pattern;
            }
        }
        return match;
    }

    public boolean match(Date date) {
        if (date == null) {
            return false;
        }
        if (longOperands == null) {
            longOperands = toDates(operands);
        }
        return match(date.getTime());
    }

    protected static int[] toInts(String[] operands) {
        int[] ints = new int[operands.length];
        for (int i = 0; i < operands.length; i++) {
            try {
                ints[i] = parseInt(operands[i]);
            } catch (NumberFormatException e) {
                ints[i] = 0;
            }
        }
        return ints;
    }

    protected static long[] toLongs(String[] operands) {
        long[] longs = new long[operands.length];
        for (int i = 0; i < operands.length; i++) {
            try {
                longs[i] = parseLong(operands[i]);
            } catch (NumberFormatException e) {
                longs[i] = 0;
            }
        }
        return longs;
    }

    protected static long[] toDates(String[] operands) {
        long[] dates = new long[operands.length];
        for (int i = 0; i < operands.length; i++) {
            try {
                Date date = parseDate(operands[i]);
                dates[i] = date.getTime();
            } catch (ParseException e) {
                dates[i] = 0;
            }
        }
        return dates;
    }

    protected static double[] toDoubles(String[] operands) {
        double[] doubles = new double[operands.length];
        for (int i = 0; i < operands.length; i++) {
            try {
                doubles[i] = parseDouble(operands[i]);
            } catch (NumberFormatException e) {
                doubles[i] = 0;
            }
        }
        return doubles;
    }

    private static int parseInt(String operand) throws NumberFormatException {
        return (int) parseLong(operand);
    }

    private static long parseLong(String operand) throws NumberFormatException {
        if (operand.endsWith("%") || operand.endsWith("B")) {
            operand = operand.substring(0, operand.length() - 1);
        }
        long multiplier;
        if (operand.endsWith("P")) {
            multiplier = 1125899906842624L;
            operand = operand.substring(0, operand.length() - 1);
        } else if (operand.endsWith("T")) {
            multiplier = 1099511627776L;
            operand = operand.substring(0, operand.length() - 1);
        } else if (operand.endsWith("G")) {
            multiplier = 1073741824L;
            operand = operand.substring(0, operand.length() - 1);
        } else if (operand.endsWith("M")) {
            multiplier = 1048576L;
            operand = operand.substring(0, operand.length() - 1);
        } else if (operand.endsWith("K")) {
            multiplier = 1024L;
            operand = operand.substring(0, operand.length() - 1);
        } else {
            multiplier = 1L;
        }
        return Long.parseLong(operand) * multiplier;
    }

    private static double parseDouble(String operand) throws NumberFormatException {
        if (operand.endsWith("%") || operand.endsWith("B")) {
            operand = operand.substring(0, operand.length() - 1);
        }
        long multiplier;
        if (operand.endsWith("P")) {
            multiplier = 1125899906842624L;
            operand = operand.substring(0, operand.length() - 1);
        } else if (operand.endsWith("T")) {
            multiplier = 1099511627776L;
            operand = operand.substring(0, operand.length() - 1);
        } else if (operand.endsWith("G")) {
            multiplier = 1073741824L;
            operand = operand.substring(0, operand.length() - 1);
        } else if (operand.endsWith("M")) {
            multiplier = 1048576L;
            operand = operand.substring(0, operand.length() - 1);
        } else if (operand.endsWith("K")) {
            multiplier = 1024L;
            operand = operand.substring(0, operand.length() - 1);
        } else {
            multiplier = 1L;
        }
        return Double.parseDouble(operand) * multiplier;
    }

    private static Date parseDate(String operand) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(operand);
    }

}
