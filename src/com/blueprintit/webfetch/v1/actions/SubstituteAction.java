/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.actions;

import java.util.regex.Pattern;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.AbstractAction;
import com.blueprintit.webfetch.v1.ConfigurationSet;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class SubstituteAction extends AbstractAction
{
	private Pattern regex;
	private String substitution;
	private String source;
	private String target;
	
	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		String text = env.evaluateAsString(source,type);
		text=regex.matcher(text).replaceAll(substitution);
		env.execute(target+"=\""+text+"\"",type);
	}
	
	public void parseElement(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("regex"))
		{
			regex = Pattern.compile(element.getAttribute("regex"));
		}
		else
		{
			throw new ConfigurationParseException("A regular expression must be specified");
		}
		if (element.hasAttribute("replacement"))
		{
			substitution = element.getAttribute("replacement");
		}
		else
		{
			throw new ConfigurationParseException("A replacement must be specified");
		}
		if (element.hasAttribute("source"))
		{
			source = element.getAttribute("source");
		}
		else
		{
			throw new ConfigurationParseException("A source must be specified");
		}
		if (element.hasAttribute("target"))
		{
			target = element.getAttribute("target");
		}
		else
		{
			throw new ConfigurationParseException("A target must be specified");
		}
		super.parseElement(element);
	}
}
