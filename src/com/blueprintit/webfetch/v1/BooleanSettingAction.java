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
public abstract class BooleanSettingAction extends ElementConfigParser implements Action
{
	protected boolean setting;
	
	public boolean isAction()
	{
		return true;
	}
	
	public void parseElement(Element element)
	{
		if (element.hasAttribute("setting"))
		{
			setting=Boolean.valueOf(element.getAttribute("setting")).booleanValue();
		}
		else
		{
			setting=true;
		}
	}
	
	public abstract void execute(ConfigurationSet config, ScriptingEnvironment env);

	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		return false;
	}
}
