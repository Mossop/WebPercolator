/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

/**
 * @author Dave
 */
public class DecisionThread implements Runnable
{
	private WebFetch webfetch;
	private Configuration config;
	private boolean running;
	
	public DecisionThread(WebFetch fetch, Configuration config)
	{
		webfetch=fetch;
		this.config=config;
	}
	
	public void start()
	{
		running=true;
		(new Thread(this)).start();		
	}
	
	public void stop()
	{
		running=false;
		wake();
	}
	
	public void wake()
	{
		notify();
	}
	
	public void sleep()
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
	
	public void run()
	{
		while (running)
		{
			Environment env = webfetch.getEnvironmentForDecision();
			while ((env==null)&&(running))
			{
				sleep();
				env = webfetch.getEnvironmentForDecision();
			}
			if (running)
			{
				config.applyConfiguration(env);
				if (env.isAccepted())
				{
					webfetch.addEnvironmentToDownload(env);
				}
			}
		}
	}
}
