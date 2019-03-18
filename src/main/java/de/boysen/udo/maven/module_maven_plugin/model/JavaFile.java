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

	public JavaFile(final String name, final String absolutePath)
	{
		this.name = name;
		this.absolutePath = absolutePath;
	}

	public JavaFile(final List<Module> parentModules, final List<Import> imports)
	{
		this.parentModules = parentModules;
		this.imports = imports;
	}

	public void addImport(Import imp)
	{
		this.imports.add(imp);
	}
}
