package com.blueprintit.jget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import com.blueprintit.webpercolator.Download;
import com.blueprintit.webpercolator.DownloadEvent;
import com.blueprintit.webpercolator.DownloadListener;
import com.blueprintit.webpercolator.DownloadQueue;
import com.blueprintit.webpercolator.GetDownload;
import com.blueprintit.webpercolator.HtmlLinkParser;
import com.blueprintit.webpercolator.Link;

/**
 * @author Dave
 */
public class JGet implements DownloadListener
{
	private DownloadQueue queue;
	private CommandLine commandline;
	private URL referer;
	private Map downloadparents;
	private Map downloadrecurses;
	private File basedir;
	private Collection urlcache;
	private Collection filecache;
	private Collection acceptfiles;
	private Collection rejectfiles;
	private Collection acceptdirs;
	private Collection rejectdirs;
	private Collection accepthosts;
	private Collection rejecthosts;
	
	private static final String DEFAULT_FILENAME = "index.html";
	
	public JGet(String[] args) throws FileNotFoundException, IOException
	{
		queue = new DownloadQueue();
		queue.setMaxDownloads(1);
		queue.addDownloadListener(this);
		downloadparents = Collections.synchronizedMap(new HashMap());
		downloadrecurses = Collections.synchronizedMap(new HashMap());
		urlcache = Collections.synchronizedCollection(new LinkedList());
		filecache = Collections.synchronizedCollection(new LinkedList());
		acceptfiles = Collections.synchronizedCollection(new LinkedList());
		rejectfiles = Collections.synchronizedCollection(new LinkedList());
		accepthosts = Collections.synchronizedCollection(new LinkedList());
		rejecthosts = Collections.synchronizedCollection(new LinkedList());
		acceptdirs = Collections.synchronizedCollection(new LinkedList());
		rejectdirs = Collections.synchronizedCollection(new LinkedList());
		parseArguments(args);
	}
	
	public String escapeRegex(String text)
	{
		text=text.replaceAll("\\.","\\\\.");
		text=text.replaceAll("\\*",".*");
		text=text.replaceAll("\\?",".");
		//System.out.println("Generated regex "+text);
		return "^"+text+"$";
	}
	
	public boolean rejected(String text, Collection accept, Collection reject)
	{
		if ((accept.size()==0)&&(reject.size()==0))
		{
			return false;
		}
		//System.out.println("Testing "+text);
		if (accept.size()>0)
		{
			Iterator loop = accept.iterator();
			while (loop.hasNext())
			{
				String test = (String)loop.next();
				if (text.matches(test))
					return false;
			}
			return true;
		}
		if (reject.size()>0)
		{
			Iterator loop = reject.iterator();
			while (loop.hasNext())
			{
				String test = (String)loop.next();
				if (text.matches(test))
					return true;
			}
		}
		return false;
	}
	
	public boolean shouldDownload(URL url, Download parent)
	{
		if (urlcache.contains(url))
			return false;
		if (parent==null)
			return true;
		if (!(url.getHost().equals(parent.getURL().getHost())))
		{
			if (!commandline.hasOption("H"))
			{
				return false;
			}
		}
		
		if (commandline.hasOption("np"))
		{
			Download current = parent;
			Download next = (Download)downloadparents.get(current);
			while (next!=null)
			{
				current=next;
				next=(Download)downloadparents.get(current);
			}
			if (url.getHost().equals(current.getURL().getHost()))
			{
				String path = url.getPath();
				path=path.substring(0,path.lastIndexOf("/"));
				String parpath = current.getURL().getPath();
				parpath=parpath.substring(0,parpath.lastIndexOf("/"));
				if (!path.startsWith(parpath))
				{
					return false;
				}
			}
		}

		String host = url.getHost();
		String path = url.getPath();
		String file = "";
		if (path.indexOf("/")>=0)
		{
			file=path.substring(path.lastIndexOf("/")+1);
			path=path.substring(0,path.lastIndexOf("/"));
		}
		if (rejected(host,accepthosts,rejecthosts))
		{
			return false;
		}
		if (rejected(file,acceptfiles,rejectfiles))
		{
			return false;
		}
		if (rejected(path,acceptdirs,rejectdirs))
		{
			return false;
		}

		return true;
	}
	
