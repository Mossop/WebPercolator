/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.io.File;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.blueprintit.webfetch.Environment;
import com.blueprintit.webpercolator.URLBuilder;

/**
 * @author Dave
 */
public class DownloadSettings
{
	private URLBuilder target;
	private URLBuilder referer;
	private File file;
	private File base;
	private String useragent;
	private boolean overwrite;
	private boolean parseLocal;
	private boolean parseRemote;
	private boolean accept;
	private boolean reject;
	private static Log log = LogFactory.getLog(DownloadSettings.class);

	public DownloadSettings(File base, Environment env)
	{
		this.base=base;
		target = new URLBuilder(env.getTarget());
		if (env.getReferer()!=null)
			referer = new URLBuilder(env.getReferer());
		accept=env.isAccepted();
		reject=env.isRejected();
		parseLocal=env.isParsingLocal();
		parseRemote=env.isParsingRemote();
		overwrite=env.isOverwriting();
		file=env.getFile();
	}
	
	public DownloadSettings(URL target, URL referer)
	{
		this.target = new URLBuilder(target);
		if (referer!=null)
			this.referer = new URLBuilder(referer);
	}
	
	void setBaseDir(File base)
	{
		this.base=base;
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
		env.setParsingLocal(parseLocal);
		env.setParsingRemote(parseRemote);
		env.setOverwriting(overwrite);
		env.setFile(file);
		env.setAccepted(accept);
		env.setRejected(reject);
		env.setUserAgent(useragent);
	}
	
	public void accept()
	{
		accept=true;
	}
	
	public void reject()
	{
		reject=true;
	}
	
	public String getFile()
	{
		if (file!=null)
		{
			return file.toString();
		}
		else
		{
			return "";
		}
	}
	
	public void setFile(String value)
	{
		if (value.length()==0)
		{
			file=null;
		}
		else
		{
			if (value.startsWith("/"))
			{
				value=value.substring(1);
			}
			value.replace('/',File.separatorChar);
			file = new File(base,value);
		}
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

	public boolean isOverwrite()
	{
		return overwrite;
	}

	public void setOverwrite(boolean overwrite)
	{
		this.overwrite = overwrite;
	}

	public boolean isParseLocal()
	{
		return parseLocal;
	}

	public void setParseLocal(boolean parse)
	{
		parseLocal = parse;
	}

	public boolean isParseRemote()
	{
		return parseRemote;
	}

	public void setParseRemote(boolean parse)
	{
		parseRemote = parse;
	}
	
	public String getUserAgent()
	{
		return useragent;
	}
	
	public void setUserAgent(String value)
	{
		useragent=value;
	}
}
