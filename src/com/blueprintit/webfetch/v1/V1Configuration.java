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

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.blueprintit.webfetch.Configuration;
import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.Environment;
import com.blueprintit.webpercolator.URLBuilder;

/**
 * @author Dave
 */
public class V1Configuration extends ConfigurationSet implements Configuration
{
	private List environments;
	private List authdetails;
	private List cookies;
	
	public V1Configuration(File base, Element element) throws ConfigurationParseException
	{
		super();
		setCascadingSetting("basedir",base);
		environments = new ArrayList();
		cookies = new ArrayList();
		authdetails = new ArrayList();
		parseConfig(element);
	}
	
	public Collection getEnvironments()
	{
		return environments;
	}

	public boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		if (element.getNodeName().equals("Url"))
		{
			try
			{
				if (element.hasAttribute("referer"))
				{
					environments.add(new Environment(new URL(getElementText(element)),new URL(element.getAttribute("referer"))));
					return true;
				}
				else
				{
					environments.add(new Environment(new URL(getElementText(element))));
					return true;
				}
			}
			catch (MalformedURLException e)
			{
				throw new ConfigurationParseException("Illegal url",e);
			}
		}
		if (element.getNodeName().equals("UrlSet"))
		{
			if ((element.hasAttribute("start"))&&(element.hasAttribute("end")))
			{
				try
				{
					URL referer = null;
					if (element.hasAttribute("referer"))
					{
						referer=new URL(element.getAttribute("referer"));
					}
					int width = 1;
					if (element.hasAttribute("padding"))
					{
						width=Integer.parseInt(element.getAttribute("padding"));
					}
					int start = Integer.parseInt(element.getAttribute("start"));
					int end = Integer.parseInt(element.getAttribute("end"));
					String base = getElementText(element);
					for (int loop=start; loop<=end; loop++)
					{
						String number = ""+loop;
						while (number.length()<width)
						{
							number="0"+number;
						}
						environments.add(new Environment(new URL(base.replaceAll("#",number)),referer));
					}
					return true;
				}
				catch (MalformedURLException e)
				{
					throw new ConfigurationParseException("Invalid url generated from UrlSet",e);
				}
				catch (NumberFormatException e)
				{
					throw new ConfigurationParseException("Invalid number",e);
				}
			}
			else
			{
				throw new ConfigurationParseException("A start and end must be specified for the UrlSet");
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
		if (element.getNodeName().equals("Cookie"))
		{
			Cookie cookie = new Cookie();
			if (element.hasAttribute("url"))
			{
				URLBuilder url;
				try
				{
					url = new URLBuilder(element.getAttribute("url"));
				}
				catch (MalformedURLException e)
				{
					throw new ConfigurationParseException("Invalid url specified for cookie",e);
				}
				cookie.setDomain(url.getHost());
				cookie.setPath(url.getPath());
				NodeList list = element.getElementsByTagName("Name");
				if (list.getLength()==1)
				{
					cookie.setName(getElementText((Element)list.item(0)));
				}
				else
				{
					throw new ConfigurationParseException("Cookie must contain a Name");
				}
				list = element.getElementsByTagName("Value");
				if (list.getLength()==1)
				{
					cookie.setValue(getElementText((Element)list.item(0)));
				}
				else
				{
					throw new ConfigurationParseException("Cookie must contain a Value");
				}
				cookies.add(cookie);
				return true;
			}
			else
			{
				throw new ConfigurationParseException("Cookie must have a url attribute");
			}
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
		
		loop = cookies.iterator();
		while (loop.hasNext())
		{
			Cookie cookie = (Cookie)loop.next();
			state.addCookie(cookie);
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
