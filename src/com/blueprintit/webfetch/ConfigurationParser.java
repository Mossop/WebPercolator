package com.blueprintit.webfetch;

import org.w3c.dom.Element;

/**
 * @author Dave
 */
public interface ConfigurationParser
{
	public Configuration parseConfiguration(Element document);
}
