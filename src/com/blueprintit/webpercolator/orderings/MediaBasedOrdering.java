/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator.orderings;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.blueprintit.webpercolator.Download;
import com.blueprintit.webpercolator.DownloadDetails;
import com.blueprintit.webpercolator.DownloadQueue;

public abstract class MediaBasedOrdering implements Comparator
{
	private static Map extensionTypes;
	private static final String DEFAULT_TYPE = "text/html";
	private static Map typeCache;
		
	static
	{
		typeCache = new HashMap();

		extensionTypes = new HashMap();
		
		extensionTypes.put("gif","image/gif");
		extensionTypes.put("jpg","image/jpg");
		extensionTypes.put("png","image/png");
		
		extensionTypes.put("css","text/css");
		extensionTypes.put("css","text/js");
		
		extensionTypes.put("html","text/html");
		extensionTypes.put("htm","text/html");
		extensionTypes.put("php","text/html");
		extensionTypes.put("asp","text/html");
		extensionTypes.put("jsp","text/html");
		extensionTypes.put("htm","text/html");
		
		extensionTypes.put("zip","application/zip");
	}
	
	private boolean useMimeType = false;
	private boolean fallbackMimeType = false;
	private DownloadQueue queue;

	public static class ImagePriority extends MediaBasedOrdering
	{
		public ImagePriority(DownloadQueue queue)
		{
			super(queue);
		}
		
		protected int typeCompare(String type1, String type2)
		{
			if (type1.equals(type2))
			{
				return 0;
			}
			else if (type1.equals("image"))
			{
				return -1;
			}
			else if (type2.equals("image"))
			{
				return 1;
			}
			return 0;
		}
	}
	
	public MediaBasedOrdering(DownloadQueue queue)
	{
		this.queue=queue;
	}
	
	private String findMimeType(Download download)
	{
		String type = null;
		if (MediaBasedOrdering.typeCache.containsKey(download))
		{
			return (String)MediaBasedOrdering.typeCache.get(download);
		}
		if (!useMimeType)
		{
			URL url = download.getURL();
			String path = url.getPath();
			int pos = path.lastIndexOf(".");
			if (pos>=0)
			{
				path=path.substring(pos+1);
				type=(String)MediaBasedOrdering.extensionTypes.get(path);
			}
		}
		if ((type==null)&&((useMimeType)||(fallbackMimeType)))
		{
			try
			{
				DownloadDetails details = download.getDownloadDetails(queue);
				type=details.getContentType();
			}
			catch (IOException e)
			{
			}
		}
		if (type==null)
		{
			type=MediaBasedOrdering.DEFAULT_TYPE;
		}
		MediaBasedOrdering.typeCache.put(download,type);
		return type;
	}
	
	public String getBaseType(String mimetype)
	{
		int pos = mimetype.indexOf("/");
		if (pos>=0)
		{
			return mimetype.substring(0,pos);
		}
		else
		{
			return mimetype;
		}
	}
	
	protected abstract int typeCompare(String type1, String type2);
	
	public int compare(Object obj1, Object obj2)
	{
		Download d1 = (Download)obj1;
		Download d2 = (Download)obj2;
		
		String type1 = getBaseType(findMimeType(d1));
		String type2 = getBaseType(findMimeType(d2));
		
		return typeCompare(type1,type2);
	}
}
