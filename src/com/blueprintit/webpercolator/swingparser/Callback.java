package com.blueprintit.webpercolator.swingparser;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.HTML;

import com.blueprintit.webpercolator.Download;
import com.blueprintit.webpercolator.Link;

/**
 * @author Dave
 */
public class Callback extends ParserCallback
{
	private Map tagattrs;
	private URL base;
	private Collection links;
	private Map taglinktypes;
	
	public Callback(URL base)
	{
		this.base=base;
		tagattrs = new HashMap();
		links = new LinkedList();
		taglinktypes = new HashMap();
		tagattrs.put(HTML.Tag.A,HTML.Attribute.HREF);
		taglinktypes.put(HTML.Tag.A,new Integer(Download.LINK_DOWNLOAD));
		tagattrs.put(HTML.Tag.IMG,HTML.Attribute.SRC);
		taglinktypes.put(HTML.Tag.IMG,new Integer(Download.IMAGE_DOWNLOAD));
		tagattrs.put(HTML.Tag.FRAME,HTML.Attribute.SRC);
		taglinktypes.put(HTML.Tag.FRAME,new Integer(Download.FRAME_DOWNLOAD));
		tagattrs.put(HTML.Tag.LINK,HTML.Attribute.HREF);
		taglinktypes.put(HTML.Tag.LINK,new Integer(Download.UNSPECIFIED_DOWNLOAD));
	}
	
	public void handleStartTag(HTML.Tag tag, MutableAttributeSet attr, int pos)
	{
		if (tag==HTML.Tag.BASE)
		{
			try
			{
				String baseref = (String)attr.getAttribute(HTML.Attribute.HREF);
				URL newbase = new URL(baseref);
				base=newbase;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			if (tagattrs.containsKey(tag))
			{
				try
				{
					String value = (String)attr.getAttribute(tagattrs.get(tag));
					URL newurl = new URL(base,value);
					links.add(new Link(newurl,((Integer)taglinktypes.get(tag)).intValue()));
					System.out.println("Found a link to "+newurl.toString());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return
	 */
	public Collection getLinks()
	{
		return links;
	}
}
