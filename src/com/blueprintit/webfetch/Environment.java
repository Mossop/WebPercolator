/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.io.File;
import java.net.URL;

/**
 * @author Dave
 */
public class Environment
{
	private URL target;
	private URL referer;
	private File file = null;
	private boolean accepted = false;
	private boolean rejected = false;
	
	public Environment(URL target)
	{
		this.target=target;
	}
	
	public Environment(URL target, URL referer)
	{
		this.target=target;
		this.referer=referer;
	}

	public URL getReferer()
	{
		return referer;
	}
	
	public void setReferer(URL referer)
	{
		this.referer = referer;
	}
	
	public URL getTarget()
	{
		return target;
	}
	
	public void setTarget(URL target)
	{
		this.target = target;
	}

	public boolean getAccepted()
	{
		return accepted;
	}

	public void setAccepted(boolean accepted)
	{
		this.accepted = accepted;
	}

	public boolean getRejected()
	{
		return rejected;
	}

	public void setRejected(boolean rejected)
	{
		this.rejected = rejected;
	}
	
	public File getFile()
	{
		return file;
	}

	public void setFile(File file)
	{
		this.file = file;
	}
}
