package de.boysen.udo.maven.module_maven_plugin.rule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.boysen.udo.maven.module_maven_plugin.model.Import;
import de.boysen.udo.maven.module_maven_plugin.module.Module;

/**
 * A extension class for a special rule treating module relations.
 */
public class RuleExtensionModule
{
	private final Rule rule;

	// For an extension, this should be the only constructor
	public RuleExtensionModule(final Rule rule)
	{
		this.rule = rule;
	}

	/**
	 * Checks if a given Import (POJO) is allowed by the rules for module relations.
	 */
	public boolean isModuleImportAllowed(final Import imp)
	{
		boolean result = false;

		if (rule.getAllowModule() != null && imp != null)
		{
			Set<String> importModuleNameSet = new HashSet<String>();
			for (Module module : imp.getModules())
			{
				importModuleNameSet.add(module.getName());
			}

			for (List<String> subList : rule.getAllowModuleCheckArray())
			{
				boolean subListResult = true;
				for (String name : subList)
				{
					if (!importModuleNameSet.contains(name))
					{
						subListResult = false;
					}
				}

				if (subListResult)
				{
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * Checks if a given Import (POJO) is disallowed by the rules for module relations.
	 * The rules for disallow are separated from rules for allow.
	 */
	public boolean isModuleImportDisallowed(final Import imp)
	{
		boolean result = false;

		if (rule.getDisallowModule() != null && imp != null)
		{
			Set<String> importModuleNameSet = new HashSet<String>();
			for (Module module : imp.getModules())
			{
				importModuleNameSet.add(module.getName());
			}

			for (List<String> subList : rule.getDisallowModuleCheckArray())
			{
				boolean subListResult = true;
				for (String name : subList)
				{
					if (!importModuleNameSet.contains(name))
					{
						subListResult = false;
					}
				}

				if (subListResult)
				{
					result = true;
				}
			}
		}

		return result;
	}
}
