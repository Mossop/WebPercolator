/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.conditions;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.AbstractCondition;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class ScriptCondition extends AbstractCondition
{
	String script;
	
	public void parseCondition(Element element) throws ConfigurationParseException
	{
		script=getElementText(element);
	}

	public boolean checkForMatch(ScriptingEnvironment env)
	{
		return env.evaluateAsBoolean(script,type);
	}

}
