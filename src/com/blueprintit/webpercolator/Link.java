package com.blueprintit.webpercolator;

import java.net.URL;

/**
 * @author Dave
 */
public class Link
{
	private int type;
	private URL url;
	
	public Link(URL url)
	{
		this(url,Download.UNSPECIFIED_DOWNLOAD);
	}
	
	public Link(URL url, int type)
	{
		this.url=url;
		this.type=type;
	}
	
	/**
	 * @return Returns the type.
	 */
	public int getType()
	{
		return type;
	}
	/**
	 * @param type The type to set.
	 */
	public void setType(int type)
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
}
