/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.conditions;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.AbstractCondition;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class RegexCondition extends AbstractCondition
{
	String target = "target";
	Pattern pattern;
	
	public void parseCondition(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("target"))
		{
			target = element.getAttribute("target");
		}
		String text = getElementText(element);
		if (text.length()==0)
		{
			System.err.println("Warning, adding a blank regular expression. Will match everything");
		}
		try
		{
			pattern = Pattern.compile(text);
		}
		catch (PatternSyntaxException e)
		{
			throw new ConfigurationParseException(text+" is not a valid regular expression",e);
		}
	}

	public boolean checkForMatch(ScriptingEnvironment env)
	{
		return pattern.matcher(env.evaluate(target)).matches();
	}

}
