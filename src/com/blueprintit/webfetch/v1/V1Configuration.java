/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1;

import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;

import com.blueprintit.webfetch.Configuration;
import com.blueprintit.webfetch.ConfigurationParseException;
import com.blueprintit.webfetch.Environment;

/**
 * @author Dave
 */
public class V1Configuration extends ConfigurationSet implements Configuration
{
	public V1Configuration(Element element) throws ConfigurationParseException
	{
		super(element);
	}
	
	public Collection getURLs()
	{
		return new ArrayList();
	}

	public void applyConfiguration(Environment env)
	{
		ScriptingEnvironment scope = new ScriptingEnvironment(env);
		if (matches(scope))
		{
			applyConfigurationSet(scope);
		}
		else
		{
			env.setRejected(true);
		}
		scope.exit();
	}
}
