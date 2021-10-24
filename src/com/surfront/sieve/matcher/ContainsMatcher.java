package com.surfront.sieve.matcher;

/**
 * The ":contains" match type describes a substring match.  If the value
 * argument contains the key argument as a substring, the match is true.
 * For instance, the string "frobnitzm" contains "frob" and "nit", but
 * not "fbm".  The null key ("") is contained in all values.
 */
public class ContainsMatcher extends CharArrayMatcher {
    protected char[] pattern;

    public ContainsMatcher(String pattern, boolean caseSensitive) {
        super(caseSensitive);
        this.pattern = toCharArray(pattern, caseSensitive);
    }

    public boolean match(char[] text) {
        return contains(pattern, text);
    }

    public final static boolean contains(char[] pattern, char[] text) {
        if (text == null || pattern.length > text.length) {
            return false;
        }
        final char first = pattern[0];
        final int sourceCount = text.length;
        final int targetCount = pattern.length;
        final int max = (sourceCount - targetCount);

        int i = 0;

        startSearchForFirstChar:
        while (true) {
            /* Look for first character. */
            while (i <= max && text[i] != first) {
                i++;
            }
            if (i > max) {
                return false;
            }

            /* Found first character, now look at the rest of v2 */
            int j = i + 1;
            int end = j + targetCount - 1;
            int k = 1;
            while (j < end) {
                if (text[j++] != pattern[k++]) {
                    i++;
                    /* Look for str's first char again. */
                    continue startSearchForFirstChar;
                }
            }
            return true;    /* Found whole string. */
        }
    }
}
