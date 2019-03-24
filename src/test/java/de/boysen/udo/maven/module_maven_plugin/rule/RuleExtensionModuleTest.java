package de.boysen.udo.maven.module_maven_plugin.rule;

import java.util.Arrays;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.boysen.udo.maven.module_maven_plugin.model.Import;
import de.boysen.udo.maven.module_maven_plugin.module.Module;

/** @link de.boysen.udo.maven.module_maven_plugin.rule.RuleExtensionModule */
public class RuleExtensionModuleTest
{
	/** @link de.boysen.udo.maven.module_maven_plugin.rule.RuleExtensionModule#isModuleImportAllowed(Import) */
	@ParameterizedTest
	@MethodSource("testIsModuleImportAllowedParams")
	public void testIsModuleImportAllowed(final Rule rule, final boolean expected, final Import imp)
	{
		(new RuleExtension(rule)).initRule();
		final RuleExtensionModule extension = new RuleExtensionModule(rule);

		Assertions.assertThat(extension.isModuleImportAllowed(imp)).isEqualTo(expected);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testIsModuleImportAllowedParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(new Rule(null, "something", null, null, null), true, new Import(Arrays.asList(new Module("something")))),
				Arguments.of(new Rule(null, "Something", null, null, null), true, new Import(Arrays.asList(new Module("somethinG")))),
				Arguments.of(new Rule(null, "something(else)", null, null, null), true, new Import(Arrays.asList(new Module("something"), new Module("else")))),
				Arguments.of(new Rule(null, "something,else", null, null, null), true, new Import(Arrays.asList(new Module("something"), new Module("nothing")))),
				
				Arguments.of(new Rule(null, "something(else)", null, null, null), false, new Import(Arrays.asList(new Module("something"), new Module("nothing")))),
				Arguments.of(new Rule(null, "nothing", null, null, null), false, new Import(Arrays.asList(new Module("something")))),
				Arguments.of(new Rule(null, "", null, null, null), false, new Import(Arrays.asList(new Module("something")))),
				Arguments.of(new Rule(null, "", null, null, null), false, new Import()),
				Arguments.of(new Rule(null, "", null, null, null), false, null),
				Arguments.of(new Rule(null, null, null, null, null), false, null),
				Arguments.of(new Rule(null, null, null, null, null), false, new Import())); 
		// @formatter:on
	}

	/** @link de.boysen.udo.maven.module_maven_plugin.rule.RuleExtensionModule#isModuleImportDisallowed(Import) */
	@ParameterizedTest
	@MethodSource("testIsModuleImportDisallowedParams")
	public void testIsModuleImportDisallowed(final Rule rule, final boolean expected, final Import imp)
	{
		(new RuleExtension(rule)).initRule();
		final RuleExtensionModule extension = new RuleExtensionModule(rule);

		Assertions.assertThat(extension.isModuleImportDisallowed(imp)).isEqualTo(expected);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testIsModuleImportDisallowedParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(new Rule(null, null, "something", null, null), true, new Import(Arrays.asList(new Module("something")))),
				Arguments.of(new Rule(null, null, "SOMethinG", null, null), true, new Import(Arrays.asList(new Module("somETHinG")))),
				Arguments.of(new Rule(null, null, "something(else)", null, null), true, new Import(Arrays.asList(new Module("something"), new Module("else")))),
				Arguments.of(new Rule(null, null, "something,else", null, null), true, new Import(Arrays.asList(new Module("something"), new Module("nothing")))),
				
				Arguments.of(new Rule(null, null, "something(else)", null, null), false, new Import(Arrays.asList(new Module("something"), new Module("nothing")))),
				Arguments.of(new Rule(null, null, "nothing", null, null), false, new Import(Arrays.asList(new Module("something")))),
				Arguments.of(new Rule(null, null, "", null, null), false, new Import(Arrays.asList(new Module("something")))),
				Arguments.of(new Rule(null, null, "", null, null), false, new Import()),
				Arguments.of(new Rule(null, null, "", null, null), false, null),
				Arguments.of(new Rule(null, null, null, null, null), false, null),
				Arguments.of(new Rule(null, null, null, null, null), false, new Import())); 
		// @formatter:on
	}
}
