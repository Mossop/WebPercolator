/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.blueprintit.webfetch.v1.V1ConfigurationParser;
import com.blueprintit.webpercolator.DownloadAdapter;
import com.blueprintit.webpercolator.DownloadEvent;
import com.blueprintit.webpercolator.DownloadQueue;
import com.blueprintit.webpercolator.HtmlLinkParser;
import com.blueprintit.webpercolator.QueueAdapter;
import com.blueprintit.webpercolator.QueueEvent;

public class WebFetch
{
	private Configuration config;
	private DownloadQueue queue;
	
	private Map filecache;
	private Set urlcache;
	private Set initialurlcache;
	
	private List awaitingDecision;
	private List awaitingParse;
	
	private List sleepingParsers;
	private List sleepingDeciders;
	
	private int parsers;
	private int deciders;
	
	private boolean running;
	
	private static Log log = LogFactory.getLog(WebFetch.class);
	
	public WebFetch(Configuration config)
	{
		running=false;
		this.config=config;
		queue = new DownloadQueue();
		
		queue.addQueueListener(new QueueAdapter() {
			public void queueComplete(Object sender, QueueEvent e)
			{
				potentiallyFinished();
			}
		});
		
		queue.addDownloadListener(new DownloadAdapter() {
			public void downloadComplete(DownloadEvent e)
			{
				System.out.println(e.getDownload().getURL()+" downloaded to "+e.getLocalFile());
				EnvironmentDownload download = (EnvironmentDownload)e.getDownload();
				boolean parse = false;
				if (download.getLocalFile()==null)
				{
					parse=true;
				}
				else
				{
					synchronized(filecache)
					{
						if (filecache.containsKey(download.getLocalFile()))
						{
							parse=((Boolean)filecache.get(download.getLocalFile())).booleanValue();
							filecache.remove(download.getLocalFile());
						}
					}
				}
				if (parse)
				{
					addParseDetails(new ParseDetails(download.getURL(),e.getLocalFile()));
				}
			}

			public void downloadFailed(DownloadEvent e)
			{
				// TODO add checking for valid HTTP errors that should not be repeated
				
				Environment env = ((EnvironmentDownload)e.getDownload()).getEnvironment();
				if (env.getAttempts()>0)
				{
					System.out.println("Retrying download "+env.getTarget());
					addEnvironmentToDownload(env);
				}
				else
				{
					System.err.println("Failed to download "+e.getDownload().getURL()+": "+e.getException().getMessage());
				}
			}

			public void downloadRedirected(DownloadEvent e)
			{
				EnvironmentDownload download = (EnvironmentDownload)e.getDownload();
				Environment env = download.getEnvironment();
				Environment newenv = new Environment(e.getRedirectURL(),env.getReferer());
				addEnvironmentForDecision(newenv);
			}
		});
		
		queue.setMaxDownloads(5);
		
		initialurlcache = new HashSet();
		urlcache = new HashSet();
		filecache = new HashMap();
		
		config.initialiseHttpState(queue.getHttpState());
		
		sleepingParsers = new LinkedList();
		sleepingDeciders = new LinkedList();
		awaitingDecision = new LinkedList();
		awaitingParse = new LinkedList();
		
		parsers=0;
		deciders=0;
		
		HtmlLinkParser parser = queue.getLinkParser();
		for (int loop=0; loop<100; loop++)
		{
			(new DecisionThread(this,config,urlcache,filecache)).start();
			deciders++;
		}
		for (int loop=0; loop<10; loop++)
		{
			(new ParsingThread(this,parser)).start();
			parsers++;
		}
	}
	
