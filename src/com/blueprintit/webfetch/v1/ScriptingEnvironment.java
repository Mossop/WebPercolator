/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

/*import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;*/
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;
import com.blueprintit.webfetch.Environment;

/**
 * @author Dave
 */
public class ScriptingEnvironment
{
	private static final int SCRIPT_UNKNOWN = -1;
	private static final int SCRIPT_PLAIN = 0;
	private static final int SCRIPT_JAVASCRIPT = 1;
	
	private Environment env;
	private Map custom;
	private Context jsContext;
	private Scriptable jsScope;
	private DownloadSettings settings;
	
	/**
	 * @param env
	 */
	public ScriptingEnvironment(Environment env)
	{
		this.env=env;
		settings = new DownloadSettings(env);
		jsContext = Context.enter();
		jsScope = jsContext.initStandardObjects();
		Scriptable target = jsContext.toObject(settings,jsScope);
		jsScope.put("download",jsScope,target);
	}
	
	public void enterNewScope()
	{
		Scriptable newScope = jsContext.initStandardObjects();
		newScope.setParentScope(jsScope);
		jsScope=newScope;
	}
	
	public void exitCurrentScope()
	{
		jsScope=jsScope.getParentScope();
	}
	
	public boolean isDecided()
	{
		return settings.isAccepted()||settings.isRejected();
	}
	
	public DownloadSettings getDownloadSettings()
	{
		return settings;
	}
	
	private int determineScriptType(String type)
	{
		if (type.indexOf("/")<0)
		{
			type="text/"+type;
		}
		if (type.equals("text/plain"))
		{
			return SCRIPT_PLAIN;
		}
		if (type.equals("text/javascript"))
		{
			return SCRIPT_JAVASCRIPT;
		}
		return SCRIPT_UNKNOWN;
	}
	
	public void execute(String script, String type)
	{
		switch (determineScriptType(type))
		{
			case SCRIPT_PLAIN:
				break;
			case SCRIPT_JAVASCRIPT:
				try
				{
					jsContext.evaluateString(jsScope,script,"",1,null);
				}
				catch (JavaScriptException e)
				{
					System.err.println("Error executing script");
				}
				break;
			default:
				throw new IllegalArgumentException("Unknown script type: "+type);
		}
	}
	
	public String evaluate(String script, String type)
	{
		switch (determineScriptType(type))
		{
			case SCRIPT_PLAIN:
				return script;
			case SCRIPT_JAVASCRIPT:
				try
				{
					Object result = jsContext.evaluateString(jsScope,script,"",1,null);
					return jsContext.toString(result);
				}
				catch (JavaScriptException e)
				{
					System.err.println("Error executing script");
					return "";
				}
			default:
				throw new IllegalArgumentException("Unknown script type: "+type);
		}
	}
	
	public void exit()
	{
		jsContext.exit();
		settings.store(env);
	}
}
