package com.blueprintit.webfetch;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.blueprintit.webpercolator.Download;

/**
 * @author Dave
 */
public class Environment
{
	private URL target;
	private URL referer;
	private boolean accepted = false;
	private boolean rejected = false;
	private Map custom;
	
	public Environment(URL target)
	{
		this.target=target;
	}
	
	public Environment(URL target, URL referer)
	{
		this.target=target;
		this.referer=referer;
	}

	public Download getDownload()
	{
		return null;
	}
	
	public Map getCustom()
	{
		if (custom==null)
		{
			custom = new HashMap();
		}
		return custom;
	}
	
	public URL getReferer()
	{
		return referer;
	}
	
	public void setReferer(URL referer)
	{
		this.referer = referer;
	}
	
	public URL getTarget()
	{
		return target;
	}
	
	public void setTarget(URL target)
	{
		this.target = target;
	}

	public boolean getAccepted()
	{
		return accepted;
	}

	public void setAccepted(boolean accepted)
	{
		this.accepted = accepted;
	}

	public boolean getRejected()
	{
		return rejected;
	}

	public void setRejected(boolean rejected)
	{
		this.rejected = rejected;
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
		Object result = getValue(key.split("."),0,-1,this);
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
		else if (type.getName().equals("java.lang.Integer"))
		{
			try
			{
				Integer test = Integer.valueOf(value);
				return test;
			}
			catch (Exception e)
			{
				return null;
			}
		}
		else if (type.getName().equals("java.lang.Boolean"))
		{
			try
			{
				Boolean test = Boolean.valueOf(value);
				return test;
			}
			catch (Exception e)
			{
				return null;
			}
		}
		else if (type.getName().equals("java.net.URL"))
		{
			try
			{
				URL test = new URL(value);
				return test;
			}
			catch (MalformedURLException e)
			{
				return null;
			}
		}
		return null;
	}
	
	public void setValue(String key, String value)
	{
		String[] args = key.split(".");
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
