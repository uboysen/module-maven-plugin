package de.boysen.udo.maven.module_maven_plugin.rule;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import de.boysen.udo.maven.module_maven_plugin.util.Util;

/**
 * An extension class for a special rule.
 */
public class RuleExtension
{
	private final Rule rule;

	// For an extension, this should be the only constructor
	public RuleExtension(final Rule rule)
	{
		this.rule = rule;
	}

	/**
	 * Initializes internal parameter.
	 */
	public void initRule()
	{
		rule.setModuleCheckArray(createCheckArray(rule.getModule()));

		rule.setAllowModuleCheckArray(createCheckArray(rule.getAllowModule()));
		rule.setDisallowModuleCheckArray(createCheckArray(rule.getDisallowModule()));

		rule.setAllowThirdPartyPatternList(Util.createPatternList(rule.getAllow3rdParty()));
		rule.setDisallowThirdPartyPatternList(Util.createPatternList(rule.getDisallow3rdParty()));
	}

	/**
	 * Checks if the given rule applies to the given set of module names.
	 */
	public boolean ruleApplies(final Set<String> nameSet)
	{
		boolean result = false;

		if (nameSet != null)
		{
			for (List<String> subList : rule.getModuleCheckArray())
			{
				boolean subListResult = true;
				for (String name : subList)
				{
					if (!nameSet.contains(name))
					{
						subListResult = false;
					}
				}

				if (subListResult)
				{
					result = true;
				}
			}
		}

		return result;
	}

	/**
	 * Creates an array of String from a String with braces and commas.
	 * e.g: "a(b(c), d(e,f), g(h(i,j))),k" will be:
	 *       (("a", "b", "c"),
	 *        ("a", "d", "e"),
	 *        ("a", "d", "f"),
	 *        ("a", "g", "h", "i"),
	 *        ("a", "g", "h", "j"),
	 *        ("k"))
	 */
	public List<List<String>> createCheckArray(final String str)
	{
		List<List<String>> result = new ArrayList<>();

		if (str != null)
		{
			List<String> list = new ArrayList<>();
			String token = "";
			int bracesDepth = 0;
			for (char c : str.toCharArray())
			{
				if (c == ',') // Every comma will finish a name and start a new sublist with the member 0 .. bracesDepth from the list before.
				{
					if (token.length() > 0)
					{
						list.add(token.trim());
					}
					if (list.size() > 0)
					{
						result.add(new ArrayList<String>(list.subList(0, list.size())));
					}
					list = new ArrayList<String>(list.subList(0, bracesDepth));
					token = "";
				} else if (c == '(') // Every open brace will finish a name and increment bracesDepth
				{
					if (token.length() > 0)
					{
						list.add(token.trim());
					}
					bracesDepth++;
					token = "";
				} else if (c == ')') // Every closing brace will finish a name and decrement bracesDepth
				{
					if (token.length() > 0)
					{
						list.add(token.trim());
					}
					bracesDepth--;
					token = "";
				} else
				{
					token += c;
				}
			}
			// After iteration add name and subList if not empty
			if (token.length() > 0)
			{
				list.add(token.trim());
			}
			if (list.size() > 0)
			{
				result.add(list);
			}
		}

		return result;
	}
}
