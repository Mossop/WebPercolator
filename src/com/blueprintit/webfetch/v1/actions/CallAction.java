/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.actions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private String tablename;
	private static Log log = LogFactory.getLog(CallAction.class);
	
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
			log.error("Warning, table "+table+" could not be found.");
		}
	}
}
