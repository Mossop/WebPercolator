package com.blueprintit.webfetch;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Dave
 */
public class URLBuilder
{
	private String scheme;
	private String host;
	private String path;
	private String query;
	private int port = -1;
	
	public URLBuilder(String url) throws MalformedURLException
	{
		this(new URL(url));
	}
	
	public URLBuilder(URL url)
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
		if (port!=80)
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
		this.host = host;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public void setPath(String path) throws  MalformedURLException
	{
		this.path = path;
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
		this.query = query;
	}
	
	public String getScheme()
	{
		return scheme;
	}
	
	public void setScheme(String scheme) throws  MalformedURLException
	{
		this.scheme = scheme;
	}
}
