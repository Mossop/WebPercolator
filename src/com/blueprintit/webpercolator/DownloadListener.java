package com.blueprintit.webpercolator;

/**
 * @author Dave
 */
public interface DownloadListener
{
	public void downloadComplete(DownloadEvent e);
	
	public void downloadFailed(DownloadEvent e);
}
