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
public class PadAction extends AbstractAction
{
	private int length;
	private String padchar = "0";
	private String source;
	private String target;
	private boolean left = true;
	private boolean numberOnly = true;
	
	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		String text = env.evaluateAsString(source,type);
		if (numberOnly)
		{
			try
			{
				Integer.parseInt(text);
			}
			catch (Exception e)
			{
				return;
			}
		}
		while (text.length()<length)
		{
			if (left)
			{
				text=padchar+text;
			}
			else
			{
				text=text+padchar;
			}
		}
		env.execute(target+"=\""+text+"\"",type);
	}
	
	public void parseElement(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("length"))
		{
			length = Integer.parseInt(element.getAttribute("length"));
		}
		else
		{
			throw new ConfigurationParseException("A length must be specified");
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
		if (element.hasAttribute("padCharacter"))
		{
			padchar = element.getAttribute("padCharacter");
		}
		if (element.hasAttribute("side"))
		{
			if (element.getAttribute("side").equals("left"))
			{
				left=true;
			}
			else if (element.getAttribute("side").equals("left"))
			{
				left=false;
			}
			else
			{
				throw new ConfigurationParseException("Side must be left or right");
			}
		}
		if (element.hasAttribute("numberOnly"))
		{
			numberOnly = Boolean.parseBoolean(element.getAttribute("numberOnly"));
		}
		super.parseElement(element);
	}
}
