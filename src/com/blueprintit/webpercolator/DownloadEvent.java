/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

import java.io.File;
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
	private File localFile;
	
	public static final int DOWNLOAD_STARTED = 0;
	public static final int DOWNLOAD_UPDATE = 1;
	public static final int DOWNLOAD_COMPLETE = 2;
	public static final int DOWNLOAD_REDIRECTED = 3;
	public static final int DOWNLOAD_FAILED = -1;
	
	public DownloadEvent(DownloadQueue q, Download r, File target, int type)
	{
		queue=q;
		download=r;
		this.type=type;
		this.localFile=target;
	}
	
	private DownloadEvent(DownloadQueue q, Download r, File target, Exception e)
	{
		this(q,r,target,DOWNLOAD_FAILED);
		exception=e;
	}
	
	private DownloadEvent(DownloadQueue q, Download r, URL u)
	{
		this(q,r,null,DOWNLOAD_REDIRECTED);
		redirect=u;
	}
	
	public static DownloadEvent createDownloadEvent(DownloadQueue q, Download r, File target, int type)
	{
		return new DownloadEvent(q,r,target,type);
	}
	
	public static DownloadEvent createRedirectEvent(DownloadQueue q, Download r, URL u)
	{
		return new DownloadEvent(q,r,u);
	}
	
	public static DownloadEvent createFailedDownloadEvent(DownloadQueue q, Download r, File target, Exception e)
	{
		return new DownloadEvent(q,r,target,e);
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
	
	/**
	 * @return Returns the localFile.
	 */
	public File getLocalFile()
	{
		return localFile;
	}
	
	public void setLocalFile(File newfile)
	{
		localFile=newfile;
	}
}
