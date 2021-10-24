package com.surfront.sieve.matcher;

public abstract class CharArrayMatcher implements Matcher {
    protected boolean caseSensitive;

    public CharArrayMatcher(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean match(String text) {
        if (text == null) {
            return false;
        }
        return match(toCharArray(text, caseSensitive));
    }

    public abstract boolean match(char[] text);

    protected static char[] toCharArray(String text, boolean caseSensitive) {
        if (text == null) {
            return null;
        }
        char[] chars = text.toCharArray();
        if (!caseSensitive) {
            for (int i = 0; i < chars.length; i++) {
                chars[i] = Character.toUpperCase(chars[i]);
            }
        }
        return chars;
    }
}
