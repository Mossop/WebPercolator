/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.conditions;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.AbstractCondition;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class StringCompare extends AbstractCondition
{
	private List values;
	private List types;
	private static Log log = LogFactory.getLog(StringCompare.class);
	
	public void parseCondition(Element element) throws ConfigurationParseException
	{
		values = new ArrayList();
		types = new ArrayList();
		if (types.size()<2)
		{
			log.warn("There should be at least two values in a string compare");
		}
	}
	
	public boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		if (element.getNodeName().equals("Value"))
		{
			String type = "text/plain";
			if (element.hasAttribute("type"))
			{
				type=element.getAttribute("type");
			}
			types.add(type);
			values.add(getElementText(element));
			return true;
		}
		return false;
	}

	public boolean checkForMatch(ScriptingEnvironment env)
	{
		if (types.size()<2)
		{
			return true;
		}
		String type = (String)types.get(0);
		String value = (String)values.get(0);
		String first = env.evaluateAsString(value,type);
		for (int loop=1; loop<types.size(); loop++)
		{
			type = (String)types.get(loop);
			value = (String)values.get(loop);
			String result = env.evaluateAsString(value,type);
			if (!result.equals(first))
				return false;
		}
		return true;
	}
}
