/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.net.URL;

import com.blueprintit.webfetch.Environment;
import com.blueprintit.webpercolator.URLBuilder;

/**
 * @author Dave
 */
public class DownloadSettings
{
	private URLBuilder target;
	private URLBuilder referer;
	private boolean accept;
	private boolean reject;
	
	public DownloadSettings(Environment env)
	{
		target = new URLBuilder(env.getTarget());
		if (env.getReferer()!=null)
			referer = new URLBuilder(env.getReferer());
		accept=env.isAccepted();
		reject=env.isRejected();
	}
	
	public DownloadSettings(URL target, URL referer)
	{
		this.target = new URLBuilder(target);
		if (referer!=null)
			this.referer = new URLBuilder(referer);
	}
	
	void store(Environment env)
	{
		env.setTarget(target.toURL());
		if (referer!=null)
		{
			env.setReferer(referer.toURL());
		}
		else
		{
			env.setReferer(null);
		}
		env.setAccepted(accept);
		env.setRejected(reject);
	}
	
	public void accept()
	{
		accept=true;
	}
	
	public void reject()
	{
		reject=true;
	}
	
	public boolean isAccepted()
	{
		return accept;
	}
	
	public boolean isRejected()
	{
		return reject;
	}
	
	public URLBuilder getUrl()
	{
		return target;
	}
	
	public URLBuilder getReferer()
	{
		return referer;
	}
}
