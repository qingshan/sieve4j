package com.surfront.sieve;

import java.io.Serializable;

public interface Comparator extends Serializable {
    /**
     * i;octet
     * Two strings are equal if they are the same length and
     * contain the same octets in the same order.
     */
    public static final String COMPARATOR_OCTET = "i;octet";
    /**
     * i;ascii-casemap
     * The i;ascii-casemap comparator first applies a mapping to the
     * attribute values which translates all US-ASCII letters to
     * uppercase, then applies the i;octet comparator as described above.
     */
    public static final String COMPARATOR_CASEMAP = "i;ascii-casemap";
    /**
     * i;ascii-numeric
     * The i;ascii-numeric comparator interprets strings as decimal
     * positive integers represented as US-ASCII digits.
     */
    public static final String COMPARATOR_NUMERIC = "i;ascii-numeric";
    /**
     * i;ip-mask
     * The i;ip-mask comparator supports match types :is and :contains.
     * Notations supported for comparison are:
     * Single host: 128.113.213.4
     * Netmask Source-IP: 128.113.1.0/255.255.255.0
     * CIDR: 198.0.0.0/8 (equivalent to 198.0.0.0/255.0.0.0)
     */
    public static final String COMPARATOR_IPMASK = "i;ip-mask";
}
