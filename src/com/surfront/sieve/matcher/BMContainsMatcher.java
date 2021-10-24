package com.surfront.sieve.matcher;

public class BMContainsMatcher extends CharArrayMatcher {
    protected char[] pattern;
    /**
     * Byte array, beginning at index 1 (for algorithmic convenience),
     * that contains the intended search pattern data.
     */
    private char[] P;

    /**
     * The length of the search pattern.
     */
    private int m;

    /**
     * Table of jump distances for each mismatched character in the
     * alphabet for a given search pattern.  Must be recomputed for
     * each new pattern.
     */
    private int[] charJump;

    /**
     * Table of partial suffix match jump distances for a given pattern.
     * Must be recomputed for each new pattern.
     */
    private int[] matchJump;

    /**
     * Creates a precomputed Boyer-Moore byte string search object
     * from the given pattern.
     *
     * @param pattern       Binary pattern to search for.
     * @param caseSensitive
     */
    public BMContainsMatcher(String pattern, boolean caseSensitive) {
        super(caseSensitive);
        this.pattern = toCharArray(pattern, caseSensitive);
        computePattern();
        computeJumps();
        computeMatchJumps();
    }

    /**
     * Compares two integers and returns the lesser value.
     *
     * @param i1 First integer to compare.
     * @param i2 Second integer to compare.
     * @return The lesser of <code>i1</code> or <code>i2</code>.
     */
    private static final int min(int i1, int i2) {
        return (i1 < i2) ? i1 : i2;
    }

    /**
     * Generates the pattern byte string <code>P</code> from a character
     * array.  The signed unicode characters are truncated to 8 bits, and
     * converted into signed byte values.  Characters between 128 and 255
     * are converted to their signed negative counterpart in
     * twos-complement fashion by subtracting 256.
     */
    private final void computePattern() {
        m = pattern.length;
        P = new char[m + 1];
        for (int i = 1, j = 0; i <= m; i++, j++) P[i] = pattern[j];
    }

    /**
     * Initializes the per-character jump table <code>charJump</code>
     * as specified by the Boyer-Moore algorithm.
     */
    private final void computeJumps() {
        charJump = new int[256];
        for (int i = 0; i < 255; i++) charJump[i] = m;
        for (int k = 1; k <= m; k++) charJump[P[k] & 0xff] = m - k;
    }

    /**
     * Computes a partial-match jump table that skips over
     * partially matching suffixes.
     */
    private void computeMatchJumps() {
        int k, q, qq, mm;
        int[] back = new int[m + 2];

        matchJump = new int[m + 2];
        mm = 2 * m;

        for (k = 1; k <= m; k++) matchJump[k] = mm - k;
        k = m;
        q = m + 1;
        while (k > 0) {
            back[k] = q;
            while ((q <= m) && (P[k] != P[q])) {
                matchJump[q] = min(matchJump[q], m - k);
                q = back[q];
            }
            k = k - 1;
            q = q - 1;
        }
        for (k = 1; k <= q; k++) {
            matchJump[k] = min(matchJump[k], m + q - k);
        }
        qq = back[q];
        while (q <= m) {
            while (q <= qq) {
                matchJump[q] = min(matchJump[q], qq - q + m);
                q = q + 1;
            }
            qq = back[qq];
        }
    }

    /**
     * Search for the previously pre-compiled pattern string in an
     * array of bytes.  This method uses the Boyer-Moore pattern
     * search algorithm.
     *
     * @param text Array of bytes in which to search
     *             for the pattern.
     * @return The array index where the pattern
     *         begins in the string, or <code>-1</code>
     *         if the pattern was not found.
     */
    public boolean match(char[] text) {
        if (text == null) {
            return false;
        }
        return (search(text, 0, text.length) != -1);
    }

    /**
     * Search for the previously pre-compiled pattern string in an
     * array of bytes.  This method uses the Boyer-Moore pattern
     * search algorithm.
     *
     * @param text   Array of bytes in which to search
     *               for the pattern.
     * @param offset The the index in <code>byteString</code>
     *               where the search is to begin.
     * @param length The number of bytes to search in
     *               <code>byteString</code>.
     * @return The array index where the pattern
     *         begins in the string, or <code>-1</code>
     *         if the pattern was not found.
     */
    public int search(char[] text, int offset, int length) {
        int j, k, len, jump;
        j = m + offset;
        k = m;
        len = min(text.length, offset + length);
        while ((j <= len) && (k > 0)) {
            if ((text[j - 1]) == P[k]) {
                j = j - 1;
                k = k - 1;
            } else {
                jump = charJump[text[j - 1] & 0xff];
                if (jump < matchJump[k]) {
                    jump = matchJump[k];
                }
                j = j + jump;
                k = m;
            }
        }
        if (k == 0) return (j);
        return (-1); // No match.
    }

}
