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
public class TableRow extends ConditionSet implements Action
{
	private static Map actionmap;
	private List actions;
	
	static
	{
		actionmap = new HashMap();
		actionmap.put("Accept","com.blueprintit.webfetch.v1.actions.AcceptAction");
		actionmap.put("Reject","com.blueprintit.webfetch.v1.actions.RejectAction");
		actionmap.put("Call","com.blueprintit.webfetch.v1.actions.CallAction");
	}
	
	public TableRow()
	{
		super(true);
		actions = new ArrayList();
	}
	
	public TableRow(Element element) throws ConfigurationParseException
	{
		this();
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
	
	public boolean parseSubElement(Element element) throws ConfigurationParseException
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

	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		Iterator loop = actions.iterator();
		while (loop.hasNext())
		{
			Action action = (Action)loop.next();
			action.execute(config,env);
		}
	}
}
