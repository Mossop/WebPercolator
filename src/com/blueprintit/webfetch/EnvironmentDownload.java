/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;

import com.blueprintit.webpercolator.Download;
import com.blueprintit.webpercolator.DownloadDetails;
import com.blueprintit.webpercolator.DownloadQueue;

/**
 * @author Dave
 */
public class EnvironmentDownload implements Download
{
	private Environment environment;
	private GetMethod method;

	public EnvironmentDownload(Environment env)
	{
		environment=env;
		method = new GetMethod(environment.getTarget().toString());
		if (environment.getReferer()!=null)
		{
			method.setRequestHeader("Referer",environment.getReferer().toString());
		}
	}
	
	public HttpMethod getHttpMethod()
	{
		return method;
	}

	public DownloadDetails getDownloadDetails(DownloadQueue queue) throws IOException
	{
		if (method.hasBeenUsed())
		{
			return new DownloadDetails(method);
		}
		else
		{
			HeadMethod method = new HeadMethod(environment.getTarget().toString());
			if (environment.getReferer()!=null)
			{
				method.setRequestHeader("Referer",environment.getReferer().toString());
			}
			queue.executeMethod(method);
			return new DownloadDetails(method);
		}
	}

	public URL getURL()
	{
		return environment.getTarget().toURL();
	}

	public File getLocalFile()
	{
		return environment.getFile();
	}
}
