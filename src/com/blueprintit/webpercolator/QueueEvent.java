/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

/**
 * @author Dave
 */
public class QueueEvent
{
	private DownloadQueue queue;
	private int type;
	
	public static final int QUEUE_STARTED = 0;
	public static final int QUEUE_COMPLETE = 1;
	
	public QueueEvent(DownloadQueue q, int type)
	{
		queue=q;
		this.type=type;
	}

	/**
	 * @return Returns the queue.
	 */
	public DownloadQueue getQueue()
	{
		return queue;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType()
	{
		return type;
	}
}
