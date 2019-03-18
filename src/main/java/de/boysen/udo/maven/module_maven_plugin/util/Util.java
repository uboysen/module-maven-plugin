package de.boysen.udo.maven.module_maven_plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import de.boysen.udo.maven.module_maven_plugin.model.Import;
import de.boysen.udo.maven.module_maven_plugin.module.Module;

/**
 * A utility class for common stateless methods.
 */
public final class Util
{
	private static final String COMMENT_REGEXP = "(//.*)*(/\\*.*\\*/)*";

	private Util()
	{
	}

	/**
	 * Creates a list of Pattern from a String with asterisks and commas.
	 * e.g: "*a*, b, c*,*d" will be [Pattern(.*a.*), Pattern(b), Pattern(c.*), Pattern(.*d)]
	 */
	public static List<Pattern> createPatternList(final String str)
	{
		List<Pattern> result = null;

		if (StringUtils.isNotEmpty(str))
		{
			final String[] strArray = str.split(",");
			result = new ArrayList<>();
			for (int i = 0; i < strArray.length; i++)
			{
				result.add(Pattern.compile(strArray[i].trim().replaceAll("\\*", ".*")));
			}
		}

		return result;
	}

	/**
	 * Returns true if one of the given Pattern matches. Otherwise false.
	 */
	public static boolean somethingInPatternListMatches(final String str, final List<Pattern> patternList)
	{
		boolean result = false;

		if (StringUtils.isNotEmpty(str) && patternList != null)
		{
			for (Pattern pattern : patternList)
			{
				Matcher m = pattern.matcher(str);
				if (m.matches())
				{
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * Returns true if all modules in Import are in parentModuleNameSet.
	 * If the Import has no modules or the parentModuleNameSet if empty, the result is null.
	 */
	public static Boolean isImportFromOwnModule(final Import imp, final Set<String> parentModuleNameSet)
	{
		Boolean result = null;

		if (!CollectionUtils.isEmpty(imp.getModules()) && !parentModuleNameSet.isEmpty())
		{
			result = true;
			for (Module impModule : imp.getModules())
			{
				if (!parentModuleNameSet.contains(impModule.getName()))
				{
					result = false;
				}
			}
		}

		return result;
	}

	/**
	 * Returns all modules from a given list when their pattern matches the given String.
	 */
	public static List<Module> getModulesForStr(final List<Module> modules, final String str)
	{
		List<Module> result = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(modules) && StringUtils.isNotEmpty(str))
		{
			for (Module module : modules)
			{
				if (Util.somethingInPatternListMatches(str, module.getPatternList()))
				{
					result.add(module);
				}
			}
		}

		return result;
	}

	/**
	 * Returns true if the given String starts with a comment or Annotation.
	 */
	public static boolean startsWithCommentOrAnnotation(final String line)
	{
		boolean result = false;

		if (line.startsWith("/") || line.startsWith("*") || line.startsWith("@"))
		{
			result = true;
		}

		return result;
	}

	/**
	 * Removes comments (e.g. // something  ) from a given String.
	 */
	public static String removeComments(final String line)
	{
		return line.replaceAll(COMMENT_REGEXP, "");
	}
}
