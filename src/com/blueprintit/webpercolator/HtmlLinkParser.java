package com.blueprintit.webpercolator;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;

/**
 * @author Dave
 */
public interface HtmlLinkParser
{
	public Collection parseLinks(URL base, String html);
	
	public Collection parseLinks(URL base, Reader in) throws IOException;
}
