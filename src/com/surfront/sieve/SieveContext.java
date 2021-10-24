package com.surfront.sieve;

import com.surfront.sieve.evaluator.Evaluator;
import com.surfront.sieve.evaluator.EvaluatorFactory;
import com.surfront.sieve.matcher.Matcher;
import com.surfront.sieve.matcher.MatcherFactory;
import com.surfront.sieve.matcher.NullMatcher;
import com.surfront.sieve.matcher.TransformerMatcher;
import com.surfront.sieve.parser.ParseException;
import com.surfront.sieve.parser.SieveParser;
import com.surfront.sieve.transformer.Transformer;
import com.surfront.sieve.transformer.TransformerFactory;
import com.surfront.sieve.variable.VariableResolver;

import java.io.*;
import java.nio.CharBuffer;
import java.util.*;

public class SieveContext<E> {
    public static final boolean DEBUG = "true".equals(System.getProperty("sieve.debug"));

    protected final transient ClassLoader classLoader;
    protected final MatcherFactory matcherFactory;
    protected final TransformerFactory transformerFactory;
    protected final TestFactory<E> testFactory;
    protected final CommandFactory<E> commandFactory;
    protected final EvaluatorFactory<E> evaluatorFactory;
    protected final Map<String, String[]> extlists = new HashMap<String, String[]>();
    protected final Map<String, Object> attributes = new HashMap<String, Object>();

    public SieveContext() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public SieveContext(ClassLoader classLoader) {
        this.classLoader = classLoader == null ? Thread.currentThread().getContextClassLoader() : classLoader;
        this.matcherFactory = new MatcherFactory(classLoader);
        this.transformerFactory = new TransformerFactory(classLoader);
        this.evaluatorFactory = new EvaluatorFactory<E>(classLoader);
        this.testFactory = new TestFactory<E>();
        this.commandFactory = new CommandFactory<E>();
        defineMatchers(this.matcherFactory);
        defineTransformers(this.transformerFactory);
        defineTests(this.testFactory);
        defineActions(this.commandFactory);
        defineEvaluators(this.evaluatorFactory);
        defineAttributes(this.attributes);
    }

    public MatcherFactory getMatcherFactory() {
        return matcherFactory;
    }

    public TransformerFactory getTransformerFactory() {
        return transformerFactory;
    }

    public EvaluatorFactory<E> getEvaluatorFactory() {
        return evaluatorFactory;
    }

    public TestFactory<E> getTestFactory() {
        return testFactory;
    }

    public CommandFactory<E> getCommandFactory() {
        return commandFactory;
    }

    public Test<E> getTest(String type, Test<E> test) {
        return testFactory.getTest(type, test);
    }

    public Test<E> getTest(String type, Test<E>[] tests) {
        return testFactory.getTest(type, tests);
    }

    public Test<E> getTest(String type, Argument[] arguments) {
        try {
            return testFactory.getTest(type, arguments);
        } catch (Exception e) {
            IllegalTest test = new IllegalTest(type);
            test.setArguments(arguments);
            return test;
        }
    }

    public ControlCommand<E> getCommand(String type, Test<E> test, Command<E>[] commands) {
        return commandFactory.getCommand(type, test, commands);
    }

    public Command<E> getCommand(String type, Argument[] arguments) {
        try {
            return commandFactory.getCommand(type, arguments);
        } catch (Exception e) {
            IllegalCommand<E> command = new IllegalCommand<E>(type);
            command.setArguments(arguments);
            return command;
        }
    }

    public Matcher getMatcher(String type, String comparator, boolean extlist, String[] patterns) {
        if (extlist) {
            patterns = getExtlist(patterns);
        }
        return matcherFactory.getMatcher(type, comparator, patterns);
    }

    public Matcher getMatcher(String type, String comparator, String[] transformers, boolean extlist, String[] patterns) {
        if (extlist) {
            patterns = getExtlist(patterns);
        }
        Matcher matcher = matcherFactory.getMatcher(type, comparator, patterns);
        if (matcher == null) {
            return null;
        }
        if (matcher instanceof NullMatcher) {
            return matcher;
        }
        Transformer transformer = getTransformer(transformers);
        if (transformer == null) {
            return matcher;
        }
        return new TransformerMatcher(transformer, matcher);
    }

    public Matcher getNetworkMatcher(boolean extlist, String[] patterns) {
        if (extlist) {
            patterns = getExtlist(patterns);
        }
        return matcherFactory.getNetworkMatcher(patterns);
    }

    public Transformer getTransformer(String[] transformers) {
        return transformerFactory.getTransformer(transformers);
    }

    public Transformer getTransformer(String transformer) {
        return transformerFactory.getTransformer(transformer);
    }

    public Evaluator<E> getEvaluator(String type, String function) {
        return evaluatorFactory.getEvaluator(type, function);
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

    public Object resolveVariable(E base, String property) {
        VariableResolver variableResolver = getVariableResolver();
        if (variableResolver == null) {
            return null;
        }
        return variableResolver.resolveVariable(base, property);
    }

    public BlockCommand<E> parse(File file) throws IOException {
        Reader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            return parse(in);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public BlockCommand<E> parse(Reader in) throws IOException {
        try {
            SieveParser<E> parser = new SieveParser<E>(in);
            parser.setSieveContext(this);
            Command<E>[] commands = parser.start();
            return new BlockCommand<E>(commands);
        } catch (ParseException e) {
            throw new IOException("Cannot load sieve script: " + e.getMessage());
        }
    }

    public BlockCommand<E> compile(File file) throws IOException {
        try {
            BlockCommand<E> command = parse(file);
            command.compile(this);
            return command;
        } catch (Exception e) {
            throw new IOException("Cannot load sieve script: " + e.getMessage());
        }
    }

    public BlockCommand<E> compile(Reader in) throws IOException {
        BlockCommand<E> command = parse(in);
        command.compile(this);
        return command;
    }

    public void compile(Command<E> command) {
        command.compile(this);
    }

    public void compile(Test<E> test) {
        test.compile(this);
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public String getResourceAsString(String name) throws IOException {
        InputStream in = null;
        try {
            in = classLoader.getResourceAsStream(name);
            if (in == null) {
                return null;
            }
            return toString(new InputStreamReader(in, "UTF-8"));
        } finally {
          if (in != null) {
              in.close();
          }
        }
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    protected void defineMatchers(MatcherFactory matcherFactory) {
    }

    protected void defineTransformers(TransformerFactory transformerFactory) {
    }

    protected void defineTests(TestFactory<E> testFactory) {
    }

    protected void defineActions(CommandFactory<E> commandFactory) {
    }

    protected void defineEvaluators(EvaluatorFactory<E> evaluatorFactory) {
    }

    protected void defineAttributes(Map<String, Object> attributes) {
    }

    protected void defineExtlist(String name, String[] patterns) {
        extlists.put(name, patterns);
    }

    protected VariableResolver getVariableResolver() {
        return null;
    }

    private static String toString(Reader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        CharBuffer buf = CharBuffer.allocate(32 * 1024);
        while (true) {
            int read = in.read(buf);
            if (read == -1) {
                break;
            }
            buf.flip();
            sb.append(buf, 0, read);
        }
        return sb.toString();
    }
}
