/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import org.apache.commons.httpclient.Credentials;

public class AuthenticationDetails
{
	private String realm;
	private String host;
	private Credentials credentials;
	
	public Credentials getCredentials()
	{
		return credentials;
	}
	
	public void setCredentials(Credentials credentials)
	{
		this.credentials = credentials;
	}
	
	public String getHost()
	{
		return host;
	}
	
	public void setHost(String host)
	{
		this.host = host;
	}
	
	public String getRealm()
	{
		return realm;
	}
	
	public void setRealm(String realm)
	{
		this.realm = realm;
	}
}
