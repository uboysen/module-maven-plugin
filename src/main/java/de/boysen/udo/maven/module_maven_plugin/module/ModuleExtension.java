package de.boysen.udo.maven.module_maven_plugin.module;

import de.boysen.udo.maven.module_maven_plugin.util.Util;

/**
 * An extension class for a special module.
 */
public class ModuleExtension
{
	private final Module module;

	/**
	 * For an extension, this should be the only constructor.
	 * 
	 * @param module The module to extend.
	 */
	public ModuleExtension(final Module module)
	{
		this.module = module;
	}

	/**
	 * Initializes internal parameter of the mojo.
	 */
	public void initModule()
	{
		module.setPatternList(Util.createPatternList(module.getPackages()));
	}
}
