package com.blueprintit.webfetch.v1;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blueprintit.webfetch.*;
import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public abstract class AbstractCondition implements Condition
{
	private boolean invert = false;

	protected String getElementText(Element element) throws ConfigurationParseException
	{
		element.normalize();
		NodeList childs = element.getChildNodes();
		StringBuffer text = new StringBuffer();
		for (int loop=0; loop<childs.getLength(); loop++)
		{
			if (childs.item(loop).getNodeType()==Node.TEXT_NODE)
			{
				text.append(childs.item(loop).getNodeValue());
			}
		}
		return text.toString();
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
