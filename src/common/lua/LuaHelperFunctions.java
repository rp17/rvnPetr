/**
 * @author Petr (http://www.sallyx.org/)
 */
package common.lua;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

final public class LuaHelperFunctions {

    /**
     * C++ version of this class delete pLua if exception occured But it is
     * completely for nothing in Java :)
     */
    public static class LuaExceptionGuard {

        ScriptEngine pLua;

        public LuaExceptionGuard(ScriptEngine L) {
            this.pLua = L;
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            pLua = null;
        }
    };

    /**
     * runs a script file
     */
    public static void RunLuaScript(ScriptEngine pL, String script_name) {
        try {
            pL.eval(new InputStreamReader(ClassLoader.getSystemResourceAsStream(script_name)));
        } catch (Exception error) {
            throw new RuntimeException("ERROR(" + (error.getMessage())
                    + "): Problem with lua script file " + script_name, error);
        }
    }

    /**
     * a function template to retrieve a number from the lua stack
     */
    @SuppressWarnings("unchecked")
    public static <T extends Number> T PopLuaNumber(ScriptEngine pL, final String name) {
        Object val = pL.get(name);
        if (val instanceof Number) {
            return (T) val;
        } else {
            String err = "<PopLuaNumber> Cannot retrieve: ";
            throw new RuntimeException(err + name);
        }
    }

    /**
     * a function template to retrieve a string from the lua stack
     */
    public static String PopLuaString(ScriptEngine pL, final String name) {
        //check that the variable is the correct type. If it is not throw an
        //exception
        Object val = pL.get(name);
        if (val instanceof String) {
            return (String) val;
        } else {
            String err = "<PopLuaString> Cannot retrieve: ";
            throw new RuntimeException(err + name);
        }
    }

    /**
     * a function template to retrieve a boolean from the lua stack
     */
    public static boolean PopLuaBool(ScriptEngine pL, final String name) {
        //check that the variable is the correct type. If it is not throw an
        //exception
        Object val = pL.get(name);
        if (val instanceof Boolean) {
            //grab the value, cast to the correct type and return
            return (Boolean) val;
        } else {
            String err = "<PopLuaBool> Cannot retrieve: ";
            throw new RuntimeException(err + name);
        }
    }

    public static String LuaPopStringFieldFromTable(LuaTable L, final String key) {
        LuaValue val = L.get(key);

        //check that the variable is the correct type. If it is not throw an
        //exception
        if (!(val instanceof LuaString)) {
            String err = "<LuaPopStringFieldFromTable> Cannot retrieve: ";
            throw new RuntimeException(err + key);
        }

        //grab the data
        return ((LuaString) val).toString();
    }

    public static <T extends Number> T LuaPopNumberFieldFromTable(LuaTable L, final String key) {
        LuaValue val = L.get(key);

        //check that the variable is the correct type. If it is not throw an
        //exception
        if (!(val instanceof LuaString)) {
            String err = "<LuaPopNumberFieldFromTable> Cannot retrieve: ";
            throw new RuntimeException(err + key);
        }

        //grab the data
        return (T) new Double(val.todouble());
    }

    //////////////////////// Lua Interface ////////////////////////////////////
    public static ScriptEngine lua_open() {
        ScriptEngine pL = new ScriptEngineManager().getEngineByExtension(".lua");
        //RunLuaScript(pL, "common/lua/LuaHelperFunctions.lua");
        return pL;
    }

    /**
     * Expose class method as a function in Lua
     *
     * @param pL Lua state
     * @param funName Class method
     * @param aClass Class
     */
    public static void lua_register(ScriptEngine pL, String funName, Class aClass) {
        String className = aClass.getName();
        try {
            // create Lua function
            String fun = "function " + funName + "(...)\n"
                    + "local cl = luajava.bindClass('" + className + "');\n"
                    + "local data = cl:" + funName + "(...);\n"
                    + "return LuaHelperFunctions:unpackUserData(data);\n"
                    + "end";
            //System.out.println(fun);
            pL.eval(fun);
        } catch (ScriptException ex) {
            throw new RuntimeException("<lua_register> Exception: " + ex.getMessage());
        }
    }

    //////////////////////// Luabind Interface ////////////////////////////////
    public static void open(ScriptEngine pL) {
        RunLuaScript(pL, "common/lua/Luabind.lua");
    }
    private static Map<String, String> luaClassNames = new HashMap<String, String>();

    public static LuabindDef lua_class(ScriptEngine pL, Class aClass, String luaClassName) {
        String cname = aClass.getName();
        try {
            pL.eval(String.format("class('@','%s','%s');", cname, luaClassName));
            luaClassNames.put(cname, luaClassName);
            return new LuabindDef(pL, aClass, luaClassName);
        } catch (ScriptException ex) {
            throw new RuntimeException("<lua_class> Exception: " + ex.getMessage());
        }
    }

    private static String lua_getLuaClassName(Class aClass) {
        return luaClassNames.get(aClass.getName());
    }

    public static class LuabindDef {

        private final ScriptEngine pL;
        private final Class aClass;
        private final String luaClassName;

        protected LuabindDef(ScriptEngine pL, Class aClass, String luaClassName) {
            this.pL = pL;
            this.aClass = aClass;
            this.luaClassName = luaClassName;
        }

        public LuabindDef def(String luaMethodName, String javaMethodName) {
            try {
                pL.eval(String.format(luaClassName + ".setMethod('%s','%s');",
                        luaMethodName, javaMethodName));
            } catch (ScriptException ex) {
                throw new RuntimeException("<LuabindDef.def> Exception: " + ex.getMessage());
            }
            return this;
        }

