package com.blueprintit.webpercolator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;

/**
 * @author Dave
 */
public class Downloader implements Runnable
{
	private Download download;
	private DownloadQueue queue;
	private boolean running;
	private HttpClient agent;
	
	/**
	 * @param r
	 */
	public Downloader(HttpClient hclient, DownloadQueue q, Download r)
	{
		agent=hclient;
		queue=q;
		download=r;
		running=false;
	}

	public synchronized void start()
	{
		if (!running)
		{
			running=true;
			(new Thread(this)).start();		
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		try
		{
			OutputStream out = new FileOutputStream(download.getLocalFile());
			HttpMethod method = download.getHttpMethod();
			try
			{
				agent.executeMethod(method);
				InputStream in = method.getResponseBodyAsStream();
				byte[] buffer = new byte[1024];
				int count = in.read(buffer);
				while (count>=0)
				{
					if (count>0)
						out.write(buffer,0,count);
					count = in.read(buffer);
				}
				out.close();
				in.close();
				method.releaseConnection();
			}
			catch (HttpException e)
			{
				method.releaseConnection();
				out.close();
			}
			catch (IOException e)
			{
				method.releaseConnection();
				out.close();
			}
		}
		catch (IOException e) // Thrown when the file could not be opened for writing.
		{
		}
	}

}
