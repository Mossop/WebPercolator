package com.blueprintit.webfetch.v1;

import org.w3c.dom.Element;
import com.blueprintit.webfetch.*;
import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public abstract class AbstractCondition extends ElementConfigParser implements Condition
{
	private boolean invert = false;

	public boolean parseSubElement(Element element)
	{
		return false;
	}
	
	public abstract void parseCondition(Element element) throws ConfigurationParseException;
	
	public final void parseConfig(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("invert"))
		{
			invert=Boolean.valueOf(element.getAttribute("invert")).booleanValue();
		}
	}

	public abstract boolean checkForMatch(Environment env);
	
	public final boolean matches(Environment env)
	{
		return invert ^ checkForMatch(env);
	}
}
