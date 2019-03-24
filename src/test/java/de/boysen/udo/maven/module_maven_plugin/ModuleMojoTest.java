package de.boysen.udo.maven.module_maven_plugin;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.plugin.MojoExecutionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import de.boysen.udo.maven.module_maven_plugin.module.Module;
import de.boysen.udo.maven.module_maven_plugin.module.ModuleExtension;
import de.boysen.udo.maven.module_maven_plugin.rule.Rule;
import de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension;

/** @link de.boysen.udo.maven.module_maven_plugin.ModuleMojo */
public class ModuleMojoTest
{
	/** @link de.boysen.udo.maven.module_maven_plugin.ModuleMojo#execute() */
	@Test
	public void testExecute() throws MojoExecutionException
	{
		final ModuleMojo mojo = new ModuleMojo();
		final Module testModule = new Module("module", "*module*");
		(new ModuleExtension(testModule)).initModule();
		mojo.setModules(Arrays.asList(testModule));
		final Rule testRule = new Rule("module");
		(new RuleExtension(testRule)).initRule();
		mojo.setRules(Arrays.asList(testRule));

		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(classLoader.getResource("test-folder").getFile());
		mojo.setCompileSourceRoots(Arrays.asList(file.getAbsolutePath()));

		Assertions.assertThat(mojo.getCompileSourceRoots().get(0)).endsWith("test-folder");

		Throwable thrown = Assertions.catchThrowable(() ->
		{
			mojo.execute();
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		Assertions.assertThat(mojo.getDefaultAllow3rdPartyPatternList()).isNull();
		Assertions.assertThat(mojo.getDefaultDisallow3rdPartyPatternList()).isNull();
		Assertions.assertThat(mojo.getDefaultAllow3rdParty()).isNull();
		Assertions.assertThat(mojo.getDefaultDisallow3rdParty()).isNull();

		mojo.setCompileSourceRoots(Arrays.asList("unlikelyToExist"));

		thrown = Assertions.catchThrowable(() ->
		{
			mojo.execute();
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();
	}
}
