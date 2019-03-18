package de.boysen.udo.maven.module_maven_plugin;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import de.boysen.udo.maven.module_maven_plugin.module.Module;
import de.boysen.udo.maven.module_maven_plugin.module.ModuleExtension;
import de.boysen.udo.maven.module_maven_plugin.rule.Rule;
import de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension;
import de.boysen.udo.maven.module_maven_plugin.util.Util;

/**
 * An extension class for a special module mojo.
 */
public class ModuleMojoExtension
{
	private final ModuleMojo mojo;

	// For an extension, this should be the only constructor
	public ModuleMojoExtension(final ModuleMojo mojo)
	{
		this.mojo = mojo;
	}

	/**
	 * Checks if all parameter are valid.
	 */
	public void checkParameter() throws MojoExecutionException
	{
		if (!mojo.getDefaultAllow().equalsIgnoreCase(ModuleMojo.DEFAULTALLOW_ALL) && !mojo.getDefaultAllow().equalsIgnoreCase(ModuleMojo.DEFAULTALLOW_NONE)
				&& !mojo.getDefaultAllow().equalsIgnoreCase(ModuleMojo.DEFAULTALLOW_OWN))
		{
			throw new MojoExecutionException("'" + mojo.getDefaultAllow() + "' is no valid parameter for <defaultAllow>!");
		}

		for (Module module : mojo.getModules())
		{
			if (StringUtils.isEmpty(module.getName()))
			{
				throw new MojoExecutionException("Module must have a name!");
			}
			if (StringUtils.isEmpty(module.getPackages()))
			{
				throw new MojoExecutionException("Module must have packages!");
			}
		}

		for (Rule rule : mojo.getRules())
		{
			if (StringUtils.isEmpty(rule.getModule()))
			{
				throw new MojoExecutionException("Rule must have a module!");
			}
		}
	}

	/**
	 * Initialize the module mojo.
	 */
	public void initMojo()
	{
		// Initialize Module and Rules from here, cause parameter from pom.xml are set after a constructor call.
		for (Module module : mojo.getModules())
		{
			(new ModuleExtension(module)).initModule();
		}
		for (Rule rule : mojo.getRules())
		{
			(new RuleExtension(rule)).initRule();
		}

		mojo.setDefaultAllow3rdPartyPatternList(Util.createPatternList(mojo.getDefaultAllow3rdParty()));
		mojo.setDefaultDisallow3rdPartyPatternList(Util.createPatternList(mojo.getDefaultDisallow3rdParty()));
	}
}
