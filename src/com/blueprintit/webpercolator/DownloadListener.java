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
public interface DownloadListener
{
	public void downloadStarted(DownloadEvent e);
	
	public void downloadUpdate(DownloadEvent e);
	
	public void downloadComplete(DownloadEvent e);
	
	public void downloadFailed(DownloadEvent e);
	
	public void downloadRedirected(DownloadEvent e);
}
