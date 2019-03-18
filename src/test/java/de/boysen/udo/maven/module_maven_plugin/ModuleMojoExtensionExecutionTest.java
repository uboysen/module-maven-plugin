package de.boysen.udo.maven.module_maven_plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import de.boysen.udo.maven.module_maven_plugin.model.Import;
import de.boysen.udo.maven.module_maven_plugin.model.JavaFile;
import de.boysen.udo.maven.module_maven_plugin.module.Module;
import de.boysen.udo.maven.module_maven_plugin.module.ModuleExtension;
import de.boysen.udo.maven.module_maven_plugin.rule.Rule;
import de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension;

/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtensionExecution */
public class ModuleMojoExtensionExecutionTest
{
	/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtensionExecution#analysePath(java.io.File) */
	@Test
	public void testAnalysePath()
	{
		final ModuleMojo mojo = new ModuleMojo();
		final Module testModule = new Module("module", "*module*");
		(new ModuleExtension(testModule)).initModule();
		mojo.setModules(Arrays.asList(testModule));
		final Rule testRule = new Rule("module");
		(new RuleExtension(testRule)).initRule();
		mojo.setRules(Arrays.asList(testRule));
		final ModuleMojoExtensionExecution extensionExecution = new ModuleMojoExtensionExecution(mojo);

		Throwable thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.analysePath(null);
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(classLoader.getResource("test-folder").getFile());
		thrown = Assertions.catchThrowable(() ->
		{
			System.out.println("name: " + file.getName() + " abs: " + file.getAbsolutePath());
			extensionExecution.analysePath(file);
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		File mockedFile = Mockito.mock(File.class);
		Mockito.when(mockedFile.isDirectory()).thenReturn(true);
		Mockito.when(mockedFile.listFiles()).thenReturn(null);
		thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.analysePath(mockedFile);
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();
	}

	/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtensionExecution#readJavaFile(String, String) */
	@Test
	public void testReadJavaFile()
	{
		final ModuleMojo mojo = new ModuleMojo();
		final Module testModule = new Module("module", "*module*");
		(new ModuleExtension(testModule)).initModule();
		mojo.setModules(Arrays.asList(testModule));
		final Rule testRule = new Rule("module");
		(new RuleExtension(testRule)).initRule();
		mojo.setRules(Arrays.asList(testRule));
		final ModuleMojoExtensionExecution extensionExecution = new ModuleMojoExtensionExecution(mojo);

		final ClassLoader classLoader = getClass().getClassLoader();
		final File file = new File(classLoader.getResource("test-folder/example.java").getFile());
		Throwable thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.readJavaFile(file.getName(), file.getAbsolutePath());
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.readJavaFile("unlikelyToExist", "unlikelyToExist");
		});
		Assertions.assertThat(thrown).isInstanceOf(MojoExecutionException.class);
	}

	/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtensionExecution#createImport(String) */
	@Test
	public void testCreateImport()
	{
		final ModuleMojo mojo = new ModuleMojo();
		final ModuleMojoExtensionExecution extensionExecution = new ModuleMojoExtensionExecution(mojo);

		Assertions.assertThat(extensionExecution.createImport("import something;").getImportStr()).isEqualTo("something");
		Assertions.assertThat(extensionExecution.createImport("import /* comment */ something;").getImportStr()).isEqualTo("something");
		Assertions.assertThat(extensionExecution.createImport("import static something;").isStatic()).isTrue();
		Assertions.assertThat(extensionExecution.createImport("import something-without-semicolon").getImportStr()).isEqualTo("something-without-semicolon");

	}

