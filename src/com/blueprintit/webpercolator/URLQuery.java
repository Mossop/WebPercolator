/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webpercolator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Dave
 */
public class URLQuery
{
	private String invalid;
	private Map fields;
	
	public URLQuery(String query)
	{
		setQuery(query);
	}
	
	public String get(String key)
	{
		if (fields!=null)
		{
			return (String)fields.get(key);
		}
		else
		{
			return null;
		}
	}
	
	public void set(String key, String value)
	{
		if (fields==null)
		{
			fields = new HashMap();
		}
		fields.put(key,value);
	}
	
	public String getQuery()
	{
		return toString();
	}
	
	public void setQuery(String query)
	{
		if (query==null)
		{
			invalid="";
			fields=null;
			return;
		}
		invalid=query;
		fields=null;
		Map newfields = new HashMap();
		String[] variables = query.split("&|;");
		for (int loop=0; loop<variables.length; loop++)
		{
			String[] parts = variables[loop].split("=");
			if (parts.length!=2)
			{
				break;
			}
			try
			{
				String key = URLDecoder.decode(parts[0],"UTF-8");
				String value = URLDecoder.decode(parts[1],"UTF-8");
				newfields.put(key,value);
			}
			catch (UnsupportedEncodingException e)
			{
			}
		}
		if (variables.length==newfields.size())
		{
			fields=newfields;
		}
	}
	
	public int length()
	{
		return toString().length();
	}
	
	public String toString()
	{
		if (fields==null)
		{
			return invalid;
		}
		else
		{
			if (fields.size()>0)
			{
				try
				{
					StringBuffer query = new StringBuffer();
					Iterator loop = fields.entrySet().iterator();
					while (loop.hasNext())
					{
						Map.Entry entry = (Entry)loop.next();
						query.append(URLEncoder.encode((String)entry.getKey(),"UTF-8"));
						query.append("=");
						query.append(URLEncoder.encode((String)entry.getValue(),"UTF-8"));					
						query.append("&");
					}
					query.delete(query.length()-1,query.length());
					return query.toString();
				}
				catch (UnsupportedEncodingException e)
				{
					return "";
				}
			}
			else
			{
				return "";
			}
		}
	}
}
