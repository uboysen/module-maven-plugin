package de.boysen.udo.maven.module_maven_plugin.model;

import java.util.ArrayList;
import java.util.List;

import de.boysen.udo.maven.module_maven_plugin.module.Module;
import lombok.Data;

/**
 * A POJO for a java file.
 */
@Data
public class JavaFile
{
	private String			name;
	private String			absolutePath;
	private List<Module>	parentModules	= new ArrayList<>();
	private List<Import>	imports			= new ArrayList<>();

	/**
	 * Constructor of the java file POJO for a given file name (physical) and absolute path.
	 * 
	 * @param name The file name (physical)
	 * @param absolutePath The absolute path.
	 */
	public JavaFile(final String name, final String absolutePath)
	{
		this.name = name;
		this.absolutePath = absolutePath;
	}

	/**
	 * Constructor of the java file POJO for a given list of modules and imports (POJO).
	 * 
	 * @param parentModules A list of modules.
	 * @param imports A list of imports (POJO).
	 */
	public JavaFile(final List<Module> parentModules, final List<Import> imports)
	{
		this.parentModules = parentModules;
		this.imports = imports;
	}

	/**
	 * Adding an import (POJO).
	 * 
	 * @param imp An import (POJO).
	 */
	public void addImport(Import imp)
	{
		this.imports.add(imp);
	}
}
