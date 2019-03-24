package de.boysen.udo.maven.module_maven_plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;

import de.boysen.udo.maven.module_maven_plugin.model.Import;
import de.boysen.udo.maven.module_maven_plugin.model.JavaFile;
import de.boysen.udo.maven.module_maven_plugin.module.Module;
import de.boysen.udo.maven.module_maven_plugin.rule.Rule;
import de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension;
import de.boysen.udo.maven.module_maven_plugin.rule.RuleExtension3rdParty;
import de.boysen.udo.maven.module_maven_plugin.rule.RuleExtensionModule;
import de.boysen.udo.maven.module_maven_plugin.util.Util;

/**
 * A extension class for a special module mojo treating executions.
 */
public class ModuleMojoExtensionExecution
{
	private final ModuleMojo mojo;

	/**
	 * For an extension, this should be the only constructor.
	 * 
	 * @param mojo The mojo to extend.
	 */
	public ModuleMojoExtensionExecution(final ModuleMojo mojo)
	{
		this.mojo = mojo;
	}

	/**
	 * Searches the given file for java files and analysis them.
	 * The method is recursive.
	 *
	 * @param file The file to start the analysis. Mostly a directory.
	 * 
	 * @throws MojoExecutionException Thrown if an IOException occurs on files 
	 *                                or some rule is validated while strict is set to true on mojo.
	 */
	public void analysePath(final File file) throws MojoExecutionException
	{
		if (file != null)
		{
			if (file.isDirectory())
			{
				final File[] files = file.listFiles();
				if (files != null)
				{
					for (File subFile : files)
					{
						analysePath(subFile);
					}
				}
			} else if (file.getName().endsWith(".java"))
			{
				JavaFile javaFile = readJavaFile(file.getName(), file.getAbsolutePath());
				analyseJavaFile(javaFile);
			}
		}
	}

