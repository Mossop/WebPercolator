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
	private String user = null;
	private String pass = null;
	private String scheme = "http";
	private String host = "localhost";
	private String path = "/";
	private URLQuery query = null;
	private int port = -1;
	private String fragment;
	
	private static Pattern SCHEME_REGEX;
	private static Pattern HOST_REGEX;
	private static Pattern PATH_REGEX;
	private static Pattern URL_REGEX;
	private static Pattern QUERY_REGEX;
	private static Pattern FRAGMENT_REGEX;
	
	static
	{		
		String scheme = "(http|https)";
		SCHEME_REGEX = Pattern.compile(scheme);
		
		String userinfo = "([^@]*)";

		String port = "(\\d{1,5})";
		
		String host = "([^/?#]*)";
		HOST_REGEX = Pattern.compile(host);
		
		String path = "(/(?:[^\\?#])*)*";
		PATH_REGEX = Pattern.compile(path);
		
		String query="([^#]*)";
		QUERY_REGEX = Pattern.compile(query);
		
		String fragment = "(.*)";
		FRAGMENT_REGEX = Pattern.compile(fragment);
		
		String url = scheme+"://(?:"+userinfo+"@)?"+host+"(?::"+port+")?"+path+"(?:\\?"+query+")?(?:#"+fragment+")?";
		URL_REGEX = Pattern.compile(url);
	}
	
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
		Matcher matcher = URL_REGEX.matcher(url);
		if (matcher.matches())
		{
			scheme=matcher.group(1);
			user=matcher.group(2);
			host=matcher.group(3);
			if (matcher.group(4)!=null)
			{
				port = Integer.parseInt(matcher.group(4));
			}
			path=matcher.group(5);
			if ((path==null)||(path.length()==0))
			{
				path="/";
			}
			query=new URLQuery(matcher.group(6));
			fragment=matcher.group(7);
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
		if (!path.startsWith("/"))
		{
			path="/"+path;
		}
		query=new URLQuery(url.getQuery());
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
			querytext="?"+query;
		}
		return scheme+"://"+host+porttext+path+querytext;
	}
	
	public String getHost()
	{
		return host;
	}
	
	public void setHost(String host) throws  MalformedURLException
	{
		if (HOST_REGEX.matcher(host).matches())
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
		if (PATH_REGEX.matcher(path).matches())
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
	
	public URLQuery getQuery()
	{
		return query;
	}
	
	public void setQuery(String query) throws  MalformedURLException
	{
		if (QUERY_REGEX.matcher(query).matches())
		{
			this.query=new URLQuery(query);
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
		if (SCHEME_REGEX.matcher(scheme).matches())
		{
			this.scheme=scheme;
		}
		else
		{
			throw new MalformedURLException("Invalid scheme: "+scheme);
		}
	}
}
