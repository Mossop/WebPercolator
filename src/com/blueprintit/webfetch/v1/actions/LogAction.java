/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.actions;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.Action;
import com.blueprintit.webfetch.v1.ConfigurationSet;
import com.blueprintit.webfetch.v1.ElementConfigParser;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class LogAction extends ElementConfigParser implements Action
{
	private String content;
	
	public void parseConfig(Element element) throws ConfigurationParseException
	{
		content=getElementText(element);
	}

	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		System.err.println(env.evaluate(content));
	}

	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		return false;
	}

}
