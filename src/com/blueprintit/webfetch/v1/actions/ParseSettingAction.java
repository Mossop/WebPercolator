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
public class ParseSettingAction extends ElementConfigParser implements Action
{
	protected boolean local;
	protected boolean remote;
	
	public boolean isAction()
	{
		return true;
	}
	
	public void parseElement(Element element)
	{
		if (element.hasAttribute("setting"))
		{
			if (element.getAttribute("setting").equals("true"))
			{
				local=true;
				remote=true;
			}
			else if (element.getAttribute("setting").equals("false"))
			{
				local=false;
				remote=false;
			}
			else if (element.getAttribute("setting").equals("remote"))
			{
				local=false;
				remote=true;
			}
			else if (element.getAttribute("setting").equals("local"))
			{
				local=true;
				remote=false;
			}
		}
		else
		{
			local=true;
			remote=true;
		}
	}
	
	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		env.getDownloadSettings().setParseLocal(local);
		env.getDownloadSettings().setParseRemote(remote);
	}

	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		return false;
	}
}
