/*
 * Created on 27-Apr-2004
 */
package com.blueprintit.webfetch;

/**
 * @author Dave
 */
public class ConfigurationParseException extends Exception
{
	public ConfigurationParseException()
	{
		super();
	}
	
	public ConfigurationParseException(String message)
	{
		super(message);
	}
	
	public ConfigurationParseException(Throwable error)
	{
		super(error);
	}
	
	public ConfigurationParseException(String message, Throwable error)
	{
		super(message,error);
	}
}
