package com.blueprintit.webfetch.v1;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public interface Action
{
	public void parseConfig(Element element) throws ConfigurationParseException;
	
	public void execute(ConfigurationSet config, ScriptingEnvironment env);
}
