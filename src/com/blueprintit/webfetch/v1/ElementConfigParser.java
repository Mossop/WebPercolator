package com.blueprintit.webfetch.v1;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public abstract class ElementConfigParser
{
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

	protected abstract boolean parseSubElement(Element element) throws ConfigurationParseException;
	
	protected void parseConfig(Element element) throws ConfigurationParseException
	{
		NodeList nodes = element.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			Node node = nodes.item(loop);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				if (!parseSubElement((Element)node))
				{
					throw new ConfigurationParseException("Unknown element in configuration: "+element.getNodeName());
				}
			}
		}
	}
}
