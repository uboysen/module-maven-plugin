# Module Maven Plugin

A Maven plugin for checking relations of defined modules and 3rd party artefacts.

The target is to make sure that a defined archtecture will be kept.

## Usage

To use the plugin, clone or download this project and install it with:

	mvn install

The Module Maven Plugin can be used in the validate phase with the goal check:

	mvn validate

It should be defined in the build part of the pom.xml. It has a common, module and rule part in the configuration:

	<build>
		<plugins>
			<plugin>
				<groupId>de.boysen.udo.maven</groupId>
				<artifactId>module-maven-plugin</artifactId>
				<version>1.0</version>
				<configuration>
					<strict>false</strict>
					<defaultAllow>own</defaultAllow>
					<modules>
						<module>
							<name>interface</name>
							<packages>*link*</packages>
						</module>
					</modules>
					<rules>
						<rule>
							<module>interface</module>
							<disallowModule>backend</disallowModule>
						</rule>
					</rules>
				</configuration>
				<executions>
					<execution>
						<phase>validate</phase>
						<goals>
							<goal>check</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			...

### configuration

#### common

These are common configuration for all modules. Later definitions in a rule have priority.

Modules are referenced by their name. A list is separated by comma. Modules in a module are referenced by braces. For example is the 'user' module in the 'backend' module referenced as 'backend(user)'. A complex reference could be: 'backend(user(admin)),interface(user)'.

3rd party imports are specified by a description with asterisks. For example means \*jsf\* all imports with jsf in the name. Lists are comma separated. Please note that a simple asterisk is used for a placeholder, not .\* as in java regex.

* **strict** (optional, default: false): If set to true, an exception occures if a rule is violated. Otherwise a warning is printed.
* **allowImportsWithAsterisk** (optional, default: true): If set to false, asterisks in import statements are not allowed.
* **allowStaticImports** (optional, default: true): If set to false, static import statements are not allowed.
* **defaultAllow** (optional, default: own): Defines the allowance for all modules. If set to **own**, only classes from the own module are allowed for import. If set to **none**, no classes from any module are allowed for import. If set to **all**, all module imports are allowed. This configuration has no effect on 3rd party imports. If a rule defines allowence for a module, the special rule has priority.
* **defaultAllow3rdParty** (optional): Defines a rule for 3rd party imports for all modules. Descriptions with asterisks and comma can be used. e.g. '\*jsf\*,\*primeface\*' All imports matching one of the description in the list are allowed for all modules by default. Special rules in later definitions have priority.
* **defaultDisallow3rdParty** (optional): Defines a rule for 3rd party imports for all modules. Descriptions with asterisks and comma can be used. e.g. '\*persistence\*,\*hibernate\*,\*db2\*' All imports matching one of the description in the list are disallowed for all modules by default. Special rules in later definitions have priority.

#### modules

A module is defined by a name and a description for the package statement of included classes.

* **name**: The name of the module. e.g. 'interface'
* **packages**: A description of packages of classes which are included. This can be a list with asterisks. e.g. '\*backend\*' as packages means, all classes with 'backend' in their package statement are included in this module.

An example for an interface module (name: interface, packages: all classes with 'link' in the package statement):

	<module>
		<name>interface</name>
		<packages>*link*</packages>
	</module>

#### rules

A rule includes the definition of modules to apply at, allow and disallow parts for modules and 3rd party imports.

* **module**: A description of modules to apply the rule at. This can be a comma separated list of module names. Modules can be specified with braces. For example 'backend(user)' means, the rule applies to all classes, witch have 'backend' and 'user' in the package statement. 'backend,interface' would apply to all classes, which have 'backend' or 'user' in the package statement.
* **allowModule**: A description of modules which are allowed for import. A comma separated list with braces can be used.
* **disallowModule**: A description of modules which are not allowed for import. A comma separated list with braces can be used.
* **allow3rdParty**: A description of 3rd part imports which are allowed. A comma separated list with asterisks can be used.
* **disallow3rdParty**: A description of 3rd part imports which are not allowed. A comma separated list with asterisks can be used.

The order of rules is important. Also the order of the parts in the rules is important. The last rule or part wins.

------------------------------------------

## Examples

Here are two common examples for architectures to check with this plugin.

### A simple layer architecture

Consider an architecture with two layer and an interface:

