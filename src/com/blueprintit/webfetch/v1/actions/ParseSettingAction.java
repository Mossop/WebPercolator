/*
 * $Author$
 * $RCSfile$
 * $Date$
 * $Revision$
 */
package com.blueprintit.webfetch.v1.actions;

import com.blueprintit.webfetch.v1.BooleanSettingAction;
import com.blueprintit.webfetch.v1.ConfigurationSet;
import com.blueprintit.webfetch.v1.ScriptingEnvironment;

/**
 * @author Dave
 */
public class ParseSettingAction extends BooleanSettingAction
{
	public void execute(ConfigurationSet config, ScriptingEnvironment env)
	{
		env.getDownloadSettings().setParse(setting);
	}
}
