/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import com.blueprintit.webpercolator.HtmlLinkParser;
import com.blueprintit.webpercolator.Link;

/**
 * @author Dave
 */
public class ParsingThread implements Runnable
{
	private WebFetch webfetch;
	private HtmlLinkParser parser;
	private boolean running;
	
	public ParsingThread(WebFetch fetch, HtmlLinkParser parser)
	{
		webfetch=fetch;
		this.parser=parser;
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
		webfetch.registerSleepingParser(this);
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
			ParseDetails details = webfetch.getParseDetails();
			while ((details==null)&&(running))
			{
				sleep();
				details = webfetch.getParseDetails();
			}
			if (running)
			{
				File file = details.getFile();
				URL base = details.getUrl();
				if ((file.isFile())&&(file.canRead()))
				{
					try
					{
						Collection links = parser.parseLinks(base, new FileReader(file));
						Iterator loop = links.iterator();
						while (loop.hasNext())
						{
							Link link = (Link)loop.next();
							Environment newenv = new Environment(link.getUrl(),base);
							webfetch.addEnvironmentForDecision(newenv);
						}
					}
					catch (IOException e)
					{
						
					}
				}
			}
		}
	}
}