	/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtensionExecution#analyseJavaFile(de.boysen.udo.maven.module_maven_plugin.model.JavaFile) */
	@ParameterizedTest
	@MethodSource("testAnalyseJavaFileParams")
	public void testAnalyseJavaFile(final JavaFile javaFile, final boolean allowAsteriks, final boolean allowStatic)
	{
		final ModuleMojo mojo = new ModuleMojo();
		mojo.setAllowImportsWithAsterisk(allowAsteriks);
		mojo.setAllowStaticImports(allowStatic);
		final Module testModule = new Module("module", "*module*");
		(new ModuleExtension(testModule)).initModule();
		mojo.setModules(Arrays.asList(testModule));
		final Rule testRule = new Rule("module");
		(new RuleExtension(testRule)).initRule();
		mojo.setRules(Arrays.asList(testRule));
		final ModuleMojoExtensionExecution extensionExecution = new ModuleMojoExtensionExecution(mojo);

		Throwable thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.analyseJavaFile(javaFile);
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testAnalyseJavaFileParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(new JavaFile(Arrays.asList(new Module()), Arrays.asList(new Import("something", false))), true, true),
				Arguments.of(new JavaFile(Arrays.asList(new Module()), Arrays.asList(new Import("something*", false))), true, true),
				Arguments.of(new JavaFile(Arrays.asList(new Module()), Arrays.asList(new Import("something*", false))), false, true),
				Arguments.of(new JavaFile(Arrays.asList(new Module()), Arrays.asList(new Import("something", true))), true, true),
				Arguments.of(new JavaFile(Arrays.asList(new Module()), Arrays.asList(new Import("something", true))), true, false)); 
		// @formatter:on
	}

	/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtensionExecution#checkModuleImport(Import, java.util.Set, String) */
	@ParameterizedTest
	@MethodSource("testCheckModuleImportParams")
	public void testCheckModuleImport(final boolean positiveTest, final List<Rule> rules, final String defaultAllow, final Import imp)
	{
		final ModuleMojo mojo = new ModuleMojo();
		mojo.setDefaultAllow(defaultAllow);
		final Module testModule = new Module("module", "*module*");
		(new ModuleExtension(testModule)).initModule();
		mojo.setModules(Arrays.asList(testModule));
		for (Rule rule : rules)
		{
			(new RuleExtension(rule)).initRule();
		}
		mojo.setRules(rules);
		final ModuleMojoExtensionExecution extensionExecution = new ModuleMojoExtensionExecution(mojo);

		Throwable thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.checkModuleImport(imp, new HashSet<String>(Arrays.asList("module")), "test");
		});
		if (positiveTest)
		{
			Assertions.assertThat(thrown).doesNotThrowAnyException();
		} else
		{
			Assertions.assertThat(thrown).isInstanceOf(MojoExecutionException.class);
		}
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testCheckModuleImportParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(true, new ArrayList<Rule>(), ModuleMojo.DEFAULTALLOW_ALL, new Import()),
				Arguments.of(true, new ArrayList<Rule>(), ModuleMojo.DEFAULTALLOW_NONE, new Import()),
				Arguments.of(true, new ArrayList<Rule>(), ModuleMojo.DEFAULTALLOW_OWN, new Import()),
				
				Arguments.of(true, Arrays.asList(new Rule("module", "module", null, null, null)), ModuleMojo.DEFAULTALLOW_OWN, new Import(Arrays.asList(new Module("module")))),
				Arguments.of(true, Arrays.asList(new Rule("module", null, "module", null, null)), ModuleMojo.DEFAULTALLOW_OWN, new Import(Arrays.asList(new Module("module")))),
				Arguments.of(true, Arrays.asList(new Rule("something", null, "module", null, null)), ModuleMojo.DEFAULTALLOW_OWN, new Import(Arrays.asList(new Module("module")))),
				
				Arguments.of(false, new ArrayList<Rule>(), "something", new Import())); 
		// @formatter:on
	}

	/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtensionExecution#check3rdPartyImport(Import, java.util.Set, String) */
	@ParameterizedTest
	@MethodSource("testCheck3rdPartyImportParams")
	public void testCheck3rdPartyImport(final String defaultAllow, final String defaultDisallow, final List<Rule> rules, final Import imp)
	{
		final ModuleMojo mojo = new ModuleMojo();
		mojo.setDefaultAllow3rdParty(defaultAllow);
		mojo.setDefaultDisallow3rdParty(defaultDisallow);
		final Module testModule = new Module("module", "*module*");
		(new ModuleExtension(testModule)).initModule();
		mojo.setModules(Arrays.asList(testModule));
		for (Rule rule : rules)
		{
			(new RuleExtension(rule)).initRule();
		}
		mojo.setRules(rules);
		(new ModuleMojoExtension(mojo)).initMojo();

		final ModuleMojoExtensionExecution extensionExecution = new ModuleMojoExtensionExecution(mojo);

		Throwable thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.check3rdPartyImport(imp, new HashSet<String>(Arrays.asList("module")), "test");
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testCheck3rdPartyImportParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of("*something*", null, new ArrayList<Rule>(), new Import("something")),
				Arguments.of(null, "*something*", new ArrayList<Rule>(), new Import("something")),
				
				Arguments.of(null, null, Arrays.asList(new Rule("module", null, null, "*something*", null)), new Import("something")),
				Arguments.of(null, null, Arrays.asList(new Rule("module", null, null, null, "*something*")), new Import("something"))); 
		// @formatter:on
	}

	/** @see de.boysen.udo.maven.module_maven_plugin.ModuleMojoExtensionExecution#showMessage(String) */
	@Test
	public void testShowMessage()
	{
		final ModuleMojo mojo = new ModuleMojo();
		final ModuleMojoExtensionExecution extensionExecution = new ModuleMojoExtensionExecution(mojo);

		Throwable thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.showMessage("Something");
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.showMessage(null);
		});
		Assertions.assertThat(thrown).doesNotThrowAnyException();

		mojo.setStrict(true);
		thrown = Assertions.catchThrowable(() ->
		{
			extensionExecution.showMessage("Something else");
		});
		Assertions.assertThat(thrown).isInstanceOf(MojoExecutionException.class);
	}
}
