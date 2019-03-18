package de.boysen.udo.maven.module_maven_plugin;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import de.boysen.udo.maven.module_maven_plugin.module.Module;
import de.boysen.udo.maven.module_maven_plugin.rule.Rule;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Mojo (Entrypoint) for the Module Maven Plugin.
 * This Plugin analysis relations of defined modules.
 */
@Mojo(name = "check", defaultPhase = LifecyclePhase.VALIDATE)
@Data
@EqualsAndHashCode(callSuper = false)
public class ModuleMojo extends AbstractMojo
{
	public static final String	DEFAULTALLOW_OWN							= "OWN";
	public static final String	DEFAULTALLOW_NONE							= "NONE";
	public static final String	DEFAULTALLOW_ALL							= "ALL";

	@Parameter(readonly = true, required = false)
	private Boolean				strict										= false;

	@Parameter(readonly = true, required = false)
	private Boolean				allowImportsWithAsterisk				= true;

	@Parameter(readonly = true, required = false)
	private Boolean				allowStaticImports						= true;

	@Parameter(readonly = true, required = false)
	private String					defaultAllow								= DEFAULTALLOW_OWN;

	@Parameter(readonly = true, required = false)
	private String					defaultAllow3rdParty;

	@Parameter(readonly = true, required = false)
	private String					defaultDisallow3rdParty;

	@Parameter(readonly = true, required = true)
	private List<Module>			modules;

	@Parameter(readonly = true, required = true)
	private List<Rule>			rules;

	@Parameter(defaultValue = "${project.compileSourceRoots}", readonly = true, required = true)
	private List<String>			compileSourceRoots;

	private List<Pattern>		defaultAllow3rdPartyPatternList		= null;
	private List<Pattern>		defaultDisallow3rdPartyPatternList	= null;

	/* Executing the Module Maven Plugin */
	@Override
	public void execute() throws MojoExecutionException
	{
		final ModuleMojoExtension extension = new ModuleMojoExtension(this);
		extension.checkParameter();
		extension.initMojo();

		for (String path : compileSourceRoots)
		{
			final File sourcePath = new File(path);
			if (sourcePath.exists())
			{
				final ModuleMojoExtensionExecution execution = new ModuleMojoExtensionExecution(this);
				execution.analysePath(sourcePath);
			}
		}
	}
}
