package com.blueprintit.webpercolator;

/**
 * @author Dave
 */
public class DownloadEvent
{
	private DownloadQueue queue;
	private Downloader downloader;
	private Download download;
	private int type;
	private Exception exception;
	
	public static final int DOWNLOAD_STARTED = 0;
	public static final int DOWNLOAD_UPDATE = 1;
	public static final int DOWNLOAD_COMPLETE = 2;
	public static final int DOWNLOAD_FAILED = -1;
	
	public DownloadEvent(DownloadQueue q, Downloader d, Download r, int type)
	{
		queue=q;
		downloader=d;
		download=r;
		this.type=type;
	}
	
	public DownloadEvent(DownloadQueue q, Downloader d, Download r, Exception e)
	{
		this(q,d,r,DOWNLOAD_FAILED);
		exception=e;
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
	 * @return Returns the downloader.
	 */
	public Downloader getDownloader()
	{
		return downloader;
	}
	
	/**
	 * @return Returns the queue.
	 */
	public DownloadQueue getQueue()
	{
		return queue;
	}
}
