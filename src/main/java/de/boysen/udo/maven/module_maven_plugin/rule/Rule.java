package de.boysen.udo.maven.module_maven_plugin.rule;

import java.util.List;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class for a special rule for specified modules.
 * The rule may treat module and / or 3rd party relations. 
 */
@Data
@NoArgsConstructor
public class Rule
{
	// Parameter initialized by pom.xml
	private String					module;
	private String					allowModule;
	private String					disallowModule;
	private String					allow3rdParty;
	private String					disallow3rdParty;

	// Initialized by MOJO
	private List<List<String>>	moduleCheckArray					= null;
	private List<List<String>>	allowModuleCheckArray			= null;
	private List<List<String>>	disallowModuleCheckArray		= null;
	private List<Pattern>		allowThirdPartyPatternList		= null;
	private List<Pattern>		disallowThirdPartyPatternList	= null;

	public Rule(final String module)
	{
		this.module = module;
	}

	public Rule(final String module, final String allowModule, final String disallowModule, final String allow3rdParty, final String disallow3rdParty)
	{
		this.module = module;
		this.allowModule = allowModule;
		this.disallowModule = disallowModule;
		this.allow3rdParty = allow3rdParty;
		this.disallow3rdParty = disallow3rdParty;
	}
}
