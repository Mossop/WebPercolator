package com.blueprintit.webfetch;

import java.io.File;
import java.net.URL;

/**
 * @author Dave
 */
public class Environment
{
	private URLBuilder target;
	private URLBuilder referer;
	private File file = null;
	private boolean accepted = false;
	private boolean rejected = false;
	
	public Environment(URL target)
	{
		this.target=new URLBuilder(target);
	}
	
	public Environment(URL target, URL referer)
	{
		this.target=new URLBuilder(target);
		this.referer=new URLBuilder(referer);
	}

	public URLBuilder getReferer()
	{
		return referer;
	}
	
	public void setReferer(URLBuilder referer)
	{
		this.referer = referer;
	}
	
	public URLBuilder getTarget()
	{
		return target;
	}
	
	public void setTarget(URLBuilder target)
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
