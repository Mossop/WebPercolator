/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public abstract class AbstractAction extends ElementConfigParser implements Action
{
	protected String type = "text/javascript";
	
	public void parseConfig(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("type"))
		{
			type=element.getAttribute("type");
		}
	}
	
	public boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		return false;
	}

	public abstract void execute(ConfigurationSet config, ScriptingEnvironment env);
}
