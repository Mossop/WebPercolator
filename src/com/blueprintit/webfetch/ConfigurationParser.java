package com.blueprintit.webfetch;

import org.w3c.dom.Document;

/**
 * @author Dave
 */
public interface ConfigurationParser
{
	public Configuration parseConfiguration(Document document) throws ConfigurationParseException;
}
