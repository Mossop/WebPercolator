package com.blueprintit.jget;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;

/**
 * @author Dave
 */
public class BetterParser extends Parser
{
	/**
	 * @see org.apache.commons.cli.Parser#flatten(org.apache.commons.cli.Options, java.lang.String[], boolean)
	 */
	protected String[] flatten(Options opts, String[] arguments, boolean stopAtNonOption)
	{
		List tokens = new LinkedList();
		int loop=0;
		while (loop<arguments.length)
		{
			if (arguments[loop].startsWith("-"))
			{
				if (arguments[loop].indexOf("=")>0)
				{
					String argname=arguments[loop].substring(0,arguments[loop].indexOf("="));
					if (opts.hasOption(argname))
					{
						tokens.add(argname);
						tokens.add(arguments[loop].substring(arguments[loop].indexOf("=")+1));
					}
					else if (stopAtNonOption)
					{
						while (loop<arguments.length)
						{
							tokens.add(arguments[loop]);
							loop++;
						}
					}
				}
				else
				{
					if (opts.hasOption(arguments[loop]))
					{
						tokens.add(arguments[loop]);
					}
					else if (stopAtNonOption)
					{
						while (loop<arguments.length)
						{
							tokens.add(arguments[loop]);
							loop++;
						}
					}
				}
			}
			else
			{
				tokens.add(arguments[loop]);
			}
			loop++;
		}
		return (String[])tokens.toArray( new String[] {} );
	}
}
