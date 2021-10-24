package com.surfront.sieve.matcher;

import com.surfront.sieve.Comparator;
import com.surfront.sieve.SieveContext;
import com.surfront.sieve.SyntaxException;

import java.lang.reflect.Constructor;
import java.util.*;

public class MatcherFactory {
    public static final String IS = "is";
    public static final String CONTAINS = "contains";
    public static final String STARTS_WITH = "starts_with";
    public static final String ENDS_WITH = "ends_with";
    public static final String MATCHES = "matches";
    public static final String REGEX = "regex";

    public static final String[] DEFAULT_MATCHER_TYPES = new String[]{
            IS,
            CONTAINS,
            STARTS_WITH,
            ENDS_WITH,
            MATCHES,
            REGEX,
    };
    protected final Map<String, Class<? extends Matcher>> matcherClasses;
    protected final Map<String, String[]> extlists;

    public MatcherFactory(ClassLoader classLoader) {
        this.matcherClasses = getMatcherClasses(classLoader);
        this.extlists = new HashMap<String, String[]>();
    }

    public Map<String, String[]> getExtlists() {
        return extlists;
    }

    public String[] getExtlist(String name) {
        return extlists.get(name);
    }

    public String[] getExtlist(String[] names) {
        List<String> patterns = new ArrayList<String>();
        for (String name : names) {
            String[] extlist = getExtlist(name);
            if (extlist == null) {
                continue;
            }
            Collections.addAll(patterns, extlist);
        }
        return patterns.toArray(new String[0]);
    }

    public String[] getExtlist(Matcher matcher) {
        List<String> patterns = new ArrayList<String>();
        for (String name : extlists.keySet()) {
            if (!matcher.match(name)) {
                continue;
            }
            String[] extlist = extlists.get(name);
            if (extlist == null) {
                continue;
            }
            Collections.addAll(patterns, extlist);
        }
        return patterns.toArray(new String[0]);
    }

    public void defineMatcher(String type, Class<? extends Matcher> matcher) {
        matcherClasses.put(type, matcher);
    }

    public void defineExtlists(Map<String, String[]> extlists) {
        for (String name : extlists.keySet()) {
            defineExtlist(name, extlists.get(name));
        }
    }

    public void defineExtlist(String name, String[] patterns) {
        extlists.put(name, patterns);
    }

    public Matcher getMatcher(String type, String comparator, boolean extlist, String[] patterns) {
        if (extlist) {
            return getMatcher(type, comparator, getExtlist(patterns));
        } else {
            return getMatcher(type, comparator, patterns);
        }
    }

    public Matcher getMatcher(String type, String comparator, String[] patterns) {
        if (matcherClasses.containsKey(type)) {
            Class<? extends Matcher> matcherClass = matcherClasses.get(type);
            if (matcherClass == null) {
                throw new SyntaxException("No such matcher: " + type);
            }
            try {
                Constructor<? extends Matcher> constructor;
                try {
                    constructor = matcherClass.getConstructor(String[].class, String.class);
                    return constructor.newInstance(patterns, comparator);
                } catch (NoSuchMethodException e) {
                    constructor = matcherClass.getConstructor(String[].class);
                    return constructor.newInstance(new Object[] {patterns});
                }
            } catch (Exception e) {
                throw new SyntaxException("Invalid matcher: " + type);
            }
        } else {
            return getMatcher(type, Comparator.COMPARATOR_OCTET.equals(comparator), patterns);
        }
    }

    public Matcher getNetworkMatcher(boolean extlist, String[] patterns) {
        if (extlist) {
            return getNetworkMatcher(getExtlist(patterns));
        } else {
            return getNetworkMatcher(patterns);
        }
    }

    public static Matcher getMatcher(String type, boolean caseSensitive, String[] patterns) {
        if (patterns == null || patterns.length == 0) {
            return new NullMatcher();
        }
        boolean debug = SieveContext.DEBUG && patterns.length > 5;
        List<Matcher> matchers = new ArrayList<Matcher>();
        for (String pattern : patterns) {
            Matcher matcher = getMatcher(type, caseSensitive, pattern);
            if (debug) {
                matcher = new DebugMatcher(matcher, pattern);
            }
            matchers.add(matcher);
        }
        if (isCharArrayMatchers(matchers)) {
            return new MultiCharArrayMatcher(matchers.toArray(new CharArrayMatcher[0]), caseSensitive);
        } else {
            return new MultiMatcher(matchers.toArray(new Matcher[0]));
        }
    }

    public static boolean isCharArrayMatchers(List<Matcher> matchers) {
        for (Matcher matcher : matchers) {
            if (!(matcher instanceof CharArrayMatcher)) {
                return false;
            }
        }
        return true;
    }

