/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.io.File;
import java.net.URL;

/**
 * @author Dave
 */
public class ParseDetails
{
	private File file;
	private URL url;
	
	public ParseDetails(URL url, File file)
	{
		this.file=file;
		this.url=url;
	}
	
	public File getFile()
	{
		return file;
	}
	
	public void setFile(File file)
	{
		this.file = file;
	}
	
	public URL getUrl()
	{
		return url;
	}
	
	public void setUrl(URL url)
	{
		this.url = url;
	}
}
