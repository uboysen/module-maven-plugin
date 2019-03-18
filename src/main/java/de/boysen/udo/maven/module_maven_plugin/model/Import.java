package de.boysen.udo.maven.module_maven_plugin.model;

import java.util.ArrayList;
import java.util.List;

import de.boysen.udo.maven.module_maven_plugin.module.Module;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A POJO for imports.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Import
{
	private String			importStr;
	private List<Module>	modules	= new ArrayList<>();
	private boolean		isStatic;

	public Import(final String importStr)
	{
		this.importStr = importStr;
	}

	public Import(final String importStr, final boolean isStatic)
	{
		this.importStr = importStr;
		this.isStatic = isStatic;
	}

	public Import(final List<Module> modules)
	{
		this.modules = modules;
	}
}