* **backend**: A module for operations on a database. All classes are in a package with 'backend' in the name. 3rd party imports with 'jsf' in the statement are not allowed.
* **frontend**: A module for the user interface e.g. JSF. All classes are in a package with 'frontend' in the name. 3rd party imports with 'persistence' or 'hibernate' in the statement are not allowed.
* **interface**: An interface module for the two modules above. Backend and frontend are only allowed to communicate with the interface. Direct communication between backend and frontend is disallowed. All classes of the interface are in a package with 'link' in the name.

The plugin definition could be:

	<build>
		<plugins>
			<plugin>
				<groupId>de.boysen.udo.maven</groupId>
				<artifactId>module-maven-plugin</artifactId>
				<version>1.0</version>
				<configuration>
					<strict>false</strict>
					<defaultAllow>own</defaultAllow>
					<modules>
						<module>
							<name>backend</name>
							<packages>*backend*</packages>
						</module>
						<module>
							<name>frontend</name>
							<packages>*frontend*</packages>
						</module>
						<module>
							<name>interface</name>
							<packages>*link*</packages>
						</module>
					</modules>
					<rules>
						<rule>
							<module>backend</module>
							<allowModule>interface</allowModule>
							<disallow3rdParty>*jsf*</disallow3rdParty>
						</rule>
						<rule>
							<module>frontend</module>
							<allowModule>interface</allowModule>
							<disallow3rdParty>*persistence*,*hibernate*</disallow3rdParty>
						</rule>
					</rules>
				</configuration>
				<executions>
					<execution>
						<phase>validate</phase>
						<goals>
							<goal>check</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			...

### A complex layer architecture

Imagine a 3 layer architecture with 'user', 'shop' and 'site' modules in every layer. The user layer is specialized for administration.

* **backend**: A backend layer (module) which handles database interaction.
* **buisness**:  A buisness layer (module) for the buisness logic.
* **frontend**: A frontend layer (module) for the user interaction (e.g. JSF).


* **user**: A vertical layer (module) for handling user dependent stuff.
* **admin**: A layer (module) in the user layer specialized on administration stuff.
* **shop**: A vertical layer (module) for handling stuff for a shop.
* **site**: A vertical layer (module) for handling all stuff which is not part of the other vertical layers.

The plugin definition could be:

	<build>
		<plugins>
			<plugin>
				<groupId>de.boysen.udo.maven</groupId>
				<artifactId>module-maven-plugin</artifactId>
				<version>1.0</version>
				<configuration>
					<strict>false</strict>
					<defaultAllow>own</defaultAllow>
					<defaultAllow3rdParty>*lombok*</defaultAllow3rdParty>
					<defaultDisallow3rdParty>*guavara*</defaultDisallow3rdParty>
					<modules>
						<module>
							<name>backend</name>
							<packages>*backend*</packages>
						</module>
						<module>
							<name>buisness</name>
							<packages>*buisness*</packages>
						</module>
						<module>
							<name>frontend</name>
							<packages>*frontend*</packages>
						</module>
						<module>
							<name>interface</name>
							<packages>*link*</packages>
						</module>
						<module>
							<name>user</name>
							<packages>*user*</packages>
						</module>
						<module>
							<name>admin</name>
							<packages>*admin*</packages>
						</module>
						<module>
							<name>shop</name>
							<packages>*shop*</packages>
						</module>
						<module>
							<name>site</name>
							<packages>*site*</packages>
						</module>
					</modules>
					<rules>
						<rule>
							<module>backend</module>
							<allowModule>interface</allowModule>
							<disallow3rdParty>*jsf*</disallow3rdParty>
						</rule>
						<rule>
							<module>buisness</module>
							<allowModule>interface</allowModule>
						</rule>
						<rule>
							<module>frontend</module>
							<allowModule>interface</allowModule>
							<disallow3rdParty>*persistence*,*hibernate*</disallow3rdParty>
						</rule>
					</rules>
				</configuration>
				<executions>
					<execution>
						<phase>validate</phase>
						<goals>
							<goal>check</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			...
			
Please note that defaultAllow is set to **own**, which means every module is only allowed to import classes from their own module by default. This means for example, that a class in the package 'something.backend.user.admin' is only allowed to import other classes with 'backend', 'user' and 'admin' in the package statement. The special rule for backend opens the import for all classes in the interface layer.

If a more strict import is wanted a rule like

	<rule>
		<module>backend(user(admin))</module>
		<disallowModule>interface</disallowModule>
		<allowModule>interface(user(admin))</allowModule>
	</rule>

could be used.
