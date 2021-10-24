package com.surfront.sieve.matcher;

public class NetworkMatcherTest {
    public static void main(String[] args) {
        test("192.168.1.100-192.168.1.199", new String[]{
                "192.168.1.10",
                "192.168.1.100",
                "192.168.1.200",
        });
        test("192.168.1.100-199", new String[]{
                "192.168.1.10",
                "192.168.1.100",
                "192.168.1.200",
        });
        test("192.168.1.*", new String[]{
                "192.168.1.10",
                "192.168.1.100",
                "192.168.1.200",
        });
        test("192.168.1.0/24", new String[]{
                "192.168.0.10",
                "192.168.1.100",
                "192.168.2.200",
        });
        test("8000::0-ffff::0", new String[]{
                "6000::1",
                "8000::1",
                "9000::1",
        });
        test("8000::0/32", new String[]{
                "6000::1",
                "8000::1",
                "9000::1",
        });
    }

    private static void test(String pattern, String[] addresses) {
        NetworkMatcher matcher = new NetworkMatcher(pattern);
        System.out.println("Pattern: " + pattern);
        System.out.println("Normalized: " + NetworkMatcher.normalizeNetwork(pattern));
        for (String address : addresses) {
            System.out.println(address + " = " + matcher.match(address));
        }
    }
}
