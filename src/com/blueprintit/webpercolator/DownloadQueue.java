package com.blueprintit.webpercolator;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

/**
 * @author Dave
 */
public class DownloadQueue implements Runnable
{
	private Map inprogress;
	private List queue;
	private Map cache;
	private boolean abort;
	private boolean running;
	private int maxdownloads;
	private HttpClient agent;
	
	public DownloadQueue()
	{
		inprogress = Collections.synchronizedMap(new HashMap());
		queue = Collections.synchronizedList(new LinkedList());
		cache = Collections.synchronizedMap(new HashMap());
		running=false;
		MultiThreadedHttpConnectionManager manager = new MultiThreadedHttpConnectionManager();
		agent = new HttpClient(manager);
		maxdownloads=10;
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
			(new Thread(this)).start();
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
			// Assuming that this wouldbe a bad thing.
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
		while ((inprogress.size()<maxdownloads)&&(queue.size()>0))
		{
			Download r = (Download)queue.remove(0);
			Downloader d = new Downloader(agent,this,r);
			inprogress.put(r,d);
			d.start();
		}
	}
	
	public synchronized void add(Download r)
	{
		if (!(cache.containsValue(r)||cache.containsKey(r.getLocalFile())))
		{
			cache.put(r.getLocalFile(),r);
			queue.add(r);
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
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		checkWaiting();
		while ((!abort)&&((inprogress.size()+queue.size())>0))
		{
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
			}
		}
		running=false;
	}
}
