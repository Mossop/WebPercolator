/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator.swingparser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.text.html.HTMLEditorKit;

import com.blueprintit.webpercolator.HtmlLinkParser;

/**
 * @author Dave
 */
public class Parser extends HTMLEditorKit implements HtmlLinkParser
{
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3545520586416730425L;

	public Parser()
	{
	}
	
	/**
	 * @see com.blueprintit.webpercolator.HtmlLinkParser#parseLinks(java.lang.String)
	 */
	public Collection parseLinks(URL base, String html)
	{
		try
		{
			return parseLinks(base,new StringReader(html));
		}
		catch (IOException e)
		{
			return new LinkedList();
		}
	}

	/**
	 * @see com.blueprintit.webpercolator.HtmlLinkParser#parseLinks(java.io.Reader)
	 */
	public Collection parseLinks(URL base, Reader in) throws IOException
	{
		Callback callback = new Callback(base);
		HTMLEditorKit.Parser parser = getParser();
		parser.parse(in,callback,true);
		return callback.getLinks();
	}

}
