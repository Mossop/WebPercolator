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
public interface QueueListener
{
	public void queueStarted(Object sender, QueueEvent e);
	
	public void queueComplete(Object sender, QueueEvent e);
}
