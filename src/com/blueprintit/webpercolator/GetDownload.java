package com.blueprintit.webpercolator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;

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
	private GetMethod method;
	
	public GetDownload(String url, File local) throws MalformedURLException
	{
		this(new URL(url),local);
	}
	
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
		method = new GetMethod(url.toString());
		if (referer!=null)
		{
			method.setRequestHeader("Referer",referer.toString());
		}
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
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadRedirected(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadRedirected(DownloadEvent e)
	{
		synchronized(listeners)
		{
			Iterator loop = listeners.iterator();
			while (loop.hasNext())
			{
				((DownloadListener)loop.next()).downloadRedirected(e);
			}
		}
	}

	public URL getURL()
	{
		return url;
	}
	
	/**
	 * @see com.blueprintit.webpercolator.Download#getHttpMethod()
	 */
	public HttpMethod getHttpMethod()
	{
		return method;
	}

	public HeadMethod getHeadMethod()
	{
		HeadMethod method = new HeadMethod(url.toString());
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

	/**
	 * @return Returns the url.
	 */
	public URL getUrl()
	{
		return url;
	}
	
	public int hashCode()
	{
		int port=url.getPort();
		if (port==-1)
		{
			port=url.getDefaultPort();
		}
		return url.getHost().hashCode()+url.getFile().hashCode()+port;
	}
	
	public boolean equals(Object obj)
	{
		URL other;
		if (obj instanceof Download)
		{
			other = ((Download)obj).getURL();
		}
		else if (obj instanceof URL)
		{
			other=(URL)obj;
		}
		else
		{
			return false;
		}
		if (!url.getProtocol().equals(other.getProtocol()))
			return false;
		if (!url.getHost().equals(other.getHost()))
			return false;
		if (!url.getFile().equals(other.getFile()))
			return false;
		int ptest1=url.getPort();
		if (ptest1==-1)
		{
			ptest1=url.getDefaultPort();
		}
		int ptest2=other.getPort();
		if (ptest2==-1)
		{
			ptest2=other.getDefaultPort();
		}
		if (ptest1!=ptest2)
		{
			return false;
		}
		return true;
	}
}
