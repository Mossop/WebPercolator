package com.blueprintit.webfetch.v1;

import java.net.URL;
import java.util.Collection;

import com.blueprintit.webfetch.Configuration;
import com.blueprintit.webpercolator.Download;

/**
 * @author Dave
 */
public class V1Configuration implements Configuration
{
	public Collection getURLs()
	{
		return null;
	}

	public Download getDownload(URL target)
	{
		return null;
	}

	public Download getDownload(URL target, URL referer)
	{
		return null;
	}

}
