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
	private boolean overwriting = false;
	private boolean parsingLocal = true;
	private boolean parsingRemote = true;
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

	public boolean isAccepted()
	{
		return accepted;
	}

	public void setAccepted(boolean accepted)
	{
		this.accepted = accepted;
	}

	public boolean isRejected()
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
	
	public boolean isParsingLocal()
	{
		return parsingLocal;
	}
	
	public void setParsingLocal(boolean parsing)
	{
		this.parsingLocal = parsing;
	}
	
	public boolean isParsingRemote()
	{
		return parsingRemote;
	}
	
	public void setParsingRemote(boolean parsing)
	{
		this.parsingRemote = parsing;
	}
	
	public boolean isOverwriting()
	{
		return overwriting;
	}
	
	public void setOverwriting(boolean overwriting)
	{
		this.overwriting = overwriting;
	}
}