        public LuabindDef base(Class baseClass) {
            if (!aClass.getSuperclass().equals(baseClass)) {
                throw new RuntimeException("<Luabindef.base> Excepton: "
                        + aClass.getName() + " is not instance of " + baseClass.getName());
            }
            try {
                pL.eval(String.format(luaClassName + ".setBase('%s');",
                        lua_getLuaClassName(baseClass)));
            } catch (ScriptException ex) {
                throw new RuntimeException("<LuabindDef.def> Exception: " + ex.getMessage());
            }
            return this;
        }
    }

    public static class LuabindObject {

        private static ScriptEngine pL;
        private final LuaValue val;

        public LuabindObject(LuaValue val) {
            this.val = val;
        }

        public LuabindObject(ScriptEngine pL, LuaValue val) {
            LuabindObject.pL = pL;
            this.val = val;
        }

        /**
         * Get the enumeration value for the type of this value.
         *
         * @return value for this type, one of TNIL, TBOOLEAN, TNUMBER, TSTRING,
         * TTABLE, TFUNCTION, TUSERDATA, TTHREAD
         */
        public int type() {
            return val.type();
        }

        /**
         * Get the String name of the type of this value.
         *
         * @return name from type name list LuaValue.TYPE_NAMES corresponding to
         * the type of this value: "nil", "boolean", "number", "string",
         * "table", "function", "userdata", "thread"
         */
        public String typename() {
            return val.typename();
        }

        /**
         * Get a value in a table including metatag processing using INDEX.
         *
         * @throws LuaError - if this is not a table, or there is no INDEX
         * metatag
         * @param key the key to look up, must not be null
         * @return LuaValue for that key, or NIL if not found
         */
        public LuabindObject get(String key) {
            return new LuabindObject(pL, val.get(key));
        }

        /**
         * Call method with 0 arguments, including metatag processing, and
         * return only the first return value.
         *
         * <p> If
         * <code>name</code> is a LuaFunction, call it, and return only its
         * first return value, dropping any others. Otherwise, look for the CALL
         * metatag and call that. </p><p> If the return value is a Varargs, only
         * the 1st value will be returned. To get multiple values, use
         * LuaValue.invoke() instead. </p><p> To call this as a method call, use
         * LuaValue.method(LuaValue) instead. </p>
         *
         * @return First return value (this()), or NIL if there were none.
         *
         * @throws LuaError if not a function and CALL is not defined, or the
         * invoked function throws a LuaError or the invoked closure throw a lua
         * error
         */
        public LuaValue call(String name) throws LuaError {
            return val.get(name).call();
        }

        public LuaValue call(String name, Object arg) throws LuaError {
            String luaClassName = lua_getLuaClassName(arg.getClass());
            try {
                pL.eval(String.format("__metatable = %s.getMetaTableForUserdata()", luaClassName));
            } catch (ScriptException ex) {
                throw new RuntimeException("<LuabindObject.call> Exception: " + ex.getMessage(), ex);
            }
            LuaValue metatable = ((LuaValue) pL.get("__metatable"));
            return val.get(name).call(LuaValue.userdataOf(arg, metatable));
        }

        public LuaValue call(String name, Object arg1, Object arg2) throws LuaError {
            String luaClassName1 = lua_getLuaClassName(arg1.getClass());
            String luaClassName2 = lua_getLuaClassName(arg2.getClass());
            try {
                pL.eval(String.format("__metatable1=%s.getMetaTableForUserdata()", luaClassName1));
                pL.eval(String.format("__metatable2=%s.getMetaTableForUserdata()", luaClassName2));
            } catch (ScriptException ex) {
                throw new RuntimeException("<LuabindObject.call> Exception: " + ex.getMessage(), ex);
            }
            LuaValue metatable1 = ((LuaValue) pL.get("__metatable1")).getmetatable();
            LuaValue metatable2 = ((LuaValue) pL.get("__metatable2")).getmetatable();
            return val.get(name).call(LuaValue.userdataOf(arg1, metatable1), LuaValue.userdataOf(arg2, metatable2));
        }

        /**
         * Call named method on this with 0 arguments, including metatag
         * processing, and return only the first return value. <p> Look up
         * this[name] and if it is a LuaFunction, call it inserting this as an
         * additional first argument. and return only its first return value,
         * dropping any others. Otherwise, look for the CALL metatag and call
         * that. </p><p> If the return value is a Varargs, only the 1st value
         * will be returned. To get multiple values, use LuaValue.invoke()
         * instead. </p><p> To call this as a plain call, use LuaValue.call()
         * instead. </p>
         *
         * @param name Name of the method to look up for invocation
         * @return All values returned from this:name() as a Varargs instance
         * @throws LuaError - if not a function and CALL is not defined, or the
         * invoked function throws a LuaError or the invoked closure throw a lua
         * error
         */
        public LuaValue method(String name) throws LuaError {
            return val.method(name);
        }

        public LuaValue method(String name, Object arg1) throws LuaError {
            return val.method(name, LuaValue.userdataOf(arg1));
        }

        public LuaValue method(String name, Object arg1, Object arg2) throws LuaError {
            return val.method(name, LuaValue.userdataOf(arg1), LuaValue.userdataOf(arg2));
        }

        public boolean is_valid() {
            return val != null;
        }
        // TODO: add all other public LuaValue methods
    }

    public static LuabindObject globals(ScriptEngine pLua) throws ScriptException {
        pLua.eval("_G=_G"); // WTF?
        return new LuabindObject(pLua, (LuaValue) pLua.get("_G"));
    }
}
