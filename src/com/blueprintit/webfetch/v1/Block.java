/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public class Block extends ConditionGroup implements ActionSet, ConditionSet
{
	private static Map actionmap;
	private List actions;
	protected boolean newScope;
	
	static
	{
		actionmap = new HashMap();
		actionmap.put("Accept","com.blueprintit.webfetch.v1.actions.AcceptAction");
		actionmap.put("Reject","com.blueprintit.webfetch.v1.actions.RejectAction");
		actionmap.put("Call","com.blueprintit.webfetch.v1.actions.CallAction");
		actionmap.put("Log","com.blueprintit.webfetch.v1.actions.LogAction");
		actionmap.put("Script","com.blueprintit.webfetch.v1.actions.ScriptAction");
		actionmap.put("Parse","com.blueprintit.webfetch.v1.actions.ParseSettingAction");
		actionmap.put("Overwrite","com.blueprintit.webfetch.v1.actions.OverwriteSettingAction");
	}
	
	public Block(boolean matchAll)
	{
		super(matchAll);
		actions = new ArrayList();
	}
	
	public Block(Element element) throws ConfigurationParseException
	{
		this(true);
		parseConfig(element);
	}

	public static Action createAction(String type)
	{
		if (actionmap.containsKey(type))
		{
			String classname = (String)actionmap.get(type);
			try
			{
				Action result = (Action)Class.forName(classname).newInstance();
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
	
	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		Iterator loop = actions.iterator();
		while (loop.hasNext())
		{
			Action action = (Action)loop.next();
			action.execute(config,env);
		}
	}

	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		Action action = createAction(element.getNodeName());
		if (action!=null)
		{
			action.parseConfig(element);
			actions.add(action);
			return true;
		}
		return super.parseSubElement(element);
	}
	
	public void parseElement(Element element) throws ConfigurationParseException
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
		super.parseElement(element);
	}
}
