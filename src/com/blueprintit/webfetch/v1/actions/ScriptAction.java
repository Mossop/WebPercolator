/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.actions;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.AbstractAction;
import com.blueprintit.webfetch.v1.ConfigurationSet;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class ScriptAction extends AbstractAction
{
	private String content;
	
	public void parseElement(Element element) throws ConfigurationParseException
	{
		content=getElementText(element);
		super.parseElement(element);
	}
	
	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		env.execute(content,type);
	}
}
