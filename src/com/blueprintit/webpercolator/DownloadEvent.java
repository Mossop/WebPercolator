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
	
	public static final int DOWNLOAD_UPDATE = 0;
	public static final int DOWNLOAD_COMPLETE = 1;
	public static final int DOWNLOAD_FAILED = 2;
	
	public DownloadEvent(DownloadQueue q, Downloader d, Download r, int type)
	{
		queue=q;
		downloader=d;
		download=r;
		this.type=type;
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
