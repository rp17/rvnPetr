/**
 *  Desc:   class encapsulating the basic functionality necessary to read a
 *          Lua config file
 *
 *  @author Petr (http://www.sallyx.org/)
 */
package common.script;

import javax.script.ScriptEngine;
import static common.lua.LuaHelperFunctions.*;

public class Scriptor
{

  private ScriptEngine m_pLuaState;

  public Scriptor() {
      m_pLuaState = lua_open();
    //open the libraries
    //luaL_openlibs(m_pLuaState);
  }

    @Override
  protected void finalize() throws Throwable {
      super.finalize();
      //lua_close(m_pLuaState);
  }

  public void RunScriptFile(String ScriptName)
  {
     RunLuaScript(m_pLuaState, ScriptName);
  }

  public ScriptEngine GetState(){return m_pLuaState;}


  public int GetInt(String VariableName)
  {
    return PopLuaNumber(m_pLuaState, VariableName);
  }
    
  public float GetFloat(String VariableName)
  {
    return PopLuaNumber(m_pLuaState, VariableName);
  }

  public double GetDouble(String VariableName)
  {
    Number x = PopLuaNumber(m_pLuaState, VariableName);
    return x.doubleValue();
  }

  public String GetString(String VariableName)
  {
    return PopLuaString(m_pLuaState, VariableName);
  }

  public boolean GetBool(String VariableName)
  {
    return PopLuaBool(m_pLuaState, VariableName);
  }
}