	/**
	 * Reads a java file and creates a POJO with imports for it.
	 *
	 * @param name The name of the physical file.
	 * @param absolutePath The absolute path to the file.
	 * 
	 * @return The corresponding POJO to the java file.
	 * 
	 * @throws MojoExecutionException Thrown if an IOException occurs while reading the file 
	 *                                or some rule is validated while strict is set to true on mojo.
	 */
	protected JavaFile readJavaFile(final String name, final String absolutePath) throws MojoExecutionException
	{
		JavaFile result = new JavaFile(name, absolutePath);

		mojo.getLog().debug("");
		mojo.getLog().debug("Analyse file: " + name);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(absolutePath), StandardCharsets.UTF_8)))
		{
			List<Module> parentModules = null;

			String line = reader.readLine();
			boolean stopParsing = false;
			while (line != null && !stopParsing)
			{
				line = line.trim();
				String compareStr = line.toLowerCase();
				if (compareStr.startsWith("import"))
				{
					result.addImport(createImport(line));
				} else if (compareStr.startsWith("package"))
				{
					parentModules = Util.getModulesForStr(mojo.getModules(), Util.removeComments(line.substring(7).trim()).replace(";", ""));
					if (parentModules.size() == 0)
					{
						showMessage(name + " is not assigned to a module!");
					}
					result.setParentModules(parentModules);
				} else if (!StringUtils.isEmpty(compareStr) && parentModules != null)
				{
					stopParsing = true;
				}

				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e)
		{
			throw new MojoExecutionException("IOException while reading java file: " + absolutePath + "!", e);
		}

		return result;
	}

	/**
	 * Creates a POJO Import for a given String. E.g: "import someting.else;"
	 *
	 * @param str The String from which to create the Import POJO.
	 * 
	 * @return The resulting Import POJO.
	 */
	protected Import createImport(final String str)
	{
		final Import result = new Import();

		boolean importIsStatic = false;

		String importStr = Util.removeComments(str.substring(6).trim());
		if (importStr.endsWith(";"))
		{
			importStr = importStr.substring(0, importStr.length() - 1); // Remove semicolon
		}
		importStr = importStr.trim();
		if (importStr.startsWith("static"))
		{
			importStr = importStr.substring(6).trim();
			importIsStatic = true;
		}

		final List<Module> impModules = Util.getModulesForStr(mojo.getModules(), importStr);

		result.setImportStr(importStr);
		result.setModules(impModules);
		result.setStatic(importIsStatic);

		return result;
	}

	/**
	 * Analysis a given JavaFile (POJO) and checks relations for modules and 3rd party artefacts.
	 *
	 * @param javaFile A java file POJO.
	 * 
	 * @throws MojoExecutionException Thrown if some rule is validated while strict is set to true on mojo.
	 */
	protected void analyseJavaFile(final JavaFile javaFile) throws MojoExecutionException
	{
		Set<String> parentModuleNameSet = new HashSet<String>();
		for (Module module : javaFile.getParentModules())
		{
			parentModuleNameSet.add(StringUtils.lowerCase(module.getName()));
		}

		for (Import imp : javaFile.getImports())
		{
			if (imp.getImportStr().contains("*") && !mojo.getAllowImportsWithAsterisk())
			{
				showMessage("Imports with asterisks are not allowed!");
			}
			if (imp.isStatic() && !mojo.getAllowStaticImports())
			{
				showMessage("Static imports are not allowed!");
			}

			if (imp.getModules().size() > 0)
			{
				checkModuleImport(imp, parentModuleNameSet, javaFile.getName());
			} else
			{
				check3rdPartyImport(imp, parentModuleNameSet, javaFile.getName());
			}
		}
	}

	/**
	 * Checks module relations for a given Import (POJO) and Set. The filename is for logging.
	 *
	 * @param imp An import POJO.
	 * @param parentModuleNameSet A set of parent module names.
	 * @param fileName Just for logging.
	 * 
	 * @throws MojoExecutionException Thrown if some rule is validated while strict is set to true on mojo.
	 */
	protected void checkModuleImport(final Import imp, final Set<String> parentModuleNameSet, final String fileName) throws MojoExecutionException
	{
		Boolean importIsAllowed = null;
		String message = null;

		switch (mojo.getDefaultAllow().toUpperCase())
		{
			case ModuleMojo.DEFAULTALLOW_NONE:
				importIsAllowed = false;
				message = fileName + ": Module import '" + imp.getImportStr() + "' is disallowed by default!";
				break;
			case ModuleMojo.DEFAULTALLOW_OWN:
				if (BooleanUtils.isTrue(Util.isImportFromOwnModule(imp, parentModuleNameSet)))
				{
					importIsAllowed = true;
					mojo.getLog().debug("Module import '" + imp.getImportStr() + "' is allowed by default. Import is from within own module.");
				}
				break;
			case ModuleMojo.DEFAULTALLOW_ALL:
				importIsAllowed = true;
				mojo.getLog().debug("Module import '" + imp.getImportStr() + "' is allowed by default.");
				break;
			default:
				throw new MojoExecutionException("Unknown default allow: '" + mojo.getDefaultAllow() + "'!");
		}

		for (Rule rule : mojo.getRules())
		{
			final RuleExtension ruleExtension = new RuleExtension(rule);
			final RuleExtensionModule ruleExtensionModule = new RuleExtensionModule(rule);

			if (ruleExtension.ruleApplies(parentModuleNameSet))
			{
				if (ruleExtensionModule.isModuleImportAllowed(imp))
				{
					importIsAllowed = true;
					mojo.getLog().debug("Module import '" + imp.getImportStr() + "' is allowed by rule for '" + rule.getModule() + "'.");
				}

				if (ruleExtensionModule.isModuleImportDisallowed(imp))
				{
					importIsAllowed = false;
					message = fileName + ": Module import '" + imp.getImportStr() + "' is disallowed by rule for '" + rule.getModule() + "'!";
				}
			}
		}

		if (importIsAllowed == null)
		{
			showMessage("No rule for module import '" + imp.getImportStr() + "' found!");
		} else if (importIsAllowed)
		{
			mojo.getLog().debug("Module import '" + imp.getImportStr() + "' is allowed.");
		} else
		{
			showMessage(message);
		}
	}

	/**
	 * Checks 3rd party relations for a given Import (POJO) and a SET. The fileName is for logging.
	 *
	 * @param imp An import POJO.
	 * @param parentModuleNameSet A Set of parent module names.
	 * @param fileName Just for logging.
	 * 
	 * @throws MojoExecutionException Thrown if some rule is validated while strict is set to true on mojo.
	 */
	protected void check3rdPartyImport(final Import imp, final Set<String> parentModuleNameSet, final String fileName) throws MojoExecutionException
	{
		Boolean importIsAllowed = null;
		String message = null;

		if (Util.somethingInPatternListMatches(imp.getImportStr(), mojo.getDefaultAllow3rdPartyPatternList()))
		{
			importIsAllowed = true;
		}

		if (Util.somethingInPatternListMatches(imp.getImportStr(), mojo.getDefaultDisallow3rdPartyPatternList()))
		{
			importIsAllowed = false;
			message = fileName + ": 3rd party import '" + imp.getImportStr() + "' is disallowed by default!";
		}

		for (Rule rule : mojo.getRules())
		{
			final RuleExtension ruleExtension = new RuleExtension(rule);
			final RuleExtension3rdParty ruleExtension3rdParty = new RuleExtension3rdParty(rule);

			if (ruleExtension.ruleApplies(parentModuleNameSet))
			{
				if (ruleExtension3rdParty.is3rdPartyImportAllowed(imp))
				{
					importIsAllowed = true;
					mojo.getLog().debug("3rd party import '" + imp.getImportStr() + "' is allowed by rule for '" + rule.getModule() + "'.");
				}

				if (ruleExtension3rdParty.is3rdPartyImportDisallowed(imp))
				{
					importIsAllowed = false;
					message = fileName + ": 3rd party import '" + imp.getImportStr() + "' is disallowed by rule for '" + rule.getModule() + "'!";
				}
			}
		}

		if (importIsAllowed == null || importIsAllowed)
		{
			mojo.getLog().debug("3rd party import '" + imp.getImportStr() + "' is allowed.");
		} else
		{
			showMessage(message);
		}
	}

	/**
	 * Logs a message or throws an exception depending on the strict parameter of the mojo.
	 *
	 * @param message The message to show.
	 * 
	 * @throws MojoExecutionException Thrown if strict is set to true on mojo.
	 */
	protected void showMessage(final String message) throws MojoExecutionException
	{
		if (message != null)
		{
			if (mojo.getStrict())
			{
				throw new MojoExecutionException(message);
			} else
			{
				mojo.getLog().warn(message);
			}
		}
	}
}
