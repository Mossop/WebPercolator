/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;

import com.blueprintit.webpercolator.Download;
import com.blueprintit.webpercolator.DownloadDetails;
import com.blueprintit.webpercolator.DownloadEvent;
import com.blueprintit.webpercolator.DownloadListener;
import com.blueprintit.webpercolator.DownloadQueue;

/**
 * @author Dave
 */
public class EnvironmentDownload implements Download, DownloadListener
{
	private Environment environment;
	private GetMethod method;
	private File localfile;

	public EnvironmentDownload(Environment env) throws IOException
	{
		environment=env;
		localfile = File.createTempFile("dqtemp",null);
		if (env.getFile()==null)
		{
			localfile.deleteOnExit();
		}
		method = new GetMethod(environment.getTarget().toString());
		if (environment.getReferer()!=null)
		{
			method.setRequestHeader("Referer",environment.getReferer().toString());
		}
		method.setRequestHeader("User-Agent",environment.getUserAgent());
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
			method.setRequestHeader("User-Agent",environment.getUserAgent());
			queue.executeMethod(method);
			return new DownloadDetails(method);
		}
	}

	public Environment getEnvironment()
	{
		return environment;
	}
	
	public URL getURL()
	{
		return environment.getTarget();
	}

	public File getLocalFile()
	{
		return localfile;
	}

	public void downloadStarted(DownloadEvent e)
	{
	}

	public void downloadUpdate(DownloadEvent e)
	{
	}

	public void downloadComplete(DownloadEvent e)
	{
		if (environment.getFile()!=null)
		{
			File target = environment.getFile();
			if (!localfile.renameTo(target))
			{
				try
				{
					FileInputStream input = new FileInputStream(localfile);
					FileOutputStream output = new FileOutputStream(target);
					byte[] buffer = new byte[1024];
					int read = input.read(buffer);
					while (read>=0)
					{
						output.write(buffer,0,read);
						read=input.read(buffer);
					}
					output.close();
					input.close();
					localfile.delete();
					localfile=target;
					e.setLocalFile(localfile);
				}
				catch (IOException exc)
				{
					exc.printStackTrace();
				}
			}
			else
			{
				localfile=target;
				e.setLocalFile(localfile);
			}
		}
	}

	public void downloadFailed(DownloadEvent e)
	{
		if ((environment.getFile()!=null)&&(e.getLocalFile()!=null)&&(e.getLocalFile().exists()))
		{
			e.getLocalFile().delete();
		}
	}

	public void downloadRedirected(DownloadEvent e)
	{
	}
}
