/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

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
	
	private void parseSubElements(Element element) throws ConfigurationParseException
	{
		NodeList nodes = element.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			Node node = nodes.item(loop);
			if (node.getNodeType()==Node.ELEMENT_NODE)
			{
				if (node.getNodeName().equals("Include"))
				{
					Element el = (Element)node;
					if (el.hasAttribute("file"))
					{
						try
						{
							InputStream reader = new FileInputStream(el.getAttribute("file"));
							DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							dbf.setValidating(false);
							try
							{
								DocumentBuilder builder = dbf.newDocumentBuilder();
								Document document = builder.parse(reader);
								Element root = document.getDocumentElement();
								if (root.getNodeName().equals("Includes"))
								{
									parseSubElements(root);
								}
								else
								{
									throw new ConfigurationParseException("Included file did not appear to be a valid format");
								}
							}
							catch (ParserConfigurationException e)
							{
								assert false;
							}
							catch (SAXException e)
							{
								throw new ConfigurationParseException("Error parsing included file",e);
							}
							catch (IOException e)
							{
								throw new ConfigurationParseException("Error accessing included file",e);
							}
						}
						catch (FileNotFoundException e)
						{
							throw new ConfigurationParseException("Could not find included file: "+el.getAttribute("file"));
						}
					}
				}
				else if (!parseSubElement((Element)node))
				{
					throw new ConfigurationParseException("Unknown element in configuration: "+node.getNodeName());
				}
			}
		}
	}
	
	public void parseConfig(Element element) throws ConfigurationParseException
	{
		parseSubElements(element);
	}
}
