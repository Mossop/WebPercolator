package com.blueprintit.webfetch;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.blueprintit.webfetch.v1.V1ConfigurationParser;

/**
 * @author Dave
 */
public class WebFetch
{
	public static Configuration loadConfig(File file)
	{
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder builder = dbf.newDocumentBuilder();
			try
			{
				Document document = builder.parse(file);
				Element root = document.getDocumentElement();
				if ((root.getLocalName().equals("WebFetchConfig"))&&(root.hasAttribute("version")))
				{
					int version = Integer.parseInt(root.getAttribute("version"));
					ConfigurationParser parser;
					switch (version)
					{
						case 1:
							parser = new V1ConfigurationParser();
							break;
						default:
							System.err.println("Unknown configuration version: "+version);
							return null;
					}
					return parser.parseConfiguration(document);
				}
				else
				{
					System.err.println("Config file is an invalid format.");
					return null;
				}
			}
			catch (SAXException e)
			{
				System.err.println("Error parsing xml: "+e.getMessage());
				return null;
			}
			catch (IOException e)
			{
				System.err.println("Error reading from config file: "+e.getMessage());
				return null;
			}
		}
		catch (ParserConfigurationException e)
		{
			System.err.println("Unable to initialise xml parser: "+e.getMessage());
			return null;
		}
	}
	
	public static void main(String[] args)
	{
		Configuration config = null;
		List urls = new ArrayList();
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
					System.err.println("Unable to parse URL "+args[loop]);
				}
			}
		}
		if (config!=null)
		{
			urls.addAll(config.getURLs());
		}
		else
		{
			System.err.println("No configuration was specified");
		}
	}
}
