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
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class AcceptAction implements Action
{
	public void parseConfig(Element element) throws ConfigurationParseException
	{
	}

	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		env.getDownloadSettings().accept();
	}
}
