package de.boysen.udo.maven.module_maven_plugin.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.boysen.udo.maven.module_maven_plugin.model.Import;
import de.boysen.udo.maven.module_maven_plugin.module.Module;
import de.boysen.udo.maven.module_maven_plugin.module.ModuleExtension;

/** @link de.boysen.udo.maven.module_maven_plugin.util.Util */
public class UtilTest
{
	/** @link de.boysen.udo.maven.module_maven_plugin.util.Util#createPatternList(String) */
	@ParameterizedTest
	@MethodSource("testCreatePatternListParams")
	public void testCreatePatternList(final String str, final List<Pattern> expected)
	{
		final List<Pattern> result = Util.createPatternList(str);
		if (result != null)
		{
			for (int i = 0; i < result.size(); i++)
			{
				Assertions.assertThat(result.get(i).pattern()).isEqualTo(expected.get(i).pattern());
			}
		} else
		{
			Assertions.assertThat(expected).isNull();
		}
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testCreatePatternListParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of("*something*", Arrays.asList(Pattern.compile("(?i:.*something.*)"))),
				Arguments.of("*something", Arrays.asList(Pattern.compile("(?i:.*something)"))),
				Arguments.of("som*ethi*ng*", Arrays.asList(Pattern.compile("(?i:som.*ethi.*ng.*)"))),
				Arguments.of("*something*,*else,and*more", Arrays.asList(Pattern.compile("(?i:.*something.*)"), Pattern.compile("(?i:.*else)"), Pattern.compile("(?i:and.*more)"))),
				
				Arguments.of("", null),
				Arguments.of(null, null)); 
		// @formatter:on
	}

	/** @link de.boysen.udo.maven.module_maven_plugin.util.Util#somethingInPatternListMatches(String, List) */
	@ParameterizedTest
	@MethodSource("testSomethingInPatternListMatchesParams")
	public void testSomethingInPatternListMatches(final boolean expected, final String str, final List<Pattern> patternList)
	{
		Assertions.assertThat(Util.somethingInPatternListMatches(str, patternList)).isEqualTo(expected);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testSomethingInPatternListMatchesParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(true, "irgendwas.something.else", Arrays.asList(Pattern.compile("(?i:.*something.*)"))),
				Arguments.of(true, "irgendwas.something.else", Arrays.asList(Pattern.compile("(?i:.*something.*)"), Pattern.compile("(?i:.*nothing.*)"))),
				
				Arguments.of(false, "irgendwas.something.else", Arrays.asList(Pattern.compile("(?i:.*nothing.*)"))),
				Arguments.of(false, null, Arrays.asList(Pattern.compile("(?i:.*something.*)"))),
				Arguments.of(false, "irgendwas.something.else", null),
				Arguments.of(false, null, null)); 
		// @formatter:on
	}

	/** @link de.boysen.udo.maven.module_maven_plugin.util.Util#isImportFromOwnModule(Import, Set) */
	@ParameterizedTest
	@MethodSource("testIsImportFromOwnModuleParams")
	public void testIsImportFromOwnModule(final Import imp, final Set<String> parentModuleNameSet, final Boolean result)
	{
		Assertions.assertThat(Util.isImportFromOwnModule(imp, parentModuleNameSet)).isEqualTo(result);
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testIsImportFromOwnModuleParams()
	{
		// @formatter:off
		return Stream.of(
				Arguments.of(new Import("", Arrays.asList(new Module("name")), false), new HashSet<String>(), null),
				Arguments.of(new Import("", Arrays.asList(), false), new HashSet<String>(Arrays.asList("something")), null),
				Arguments.of(new Import("", Arrays.asList(new Module("a"), new Module("b")), false), new HashSet<String>(Arrays.asList("a", "c", "b")), true),
				Arguments.of(new Import("", Arrays.asList(new Module("a"), new Module("b")), false), new HashSet<String>(Arrays.asList("a", "c", "d")), false)); 
		// @formatter:on
	}

	/** @link de.boysen.udo.maven.module_maven_plugin.util.Util#getModulesForStr(List, String) */
	@ParameterizedTest
	@MethodSource("testGetModulesForStrParams")
	public void testGetModulesForStr(final boolean positiveTest, final List<Module> modules, final String str, final List<Module> result)
	{
		if (positiveTest)
		{
			Assertions.assertThat(Util.getModulesForStr(modules, str)).isEqualTo(result);
		} else
		{
			Assertions.assertThat(Util.getModulesForStr(modules, str)).isNotEqualTo(result);
		}
	}

	@SuppressWarnings("unused")
	private static Stream<Arguments> testGetModulesForStrParams()
	{
		final Module m1 = new Module("m1", "*m1*");
		(new ModuleExtension(m1)).initModule();
		final Module m2 = new Module("m2", "*m2*");
		(new ModuleExtension(m2)).initModule();
		final Module m3 = new Module("m3", "*m3*");
		(new ModuleExtension(m3)).initModule();

		// @formatter:off
		return Stream.of(
				Arguments.of(true, Arrays.asList(m1, m2, m3), null, Arrays.asList()),
				Arguments.of(true, null, null, Arrays.asList()),
				Arguments.of(true, Arrays.asList(m1, m2, m3), "de.mycompany.m1", Arrays.asList(m1)),
				Arguments.of(true, Arrays.asList(m1, m2, m3), "de.mycompany", Arrays.asList()),
				Arguments.of(true, Arrays.asList(m1, m2, m3), "de.mycompany.m1.m2.m3", Arrays.asList(m1, m2, m3)),
				
				Arguments.of(false, Arrays.asList(m1, m2, m3), "de.mycompany.m1", Arrays.asList()));
	   // @formatter:on
	}

	/** @link de.boysen.udo.maven.module_maven_plugin.util.Util#startsWithCommentOrAnnotation(String) */
	@Test
	public void testStartsWithCommentOrAnnotation()
	{
		Assertions.assertThat(Util.startsWithCommentOrAnnotation("// some comment")).isTrue();
		Assertions.assertThat(Util.startsWithCommentOrAnnotation("/* some comment */")).isTrue();
		Assertions.assertThat(Util.startsWithCommentOrAnnotation("* some comment in next line")).isTrue();
		Assertions.assertThat(Util.startsWithCommentOrAnnotation("@SomeAnnotation //with comment")).isTrue();

		Assertions.assertThat(Util.startsWithCommentOrAnnotation("public class SomeClassDefinition")).isFalse();
		Assertions.assertThat(Util.startsWithCommentOrAnnotation("import something.at.anywhere; //And a comment")).isFalse();
		Assertions.assertThat(Util.startsWithCommentOrAnnotation("package anywhere.or.else; //And a comment")).isFalse();
	}

	/** @link de.boysen.udo.maven.module_maven_plugin.util.Util#removeComments(String) */
	@Test
	public void testRemoveComments()
	{
		Assertions.assertThat("irgendwas").isEqualTo(Util.removeComments("irgendwas//comment mit text "));
		Assertions.assertThat("irgendwas").isEqualTo(Util.removeComments("/**comment mit was*/irgendwas//comment mit text "));
		Assertions.assertThat("").isEqualTo(Util.removeComments("///**comment mit was*/irgendwas//comment mit text "));
		Assertions.assertThat("").isEqualTo(Util.removeComments("//simple comment"));
	}
}
