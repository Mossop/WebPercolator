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
	public static final int UNSPECIFIED_DOWNLOAD = 0;
	public static final int LINK_DOWNLOAD = 1;
	public static final int IMAGE_DOWNLOAD = 2;
	public static final int FRAME_DOWNLOAD = 3;
	public static final int OBJECT_DOWNLOAD = 4;
	public static final int APPLET_DOWNLOAD = 5;
	
	public HttpMethod getHttpMethod();
	
	public DownloadDetails getDownloadDetails(DownloadQueue queue) throws IOException;
	
	public URL getURL();
	
	public File getLocalFile();
	
	public int getType();
}
