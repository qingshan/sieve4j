package com.surfront.sieve.script;

import com.surfront.sieve.SieveContext;
import com.surfront.sieve.SyntaxException;
import com.surfront.sieve.evaluator.Function;

import javax.script.*;

public class Language<E> {
    public static final String CALL_METHOD = "call";
    public static final String CALL_RESULT = "result";
    protected final SieveContext<E> context;
    protected final String language;
    protected final String extension;
    protected final String initializer;
    protected final String argument;
    protected final ScriptEngineManager scriptEngineManager;

    protected Language(SieveContext<E> context, String language, String extension, String initializer, String argument) {
        this.context = context;
        this.language = language;
        this.extension = extension;
        this.initializer = initializer;
        this.argument = argument;
        this.scriptEngineManager = getScriptEngineManager(context);
    }

    public String getLanguage() {
        return language;
    }

    public String getExtension() {
        return extension;
    }

    public String getInitializer() {
        return initializer;
    }

    public Script<E> getScript(String scriptText) {
        Function function = null;
        try {
            function = Function.parse(scriptText);
        } catch (Exception e) {
            //ignore it
        }
        if (function == null) {
            return getInlineScript(scriptText);
        } else {
            return getFunctionScript(function.getName(), function.getArguments());
        }
    }

    public Script<E> getInlineScript(String scriptText) {
        try {
            ScriptEngine engine = getScriptEngine();
            return new InlineScript(engine, scriptText);
        } catch (ScriptException e) {
            throw new SyntaxException("Invalid script: " + scriptText, e);
        }
    }

    public Script<E> getFunctionScript(String function, Object[] arguments) {
        try {
            ScriptEngine engine = getScriptEngine();
            String scriptText = context.getResourceAsString(function + "." + extension);
            Object evaluator = null;
            if (scriptText != null && scriptText.length() > 0) {
                evaluator = engine.eval(scriptText);
                if (evaluator == null) {
                    evaluator = ((Invocable) engine).invokeFunction(function, arguments);
                }
            }
            return new FunctionScript(engine, evaluator, function);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SyntaxException("Invalid script: " + function, e);
        }
    }

    public Object translate(E data) {
        return data;
    }

    protected  <T> T convert(Class<T> type, Object result) {
        if (Boolean.class.equals(type)) {
            return (T) toBoolean(result);
        } else {
            return (T) result;
        }
    }

    protected Boolean toBoolean(Object result) {
        return !isNullOrFalse(result);
    }

    protected Boolean isNullOrFalse(Object result) {
        return result == null || "false".equals(result) || Boolean.FALSE.equals(result);
    }

    protected ScriptEngine getScriptEngine() throws ScriptException {
        ScriptEngine engine = scriptEngineManager.getEngineByName(language);
        if (initializer != null && initializer.length() > 0) {
            engine.eval(initializer);
        }
        return engine;
    }

    protected static <E> ScriptEngineManager getScriptEngineManager(SieveContext<E> context) {
        String name = ScriptEngineManager.class.getName();
        ScriptEngineManager scriptEngineManager = (ScriptEngineManager) context.getAttribute(name);
        if (scriptEngineManager == null) {
            scriptEngineManager = new ScriptEngineManager(context.getClassLoader());
            context.setAttribute(name, scriptEngineManager);
        }
        return scriptEngineManager;
    }

    protected class InlineScript implements Script<E> {
        private final ScriptEngine engine;
        private final String scriptText;
        private final CompiledScript compiledScript;

        public InlineScript(ScriptEngine engine, String scriptText) throws ScriptException {
            this.engine = engine;
            this.scriptText = scriptText;
            if ("ruby".equals(language)) {
                //http://jira.codehaus.org/browse/JRUBY-5553
                engine.put(argument, null);
            }
            if (engine instanceof Compilable) {
                Compilable compilable = (Compilable) engine;
                this.compiledScript = compilable.compile(scriptText);
            } else {
                this.compiledScript = null;
            }
        }

        public <T> T evaluate(E data, Class<T> type) {
            Bindings bindings = engine.createBindings();
            bindings.put(argument, translate(data));
            Object result = null;
            try {
                if (compiledScript != null) {
                    result = compiledScript.eval(bindings);
                } else {
                    if (scriptText != null) {
                        result = engine.eval(scriptText, bindings);
                    }
                }
                if (result == null) {
                    result = engine.get(CALL_RESULT);
                }
            } catch (ScriptException e) {
                //TODO debug
                //Ignore it
            }
            return convert(type, result);
        }
    }

    protected class FunctionScript implements Script<E> {
        protected final ScriptEngine engine;
        protected final Object evaluator;
        protected final String function;

        public FunctionScript(ScriptEngine engine, Object evaluator, String function) {
            this.engine = engine;
            this.evaluator = evaluator;
            this.function = function == null ? CALL_METHOD : function;
        }

        public <T> T evaluate(E data, Class<T> type) {
            Object result = null;
            try {
                if (evaluator == null) {
                    //result = invokeFunction(CALL_METHOD, function, translate(data));
                    result = invokeFunction(function, translate(data));
                } else {
                    result = invokeMethod(evaluator, CALL_METHOD, translate(data));
                }
            } catch (Exception e) {
                //TODO debug
                //Ignore it
            }
            return convert(type, result);
        }

        protected Object invokeFunction(String method, Object... args) throws ScriptException, NoSuchMethodException {
            return ((Invocable) engine).invokeFunction(method, args);
        }

        protected Object invokeMethod(Object obj, String method, Object... args) throws ScriptException, NoSuchMethodException {
            return ((Invocable) engine).invokeMethod(obj, method, args);
        }
    }
}
