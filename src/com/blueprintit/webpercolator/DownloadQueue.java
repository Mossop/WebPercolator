package com.blueprintit.webpercolator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import com.blueprintit.webpercolator.swingparser.Parser;

/**
 * @author Dave
 */
public class DownloadQueue
{
	private Map inprogress;
	private List queue;
	private Map cache;
	private boolean abort;
	private boolean running;
	private int maxdownloads;
	private HttpClient agent;
	private List listeners;
	private HtmlLinkParser parser;
	
	public DownloadQueue()
	{
		inprogress = Collections.synchronizedMap(new HashMap());
		queue = Collections.synchronizedList(new LinkedList());
		cache = Collections.synchronizedMap(new HashMap());
		listeners = new LinkedList();
		running=false;
		MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
		agent = new HttpClient(manager);
		maxdownloads=10;
		parser = new Parser();
	}
	
	public HtmlLinkParser getLinkParser()
	{
		return parser;
	}
	
	public void executeMethod(HttpMethod method) throws HttpException, IOException
	{
		agent.executeMethod(method);
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
	
	void processDownloadEvent(DownloadEvent ev)
	{
		synchronized(listeners)
		{
			Iterator loop = listeners.iterator();
			while (loop.hasNext())
			{
				DownloadListener listener = (DownloadListener)loop.next();
				switch (ev.getType())
				{
					case DownloadEvent.DOWNLOAD_STARTED:
						listener.downloadStarted(ev);
						break;
					case DownloadEvent.DOWNLOAD_UPDATE:
						listener.downloadUpdate(ev);
						break;
					case DownloadEvent.DOWNLOAD_COMPLETE:
						listener.downloadComplete(ev);
						break;
					case DownloadEvent.DOWNLOAD_FAILED:
						listener.downloadFailed(ev);
						break;
					case DownloadEvent.DOWNLOAD_REDIRECTED:
						listener.downloadRedirected(ev);
						break;
				}
			}
		}
		if (ev.getDownload() instanceof DownloadListener)
		{
			DownloadListener listener = (DownloadListener)ev.getDownload();
			switch (ev.getType())
			{
				case DownloadEvent.DOWNLOAD_STARTED:
					listener.downloadStarted(ev);
					break;
				case DownloadEvent.DOWNLOAD_UPDATE:
					listener.downloadUpdate(ev);
					break;
				case DownloadEvent.DOWNLOAD_COMPLETE:
					listener.downloadComplete(ev);
					break;
				case DownloadEvent.DOWNLOAD_FAILED:
					listener.downloadFailed(ev);
					break;
				case DownloadEvent.DOWNLOAD_REDIRECTED:
					listener.downloadRedirected(ev);
					break;
			}
		}
		switch (ev.getType())
		{
			case DownloadEvent.DOWNLOAD_COMPLETE:
			case DownloadEvent.DOWNLOAD_FAILED:
			case DownloadEvent.DOWNLOAD_REDIRECTED:
				inprogress.remove(ev.getDownload());
				checkWaiting();
				break;
		}
	}
	
	public void complete()
	{
		start();
		waitFor();
	}
	
	public synchronized void start()
	{
		if (!running)
		{
			running=true;
			abort=false;
			checkWaiting();
		}
	}
	
	public synchronized HttpState getHttpState()
	{
		return agent.getState();
	}
	
	public synchronized void setHttpState(HttpState value)
	{
		if (running)
		{
			// Assuming that this would be a bad thing.
			throw new IllegalStateException("Cannot change Http state while downloading");
		}
		agent.setState(value);
	}
	
	public synchronized void setMaxDownloads(int value)
	{
		maxdownloads=value;
		checkWaiting();
	}
	
	private synchronized void checkWaiting()
	{
		while ((!abort)&&(inprogress.size()<maxdownloads)&&(queue.size()>0))
		{
			Download r = (Download)queue.remove(0);
			Downloader d = new Downloader(this,r);
			inprogress.put(r,d);
			d.start();
		}
		if (inprogress.size()==0)
		{
			running=false;
		}
	}
	
	public synchronized void add(Download r)
	{
		if (!(cache.containsValue(r)||cache.containsKey(r.getLocalFile())))
		{
			cache.put(r.getLocalFile(),r);
			queue.add(r);
			checkWaiting();
		}
	}
	
	public void waitFor()
	{
		while (running)
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
			}
		}
	}
	
	public static void main(String[] args) throws MalformedURLException
	{
		DownloadQueue queue = new DownloadQueue();
		Download down = new GetDownload("http://www.blueprintit.co.uk",new File("c:\\test.html"));
		queue.add(down);
		queue.start();
		queue.waitFor();
		System.out.println("Complete");
	}
}
