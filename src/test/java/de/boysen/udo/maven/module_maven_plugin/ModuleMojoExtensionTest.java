package de.boysen.udo.maven.module_maven_plugin;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.maven.plugin.MojoExecutionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import de.boysen.udo.maven.module_maven_plugin.module.Module;
import de.boysen.udo.maven.module_maven_plugin.rule.Rule;

/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtension */
public class ModuleMojoExtensionTest
{
	/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtension#checkParameter() */
	@Test
	public void testCheckParameter()
	{
		final ModuleMojo mojo = new ModuleMojo();
		mojo.setModules(new ArrayList<Module>()); // In operation checked by Maven
		mojo.setRules(new ArrayList<Rule>()); // In operation checked by Maven
		final ModuleMojoExtension extension = new ModuleMojoExtension(mojo);
		Throwable thrown = Assertions.catchThrowable(() ->
		{
			extension.checkParameter();
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		mojo.setDefaultAllow(ModuleMojo.DEFAULTALLOW_ALL);
		thrown = Assertions.catchThrowable(() ->
		{
			extension.checkParameter();
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		mojo.setDefaultAllow(ModuleMojo.DEFAULTALLOW_NONE);
		thrown = Assertions.catchThrowable(() ->
		{
			extension.checkParameter();
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		mojo.setModules(Arrays.asList(new Module()));
		mojo.setRules(new ArrayList<Rule>());
		thrown = Assertions.catchThrowable(() ->
		{
			extension.checkParameter();
		});
		Assertions.assertThat(thrown).isInstanceOf(MojoExecutionException.class);

		mojo.setModules(Arrays.asList(new Module("something", null)));
		mojo.setRules(new ArrayList<Rule>());
		thrown = Assertions.catchThrowable(() ->
		{
			extension.checkParameter();
		});
		Assertions.assertThat(thrown).isInstanceOf(MojoExecutionException.class);

		mojo.setModules(new ArrayList<Module>());
		mojo.setRules(Arrays.asList(new Rule()));
		thrown = Assertions.catchThrowable(() ->
		{
			extension.checkParameter();
		});
		Assertions.assertThat(thrown).isInstanceOf(MojoExecutionException.class);

		mojo.setDefaultAllow("SomethingWrong");
		thrown = Assertions.catchThrowable(() ->
		{
			extension.checkParameter();
		});
		Assertions.assertThat(thrown).isInstanceOf(MojoExecutionException.class);
	}
}
