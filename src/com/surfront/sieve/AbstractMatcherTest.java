package com.surfront.sieve;

import com.surfront.sieve.matcher.Matcher;

public abstract class AbstractMatcherTest<E> extends AbstractTest<E> {
    protected String compare;
    protected String comparator;
    protected String[] transformers;
    protected boolean extlist;
    protected String[] patterns;

    protected transient Matcher patternsMatcher;

    public String getCompare() {
        return compare;
    }

    public void setCompare(String compare) {
        this.compare = compare;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    public String[] getTransformers() {
        return transformers;
    }

    public void setTransformers(String[] transformers) {
        this.transformers = transformers;
    }

    public boolean isExtlist() {
        return extlist;
    }

    public void setExtlist(boolean extlist) {
        this.extlist = extlist;
    }

    public String[] getPatterns() {
        return patterns;
    }

    public void setPatterns(String[] patterns) {
        this.patterns = patterns;
    }

    public void compile(SieveContext<E> context) {
        this.patternsMatcher = context.getMatcher(compare, comparator, transformers, extlist, patterns);
    }

    protected boolean test(String[] values) {
        return match(patternsMatcher, values);
    }

    protected boolean test(String value) {
        return match(patternsMatcher, value);
    }

    protected static boolean match(Matcher matcher, String[] values) {
        for (String value : values) {
            if (match(matcher, value)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean match(Matcher matcher, String text) {
        return matcher != null && matcher.match(text);
    }
}
