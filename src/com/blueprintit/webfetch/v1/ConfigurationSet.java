package com.blueprintit.webfetch.v1;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.Environment;

/**
 * @author Dave
 */
class ConfigurationSet extends ConditionSet
{
	private List configsets;
	private ConfigurationSet parent;
	private Map tables;
	private Collection patterns;
	private Map settings;
	
	ConfigurationSet(Element element) throws ConfigurationParseException
	{
		super(true);
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
		
	protected void parseConfig(Element element) throws ConfigurationParseException
	{
		NodeList nodes = element.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			Node node = nodes.item(loop);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element el = (Element)node;
				if (el.getNodeName().equals("Subset"))
				{
					configsets.add(new ConfigurationSet(el));
				}
				else
				{
					if (!parsePossibleCondition(el))
					{
						throw new ConfigurationParseException("Unknown element in configuration: "+element.getNodeName());
					}
				}
			}
		}
	}
	
	private void doApplyConfiguration(Environment env)
	{	
	}
	
	void applyConfiguration(Environment env)
	{
		Iterator loop = configsets.iterator();
		while (loop.hasNext())
		{
			ConfigurationSet config = (ConfigurationSet)loop.next();
			if (config.matches(env))
			{
				config.applyConfiguration(env);
				if (env.getRejected()||env.getAccepted())
					return;
			}
		}
		doApplyConfiguration(env);
	}
	
	Table findTable(String name)
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