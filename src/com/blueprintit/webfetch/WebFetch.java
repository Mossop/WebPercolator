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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
import com.blueprintit.webpercolator.DownloadEvent;
import com.blueprintit.webpercolator.DownloadListener;
import com.blueprintit.webpercolator.DownloadQueue;
import com.blueprintit.webpercolator.HtmlLinkParser;
import com.blueprintit.webpercolator.Link;

public class WebFetch implements DownloadListener
{
	private Configuration config;
	private DownloadQueue queue;
	private Set urlcache;
	
	private static Log log = LogFactory.getLog(WebFetch.class);
	
	public WebFetch(Configuration config)
	{
		this.config=config;
		queue = new DownloadQueue();
		urlcache = Collections.synchronizedSet(new HashSet());
		config.initialiseHttpState(queue.getHttpState());
	}
	
	private void parse(URL base, File file)
	{
		if ((file.isFile())&&(file.canRead()))
		{
			HtmlLinkParser parser = queue.getLinkParser();
			try
			{
				Collection links = parser.parseLinks(base, new FileReader(file));
				Iterator loop = links.iterator();
				while (loop.hasNext())
				{
					Link link = (Link)loop.next();
					Environment newenv = new Environment(link.getUrl(),base);
					addEnvironment(newenv);
				}
			}
			catch (IOException e)
			{
				
			}
		}
	}
	
	public synchronized void addEnvironment(Environment env)
	{
		if (!urlcache.contains(env.getTarget()))
		{
			urlcache.add(env.getTarget());
			config.applyConfiguration(env);
			if (env.isAccepted())
			{
				if (env.getFile()==null)
				{
					if (env.isParsing())
					{
						log.info("Would download to temporary place and attempt to parse");
						//queue.add(new EnvironmentDownload(env));
					}
					else
					{
						log.info("No point in downloading "+env.getTarget());
					}
				}
				else
				{
					if ((!(env.getFile().exists()))||(env.isOverwriting()))
					{
						log.info("Would download to "+env.getFile().getAbsolutePath());
						//queue.add(new EnvironmentDownload(env));
					}
					else if (env.isParsing())
					{
						log.info("Would parse "+env.getFile());
						//parse(env.getTarget(),env.getFile());
					}
				}
			}
		}
	}
	
	public int getRemaining()
	{
		return queue.getRemaining();
	}
	
	public void start()
	{
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
				wf.addEnvironment(env);
			}
			if (wf.getRemaining()>0)
			{
				wf.start();
			}
			else
			{
				log.fatal("No valid urls specified to download");
			}
		}
		else
		{
			log.fatal("No configuration was specified");
		}
	}

	public void downloadStarted(DownloadEvent e)
	{
	}

	public void downloadUpdate(DownloadEvent e)
	{
	}

	public void downloadComplete(DownloadEvent e)
	{
		System.out.println(e.getDownload().getURL()+" downloaded to "+e.getLocalFile());
		EnvironmentDownload download = (EnvironmentDownload)e.getDownload();
		if (download.isParsable())
		{
			parse(download.getURL(),download.getLocalFile());
		}
	}

	public void downloadFailed(DownloadEvent e)
	{
		System.err.println("Failed to download "+e.getDownload().getURL()+": "+e.getException().getMessage());
	}

	public void downloadRedirected(DownloadEvent e)
	{
		EnvironmentDownload download = (EnvironmentDownload)e.getDownload();
		Environment env = download.getEnvironment();
		Environment newenv = new Environment(e.getRedirectURL(),env.getReferer());
		addEnvironment(newenv);
	}
}