	private boolean isFinished()
	{
		if (!running)
		{
			return false;
		}
		synchronized(sleepingDeciders)
		{
			synchronized(sleepingParsers)
			{
				synchronized(awaitingDecision)
				{
					synchronized(awaitingParse)
					{
						if (queue.getRemaining()>0)
						{
							return false;
						}
						if (sleepingDeciders.size()<deciders)
						{
							return false;
						}
						if (sleepingParsers.size()<parsers)
						{
							return false;
						}
						if (awaitingDecision.size()>0)
						{
							return false;
						}
						if (awaitingParse.size()>0)
						{
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	public void potentiallyFinished()
	{
		if (isFinished())
		{
			System.out.println("Downloads might be complete");
		}
	}
	
	private void wakeDecider()
	{
		DecisionThread decider = null;
		synchronized(sleepingDeciders)
		{
			if (sleepingDeciders.size()>0)
			{
				decider = (DecisionThread)sleepingDeciders.remove(0);
			}
		}
		if (decider!=null)
		{
			decider.wake();
		}
	}
	
	private void wakeParser()
	{
		ParsingThread parser = null;
		synchronized(sleepingParsers)
		{
			if (sleepingParsers.size()>0)
			{
				parser = (ParsingThread)sleepingParsers.remove(0);
			}
		}
		if (parser!=null)
		{
			parser.wake();
		}
	}
	
	public void registerSleepingDecider(DecisionThread thread)
	{
		synchronized(sleepingDeciders)
		{
			sleepingDeciders.add(thread);
		}
		potentiallyFinished();
	}
	
	public void registerSleepingParser(ParsingThread thread)
	{
		synchronized(sleepingParsers)
		{
			sleepingParsers.add(thread);
		}
		potentiallyFinished();
	}
	
	public void addEnvironmentForDecision(Environment env)
	{
		synchronized(initialurlcache)
		{
			if (initialurlcache.contains(env.getTarget()))
			{
				return;
			}
			initialurlcache.add(env.getTarget());
		}
		synchronized(awaitingDecision)
		{
			awaitingDecision.add(env);
			//log.info("Decision queue is "+awaitingDecision.size());
		}
		wakeDecider();
	}
	
	public Environment getEnvironmentForDecision()
	{
		Environment result = null;
		synchronized(awaitingDecision)
		{
			if (awaitingDecision.size()>0)
			{
				result = (Environment)awaitingDecision.remove(0);
			}
		}
		return result;
	}
	
	public void addParseDetails(ParseDetails details)
	{
		synchronized(awaitingParse)
		{
			awaitingParse.add(details);
			//log.info("Parsing queue is "+awaitingParse.size());
		}
		wakeParser();
	}
	
	public ParseDetails getParseDetails()
	{
		ParseDetails result = null;
		synchronized(awaitingParse)
		{
			if (awaitingParse.size()>0)
			{
				result = (ParseDetails)awaitingParse.remove(0);
			}
		}
		return result;
	}

	public void addEnvironmentToDownload(Environment env)
	{
		env.setAttempts(env.getAttempts()-1);
		queue.add(new EnvironmentDownload(env));
		queue.start();
	}
		
	public void start()
	{
		running=true;
		queue.start();
	}
	
	public static Configuration loadConfig(File file)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			try
			{
				file=file.getCanonicalFile();
				Document document = builder.parse(file);
				Element root = document.getDocumentElement();
				if ((root.getNodeName().equals("WebFetchConfig"))&&(root.hasAttribute("version")))
				{
					int version = Integer.parseInt(root.getAttribute("version"));
					ConfigurationParser parser;
					switch (version)
					{
						case 1:
							parser = new V1ConfigurationParser();
							break;
						default:
							log.fatal("Unknown configuration version: "+version);
							return null;
					}
					try
					{
						return parser.parseConfiguration(file.getParentFile(),document);
					}
					catch (ConfigurationParseException e)
					{
						log.fatal("Unable to parse configuration file",e);
						return null;
					}
				}
				else
				{
					log.fatal("Config file is an invalid format.");
					return null;
				}
			}
			catch (SAXException e)
			{
				log.fatal("Error parsing xml",e);
				return null;
			}
			catch (IOException e)
			{
				log.fatal("Error reading from config file",e);
				return null;
			}
		}
		catch (ParserConfigurationException e)
		{
			log.fatal("Unable to initialise xml parser",e);
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		Configuration config = null;
		Collection urls = new LinkedList();
		for (int loop=0; loop<args.length; loop++)
		{
			boolean used=false;
			if (config==null)
			{
				File thisfile = new File(args[loop]);
				if (thisfile.isFile())
				{
					config=loadConfig(thisfile);
					if (config!=null)
					{
						used=true;
					}
				}
			}
			if (!used)
			{
				try
				{
					URL url = new URL(args[loop]);
					urls.add(url);
				}
				catch (MalformedURLException e)
				{
					log.error("Unable to parse URL "+args[loop]);
				}
			}
		}
		if (config!=null)
		{
			WebFetch wf = new WebFetch(config);
			urls.addAll(config.getURLs());
			Collection environments = new LinkedList();
			Iterator loop = urls.iterator();
			while (loop.hasNext())
			{
				URL url = (URL)loop.next();
				Environment env = new Environment(url);
				wf.addEnvironmentForDecision(env);
			}
			wf.start();
		}
		else
		{
			log.fatal("No configuration was specified");
		}
	}
}
