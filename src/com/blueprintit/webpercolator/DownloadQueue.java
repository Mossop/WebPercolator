/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
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
import org.apache.commons.httpclient.methods.HeadMethod;

import com.blueprintit.webpercolator.swingparser.Parser;

/**
 * @author Dave
 */
public class DownloadQueue
{
	private Map inprogress;
	private List queue;
	private Map cache;
	private boolean running;
	private HttpClient agent;
	private List downloadListeners;
	private List queueListeners;
	private HtmlLinkParser parser;
	private Comparator ordering;
	private boolean cancompare;
	private MultiThreadedHttpConnectionManager manager;
	private boolean globalProxySet = false;
	private String globalProxyHost;
	private int globalProxyPort;
	
	public DownloadQueue()
	{
		cancompare=true;
		inprogress = new HashMap();
		queue = new LinkedList();
		downloadListeners = new LinkedList();
		queueListeners = new LinkedList();
		running=false;
		manager = new MultiThreadedHttpConnectionManager();
		agent = new HttpClient(manager);
		manager.setMaxTotalConnections(10);
		manager.setMaxConnectionsPerHost(10);
		parser = new Parser();
		ordering = null;
	}
	
	public synchronized void setGlobalProxy(String host, int port)
	{
		globalProxyHost=host;
		globalProxyPort=port;
		globalProxySet=true;
	}
	
	public synchronized void clearGlobalProxy()
	{
		globalProxySet=false;
	}
	
	public synchronized boolean isGlobalProxySet()
	{
		return globalProxySet;
	}
	
	public HeadMethod getURLDetails(URL url) throws HttpException, IOException
	{
		HeadMethod method = new HeadMethod(url.toString());
		if (globalProxySet)
		{
			method.getHostConfiguration().setProxy(globalProxyHost,globalProxyPort);
		}
		agent.executeMethod(method);
		method.releaseConnection();
		return method;
	}
	
	public synchronized int getRemaining()
	{
		return inprogress.size()+queue.size();
	}
	
	public HtmlLinkParser getLinkParser()
	{
		return parser;
	}
	
	public void executeMethod(HttpMethod method) throws HttpException, IOException
	{
		agent.executeMethod(method);
	}
	
	public void addQueueListener(QueueListener l)
	{
		synchronized(queueListeners)
		{
			queueListeners.add(l);
		}
	}
	
	public void removeQueueListener(QueueListener l)
	{
		synchronized(queueListeners)
		{
			queueListeners.remove(l);
		}
	}
	
	public void addDownloadListener(DownloadListener l)
	{
		synchronized(downloadListeners)
		{
			downloadListeners.add(l);
		}
	}
	
	public void removeDownloadListener(DownloadListener l)
	{
		synchronized(downloadListeners)
		{
			downloadListeners.remove(l);
		}
	}
	
	void processDownloadEvent(DownloadEvent ev)
	{
		List list;
		synchronized(downloadListeners)
		{
			list = new LinkedList(downloadListeners);
		}
		Iterator loop = list.iterator();
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
				downloadComplete(ev.getDownload());
				break;
		}
	}
	
	private synchronized void downloadComplete(Download download)
	{
		inprogress.remove(download);
		checkWaiting();
	}
	
	/*public void complete()
	{
		start();
		waitFor();
	}*/
	
	public synchronized void start()
	{
		if ((!running)&&(queue.size()>0))
		{
			List listeners;
			synchronized(queueListeners)
			{
				listeners = new LinkedList(queueListeners);
			}
			QueueEvent e = new QueueEvent(this,QueueEvent.QUEUE_STARTED);
			Iterator loop = listeners.iterator();
			while (loop.hasNext())
			{
				QueueListener l = (QueueListener)loop.next();
				l.queueStarted(this,e);
			}
			running=true;
			checkWaiting();
		}
	}
	
	private void queueComplete()
	{
		List listeners;
		synchronized(queueListeners)
		{
			listeners = new LinkedList(queueListeners);
		}
		QueueEvent e = new QueueEvent(this,QueueEvent.QUEUE_COMPLETE);
		Iterator loop = listeners.iterator();
		while (loop.hasNext())
		{
			QueueListener l = (QueueListener)loop.next();
			l.queueComplete(this,e);
		}		
	}
	
	public synchronized void stop()
	{
		if (running)
		{
			running=false;
		}
	}
	
	public synchronized HttpState getHttpState()
	{
		return agent.getState();
	}
	
	public synchronized void setHttpState(HttpState value)
	{
		if (inprogress.size()>0)
		{
			// Assuming that this would be a bad thing.
			throw new IllegalStateException("Cannot change Http state while downloading");
		}
		agent.setState(value);
	}
	
	public synchronized void setMaxDownloads(int value)
	{
		manager.setMaxConnectionsPerHost(value);
		manager.setMaxTotalConnections(value);
		checkWaiting();
	}
	
	private synchronized void checkWaiting()
	{
		if (running)
		{
			int pos = 0;
			while ((inprogress.size()<manager.getMaxTotalConnections())&&(pos<queue.size()))
			{
				Download r = (Download)queue.get(pos);
				boolean canstart=true;
				if ((inprogress.size()>0)&&(r.getLocalFile()!=null))
				{
					Iterator loop = inprogress.values().iterator();
					while (loop.hasNext())
					{
						Downloader d = (Downloader)loop.next();
						if (r.getLocalFile().equals(d.getFile()))
						{
							canstart=false;
							break;
						}
					}
				}
				if (canstart)
				{
					queue.remove(pos);					
					Downloader d = new Downloader(this,r);
					inprogress.put(r,d);
					d.start();
				}
				else
				{
					pos++;
				}
			}
			if (queue.size()==0)
			{
				cancompare=true;
			}
			if (inprogress.size()==0)
			{
				assert queue.size()==0;
				queueComplete();
			}
		}
	}
	
	public synchronized void setOrdering(Comparator o)
	{
		ordering=o;
		if (ordering!=null)
		{
			Collections.sort(queue,ordering);
		}
		else if (cancompare)
		{
			Collections.sort(queue);
		}
	}
	
	public synchronized void add(Download r)
	{
		if (!(r instanceof Comparable))
		{
			cancompare=false;
		}
		if ((globalProxySet)&&(!r.getHttpMethod().getHostConfiguration().isProxySet()))
		{
			r.getHttpMethod().getHostConfiguration().setProxy(globalProxyHost,globalProxyPort);
		}
		int pos = queue.size();
		if ((ordering!=null)||(cancompare))
		{
			if (ordering!=null)
			{
				pos=Collections.binarySearch(queue,r,ordering);
			}
			else
			{
				pos=Collections.binarySearch(queue,r);
			}
			if (pos<0)
			{
				pos=-(pos+1);
			}
		}
		queue.add(pos,r);
		checkWaiting();
	}
	
	/*public void waitFor()
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
	}*/
}
