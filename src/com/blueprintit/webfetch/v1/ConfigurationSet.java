package com.blueprintit.webfetch.v1;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

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
	
	ConfigurationSet(Element element)
	{
		tables = new HashMap();
		configsets = new LinkedList();
		patterns = new LinkedList();
		matchAll=false;
	}
	
	ConfigurationSet(ConfigurationSet parent, Element element)
	{
		this(element);
		this.parent=parent;
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
