/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.v1.AbstractAction;
import com.blueprintit.webfetch.v1.Condition;
import com.blueprintit.webfetch.v1.ConfigurationSet;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class SetAction extends AbstractAction implements Condition
{
	private String sourceText;
	private String source;
	private Pattern regex;
	private String regexReplace;
	private String target;
	private boolean any = false;
	private boolean all = true;
	private boolean start = false;
	private static Log log = LogFactory.getLog(SetAction.class);
	
	public boolean isCondition()
	{
		return regex!=null;
	}

	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		String text = getText(env);
		if ((regexReplace!=null)&&(regex!=null))
		{
			Matcher matcher = regex.matcher(text);
			if (matcher.find())
			{
				text=text.substring(matcher.start(),matcher.end());
				text=regex.matcher(text).replaceAll(regexReplace);
			}
			else
			{
				text="";
			}
		}
		String script = target+"=\""+text+"\"";
		env.execute(script,type);
	}

	private String getText(ScriptingEnvironment env)
	{
		if (source!=null)
		{
			return env.evaluateAsString(source,type);
		}
		else
		{
			return sourceText;
		}
	}
	
	public boolean matches(ScriptingEnvironment env)
	{
		if (regex!=null)
		{
			return regex.matcher(getText(env)).find();
		}
		else
		{
			return true;
		}
	}

	public void parseElement(Element element) throws ConfigurationParseException
	{
		super.parseElement(element);
		if (element.hasAttribute("match"))
		{
			any=false;
			if (element.getAttribute("match").equals("any"))
			{
				any=true;
			}
			else if (element.getAttribute("match").equals("all"))
			{
				all=true;
			}
			else if (element.getAttribute("match").equals("start"))
			{
				start=true;
			}
			else
			{
				throw new ConfigurationParseException("Invalid setting for match");
			}
		}
		if (element.hasAttribute("regex"))
		{
			int flags=0;
			if ((element.hasAttribute("insensitive"))&&(Boolean.valueOf(element.getAttribute("insensitive")).booleanValue()))
			{
				flags=flags|Pattern.CASE_INSENSITIVE;
			}
			regex = Pattern.compile(element.getAttribute("regex"),flags);
		}
		if (element.hasAttribute("replacement"))
		{
			regexReplace = element.getAttribute("replacement");
		}
		if (element.hasAttribute("source"))
		{
			source = element.getAttribute("source");
		}
		if (element.hasAttribute("target"))
		{
			target = element.getAttribute("target");
		}
		if (element.hasAttribute("sourceText"))
		{
			sourceText = element.getAttribute("sourceText");
		}
		
		if ((regexReplace!=null)&&(regex==null))
		{
			log.warn("There needs to be a regex specified for a replacemernt to work");
		}
		if ((source==null)&&(sourceText==null))
		{
			throw new ConfigurationParseException("There must be either a source or a sourceText specified");
		}
		if (target==null)
		{
			throw new ConfigurationParseException("There must be a target specified");
		}
	}
}
