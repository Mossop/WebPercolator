/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public class ConfigurationSet extends Block
{
	private List configsets;
	private ConfigurationSet parent;
	private Map tables;
	private Collection patterns;
	private Map settings;
	private boolean newScope;
	
	ConfigurationSet(Element element) throws ConfigurationParseException
	{
		super(true);
		newScope=true;
		tables = new HashMap();
		settings = new HashMap();
		configsets = new LinkedList();
		patterns = new LinkedList();
		parseConfig(element);
	}
	
	ConfigurationSet(ConfigurationSet parent, Element element) throws ConfigurationParseException
	{
		this(element);
		this.parent=parent;
	}

	private Object getSetting(String name)
	{
		if (settings.containsKey(name))
		{
			return settings.get(name);
		}
		else
		{
			return parent.getSetting(name);
		}
	}
	
	private void setSetting(String name, Object value)
	{
		settings.put(name,value);
	}
	
	private void setSetting(String name, boolean value)
	{
		setSetting(name,Boolean.valueOf(value));
	}
	
	private void setSetting(String name, int value)
	{
		setSetting(name,new Integer(value));
	}
	
	private boolean getBooleanSetting(String name)
	{
		Object obj = getSetting(name);
		assert obj instanceof Boolean;
		return ((Boolean)obj).booleanValue();
	}
	
	private int getIntSetting(String name)
	{
		Object obj = getSetting(name);
		assert obj instanceof Integer;
		return ((Integer)obj).intValue();
	}
	
	private String getStringSetting(String name)
	{
		Object obj = getSetting(name);
		assert obj instanceof String;
		return (String)obj;
	}
		
	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		if (element.getNodeName().equals("Subset"))
		{
			configsets.add(new ConfigurationSet(element));
			return true;
		}
		if (element.getNodeName().equals("Table"))
		{
			Table table = new Table(element);
			tables.put(table.getName(),table);
			return true;
		}
		return super.parseSubElement(element);
	}
	
	public void parseConfig(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("scope"))
		{
			if (element.getAttribute("scope").equals("new"))
			{
				newScope=true;
			}
			else if (element.getAttribute("scope").equals("current"))
			{
				newScope=false;
			}
			else
			{
				throw new ConfigurationParseException("scope should be new or current");
			}
		}
		super.parseConfig(element);
	}
	
	private void doApplyConfigurationSet(ScriptingEnvironment env)
	{
		Table start = findTable("");
		if (start!=null)
		{
			start.execute(this,env);
		}
		else
		{
			System.err.println("No default table found");
		}
	}
	
	protected void applyConfigurationSet(ScriptingEnvironment env)
	{
		if (newScope)
			env.enterNewScope();
		execute(this,env);
		if (!env.isDecided())
		{
			Iterator loop = configsets.iterator();
			while (loop.hasNext())
			{
				ConfigurationSet config = (ConfigurationSet)loop.next();
				if (config.matches(env))
				{
					config.applyConfigurationSet(env);
					if (env.isDecided())
						return;
				}
			}
			doApplyConfigurationSet(env);
		}
		if (newScope)
			env.exitCurrentScope();
	}
	
	public Table findTable(String name)
	{
		if (tables.containsKey(name))
		{
			return (Table)tables.get(name);
		}
		else if (parent!=null)
		{
			return parent.findTable(name);
		}
		else
		{
			return null;
		}
	}
}
