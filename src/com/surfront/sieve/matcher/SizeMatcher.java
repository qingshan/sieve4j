package com.surfront.sieve.matcher;

public class SizeMatcher implements Matcher {
    public static final String OVER = "over";
    public static final String UNDER = "under";

    public static final String KB = "K";
    public static final String MB = "M";
    public static final String GB = "G";

    protected long size;
    protected String compare;

    public SizeMatcher(long size, String unit, String compare) {
        if (GB.equalsIgnoreCase(unit)) {
            this.size = size << 30;
        } else if (MB.equalsIgnoreCase(unit)) {
            this.size = size << 20;
        } else if (KB.equalsIgnoreCase(unit)) {
            this.size = size << 10;
        } else {
            this.size = size;
        }
        this.compare = compare;
    }

    public boolean match(String text) {
        return match(Long.parseLong(text));
    }

    public boolean match(long size) {
        if (OVER.equalsIgnoreCase(compare)) { //:over
            return (size > this.size);
        } else { //:under
            return (size < this.size);
        }
    }
}
