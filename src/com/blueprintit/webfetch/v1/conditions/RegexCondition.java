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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	private static Log log = LogFactory.getLog(RegexCondition.class);
	
	public void parseCondition(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("target"))
		{
			target = element.getAttribute("target");
		}
		else if (element.hasAttribute("source"))
		{
			target = element.getAttribute("source");
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
		int flags=0;
		if ((element.hasAttribute("insensitive"))&&(Boolean.valueOf(element.getAttribute("insensitive")).booleanValue()))
		{
			flags=flags|Pattern.CASE_INSENSITIVE;
		}
		String text = getElementText(element);
		if (text.length()==0)
		{
			log.warn("Warning, adding a blank regular expression. Will match everything");
		}
		try
		{
			pattern = Pattern.compile(text,flags);
		}
		catch (PatternSyntaxException e)
		{
			throw new ConfigurationParseException(text+" is not a valid regular expression",e);
		}
	}

	public boolean checkForMatch(ScriptingEnvironment env)
	{
		Matcher matcher = pattern.matcher(env.evaluateAsString(target,type));
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