	public File chooseFilename(URL url)
	{
		File basedir=this.basedir;
		if ((commandline.hasOption("x")||(commandline.hasOption("r")))&&(!commandline.hasOption("nd")))
		{
			StringBuffer path = new StringBuffer();
			if (!commandline.hasOption("nH"))
			{
				path.append(url.getHost());
				path.append(File.separatorChar);
			}
			String pathname = url.getFile();
			if (!pathname.startsWith("/"))
			{
				pathname="/"+pathname;
			}
			String file = pathname.substring(pathname.lastIndexOf("/")+1);
			String dirs = pathname.substring(0,pathname.lastIndexOf("/"));
			if (dirs.length()>0)
			{
				String[] bits = dirs.split("/");
				int pos = 0;
				if (commandline.hasOption("cut-dirs"))
				{
					pos=Integer.parseInt(commandline.getOptionValue("cut-dirs"));
				}
				pos++;
				while (pos<bits.length)
				{
					path.append(bits[pos]);
					path.append(File.separatorChar);
					pos++;
				}
			}
			basedir=new File(basedir,path.toString());
			if (!basedir.exists())
			{
				basedir.mkdirs();
			}
		}

		String pathname = url.getFile();
		if (!pathname.startsWith("/"))
		{
			pathname="/"+pathname;
		}
		String file = pathname.substring(pathname.lastIndexOf("/")+1);
		if (file.length()==0)
		{
			file=DEFAULT_FILENAME;
		}
		file=file.replace('*','_');
		file=file.replace('?','_');
		
		File aim = new File(basedir,file);
		if (aim.isDirectory())
		{
			basedir=aim;
			file=DEFAULT_FILENAME;
			aim = new File(basedir,file);
		}
		
		if ((aim.exists())||(filecache.contains(aim)))
		{
			if (commandline.hasOption("r"))
			{
				if (commandline.hasOption("nc"))
				{
					return null;
				}
			}
			else
			{
				if (!commandline.hasOption("nc"))
				{
					int suffix=0;
					while ((aim.exists())||(filecache.contains(aim)))
					{
						suffix++;
						aim = new File(basedir,file+"."+suffix);
					}
				}
			}
		}
		return aim;
	}
	
