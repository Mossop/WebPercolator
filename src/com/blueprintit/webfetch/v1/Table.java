package com.blueprintit.webfetch.v1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.Environment;

/**
 * @author Dave
 */
public class Table
{
	private List rows;
	private String name = "";
	
	public Table()
	{
		rows = new ArrayList();
	}
	
	public Table(Element element) throws ConfigurationParseException
	{
		this();
		if (element.hasAttribute("name"))
		{
			name=element.getAttribute("name");
		}
		NodeList childs = element.getChildNodes();
		for (int loop=0; loop<childs.getLength(); loop++)
		{
			if (childs.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element el = (Element)childs.item(loop);
				if (el.getNodeName().equals("Row"))
				{
					rows.add(new TableRow(el));
				}
				else
				{
					throw new ConfigurationParseException("Invalid element in configuration: "+el.getNodeName());
				}
			}
		}
	}
	
	public void execute(ConfigurationSet config, Environment env)
	{
		Iterator loop = rows.iterator();
		while (loop.hasNext())
		{
			TableRow row = (TableRow)loop.next();
			if (!row.matches(env))
			{
				break;
			}
			row.execute(config,env);
			if ((env.getAccepted())||(env.getRejected()))
			{
				break;
			}
		}
	}
	
	public String getName()
	{
		return name;
	}
}
