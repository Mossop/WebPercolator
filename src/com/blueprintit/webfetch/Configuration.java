/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.util.Collection;

import org.apache.commons.httpclient.HttpState;

/**
 * @author Dave
 */
public interface Configuration
{
	public Collection getEnvironments();
	
	public void initialiseHttpState(HttpState state);
	
	public void applyConfiguration(Environment env);

	public int getMaxDownloads();
}
