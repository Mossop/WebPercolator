/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.actions;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.Action;
import com.blueprintit.webfetch.v1.ConfigurationSet;
import com.blueprintit.webfetch.v1.ElementConfigParser;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class UserAgentAction extends ElementConfigParser implements Action
{
	private String useragent;
	private static Map DEFAULTS = new HashMap();
	
	static
	{
		DEFAULTS.put("Firefox-0.9","Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7) Gecko/20040719 Firefox/0.9");
		DEFAULTS.put("Firefox",DEFAULTS.get("Firefox-0.9"));

		DEFAULTS.put("MSIE-4.0","Mozilla/4.0 (compatible; MSIE 4.0; Windows NT 5.1)");
		DEFAULTS.put("MSIE-5.0","Mozilla/4.0 (compatible; MSIE 5.0; Windows NT 5.1)");
		DEFAULTS.put("MSIE-6.0","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		DEFAULTS.put("MSIE",DEFAULTS.get("MSIE-6.0"));

		DEFAULTS.put("Opera-7.5","Opera/7.51 (Windows NT 5.1; U) [en]");
		DEFAULTS.put("Opera",DEFAULTS.get("Opera-7"));
		
		DEFAULTS.put("Safari-125","Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en) AppleWebKit/125.2 (KHTML, like Gecko) Safari/125.8");
		DEFAULTS.put("Safari",DEFAULTS.get("Safari-125"));
	}
	
	protected boolean parseSubElement(Element element) throws ConfigurationParseException
	{
		return false;
	}

	public void parseElement(Element element) throws ConfigurationParseException
	{
		if (element.hasAttribute("browser"))
		{
			String browser = element.getAttribute("browser");
			if (DEFAULTS.containsKey(browser))
			{
				useragent=(String)DEFAULTS.get(browser);
			}
			else
			{
				throw new ConfigurationParseException("Unknown browser was specified as the user agent");
			}
		}
		else
		{
			useragent=getElementText(element);
			if ((useragent==null)||(useragent.length()==0))
			{
				throw new ConfigurationParseException("When no browser is specified a user agent must be give as the content of the UserAgent tag");
			}
		}
	}
	
	public boolean isAction()
	{
		return true;
	}

	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		env.getDownloadSettings().setUserAgent(useragent);
	}
}
