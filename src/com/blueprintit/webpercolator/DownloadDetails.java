/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethodBase;

/**
 * @author Dave
 */
public class DownloadDetails
{
	private URL url = null;
	private String type = null;
	private int length;
	private int statuscode;
	private String statustext;
	
	public DownloadDetails(HttpMethodBase method)
	{
		statuscode=method.getStatusCode();
		statustext=method.getStatusText();
		
		Header header = method.getResponseHeader("Content-Length");
		if (header!=null)
		{
			length=Integer.parseInt(header.getValue());
		}
		
		header = method.getResponseHeader("Content-Type");
		if (header!=null)
		{
			type=header.getValue();
		}
		
		String u = method.getHostConfiguration().getHostURL()+method.getPath();
		if (method.getQueryString()!=null)
		{
			u+="?"+method.getQueryString();
		}
		try
		{
			url = new URL(u);
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
	}
	
	public URL getURL()
	{
		return url;
	}
	
	public int getContentLength()
	{
		return length;
	}
	
	public String getContentType()
	{
		return type;
	}

	public int getStatusCode()
	{
		return statuscode;
	}

	public String getStatusText()
	{
		return statustext;
	}
	
	public String getStatusLine()
	{
		return getStatusCode()+" "+getStatusText();
	}
}
