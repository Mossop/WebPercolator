/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Dave
 */
public class DecisionThread implements Runnable
{
	private WebFetch webfetch;
	private Configuration config;
	private boolean running = false;
	private Set urlcache;
	private Map filecache;
	
	private Log log = LogFactory.getLog(DecisionThread.class);
	
	public DecisionThread(WebFetch fetch, Configuration config, Set urlcache, Map filecache)
	{
		this.filecache=filecache;
		this.urlcache=urlcache;
		webfetch=fetch;
		this.config=config;
	}
	
	public synchronized void start()
	{
		running=true;
		(new Thread(this)).start();		
	}
	
	public synchronized void stop()
	{
		running=false;
		wake();
	}
	
	public synchronized boolean isRunning()
	{
		return running;
	}
	
	public synchronized void wake()
	{
		notify();
	}
	
	private synchronized void sleep()
	{
		webfetch.registerSleepingDecider(this);
		try
		{
			wait();
		}
		catch (InterruptedException e)
		{
			// Expected case
		}
	}
	
	private boolean checkDirectory(File dir)
	{
		if (dir.isDirectory())
		{
			return true;
		}
		else if (dir.exists())
		{
			return false;
		}
		else
		{
			return checkDirectory(dir.getParentFile());
		}
	}
	
	private void prepareDownload(Environment env)
	{
		synchronized(urlcache)
		{
			if (urlcache.contains(env.getTarget()))
			{
				return;
			}
			urlcache.add(env.getTarget());
		}
		if (env.getFile()==null)
		{
			if (env.isParsingRemote())
			{
				log.info("Added: "+env.getTarget());
				webfetch.addEnvironmentToDownload(env);
			}
			else
			{
				log.info("No point in downloading "+env.getTarget());
			}
		}
		else
		{
			File target = env.getFile();
			synchronized(filecache)
			{
				if (filecache.containsKey(target))
				{
					if (env.isParsingRemote())
					{
						filecache.put(target,Boolean.TRUE);
					}
					return;
				}
				if ((!target.exists())||(env.isOverwriting()))
				{
					File parent = target.getParentFile();
					if ((parent.isDirectory())||((checkDirectory(parent))&&(parent.mkdirs())))
					{
						log.info("Added: "+env.getTarget());
						webfetch.addEnvironmentToDownload(env);
						filecache.put(target,Boolean.valueOf(env.isParsingRemote()));
					}
					else
					{
						log.error("Would be unable to store at "+env.getFile());
					}
				}
				else if (env.isParsingLocal())
				{
					log.info("Parsing local: "+env.getTarget());
					webfetch.addParseDetails(new ParseDetails(env.getTarget(),env.getFile()));
				}
			}
		}
	}
	
	public void run()
	{
		while (isRunning())
		{
			Environment env = webfetch.getEnvironmentForDecision();
			while ((env==null)&&(isRunning()))
			{
				sleep();
				env = webfetch.getEnvironmentForDecision();
			}
			if (env!=null)
			{
				try
				{
					config.applyConfiguration(env);
					if (env.isAccepted())
					{
						prepareDownload(env);
					}
				}
				catch (Throwable t)
				{
					log.error("Error applying configuration to "+env.getTarget(),t);
				}
			}
		}
	}
}
