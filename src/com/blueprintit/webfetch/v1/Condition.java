package com.blueprintit.webfetch.v1;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.*;
import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public interface Condition
{
	public void parse(Element element) throws ConfigurationParseException;
	
	public boolean matches(Environment env);
}
