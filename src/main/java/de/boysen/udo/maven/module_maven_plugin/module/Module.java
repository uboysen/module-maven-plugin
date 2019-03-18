package de.boysen.udo.maven.module_maven_plugin.module;

import java.util.List;
import java.util.regex.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A class for a module.
 */
@Data
@NoArgsConstructor
public class Module
{
	// Parameter initialized by pom.xml
	private String				name;
	private String				packages;

	// Initialized by MOJO
	private List<Pattern>	patternList	= null;

	public Module(final String name)
	{
		this.name = name;
	}

	public Module(final String name, final String packages)
	{
		this.name = name;
		this.packages = packages;
	}
}
