/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

import java.io.File;
import java.io.IOException;
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
	private File local;
	private GetMethod method;
	private String useragent;
	private static final String USERAGENT = "Mozilla/4.0 (compatible; Java; HttpClient; en-US) WebPercolator/1.0";
	
	public GetDownload(String url, File local) throws MalformedURLException
	{
		this(new URL(url),local);
	}
	
	public GetDownload(URL url, File local)
	{
		this(url,local,null);
	}
	
	public GetDownload(URL url, File local, URL referer)
	{
		this(url,local,referer,USERAGENT);
	}
	
	public GetDownload(URL url, File local, URL referer, String useragent)
	{
		listeners = new LinkedList();
		this.url=url;
		this.referer=referer;
		this.local=local;
		this.useragent=useragent;
		method = new GetMethod(url.toString());
		if (referer!=null)
		{
			method.setRequestHeader("Referer",referer.toString());
		}
		method.setRequestHeader("User-Agent",useragent);
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

	public DownloadDetails getDownloadDetails(DownloadQueue queue) throws IOException
	{
		if (method.hasBeenUsed())
		{
			return new DownloadDetails(method);
		}
		else
		{
			HeadMethod method = new HeadMethod(url.toString());
			if (referer!=null)
			{
				method.setRequestHeader("Referer",referer.toString());
			}
			method.setRequestHeader("User-Agent",useragent);
			queue.executeMethod(method);
			return new DownloadDetails(method);
		}
	}

	/**
	 * @see com.blueprintit.webpercolator.Download#getLocalFile()
	 */
	public File getLocalFile()
	{
		return local;
	}
	
	public void setLocalFile(File file)
	{
		local=file;
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
	
	/**
	 * @return Returns the referer.
	 */
	public URL getReferer()
	{
		return referer;
	}
	
	/**
	 * @param referer The referer to set.
	 */
	public void setReferer(URL referer)
	{
		this.referer = referer;
		if (referer!=null)
		{
			method.setRequestHeader("Referer",referer.toString());
		}
	}
	
	public void setUserAgent(String useragent)
	{
		this.useragent=useragent;
		method.setRequestHeader("User-Agent",useragent);
	}
	
	/**
	 * @param url The url to set.
	 */
	public void setUrl(URL url)
	{
		this.url = url;
		method = new GetMethod(url.toString());
		if (referer!=null)
		{
			method.setRequestHeader("Referer",referer.toString());
		}
		method.setRequestHeader("User-Agent",useragent);
	}
}
