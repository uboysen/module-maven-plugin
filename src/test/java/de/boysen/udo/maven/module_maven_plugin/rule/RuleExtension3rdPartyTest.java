package de.boysen.udo.maven.module_maven_plugin.rule;

import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.boysen.udo.maven.module_maven_plugin.model.Import;

/** @see de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension3rdParty */
public class RuleExtension3rdPartyTest
{
	/** @see de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension3rdParty#is3rdPartyImportAllowed(de.boysen.udo.maven.module_maven_plugin.model.Import) */
	@ParameterizedTest
	@MethodSource("testIs3rdPartyImportAllowedParams")
	public void testIs3rdPartyImportAllowed(final Rule rule, final boolean expected, final Import imp)
	{
		(new RuleExtension(rule)).initRule();
		final RuleExtension3rdParty extension = new RuleExtension3rdParty(rule);

		Assertions.assertThat(extension.is3rdPartyImportAllowed(imp)).isEqualTo(expected);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testIs3rdPartyImportAllowedParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(new Rule(null, null, null, "something*", null), true, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, "some*", null), true, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, "*and*", null), true, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, "*and*,nothing", null), true, new Import("something.and.more")),
				
				Arguments.of(new Rule(null, null, null, "nothing", null), false, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, "", null), false, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, "", null), false, new Import()),
				Arguments.of(new Rule(null, null, null, "", null), false, null),
				Arguments.of(new Rule(null, null, null, null, null), false, null),
				Arguments.of(new Rule(null, null, null, null, null), false, new Import())); 
		// @formatter:on
	}

	/** @see de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension3rdParty#is3rdPartyImportDisallowed(Import) */
	@ParameterizedTest
	@MethodSource("testIs3rdPartyImportDisallowedParams")
	public void testIs3rdPartyImportDisallowed(final Rule rule, final boolean expected, final Import imp)
	{
		(new RuleExtension(rule)).initRule();
		final RuleExtension3rdParty extension = new RuleExtension3rdParty(rule);

		Assertions.assertThat(extension.is3rdPartyImportDisallowed(imp)).isEqualTo(expected);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testIs3rdPartyImportDisallowedParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(new Rule(null, null, null, null, "something*"), true, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, null, "some*"), true, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, null, "*and*"), true, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, null, "*and*,nothing"), true, new Import("something.and.more")),
				
				Arguments.of(new Rule(null, null, null, null, "nothing"), false, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, null, ""), false, new Import("something.and.more")),
				Arguments.of(new Rule(null, null, null, null, ""), false, new Import()),
				Arguments.of(new Rule(null, null, null, null, ""), false, null),
				Arguments.of(new Rule(null, null, null, null, null), false, null),
				Arguments.of(new Rule(null, null, null, null, null), false, new Import())); 
		// @formatter:on
	}
}
