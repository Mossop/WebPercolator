/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

import java.net.URL;

/**
 * @author Dave
 */
public class Link
{
	private LinkType type;
	private URL url;
	
	public Link(URL url)
	{
		this(url,LinkType.UNSPECIFIED);
	}
	
	public Link(URL url, LinkType type)
	{
		this.url=url;
		this.type=type;
	}
	
	/**
	 * @return Returns the type.
	 */
	public LinkType getType()
	{
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(LinkType type)
	{
		this.type = type;
	}
	/**
	 * @return Returns the url.
	 */
	public URL getUrl()
	{
		return url;
	}
	/**
	 * @param url The url to set.
	 */
	public void setUrl(URL url)
	{
		this.url = url;
	}
	
	public int hashCode()
	{
		return url.hashCode();
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof Link)
		{
			return url.equals(((Link)obj).url);
		}
		else if (obj instanceof URL)
		{
			return url.equals(obj);
		}
		return false;
	}
}
