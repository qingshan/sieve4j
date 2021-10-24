package com.surfront.sieve.matcher;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class NetworkMatcher implements Matcher {
    protected final String pattern;
    protected final InetAddress startIp;
    protected final InetAddress endIp;
    protected transient AddressMatcher matcher;

    public NetworkMatcher(String pattern) {
        this.pattern = pattern;
        if ("0.0.0.0/0".equals(pattern)) {
            pattern = "0.0.0.0-255.255.255.255";
        }
        int index;
        if ((index = pattern.indexOf('-')) != -1) {
            String ip = pattern.substring(0, index);
            String mask = pattern.substring(index + 1);
            if (ip.indexOf('.') != -1 && mask.indexOf('.') == -1) {
                //Example: 192.168.1.1-100
                startIp = toAddress(ip);
                int lastIndex = ip.lastIndexOf('.');
                if (lastIndex != -1) {
                    endIp = toAddress(ip.substring(0, lastIndex) + "." + mask);
                } else {
                    //Invalid, endIp = -1
                    endIp = null;
                }
            } else {
                //Example: 192.168.1.1-192.168.1.100
                startIp = toAddress(ip);
                endIp = toAddress(mask);
            }
        } else if ((index = pattern.indexOf('/')) != -1) {
            String ip = pattern.substring(0, index);
            String maskIp = pattern.substring(index + 1);
            InetAddress address = toAddress(ip);
            int size = address instanceof Inet6Address ? 128 : 32;
            int mask;
            if (maskIp.indexOf('.') == -1) {
                //Example: 192.168.1.0/24
                mask = Integer.parseInt(maskIp);
            } else {
                //Example: 192.168.1.0/255.255.255.0
                mask = toMask(maskIp);
                if (address instanceof Inet6Address) {
                    mask += 96;
                }
            }
            BigInteger addressInt = toBigInteger(address);
            BigInteger startInt = mask(addressInt, size, mask);
            BigInteger endInt = unmask(startInt, size, mask);
            if (address instanceof Inet6Address) {
                startIp = toAddress(startInt);
                endIp = toAddress(endInt);
            } else {
                startIp = toAddress(startInt.longValue());
                endIp = toAddress(endInt.longValue());
            }
        } else if (pattern.indexOf('*') != -1) {
            //Example: 192.168.1.*
            startIp = toAddress(pattern.replace("*", "0"));
            endIp = toAddress(pattern.replace("*", "255"));
        } else {
            startIp = endIp = toAddress(pattern);
        }
    }

    public InetAddress getStartIp() {
        return startIp;
    }

    public InetAddress getEndIp() {
        return endIp;
    }

    public boolean isIpv4() {
        return startIp instanceof Inet4Address && endIp instanceof Inet4Address;
    }

    public boolean isIpv6() {
        return startIp instanceof Inet6Address && endIp instanceof Inet6Address;
    }

    public boolean isValid() {
        return startIp != null && endIp != null;
    }

    public boolean match(InetAddress address) {
        return match(address.getAddress());
    }

    public boolean match(String text) {
        AddressMatcher matcher = getMatcher();
        if (matcher == null) {
            return false;
        }
        if (text == null || text.length() == 0) {
            return false;
        }
        return matcher.match(text);
    }

    public boolean match(long ip) {
        AddressMatcher matcher = getMatcher();
        if (matcher == null) {
            return false;
        }
        return matcher.match(ip);
    }

    public boolean match(byte[] addr) {
        AddressMatcher matcher = getMatcher();
        if (matcher == null) {
            return false;
        }
        return matcher.match(addr);
    }

    private AddressMatcher getMatcher() {
        if (startIp == null || endIp == null) {
            return null;
        }
        if (matcher == null) {
            matcher = startIp instanceof Inet6Address ? new Inet6AddressMatcher(startIp, endIp) : new Inet4AddressMatcher(startIp, endIp);
        }
        return matcher;
    }

    private static BigInteger mask(BigInteger addressInt, int size, int mask) {
        if (mask == 0) {
            return BigInteger.ZERO;
        }
        BigInteger maskInt = BigInteger.ONE.shiftLeft(size - mask).subtract(BigInteger.ONE).not();
        return addressInt.and(maskInt);
    }

    private static BigInteger unmask(BigInteger addressInt, int size, int mask) {
        if (mask == 0) {
            return BigInteger.ZERO;
        }
        return addressInt.add(BigInteger.ONE.shiftLeft(size - mask)).subtract(BigInteger.ONE);
    }

    private static int toMask(String maskIp) {
        StringTokenizer nm = new StringTokenizer(maskIp, ".");
        int i = 0;
        int[] netmask = new int[4];
        while (nm.hasMoreTokens()) {
            netmask[i] = Integer.parseInt(nm.nextToken());
            i++;
        }
        int mask = 0;
        for (i = 0; i < 4; i++) {
            mask += Integer.bitCount(netmask[i]);
        }
        return mask;
    }

    private static InetAddress toAddress(String text) {
        try {
            return InetAddress.getByName(text);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public static NetworkMatcher[] getIpv4NetworkMatchers(String[] networks) {
        if (networks == null) {
            return new NetworkMatcher[0];
        }
        List<NetworkMatcher> matchers = new ArrayList<NetworkMatcher>();
        for (String network : networks) {
            NetworkMatcher matcher = new NetworkMatcher(network);
            if (matcher.isValid() && matcher.isIpv4()) {
                matchers.add(matcher);
            }
        }
        return matchers.toArray(new NetworkMatcher[0]);
    }

    public static NetworkMatcher[] getIpv6NetworkMatchers(String[] networks) {
        if (networks == null) {
            return new NetworkMatcher[0];
        }
        List<NetworkMatcher> matchers = new ArrayList<NetworkMatcher>();
        for (String network : networks) {
            NetworkMatcher matcher = new NetworkMatcher(network);
            if (matcher.isValid() && matcher.isIpv6()) {
                matchers.add(matcher);
            }
        }
        return matchers.toArray(new NetworkMatcher[0]);
    }

    public static String[] normalizeNetworks(String[] networks) {
        if (networks == null) {
            return new String[0];
        }
        Set<String> results = new TreeSet<String>();
        for (String network : networks) {
            String result = normalizeNetwork(network);
            if (result != null) {
                results.add(result);
            }
        }
        return results.toArray(new String[0]);
    }

    public static String normalizeNetwork(String network) {
        NetworkMatcher matcher = new NetworkMatcher(network);
        if (!matcher.isValid()) {
            return null;
        }
        InetAddress startIp = matcher.getStartIp();
        InetAddress endIp = matcher.getEndIp();
        return startIp.getHostAddress() + "-" + endIp.getHostAddress();
    }

    private static BigInteger toBigInteger(InetAddress addr) {
        return toBigInteger(addr.getAddress());
    }

    private static BigInteger toBigInteger(byte[] addr) {
        return new BigInteger(addr);
    }

    private static long toLong(InetAddress addr) {
        return toLong(addr.getAddress());
    }

    private static long toLong(byte[] addr) {
        int address = addr[3] & 0xFF;
        address |= ((addr[2] << 8) & 0xFF00);
        address |= ((addr[1] << 16) & 0xFF0000);
        address |= ((addr[0] << 24) & 0xFF000000);
        if (address < 0) {
            long unsign = (long) address;
            unsign -= Integer.MIN_VALUE;
            unsign -= Integer.MIN_VALUE;
            return unsign;
        } else {
            return address;
        }
    }

    public static boolean isValidNetwork(String network) {
        NetworkMatcher matcher = new NetworkMatcher(network);
        return matcher.isValid();
    }

    private static InetAddress toAddress(long addr) {
        try {
            byte[] a = new byte[4];
            a[0] = (byte) (addr >> 24 & 0xFF);
            a[1] = (byte) (addr >> 16 & 0xFF);
            a[2] = (byte) (addr >> 8 & 0xFF);
            a[3] = (byte) (addr & 0xFF);
            return InetAddress.getByAddress(a);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private static InetAddress toAddress(BigInteger addr) {
        byte[] a = new byte[16];
        byte[] b = addr.toByteArray();
        if (b.length > 16 && !(b.length == 17 && b[0] == 0)) {
            return null;
        }
        if (b.length == 16) {
            try {
                return InetAddress.getByAddress(b);
            } catch (UnknownHostException e) {
                return null;
            }
        }
        // handle the case where the IPv6 address starts with "FF".
        if (b.length == 17) {
            System.arraycopy(b, 1, a, 0, 16);
        } else {
            // copy the address into a 16 byte array, zero-filled.
            int p = 16 - b.length;
            for (int i = 0; i < b.length; i++) {
                a[p + i] = b[i];
            }
        }
        try {
            return InetAddress.getByAddress(a);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private static interface AddressMatcher extends Matcher {
        public boolean match(InetAddress address);

        public boolean match(byte[] addr);

        public boolean match(long ip);

        public boolean match(BigInteger ip);
    }

    private static class Inet4AddressMatcher implements AddressMatcher {
        private long startIp;
        private long endIp;

        private Inet4AddressMatcher(InetAddress startAddr, InetAddress endAddr) {
            this.startIp = toLong(startAddr);
            this.endIp = toLong(endAddr);
        }

        public boolean match(InetAddress address) {
            return match(address.getAddress());
        }

        public boolean match(String text) {
            if (text == null || text.length() == 0) {
                return false;
            }
            byte[] addr = toAddress(text.toCharArray());
            return match(addr);
        }

        public boolean match(byte[] addr) {
            if (addr == null) {
                return false;
            }
            return match(toLong(addr));
        }

        public boolean match(long ip) {
            if (startIp == -1 || endIp == -1) {
                return false;
            }
            return ip >= startIp && ip <= endIp;
        }

        public boolean match(BigInteger ip) {
            return match(ip.longValue());
        }

        private static byte[] toAddress(char[] srcb) {
            if (srcb.length == 0) {
                return null;
            }
            int octets = 0;
            byte[] dst = new byte[4];
            boolean saw_digit = false;
            int cur = 0;
            for (int i = 0; i < srcb.length; ) {
                char ch = srcb[i++];
                if (Character.isDigit(ch)) {
                    int sum = dst[cur] * 10 + (Character.digit(ch, 10) & 0xff);
                    if (sum > 255) {
                        return null;
                    }
                    dst[cur] = (byte) (sum & 0xff);
                    if (!saw_digit) {
                        if (++octets > 4) {
                            return null;
                        }
                        saw_digit = true;
                    }
                } else if (ch == '.' && saw_digit) {
                    if (octets == 4) {
                        return null;
                    }
                    cur++;
                    dst[cur] = 0;
                    saw_digit = false;
                } else {
                    return null;
                }
            }
            if (octets < 4) {
                return null;
            }
            return dst;
        }

    }

    private static class Inet6AddressMatcher implements AddressMatcher {
        private BigInteger startIp;
        private BigInteger endIp;

        private Inet6AddressMatcher(InetAddress startAddr, InetAddress endAddr) {
            this.startIp = toBigInteger(startAddr);
            this.endIp = toBigInteger(endAddr);
        }

        public boolean match(InetAddress address) {
            return match(address.getAddress());
        }

        public boolean match(String text) {
            return match(toAddress(text));
        }

        public boolean match(byte[] addr) {
            return match(toBigInteger(addr));
        }

        public boolean match(BigInteger ip) {
            if (startIp == null || endIp == null) {
                return false;
            }
            return ip.compareTo(startIp) >= 0 && ip.compareTo(endIp) <= 0;
        }

        public boolean match(long ip) {
            return match(BigInteger.valueOf(ip));
        }
    }
}
