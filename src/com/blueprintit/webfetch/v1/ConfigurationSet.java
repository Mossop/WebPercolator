/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.io.File;
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
	private Map localsettings;
	
	protected ConfigurationSet()
	{
		super(true);
		newScope=true;
		tables = new HashMap();
		settings = new HashMap();
		localsettings = new HashMap();
		configsets = new LinkedList();
		patterns = new LinkedList();
	}
	
	ConfigurationSet(Element element) throws ConfigurationParseException
	{
		this();
		parseConfig(element);
	}
	
	ConfigurationSet(ConfigurationSet parent, Element element) throws ConfigurationParseException
	{
		this();
		this.parent=parent;
		parseConfig(element);
	}

	private Object getLocalSetting(String name)
	{
		return localsettings.get(name);
	}
	
	private Object getCascadingSetting(String name)
	{
		if (settings.containsKey(name))
		{
			return settings.get(name);
		}
		else if (parent!=null)
		{
			return parent.getCascadingSetting(name);
		}
		else
		{
			return null;
		}
	}
	
	protected Object getSetting(String name)
	{
		Object result = getLocalSetting(name);
		if (result!=null)
			return result;

		result = getCascadingSetting(name);
		if (result!=null)
			return result;
		
		return null;
	}
	
	private boolean isCascadedSet(String name)
	{
		return settings.containsKey(name)||((parent!=null)&&(parent.isCascadedSet(name)));
	}
	
	private boolean isLocalSet(String name)
	{
		return localsettings.containsKey(name);
	}
	
	protected boolean isSet(String name)
	{
		return isLocalSet(name)||isCascadedSet(name);
	}
	
	protected void setLocalSetting(String name, boolean value)
	{
		setSetting(name,value,false);
	}

	protected void setCascadingSetting(String name, boolean value)
	{
		setSetting(name,value,false);
	}

	protected void setLocalSetting(String name, int value)
	{
		setSetting(name,value,false);
	}

	protected void setCascadingSetting(String name, int value)
	{
		setSetting(name,value,false);
	}

	protected void setLocalSetting(String name, Object value)
	{
		setSetting(name,value,false);
	}

	protected void setCascadingSetting(String name, Object value)
	{
		setSetting(name,value,false);
	}

	protected void setSetting(String name, Object value, boolean cascading)
	{
		if (cascading)
		{
			settings.put(name,value);			
		}
		else
		{
			localsettings.put(name,value);
		}
	}
	
	protected void setSetting(String name, boolean value, boolean cascading)
	{
		setSetting(name,Boolean.valueOf(value),cascading);
	}
	
	protected void setSetting(String name, int value, boolean cascading)
	{
		setSetting(name,new Integer(value),cascading);
	}
	
	protected boolean getBooleanSetting(String name)
	{
		Object obj = getSetting(name);
		assert obj instanceof Boolean;
		return ((Boolean)obj).booleanValue();
	}
	
	protected int getIntSetting(String name)
	{
		Object obj = getSetting(name);
		assert obj instanceof Integer;
		return ((Integer)obj).intValue();
	}
	
	protected String getStringSetting(String name)
	{
		Object obj = getSetting(name);
		assert obj instanceof String;
		return (String)obj;
	}
		
	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		if (element.getNodeName().equals("Subset"))
		{
			configsets.add(new ConfigurationSet(this,element));
			return true;
		}
		if (element.getNodeName().equals("Table"))
		{
			Table table = new Table(element);
			tables.put(table.getName(),table);
			return true;
		}
		if (element.getNodeName().equals("Default"))
		{
			boolean cascade=false;
			if (element.hasAttribute("cascade"))
			{
				cascade = Boolean.valueOf(element.getAttribute("cascade")).booleanValue();
			}
			if (element.hasAttribute("policy"))
			{
				if (element.getAttribute("policy").equals("accept"))
				{
					setSetting("policy",true,cascade);				
				}
				else if (element.getAttribute("policy").equals("reject"))
				{
					setSetting("policy",false,cascade);
				}
				else
				{
					throw new ConfigurationParseException("A policy must be accept or reject.");
				}
			}
			else
			{
				throw new ConfigurationParseException("A policy must be set with the default element.");
			}
		}
		return super.parseSubElement(element);
	}
	
	public void parseElement(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("basedir"))
		{
			File current = (File)getSetting("basedir");
			File newdir = new File(current,element.getAttribute("basedir"));
			if (newdir.isDirectory())
			{
				throw new ConfigurationParseException("basedir attribute must specify a valid directory");
			}
			setCascadingSetting("basedir",current);
		}
		super.parseElement(element);
	}
	
	private void doApplyConfigurationSet(ScriptingEnvironment env)
	{
		Table start = findTable(null);
		if (start!=null)
		{
			start.execute(this,env);
		}
		else
		{
			System.err.println("No default table found");
		}
		if (!env.isDecided())
		{
			if (isSet("policy"))
			{
				if (getBooleanSetting("policy"))
				{
					env.getDownloadSettings().accept();
				}
				else
				{
					env.getDownloadSettings().reject();
				}
			}
		}
	}
	
	protected void applyConfigurationSet(ScriptingEnvironment env)
	{
		if (newScope)
			env.enterNewScope();
		env.setBaseDir((File)getSetting("basedir"));
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
						break;
					env.setBaseDir((File)getSetting("basedir"));
				}
			}
		}
		if (!env.isDecided())
			doApplyConfigurationSet(env);
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
