/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

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
	private File target;
	
	/**
	 * @param r
	 */
	public Downloader(DownloadQueue q, Download r)
	{
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
	
	public File getFile()
	{
		return target;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		queue.processDownloadEvent(new DownloadEvent(queue,download,null,DownloadEvent.DOWNLOAD_STARTED));
		HttpMethod method = download.getHttpMethod();
		try
		{
			queue.executeMethod(method);
			if ((method.getStatusCode()>=200)&&(method.getStatusCode()<300))
			{
				try
				{
					target = download.getLocalFile();
					if (target==null)
					{
						target = File.createTempFile("dqtemp",null);
						target.deleteOnExit();
					}
					OutputStream out = new FileOutputStream(target);
					try
					{
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
						queue.processDownloadEvent(new DownloadEvent(queue,download,target,DownloadEvent.DOWNLOAD_COMPLETE));
					}
					catch (IOException e)
					{
						e.printStackTrace();
						method.releaseConnection();
						out.close();
						queue.processDownloadEvent(new DownloadEvent(queue,download,target,e));
					}
				}
				catch (IOException e) // Thrown when the file could not be opened for writing.
				{
					e.printStackTrace();
					method.releaseConnection();
					queue.processDownloadEvent(new DownloadEvent(queue,download,null,e));
				}
			}
			else if ((method.getStatusCode()>=300)&&(method.getStatusCode()<400))
			{
				URL redirect = new URL(method.getResponseHeader("Location").getValue());
				//System.out.println("Redirect to: "+redirect.toString());
				method.releaseConnection();
				queue.processDownloadEvent(new DownloadEvent(queue,download,redirect));
			}
			else
			{
				method.releaseConnection();
				queue.processDownloadEvent(new DownloadEvent(queue,download,null,DownloadEvent.DOWNLOAD_FAILED));
			}
		}
		catch (HttpException e)
		{
			e.printStackTrace();
			method.releaseConnection();
			queue.processDownloadEvent(new DownloadEvent(queue,download,null,e));
		}
		catch (IOException e)
		{
			e.printStackTrace();
			method.releaseConnection();
			queue.processDownloadEvent(new DownloadEvent(queue,download,null,e));
		}
		running=false;
	}
}
