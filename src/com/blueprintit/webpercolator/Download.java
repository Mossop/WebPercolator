package com.blueprintit.webpercolator;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.httpclient.HttpMethod;

/**
 * @author Dave
 */
public interface Download
{
	public HttpMethod getHttpMethod();
	
	public DownloadDetails getDownloadDetails(DownloadQueue queue) throws IOException;
	
	public URL getURL();
	
	public File getLocalFile();
}
