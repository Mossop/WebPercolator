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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.blueprintit.webfetch.Configuration;
import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.Environment;

/**
 * @author Dave
 */
public class V1Configuration extends ConfigurationSet implements Configuration
{
	private List urls;
	private List authdetails;
	
	public V1Configuration(File base, Element element) throws ConfigurationParseException
	{
		super();
		setCascadingSetting("basedir",base);
		urls = new ArrayList();
		authdetails = new ArrayList();
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
		if (element.getNodeName().equals("Authenticate"))
		{
			AuthenticationDetails details = new AuthenticationDetails();
			if (element.hasAttribute("host"))
			{
				details.setHost(element.getAttribute("host"));
			}
			if (element.hasAttribute("realm"))
			{
				details.setRealm(element.getAttribute("realm"));
			}
			String user;
			String pass;
			NodeList list = element.getElementsByTagName("Username");
			if (list.getLength()==1)
			{
				user = getElementText((Element)list.item(0));
			}
			else
			{
				throw new ConfigurationParseException("Authenticate must contain a Username");
			}
			list = element.getElementsByTagName("Password");
			if (list.getLength()==1)
			{
				pass = getElementText((Element)list.item(0));
			}
			else
			{
				throw new ConfigurationParseException("Authenticate must contain a Password");
			}
			details.setCredentials(new UsernamePasswordCredentials(user,pass));
			authdetails.add(details);
			return true;
		}
		return super.parseSubElement(element);
	}
	
	public void initialiseHttpState(HttpState state)
	{
		Iterator loop = authdetails.iterator();
		while (loop.hasNext())
		{
			AuthenticationDetails details = (AuthenticationDetails)loop.next();
			state.setCredentials(details.getRealm(),details.getHost(),details.getCredentials());
		}
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
