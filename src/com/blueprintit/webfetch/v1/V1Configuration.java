/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.Configuration;
import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.Environment;

/**
 * @author Dave
 */
public class V1Configuration extends ConfigurationSet implements Configuration
{
	private List urls;
	
	public V1Configuration(File base, Element element) throws ConfigurationParseException
	{
		super();
		setCascadingSetting("basedir",base);
		urls = new ArrayList();
		parseConfig(element);
	}
	
	public Collection getURLs()
	{
		return urls;
	}

	public boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		if (element.getNodeName().equals("Url"))
		{
			try
			{
				urls.add(new URL(getElementText(element)));
				return true;
			}
			catch (MalformedURLException e)
			{
				throw new ConfigurationParseException("Illegal url",e);
			}
		}
		return super.parseSubElement(element);
	}
	
	public void applyConfiguration(Environment env)
	{
		ScriptingEnvironment scope = new ScriptingEnvironment((File)getSetting("basedir"),env);
		try
		{
			if (matches(scope))
			{
				applyConfigurationSet(scope);
			}
			else
			{
				env.setRejected(true);
			}
		}
		catch (RuntimeException r)
		{
			scope.exit();
			throw r;
		}
		scope.exit();
	}
}
