package com.blueprintit.webpercolator;

import java.io.File;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author Dave
 */
public class GetDownload implements DownloadListener, Download
{
	private List listeners;
	private URL url;
	private URL referer;
	private int type;
	private File local;
	
	public GetDownload(URL url, File local)
	{
		this(url,local,Download.UNSPECIFIED_DOWNLOAD,null);
	}
	
	public GetDownload(URL url, File local, int type, URL referer)
	{
		listeners = new LinkedList();
		this.url=url;
		this.referer=referer;
		this.local=local;
		this.type=type;
	}
	
	public void addDownloadListener(DownloadListener l)
	{
		synchronized(listeners)
		{
			listeners.add(l);
		}
	}
	
	public void removeDownloadListener(DownloadListener l)
	{
		synchronized(listeners)
		{
			listeners.remove(l);
		}
	}
	
	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadStarted(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadStarted(DownloadEvent e)
	{
		synchronized(listeners)
		{
			Iterator loop = listeners.iterator();
			while (loop.hasNext())
			{
				((DownloadListener)loop.next()).downloadStarted(e);
			}
		}
	}

	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadUpdate(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadUpdate(DownloadEvent e)
	{
		synchronized(listeners)
		{
			Iterator loop = listeners.iterator();
			while (loop.hasNext())
			{
				((DownloadListener)loop.next()).downloadUpdate(e);
			}
		}
	}

	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadComplete(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadComplete(DownloadEvent e)
	{
		synchronized(listeners)
		{
			Iterator loop = listeners.iterator();
			while (loop.hasNext())
			{
				((DownloadListener)loop.next()).downloadComplete(e);
			}
		}
	}

	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadFailed(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadFailed(DownloadEvent e)
	{
		synchronized(listeners)
		{
			Iterator loop = listeners.iterator();
			while (loop.hasNext())
			{
				((DownloadListener)loop.next()).downloadFailed(e);
			}
		}
	}

	/**
	 * @see com.blueprintit.webpercolator.Download#getHttpMethod()
	 */
	public HttpMethod getHttpMethod()
	{
		GetMethod method = new GetMethod(url.toString());
		if (referer!=null)
		{
			method.setRequestHeader("Referer",referer.toString());
		}
		return method;
	}

	/**
	 * @see com.blueprintit.webpercolator.Download#getLocalFile()
	 */
	public File getLocalFile()
	{
		return local;
	}

	/**
	 * @see com.blueprintit.webpercolator.Download#getType()
	 */
	public int getType()
	{
		return type;
	}

}
