package com.blueprintit.webfetch.v1;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webpercolator.Download;

/**
 * @author Dave
 */
class ConfigurationSet
{
	private List configsets;
	private ConfigurationSet parent;
	private Map tables;
	private Collection patterns;
	private boolean matchAll;
	private Map settings;
	
	ConfigurationSet(Element element) throws ConfigurationParseException
	{
		tables = new HashMap();
		settings = new HashMap();
		configsets = new LinkedList();
		patterns = new LinkedList();
		matchAll=false;
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
	
	
	protected String getElementText(Element element) throws ConfigurationParseException
	{
		element.normalize();
		NodeList childs = element.getChildNodes();
		StringBuffer text = new StringBuffer();
		for (int loop=0; loop<childs.getLength(); loop++)
		{
			if (childs.item(loop).getNodeType()==Node.TEXT_NODE)
			{
				text.append(childs.item(loop).getNodeValue());
			}
		}
		return text.toString();
	}
	
	protected void parseConfig(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("matchStyle"))
		{
			String value = element.getAttribute("matchStyle").toLowerCase();
			if (value.equals("all"))
			{
				matchAll=true;
			}
			else if (value.equals("any"))
			{
				matchAll=false;
			}
			else
			{
				throw new ConfigurationParseException("");
			}
		}
		NodeList nodes = element.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			Node node = nodes.item(loop);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				Element el = (Element)node;
				if (el.getNodeName().equals("Match"))
				{
					String text = getElementText(el);
					if (text.length()==0)
					{
						System.err.println("Warning, adding a blank regular expression. Will match everything");
					}
					try
					{
						Pattern pattern = Pattern.compile(text);
						patterns.add(pattern);
					}
					catch (PatternSyntaxException e)
					{
						throw new ConfigurationParseException(text+" is not a valid regular expression",e);
					}
				}
				else if (el.getNodeName().equals("Subset"))
				{
					configsets.add(new ConfigurationSet(el));
				}
				else
				{
					throw new ConfigurationParseException("Unknown element in configuration: "+el.getNodeName());
				}
			}
		}
	}
	
	private boolean matches(URL target)
	{
		if (patterns.size()==0)
		{
			return true;
		}
		else
		{
			int matchcount=0;
			Iterator loop = patterns.iterator();
			while (loop.hasNext())
			{
				Pattern pattern = (Pattern)loop.next();
				if (pattern.matcher(target.toString()).matches())
				{
					if (!matchAll)
					{
						return true;
					}
				}
				else
				{
					if (matchAll)
					{
						return false;
					}
				}
			}
			return true;
		}
	}
	
	Download getDownload(URL target, URL referer)
	{
		if (matches(target))
		{
			Iterator loop = configsets.iterator();
			while (loop.hasNext())
			{
				ConfigurationSet config = (ConfigurationSet)loop.next();
				Download result = config.getDownload(target,referer);
				if (result!=null)
				{
					return result;
				}
			}
			// Code
			return null;
		}
		else
		{
			return null;
		}
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
