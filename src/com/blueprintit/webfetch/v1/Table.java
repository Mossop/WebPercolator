/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public class Table extends ElementConfigParser implements Action
{
	private boolean newScope;
	private List rows;
	private String name = "";
	
	public Table()
	{
		newScope=true;
		rows = new ArrayList();
	}
	
	public Table(Element element) throws ConfigurationParseException
	{
		this();
		parseConfig(element);
	}
	
	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		if (newScope)
			env.enterNewScope();
		Iterator loop = rows.iterator();
		while (loop.hasNext())
		{
			TableRow row = (TableRow)loop.next();
			if (!row.matches(env))
			{
				continue;
			}
			row.execute(config,env);
			if (env.isDecided())
			{
				break;
			}
		}
		if (newScope)
			env.exitCurrentScope();
	}
	
	public String getName()
	{
		return name;
	}

	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		if (element.getNodeName().equals("Row"))
		{
			rows.add(new TableRow(element));
			return true;
		}
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
		return false;
	}
	
	public void parseConfig(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("name"))
		{
			name=element.getAttribute("name");
		}
		super.parseConfig(element);
	}
}
