package com.blueprintit.webfetch.v1.actions;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.Action;
import com.blueprintit.webfetch.v1.ConfigurationSet;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;
import com.blueprintit.webfetch.v1.Table;

/**
 * @author Dave
 */
public class CallAction implements Action
{
	String tablename;
	
	public void parseConfig(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("table"))
		{
			tablename=element.getAttribute("table");
		}
		else
		{
			throw new ConfigurationParseException("No table was specified.");
		}
	}

	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		Table table = config.findTable(tablename);
		if (table!=null)
		{
			table.execute(config,env);
		}
		else
		{
			System.err.println("Warning, table "+table+" could not be found.");
		}
	}
}
