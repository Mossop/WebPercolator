/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch;

import java.io.File;

import org.w3c.dom.Document;

/**
 * @author Dave
 */
public interface ConfigurationParser
{
	public Configuration parseConfiguration(File base, Document document) throws ConfigurationParseException;
}
