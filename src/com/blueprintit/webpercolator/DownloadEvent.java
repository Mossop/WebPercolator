package com.blueprintit.webpercolator;

import java.net.URL;

/**
 * @author Dave
 */
public class DownloadEvent
{
	private DownloadQueue queue;
	private Download download;
	private int type;
	private Exception exception;
	private URL redirect;
	
	public static final int DOWNLOAD_STARTED = 0;
	public static final int DOWNLOAD_UPDATE = 1;
	public static final int DOWNLOAD_COMPLETE = 2;
	public static final int DOWNLOAD_REDIRECTED = 3;
	public static final int DOWNLOAD_FAILED = -1;
	
	public DownloadEvent(DownloadQueue q, Download r, int type)
	{
		queue=q;
		download=r;
		this.type=type;
	}
	
	public DownloadEvent(DownloadQueue q, Download r, Exception e)
	{
		this(q,r,DOWNLOAD_FAILED);
		exception=e;
	}
	
	public DownloadEvent(DownloadQueue q, Download r, URL u)
	{
		this(q,r,DOWNLOAD_REDIRECTED);
		redirect=u;
	}
	
	/**
	 * @return Returns the url that the request was redirected to.
	 */
	public URL getRedirectURL()
	{
		return redirect;
	}
	
	/**
	 * @return Returns the exception generated.
	 */
	public Exception getException()
	{
		return exception;
	}
	
	/**
	 * @return Returns the type.
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * @return Returns the download.
	 */
	public Download getDownload()
	{
		return download;
	}
	
	/**
	 * @return Returns the queue.
	 */
	public DownloadQueue getQueue()
	{
		return queue;
	}
}
