package com.blueprintit.webpercolator.swingparser;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
		links = new HashSet();
		taglinktypes = new HashMap();
		specifyLinkType(HTML.Tag.A,     HTML.Attribute.HREF, Download.LINK_DOWNLOAD);
		specifyLinkType(HTML.Tag.IMG,   HTML.Attribute.SRC,  Download.IMAGE_DOWNLOAD);
		specifyLinkType(HTML.Tag.FRAME, HTML.Attribute.SRC,  Download.FRAME_DOWNLOAD);
		specifyLinkType(HTML.Tag.LINK,  HTML.Attribute.HREF, Download.UNSPECIFIED_DOWNLOAD);
		specifyLinkType(HTML.Tag.INPUT, HTML.Attribute.SRC,  Download.IMAGE_DOWNLOAD);
	}
	
	public void specifyLinkType(HTML.Tag tag, HTML.Attribute attr, int type)
	{
		tagattrs.put(tag,attr);
		taglinktypes.put(tag,new Integer(type));
	}
	
	public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attr, int pos)
	{
		handleStartTag(tag,attr,pos);
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
					if (value!=null)
					{
						URL newurl = new URL(base,value);
						Link newlink = new Link(newurl,((Integer)taglinktypes.get(tag)).intValue());
						if (!links.contains(newlink))
						{
							links.add(newlink);
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return Returns the links retrieved
	 */
	public Collection getLinks()
	{
		return links;
	}
}
