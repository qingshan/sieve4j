package com.surfront.sieve.matcher;

import java.lang.String;

public class MatchesMatcherTest {
    public void testMatchesPatterns() {
        String[] patterns = new String[]{
                "qabc.test.com",
                "*",
                "**",
                "?*",
                "*?",
                "*?*",
                "?*?",
                "*.test.com",
                "?abc.test.com",
                "qabc.*.com",
                "qabc.t?st.com",
                "qabc.test.*",
                "qabc.test.co?",
        };
        char[] text = "qabc.test.com".toCharArray();
        for (String pattern : patterns) {
            assertTrue(MatchesMatcher.matches(pattern.toCharArray(), 0, text, 0));
        }
    }

    public void testNotMatchesPatterns() {
        String[] patterns = new String[]{
                "?",
                "??",
                "?.test.com",
                "qabc.?.com",
                "qabc.test.?",
        };
        char[] text = "qabc.test.com".toCharArray();
        for (String pattern : patterns) {
            assertFalse(MatchesMatcher.matches(pattern.toCharArray(), 0, text, 0));
        }
    }

    public void testEscapePatterns() {
        char[] wildcards = new char[] {
                '?',
                '*',
                '\\',
        };
        for (char wildcard : wildcards) {
            char[] pattern = new char[] {'\\' , wildcard};
            char[] text = new char[] {wildcard};
            assertTrue(MatchesMatcher.matches(pattern, 0, text, 0));
        }
    }

    private void assertTrue(boolean value) {

    }

    private void assertFalse(boolean value) {

    }
}