	public synchronized void submitURL(URL url, Download parent, int recursedepth, int type)
	{
		if (!shouldDownload(url,parent))
			return;
		
		File local = chooseFilename(url);
		if (local==null)
			return;
		
		URL referer = this.referer;
		if (parent!=null)
		{
			referer=parent.getURL();
		}
		
		GetDownload download = new GetDownload(url,local,type,referer);

		urlcache.add(url);
		filecache.add(local);
		
		download.getHttpMethod().setFollowRedirects(false);
		downloadparents.put(download,parent);
		downloadrecurses.put(download,new Integer(recursedepth));
		queue.add(download);
	}
	
	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadStarted(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadStarted(DownloadEvent e)
	{
		// Nothing to do.
	}

	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadUpdate(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadUpdate(DownloadEvent e)
	{
		// Nothing to do.
	}

	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadComplete(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadComplete(DownloadEvent e)
	{
		Download download = e.getDownload();
		System.out.println("Downloaded "+download.getURL()+" to "+download.getLocalFile());
		Header type = download.getHttpMethod().getResponseHeader("Content-Type");
		String content = type.getValue();
		int pos = content.indexOf(";");
		if (pos>=0)
		{
			content=content.substring(0,pos);
		}
		if (content.equals("text/html"))
		{
			int recursedepth = ((Integer)downloadrecurses.get(download)).intValue();
			if ((recursedepth!=0)||(commandline.hasOption("p")))
			{
				HtmlLinkParser parser = queue.getLinkParser();
				try
				{
					Iterator loop = parser.parseLinks(download.getURL(),new FileReader(download.getLocalFile())).iterator();
					while (loop.hasNext())
					{
						Link link = (Link)loop.next();
						if ((link.getType()==Download.LINK_DOWNLOAD)&&(recursedepth!=0))
						{
							//System.out.println("Recursing to "+link.getUrl());
							submitURL(link.getUrl(),download,recursedepth-1,link.getType());
						}
						else
						{
							if (recursedepth==0)
							{
								//System.out.println("Recursing to "+link.getUrl());
								submitURL(link.getUrl(),download,0,link.getType());
							}
							else
							{
								//System.out.println("Recursing to "+link.getUrl());
								submitURL(link.getUrl(),download,recursedepth-1,link.getType());
							}
						}
					}
				}
				catch (IOException e1)
				{
					System.err.println("Could not open stored file for parsing");
				}
			}
		}
	}

	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadFailed(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadFailed(DownloadEvent e)
	{
		System.err.println("Download of "+e.getDownload().getURL()+" failed.");
	}

	/**
	 * @see com.blueprintit.webpercolator.DownloadListener#downloadRedirected(com.blueprintit.webpercolator.DownloadEvent)
	 */
	public void downloadRedirected(DownloadEvent e)
	{
		Download d = e.getDownload();
		Integer recurse = (Integer)downloadrecurses.get(d);
		Download parent = (Download)downloadparents.get(d);
		System.out.println("Redirected from "+d.getURL()+" to "+e.getRedirectURL());
		filecache.remove(d.getLocalFile());
		submitURL(e.getRedirectURL(),parent,recurse.intValue(),d.getType());
	}

	public static void main(String[] args) throws Exception
	{
		(new JGet(args)).start();		
	}
	
	public void displayHelp(Options options)
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("jget [option/url] [option/url] ...",options);
		System.out.println();
		System.out.println("Options with arguments can be expressed in multiple ways. The following are all equivalent:");
		System.out.println();
		System.out.println("jget -B http://www.blueprintit.co.uk /~dave");
		System.out.println("jget -B=http://www.blueprintit.co.uk /~dave");
		System.out.println("jget --base http://www.blueprintit.co.uk /~dave");
		System.out.println("jget --base=http://www.blueprintit.co.uk /~dave");
	}
	
	public void parseArguments(String[] args)
	{
		URL base = null;
		CommandLineParser parser = new BetterParser();
		Options options = new Options();
		//OptionBuilder.withValueSeparator('=');
		options.addOption("h","help",false,"Displays this help message");
		options.addOption("?",false,"Displays this help message");

		options.addOption(OptionBuilder.withLongOpt("input-file").hasArg().withArgName("file").withDescription("Reads urls from the specified input file").create("i"));
		options.addOption("F","force-html",false,"Forces the input file to be read as html");
		options.addOption(OptionBuilder.withLongOpt("base").hasArg().withArgName("url").withDescription("Specifies the base url used for relative urls").create("B"));
		
		options.addOption("nc","no-clobber",false,"During recursive downloading prevents files being overwritten, " +
				"otherwise forces an overwrite rather than adding a unique suffix to the name");
		//options.addOption("N","timestamping",false,"Decide whether to overwrite files based on local and remote " +
		//		"timestamps. Will be ignored if -nc is specified");
		
		options.addOption("nd","no-directories",false,"During recursive retrieval do not create a directory hierarchy");
		options.addOption("x","force-directories",false,"Force creating a directory hierarchy, even if not doing a " +
				"recursive retrieval");
		
		options.addOption("nH","no-host-directories",false,"Do not include the host name in the directory hierarchy");
		options.addOption(OptionBuilder.withLongOpt("cut-dirs").hasArg().withArgName("number").withDescription("Ignore a number of components from the directory hierarchy").create());
		
		options.addOption(OptionBuilder.withLongOpt("directory-prefix").hasArg().withArgName("directory").withDescription("Store files starting from the given directory").create("P"));

		options.addOption(OptionBuilder.withLongOpt("http-user").hasArg().withArgName("user").withDescription("Specify the username to use for authentication").create());
		options.addOption(OptionBuilder.withLongOpt("http-passwd").hasArg().withArgName("password").withDescription("Specify the password to use for authentication").create());
		
		options.addOption(OptionBuilder.withLongOpt("referer").hasArg().withArgName("url").withDescription("Specify the referer to send to the remote server").create());
		
		options.addOption("r","recursive",false,"Turn on recursive retrieval");
		options.addOption(OptionBuilder.withLongOpt("level").hasArg().withArgName("depth").withDescription("Specify the depth of recursive retrieval").create("l"));

		options.addOption("p","page-requisites",false,"Retrieve any media included in a page, like images and stylesheets");

		options.addOption(OptionBuilder.withLongOpt("accept").hasArg().withArgName("acclist").withDescription("Comma-separated list of file name suffixes to accept").create("A"));
		options.addOption(OptionBuilder.withLongOpt("reject").hasArg().withArgName("rejlist").withDescription("Comma-separated list of file name suffixes to reject").create("R"));

		options.addOption(OptionBuilder.withLongOpt("include-directories").hasArg().withArgName("list").withDescription("Comma-separated list of directory names to accept").create("I"));
		options.addOption(OptionBuilder.withLongOpt("exclude-directories").hasArg().withArgName("list").withDescription("Comma-separated list of directory names to reject").create("X"));

		options.addOption(OptionBuilder.withLongOpt("domains").hasArg().withArgName("domain-list").withDescription("Comma-separated list of domains to accept").create("D"));
		options.addOption(OptionBuilder.withLongOpt("exclude-domains").hasArg().withArgName("domain-list").withDescription("Comma-separated list of domains to reject").create());

		options.addOption("H","span-hosts",false,"Allow recursive retrieval to span hosts");
		options.addOption("np","no-parent",false,"Do not ascend above the parent directory of the initial resource");

		options.addOption(OptionBuilder.withLongOpt("threads").hasArg().withArgName("number").withDescription("The number of connections to use").create());

		try
		{
			commandline = parser.parse(options,args);
			
			if (commandline.hasOption("h"))
			{
				displayHelp(options);
			}
			else if (commandline.hasOption("?"))
			{
				displayHelp(options);
			}
			else
			{
				if (commandline.hasOption("B"))
				{
					try
					{
						base = new URL(commandline.getOptionValue("B"));
					}
					catch (MalformedURLException e)
					{
						System.err.println("The argument to the base command line option must be a valid url");
					}
				}
				
				if (commandline.hasOption("P"))
				{
					basedir = (new File(commandline.getOptionValue("P"))).getAbsoluteFile();
					if (!basedir.exists())
					{
						System.err.println("The specified base directory must exist.");
					}
				}
				else
				{
					basedir = (new File("")).getAbsoluteFile();
				}
				
				if (commandline.hasOption("threads"))
				{
					queue.setMaxDownloads(Integer.parseInt(commandline.getOptionValue("threads")));
				}
				
				if (commandline.hasOption("http-user")&&commandline.hasOption("http-passwd"))
				{
					queue.getHttpState().setCredentials(null,null,new UsernamePasswordCredentials(commandline.getOptionValue("http-user"),commandline.getOptionValue("http-passwd")));
				}
				
				if (commandline.hasOption("referer"))
				{
					try
					{
						referer = new URL(commandline.getOptionValue("referer"));
					}
					catch (MalformedURLException e)
					{
						System.err.println("The argument to referer should be a valid url");
					}
				}
				
				if (commandline.hasOption("A"))
				{
					String[] list = commandline.getOptionValues("A");
					for (int loop=0; loop<list.length; loop++)
					{
						String[] parts = list[loop].split(",");
						for (int partloop=0; partloop<parts.length; partloop++)
						{
							if (parts[partloop].length()>0)
							{
								if (!parts[partloop].startsWith("*"))
								{
									parts[partloop]="*"+parts[partloop];
								}
								acceptfiles.add(escapeRegex(parts[partloop]));
							}
						}
					}
				}
				
				if (commandline.hasOption("R"))
				{
					String[] list = commandline.getOptionValues("R");
					for (int loop=0; loop<list.length; loop++)
					{
						String[] parts = list[loop].split(",");
						for (int partloop=0; partloop<parts.length; partloop++)
						{
							if (parts[partloop].length()>0)
							{
								if (!parts[partloop].startsWith("*"))
								{
									parts[partloop]="*"+parts[partloop];
								}
								rejectfiles.add(escapeRegex(parts[partloop]));
							}
						}
					}
				}
				
				if (commandline.hasOption("I"))
				{
					String[] list = commandline.getOptionValues("I");
					for (int loop=0; loop<list.length; loop++)
					{
						String[] parts = list[loop].split(",");
						for (int partloop=0; partloop<parts.length; partloop++)
						{
							if (parts[partloop].length()>0)
							{
								acceptdirs.add(escapeRegex(parts[partloop]));
							}
						}
					}
				}
				
				if (commandline.hasOption("X"))
				{
					String[] list = commandline.getOptionValues("X");
					for (int loop=0; loop<list.length; loop++)
					{
						String[] parts = list[loop].split(",");
						for (int partloop=0; partloop<parts.length; partloop++)
						{
							if (parts[partloop].length()>0)
							{
								rejectdirs.add(escapeRegex(parts[partloop]));
							}
						}
					}
				}
				
				if (commandline.hasOption("D"))
				{
					String[] list = commandline.getOptionValues("D");
					for (int loop=0; loop<list.length; loop++)
					{
						String[] parts = list[loop].split(",");
						for (int partloop=0; partloop<parts.length; partloop++)
						{
							if (parts[partloop].length()>0)
							{
								accepthosts.add(escapeRegex(parts[partloop]));
							}
						}
					}
				}
				
				if (commandline.hasOption("exclude-domains"))
				{
					String[] list = commandline.getOptionValues("exclude-domains");
					for (int loop=0; loop<list.length; loop++)
					{
						String[] parts = list[loop].split(",");
						for (int partloop=0; partloop<parts.length; partloop++)
						{
							if (parts[partloop].length()>0)
							{
								rejecthosts.add(escapeRegex(parts[partloop]));
							}
						}
					}
				}
				
				int recursedepth=0;
				if (commandline.hasOption("r"))
				{
					recursedepth=5;
					if (commandline.hasOption("l"))
					{
						recursedepth=Integer.parseInt(commandline.getOptionValue("l"));
						if (recursedepth==0)
						{
							recursedepth=-1;
						}
					}
				}
				
				if (commandline.hasOption("i"))
				{
					File input = new File(commandline.getOptionValue("i"));
					if (input.exists())
					{
						try
						{
							if (commandline.hasOption("F"))
							{
								HtmlLinkParser linkparser = queue.getLinkParser();
								Iterator loop = linkparser.parseLinks(base,new FileReader(input)).iterator();
								while (loop.hasNext())
								{
									Link link = (Link)loop.next();
									if (link.getType()==Download.LINK_DOWNLOAD)
									{
										submitURL(link.getUrl(),null,recursedepth,Download.UNSPECIFIED_DOWNLOAD);
									}
								}
							}
							else
							{
								BufferedReader in = new BufferedReader(new FileReader(input));
								String line = in.readLine();
								while (line!=null)
								{
									try
									{
										URL link = new URL(base,line);
										submitURL(link,null,recursedepth,Download.UNSPECIFIED_DOWNLOAD);
									}
									catch (MalformedURLException e)
									{
										System.err.println("Invalid URL in input file: "+line);
									}
									line=in.readLine();
								}
								in.close();
							}
						}
						catch (IOException e)
						{
							System.err.println("There was an error reading from the input file");
						}
					}
					else
					{
						System.err.println("The input file specified does not exist");
					}
				}
				
				Iterator loop = commandline.getArgList().iterator();
				while (loop.hasNext())
				{
					String test = (String)loop.next();
					try
					{
						URL link = new URL(base,test);
						submitURL(link,null,recursedepth,Download.UNSPECIFIED_DOWNLOAD);
					}
					catch (MalformedURLException e)
					{
						System.err.println("Unknown option: "+test);
					}
				}
				
				if (urlcache.size()==0)
				{
					displayHelp(options);
				}
			}
		}
		catch (ParseException e)
		{
			System.err.println("An error occured parsing the command line options");
			displayHelp(options);
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		queue.start();
	}
}
