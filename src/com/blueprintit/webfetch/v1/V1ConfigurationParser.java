/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.io.File;

import org.w3c.dom.Document;
import com.blueprintit.webfetch.Configuration;
import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.ConfigurationParser;

/**
 * @author Dave
 */
public class V1ConfigurationParser implements ConfigurationParser
{
	public Configuration parseConfiguration(File base, Document document) throws ConfigurationParseException
	{
		return new V1Configuration(base,document.getDocumentElement());
	}
}
