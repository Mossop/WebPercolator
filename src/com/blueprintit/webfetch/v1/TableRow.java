/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.ConfigurationParseException;

/**
 * @author Dave
 */
public class TableRow extends Block
{	
	public TableRow()
	{
		super(true);
	}
	
	public TableRow(Element element) throws ConfigurationParseException
	{
		super(element);
	}
}
