package com.blueprintit.webfetch.v1;

import java.net.URL;
import java.util.Collection;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.Configuration;
import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webpercolator.Download;

/**
 * @author Dave
 */
public class V1Configuration extends ConfigurationSet implements Configuration
{
	public V1Configuration(Element element) throws ConfigurationParseException
	{
		super(element);
	}
	
	public Collection getURLs()
	{
		return null;
	}

	public Download getDownload(URL target)
	{
		return getDownload(target,null);
	}

	public Download getDownload(URL target, URL referer)
	{
		return super.getDownload(target,referer);
	}

}
