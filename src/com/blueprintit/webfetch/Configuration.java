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
	public Collection getURLs();
	
	public void initialiseHttpState(HttpState state);
	
	public void applyConfiguration(Environment env);
}
