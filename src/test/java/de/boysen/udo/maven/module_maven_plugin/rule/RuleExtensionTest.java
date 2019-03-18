package de.boysen.udo.maven.module_maven_plugin.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** @see de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension */
public class RuleExtensionTest
{
	/** @see de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension#ruleApplies(java.util.Set) */
	@ParameterizedTest
	@MethodSource("testRuleAppliesParams")
	public void testRuleApplies(final Rule rule, final boolean expected, final Set<String> nameSet)
	{
		RuleExtension extension = new RuleExtension(rule);
		extension.initRule();

		Assertions.assertThat(extension.ruleApplies(nameSet)).isEqualTo(expected);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testRuleAppliesParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(new Rule("something"), true, new HashSet<String>(Arrays.asList("something"))),
				Arguments.of(new Rule("something(else)"), true, new HashSet<String>(Arrays.asList("something", "else"))),
				Arguments.of(new Rule("something"), true, new HashSet<String>(Arrays.asList("something", "else"))),
				Arguments.of(new Rule("something,nothing"), true, new HashSet<String>(Arrays.asList("something"))),
				
				Arguments.of(new Rule("something"), false, null),
				Arguments.of(new Rule("something"), false, new HashSet<String>(Arrays.asList("nothing", "else"))),
				Arguments.of(new Rule("something(else)"), false, new HashSet<String>(Arrays.asList("something"))),
				Arguments.of(new Rule("something(else)"), false, new HashSet<String>(Arrays.asList("something", "and", "nothing")))); 
		// @formatter:on
	}

	/** @see de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension#createCheckArray(String) */
	@ParameterizedTest
	@MethodSource("testCreateCheckArraysParams")
	public void testCreateCheckArrays(final boolean positiveTest, final String allowedStr, final List<List<String>> expectedArray)
	{
		Rule rule = new Rule();
		RuleExtension extension = new RuleExtension(rule);

		List<List<String>> checkArray = extension.createCheckArray(allowedStr);
		if (positiveTest)
		{
			Assertions.assertThat(checkArray).isEqualTo(expectedArray);
		} else
		{
			Assertions.assertThat(checkArray).isNotEqualTo(expectedArray);
		}
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testCreateCheckArraysParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(true, "()", new ArrayList<String>()),
				Arguments.of(true, "(something)", Arrays.asList(Arrays.asList("something"))),
				Arguments.of(true, "", new ArrayList<String>()),
				Arguments.of(true, ",", new ArrayList<String>()),
				Arguments.of(true, ",something", Arrays.asList(Arrays.asList("something"))),
				Arguments.of(true, "something,", Arrays.asList(Arrays.asList("something"))),
				Arguments.of(true, "a(b(c), d(e,f), g(h(i,j))),k", Arrays.asList(
						Arrays.asList("a", "b", "c"),
						Arrays.asList("a", "d", "e"),
						Arrays.asList("a", "d", "f"),
						Arrays.asList("a", "g", "h", "i"),
						Arrays.asList("a", "g", "h", "j"),
						Arrays.asList("k"))),
				Arguments.of(true, "backend(user(admin)), interface", Arrays.asList(
						Arrays.asList("backend", "user", "admin"),
						Arrays.asList("interface"))), 
				Arguments.of(true, "backend(user,shop), interface", Arrays.asList(
						Arrays.asList("backend", "user"), 
						Arrays.asList("backend", "shop"), 
						Arrays.asList("interface"))), 
				Arguments.of(true, "interface, shop(backend,frontend)", Arrays.asList(
						Arrays.asList("interface"), 
						Arrays.asList("shop", "backend"), 
						Arrays.asList("shop", "frontend"))),
				Arguments.of(true, "interface", Arrays.asList(
						Arrays.asList("interface"))),
				
				Arguments.of(false, "interface, shop(backend,frontend)", Arrays.asList(
						Arrays.asList("interface"), 
						Arrays.asList("shop", "something wrong"), 
						Arrays.asList("shop", "frontend")))); 
		// @formatter:on
	}
}
