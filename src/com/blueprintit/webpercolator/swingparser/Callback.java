package com.blueprintit.webpercolator.swingparser;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.HTML;

import com.blueprintit.webpercolator.Link;
import com.blueprintit.webpercolator.LinkType;

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
		specifyLinkType(HTML.Tag.A,     HTML.Attribute.HREF, LinkType.LINK);
		specifyLinkType(HTML.Tag.IMG,   HTML.Attribute.SRC,  LinkType.IMAGE);
		specifyLinkType(HTML.Tag.FRAME, HTML.Attribute.SRC,  LinkType.FRAME);
		specifyLinkType(HTML.Tag.LINK,  HTML.Attribute.HREF, LinkType.UNSPECIFIED);
		specifyLinkType(HTML.Tag.INPUT, HTML.Attribute.SRC,  LinkType.IMAGE);
		specifyLinkType(HTML.Tag.AREA,  HTML.Attribute.HREF, LinkType.LINK);
	}
	
	public void specifyLinkType(HTML.Tag tag, HTML.Attribute attr, LinkType type)
	{
		tagattrs.put(tag,attr);
		taglinktypes.put(tag,type);
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
				if (baseref!=null)
				{
					URL newbase = new URL(baseref);
					base=newbase;
				}
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
						if (!(value.startsWith("javascript:")||value.startsWith("mailto:")))
						{
							value=value.replaceAll(" ","%20");
							URL newurl = new URL(base,value);
							Link newlink = new Link(newurl,(LinkType)taglinktypes.get(tag));
							if (!links.contains(newlink))
							{
								links.add(newlink);
							}
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