    public static Matcher getMatcher(String type, boolean caseSensitive, String pattern) {
        if (pattern == null || pattern.length() == 0) {
            return new NullMatcher();
        }
        if (CONTAINS.equalsIgnoreCase(type)) {
            return new BMContainsMatcher(pattern, caseSensitive);
        } else if (STARTS_WITH.equalsIgnoreCase(type)) {
            return new StartsWithMatcher(pattern, caseSensitive);
        } else if (ENDS_WITH.equalsIgnoreCase(type)) {
            return new EndsWithMatcher(pattern, caseSensitive);
        } else if (MATCHES.equalsIgnoreCase(type)) {
            return new MatchesMatcher(pattern, caseSensitive);
        } else if (REGEX.equalsIgnoreCase(type)) {
            return new RegexMatcher(pattern, caseSensitive);
        } else if (IS.equalsIgnoreCase(type)) {
            return new IsMatcher(pattern, caseSensitive);
        } else {
            throw new SyntaxException("Invalid matcher: " + type);
        }
    }

    public static Matcher getIncludeExcludeMatcher(String type, boolean caseSensitive, String[] includes, String[] excludes) {
        Matcher includeMatcher = getMatcher(type, caseSensitive, includes);
        Matcher excludeMatcher = getMatcher(type, caseSensitive, excludes);
        return new IncludeExcludeMatcher(includeMatcher, excludeMatcher);
    }

    public static Matcher getIncludeExcludeMatcher(String type, boolean caseSensitive, String[] patterns) {
        Set<String> includes = new HashSet<String>();
        Set<String> excludes = new HashSet<String>();
        if (patterns == null || patterns.length == 0) {
            return new NullMatcher();
        }
        for (String pattern : patterns) {
            if (pattern.startsWith("+")) {
                includes.add(pattern.substring(1));
            } else if (pattern.startsWith("-")) {
                excludes.add(pattern.substring(1));
            }
        }
        return getIncludeExcludeMatcher(type, caseSensitive, includes.toArray(new String[0]), excludes.toArray(new String[0]));
    }

    public static ByteMatcher getByteMatcher(byte[] pattern) {
        return new BMByteMatcher(pattern);
    }

    public static Matcher getNetworkMatcher(String[] patterns) {
        NetworkMatcher[] matchers = getNetworkMatchers(patterns);
        return new MultiMatcher(matchers);
    }

    public static NetworkMatcher[] getNetworkMatchers(String[] patterns) {
        if (patterns == null || patterns.length == 0) {
            return new NetworkMatcher[0];
        }
        NetworkMatcher[] matchers = new NetworkMatcher[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            matchers[i] = new NetworkMatcher(patterns[i].trim());
        }
        return matchers;
    }

    public static Matcher getIncludeExcludeNetworkMatcher(String[] patterns) {
        Set<String> includes = new HashSet<String>();
        Set<String> excludes = new HashSet<String>();
        if (patterns == null || patterns.length == 0) {
            return new NullMatcher();
        }
        for (String pattern : patterns) {
            if (pattern.startsWith("+")) {
                includes.add(pattern.substring(1));
            } else if (pattern.startsWith("-")) {
                excludes.add(pattern.substring(1));
            }
        }
        return getIncludeExcludeNetworkMatcher(includes.toArray(new String[0]), excludes.toArray(new String[0]));
    }

    public static Matcher getIncludeExcludeNetworkMatcher(String[] includes, String[] excludes) {
        Matcher includeMatcher = getNetworkMatcher(includes);
        Matcher excludeMatcher = getNetworkMatcher(excludes);
        return new IncludeExcludeMatcher(includeMatcher, excludeMatcher);
    }

    public static TimeMatcher[] getTimeMatchers(String[] patterns) {
        if (patterns == null || patterns.length == 0) {
            return new TimeMatcher[0];
        }
        TimeMatcher[] matchers = new TimeMatcher[patterns.length];
        for (int i = 0; i < patterns.length; i++) {
            matchers[i] = new TimeMatcher(patterns[i]);
        }
        return matchers;
    }

    public static Matcher getHashMatcher(String[] patterns) {
        return new HashIsMatcher(patterns);
    }

    private static Map<String, Class<? extends Matcher>> getMatcherClasses(ClassLoader classLoader) {
        Map<String, Class<? extends Matcher>> matcherClasses = new HashMap<String, Class<? extends Matcher>>();
        for (MatcherProvider provider : ServiceLoader.load(MatcherProvider.class, classLoader)) {
            matcherClasses.putAll(provider.getClasses());
        }
        return matcherClasses;
    }
}
