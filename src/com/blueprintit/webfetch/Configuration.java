package com.blueprintit.webfetch;

import java.util.Collection;

/**
 * @author Dave
 */
public interface Configuration
{
	public Collection getURLs();
	
	public void applyConfiguration(Environment env);
}
