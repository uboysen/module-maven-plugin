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

	/**
	 * Constructs the import POJO for a given import string.
	 * 
	 * @param importStr The import string (e.g. 'something.else.or.more').
	 */
	public Import(final String importStr)
	{
		this.importStr = importStr;
	}

	/**
	 * Constructs the import POJO for a given import string and static flag.
	 * 
	 * @param importStr The import string (e.g. 'something.else.or.more').
	 * @param isStatic The static flag.
	 */
	public Import(final String importStr, final boolean isStatic)
	{
		this.importStr = importStr;
		this.isStatic = isStatic;
	}

	/**
	 * Constructs the import POJO for a given list of modules.
	 * 
	 * @param modules The list of modules.
	 */
	public Import(final List<Module> modules)
	{
		this.modules = modules;
	}
}
