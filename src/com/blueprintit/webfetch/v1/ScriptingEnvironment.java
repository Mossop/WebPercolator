/*
 * Created on 28-Apr-2004
 */
package com.blueprintit.webfetch.v1;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.blueprintit.webfetch.Environment;

/**
 * @author Dave
 */
public class ScriptingEnvironment
{
	private Environment env;
	private Map custom;
	
	/**
	 * @param env
	 */
	public ScriptingEnvironment(Environment env)
	{
		this.env=env;
	}
	
	public boolean getAccepted()
	{
		return env.getAccepted();
	}
	
	public void setAccepted(boolean value)
	{
		env.setAccepted(value);
	}
	
	public boolean getRejected()
	{
		return env.getRejected();
	}
	
	public void setRejected(boolean value)
	{
		env.setRejected(value);
	}
	
	public Map getCustom()
	{
		if (custom==null)
		{
			custom = new HashMap();
		}
		return custom;
	}
	
	private Object getValue(String[] args, int pos, int max, Object key)
	{
		if ((pos==args.length)||(pos==max))
		{
			return key;
		}
		if (key==null)
		{
			return null;
		}
		if (key instanceof Map)
		{
			Object result = ((Map)key).get(args[pos]);
			return getValue(args,pos+1,max,result);
		}
		else
		{
			String methodname ="get"+args[pos].substring(0,1).toUpperCase()+args[pos].substring(1);
			try
			{
				Method call = key.getClass().getMethod(methodname,new Class[0]);
				Object result = call.invoke(key,new Object[0]);
				return getValue(args,pos+1,max,result);
			}
			catch (Exception e)
			{
				return null;
			}
		}
	}
	
	public String getValue(String key)
	{
		Object result = getValue(key.split("\\."),0,-1,this);
		if (result==null)
		{
			System.err.println("Property "+key+" could not be found.");
			return "";
		}
		else
		{
			return result.toString();
		}
	}
	
	private Object convertValue(String value, Class type)
	{
		if (type.getName().equals("java.lang.String"))
		{
			return value;
		}
		else
		{
			try
			{
				Constructor con = type.getConstructor(new Class[] {value.getClass()});
				return con.newInstance(new Object[] {value});
			}
			catch (Exception e)
			{
				return null;
			}
		}
	}
	
	public void setValue(String key, String value)
	{
		String[] args = key.split("\\.");
		Object object = getValue(args,0,args.length-1,this);
		if (object!=null)
		{
			if (object instanceof Map)
			{
				((Map)object).put(args[args.length-1],value);
			}
			else
			{
				String methodname ="set"+args[args.length-1].substring(0,1).toUpperCase()+args[args.length-1].substring(1);
				try
				{
					Method[] methods = key.getClass().getMethods();
					Method method = null;
					Object converted = null;
					for (int loop=0; loop<methods.length; loop++)
					{
						if ((methods[loop].getName().equals(methodname))&&(methods[loop].getReturnType()==Void.TYPE))
						{
							Class[] params = methods[loop].getParameterTypes();
							if (params.length==1)
							{
								converted = convertValue(value,params[0]);
								if (converted!=null)
								{
									method = methods[loop];
									break;
								}
							}
						}
					}
					if (method!=null)
					{
						method.invoke(object,new Object[] {converted});
					}
					else
					{
						System.err.println("Could not find a property called "+key+" that could be set to "+value);
					}
				}
				catch (Exception e)
				{
					System.err.println("Property "+key+" could not be found.");
				}
			}
		}
		else
		{
			System.err.println("Property "+key+" could not be found.");
		}
	}
}
