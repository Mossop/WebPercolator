/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Dave
 */
public class URLBuilder
{
	private String url;
	private String scheme = "http";
	private String host = "localhost";
	private String path = "/";
	private String query = null;
	private int port = -1;
	
	private static final String XALPHA_REGEX = "[\\p{Alnum}$_@&!\"',\\-\\(\\)\\+\\*]|%\\p{XDigit}{2}";
	
	private static final String HOST_PART = "\\p{Alpha}(?:"+XALPHA_REGEX+")*?";

	private static final String NAMEDHOST_REGEX = HOST_PART+"(?:\\."+HOST_PART+")*";
	private static final String NUMBEREDHOST_REGEX = "\\d{1,3}(?:\\.\\d{1,3}){3}";
	
	private static final String HOST_REGEX = NUMBEREDHOST_REGEX+"|"+NAMEDHOST_REGEX;
	private static final String SCHEME_REGEX = "http|https"; // Not using ftp: +"ftp";
	private static final String PATH_REGEX = "(?:/(?:"+XALPHA_REGEX+"|\\.)*)*";
	private static final String QUERY_REGEX = "(?:"+XALPHA_REGEX+")*";
	private static final String FRAGMENT_REGEX = "(?:"+XALPHA_REGEX+")*";
	
	private static final String URL_REGEX = "("+SCHEME_REGEX+")://("+HOST_REGEX+")("+PATH_REGEX+")(?:\\?("+QUERY_REGEX+"))?(?:#("+FRAGMENT_REGEX+"))?";
	
	public URLBuilder(String url) throws MalformedURLException
	{
		setUrl(url);
	}
	
	public URLBuilder(URL url)
	{
		setUrl(url);
	}
	
	public String getUrl()
	{
		return toString();
	}
	
	public void setUrl(String url) throws MalformedURLException
	{
		Pattern urlsplit = Pattern.compile("^"+URL_REGEX+"$");
		Matcher matcher = urlsplit.matcher(url);
		if (matcher.matches())
		{
			scheme=matcher.group(1);
			host=matcher.group(2);
			path=matcher.group(3);
			if (path.length()==0)
			{
				path="/";
			}
			query=matcher.group(4);
		}
		else
		{
			throw new MalformedURLException(url+" is not a valid URL");
		}
	}
	
	public void setUrl(URL url)
	{
		scheme=url.getProtocol();
		port=url.getPort();
		path=url.getPath();
		query=url.getQuery();
		host=url.getHost();
	}
	
	public URL toURL()
	{
		try
		{
			return new URL(toString());
		}
		catch (MalformedURLException e)
		{
			return null;
		}
	}
	
	public String toString()
	{
		String porttext = "";
		if (port>0)
		{
			porttext=":"+port;
		}
		String querytext = "";
		if ((query!=null)&&(query.length()>0))
		{
			querytext="?"+querytext;
		}
		return scheme+"://"+host+porttext+path+querytext;
	}
	
	public String getHost()
	{
		return host;
	}
	
	public void setHost(String host) throws  MalformedURLException
	{
		if (host.matches("^"+HOST_REGEX+"$"))
		{
			this.host=host;
		}
		else
		{
			throw new MalformedURLException("Invalid host name: "+host);
		}
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path) throws  MalformedURLException
	{
		if (path.matches("^"+PATH_REGEX+"$"))
		{
			this.path=path;
		}
		else
		{
			throw new MalformedURLException("Invalid path: "+path);
		}
	}
	
	public int getPort()
	{
		return port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public String getQuery()
	{
		return query;
	}
	
	public void setQuery(String query) throws  MalformedURLException
	{
		if (query.matches("^"+QUERY_REGEX+"$"))
		{
			this.query=query;
		}
		else
		{
			throw new MalformedURLException("Invalid query: "+query);
		}
	}
	
	public String getScheme()
	{
		return scheme;
	}
	
	public void setScheme(String scheme) throws  MalformedURLException
	{
		if (scheme.matches("^"+SCHEME_REGEX+"$"))
		{
			this.scheme=scheme;
		}
		else
		{
			throw new MalformedURLException("Invalid scheme: "+scheme);
		}
	}
}
