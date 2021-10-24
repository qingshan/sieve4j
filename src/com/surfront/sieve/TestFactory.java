package com.surfront.sieve;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class TestFactory<E> {
    //Test mappings
    protected final Map<String, Class<? extends Test>> testClasses = new HashMap<String, Class<? extends Test>>();

    public Map<String, Class<? extends Test>> getTestClasses() {
        return testClasses;
    }

    public void defineTest(String type, Class<? extends Test> clazz) {
        defineTest(type, null, clazz);
    }

    public void defineTest(String type, String tag, Class<? extends Test> clazz) {
        if (tag == null) {
            testClasses.put(type, clazz);
        } else {
            if (type == null) {
                testClasses.put(":" + tag, clazz);
            } else {
                testClasses.put(type + ":" + tag, clazz);
            }
        }
    }

    public Test<E> getTest(String type, Test<E> test) {
        if (type.equals(NotTest.TYPE)) {
            return NotTest.newTest(test);
        } else {
            throw new SyntaxException("No such test: " + type);
        }
    }

    public Test<E> getTest(String type, Test<E>[] tests) {
        if (type.equals(AllofTest.TYPE)) {
            return AllofTest.newTest(tests);
        } else if (type.equals(AnyofTest.TYPE)) {
            return AnyofTest.newTest(tests);
        } else if (type.startsWith(SomeofTest.TYPE)) {
            try {
                int count = Integer.parseInt(type.substring(7));
                return SomeofTest.newTest(count, tests);
            } catch (NumberFormatException e) {
                throw new SyntaxException("No such test: " + type);
            }
        } else {
            throw new SyntaxException("No such test: " + type);
        }
    }

    public Test<E> getTest(String type, Argument[] arguments) {
        if (type.equals(TrueTest.TYPE)) {
            if (arguments.length == 0) {
                return TrueTest.newTest();
            } else {
                throw new SyntaxException("Invalid Arguments in " + type + " test command");
            }
        } else if (type.equals(FalseTest.TYPE)) {
            if (arguments.length == 0) {
                return FalseTest.newTest();
            } else {
                throw new SyntaxException("Invalid Arguments in " + type + " test command");
            }
        } else {
            return getAbstractTest(type, arguments);
        }
    }

    protected AbstractTest<E> getAbstractTest(String type, Argument[] arguments) {
        Class<? extends Test> testClass = null;
        if (arguments.length > 0 && arguments[0] instanceof TagArgument) {
            String tag = (String) arguments[0].getValue();
            if (testClasses.containsKey(type + ":" + tag)) {
                testClass = testClasses.get(type + ":" + tag);
            } else if (testClasses.containsKey(":" + tag)) {
                testClass = testClasses.get(":" + tag);
            }
        }
        if (testClass == null) {
            testClass = testClasses.get(type);
        }
        return getAbstractTest((Class<? extends AbstractTest>) testClass, type, arguments);
    }

    public <T> AbstractTest<T> getAbstractTest(Class<? extends AbstractTest> testClass, String type, Argument[] arguments) {
        AbstractTest<T> test = null;
        if (testClass != null) {
            try {
                Constructor<? extends AbstractTest> constructor;
                try {
                    constructor = testClass.getConstructor(String.class);
                    test = constructor.newInstance(type);
                } catch (NoSuchMethodException e) {
                    constructor = testClass.getConstructor();
                    test = constructor.newInstance();
                }
            } catch (Exception e) {
                throw new SyntaxException("Invalid test: " + type);
            }
        }
        if (test == null) {
            throw new SyntaxException("No such test: " + type);
        }
        try {
            test.setArguments(arguments);
        } catch (RuntimeException e) {
            throw new SyntaxException("Illegal arguments: " + type);
        }
        return test;
    }
}
