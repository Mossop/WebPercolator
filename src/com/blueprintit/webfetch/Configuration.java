/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.util.Collection;
import java.util.Comparator;

import org.apache.commons.httpclient.HttpState;

import com.blueprintit.webpercolator.DownloadQueue;

/**
 * @author Dave
 */
public interface Configuration
{
	public Collection getEnvironments();
	
	public void initialiseHttpState(HttpState state);
	
	public void applyConfiguration(Environment env);

	public int getMaxDownloads();

	//public Comparator getOrdering(DownloadQueue queue);
}
