/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.conditions;

import java.util.regex.Matcher;
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
	String target = "download.url";
	boolean all=false;
	boolean any=true;
	boolean start=false;
	Pattern pattern;
	
	public void parseCondition(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("target"))
		{
			target = element.getAttribute("target");
		}
		if (element.hasAttribute("match"))
		{
			any=false;
			if (element.getAttribute("match").equals("any"))
			{
				any=true;
			}
			else if (element.getAttribute("match").equals("all"))
			{
				all=true;
			}
			else if (element.getAttribute("match").equals("start"))
			{
				start=true;
			}
			else
			{
				throw new ConfigurationParseException("Invalid setting for match");
			}
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
		boolean result;
		Matcher matcher = pattern.matcher(env.evaluate(target,type));
		if (any)
		{
			return matcher.find();
		}
		if (all)
		{
			return matcher.matches();
		}
		if (start)
		{
			return matcher.lookingAt();
		}
		return false;
	}

}
