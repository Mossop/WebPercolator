package com.blueprintit.webfetch.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import com.blueprintit.webfetch.*;
import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public class ConditionSet extends ElementConfigParser implements Condition
{
	private static Map conditionmap;
	private List conditions;
	private boolean matchAll;
	
	static
	{
		conditionmap = new HashMap();
		conditionmap.put("Or","com.blueprintit.webfetch.v1.ConditionSet");
		conditionmap.put("And","com.blueprintit.webfetch.v1.ConditionSet");
		conditionmap.put("Conditions","com.blueprintit.webfetch.v1.ConditionSet");
	}
	
	private ConditionSet()
	{
		conditions = new ArrayList();
	}
	
	public ConditionSet(boolean matchAll)
	{
		this();
		this.matchAll=matchAll;
	}
	
	public ConditionSet(Element element) throws ConfigurationParseException
	{
		this();
		parseConfig(element);
	}

	public static Condition createCondition(String type)
	{
		if (conditionmap.containsKey(type))
		{
			String classname = (String)conditionmap.get(type);
			try
			{
				Condition result = (Condition)Class.forName(classname).newInstance();
				return result;
			}
			catch (Exception e)
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
	
	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		Condition condition = createCondition(element.getNodeName());
		if (condition!=null)
		{
			conditions.add(condition);
			return true;
		}
		return false;
	}
	
	public void parseConfig(Element element) throws ConfigurationParseException
	{
		if (element.getNodeName().equals("Conditions"))
		{
			if (element.hasAttribute("matchStyle"))
			{
				String style = element.getAttribute("matchStyle").toLowerCase();
				if (style.equals("or"))
				{
					matchAll=false;
				}
				else if (style.equals("and"))
				{
					matchAll=true;
				}
				else 
				{
					throw new ConfigurationParseException("An invalid match style was specified");
				}
			}
			else
			{
				throw new ConfigurationParseException("A match style must be specified for a Conditions tag");
			}
		}
		else if (element.getNodeName().equals("Or"))
		{
			matchAll=false;
		}
		else if (element.getNodeName().equals("And"))
		{
			matchAll=true;
		}
		else
		{
			assert false;
		}
		super.parseConfig(element);
	}

	public boolean matches(Environment env)
	{
		if (conditions.size()==0)
		{
			return true;
		}
		else
		{
			int matchcount=0;
			Iterator loop = conditions.iterator();
			while (loop.hasNext())
			{
				Condition condition = (Condition)loop.next();
				if (condition.matches(env))
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
}
