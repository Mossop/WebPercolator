package com.blueprintit.webfetch;

import java.net.URL;
import java.util.Collection;

import com.blueprintit.webpercolator.Download;

/**
 * @author Dave
 */
public interface Configuration
{
	public Collection getURLs();
	
	public Download getDownload(URL target);

	public Download getDownload(URL target, URL referer);
}
