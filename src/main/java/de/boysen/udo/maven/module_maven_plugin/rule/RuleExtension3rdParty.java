package de.boysen.udo.maven.module_maven_plugin.rule;

import de.boysen.udo.maven.module_maven_plugin.model.Import;
import de.boysen.udo.maven.module_maven_plugin.util.Util;

/**
 * A extension class for a special rule treating 3rd party relations.
 */
public class RuleExtension3rdParty
{
	private final Rule rule;

	// For an extension, this should be the only constructor
	public RuleExtension3rdParty(final Rule rule)
	{
		this.rule = rule;
	}

	/**
	 * Checks if a given Import (POJO) is allowed by the rules for 3rd party relations.
	 */
	public boolean is3rdPartyImportAllowed(final Import imp)
	{
		boolean result = false;

		if (imp != null && rule.getAllow3rdParty() != null && Util.somethingInPatternListMatches(imp.getImportStr(), rule.getAllowThirdPartyPatternList()))
		{
			result = true;
		}

		return result;
	}

	/**
	 * Checks if a given Import (POJO) is disallowed by the rules for 3rd party relations.
	 * The rules for disallow are separated from rules for allow.
	 */
	public boolean is3rdPartyImportDisallowed(final Import imp)
	{
		boolean result = false;

		if (imp != null && rule.getDisallow3rdParty() != null && Util.somethingInPatternListMatches(imp.getImportStr(), rule.getDisallowThirdPartyPatternList()))
		{
			result = true;
		}

		return result;
	}
}
