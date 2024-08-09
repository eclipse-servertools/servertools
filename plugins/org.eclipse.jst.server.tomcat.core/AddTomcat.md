# Steps to Add a New Version of Tomcat

This document describes how to add a new version of Tomcat to Eclipse WTP.  It is meant as a guide. For more details, refer to commits to prior versions. Changes to Tomcat that require refactoring of the current implementation is beyond the scope of this document. It is assumed that the new Tomcat works the same as older versions to the extent that the current ServerTools implementation still functions adequately.

When modifying files, specify the current year in the copyright headers where present.

### Modifications to plugins/org.eclipse.jst.server.tomcat.core

1. Modify **plugin.properties** to add properties strings for the new version of Tomcat. Use existing properties as a template and make appropriate changes.  Update the **runtimeTypeTomcat???Description** value to include the new specifications that this version supports.
2. Modify **plugin.xml** to add new configuration elements. Add new elements using existing ones as a template and make appropriate changes. Be sure to update **id** values appropriately.
    1. Add a new **runtimeType** to the **org.eclipse.wst.server.core.runtimeTypes** extension. In the new runtimeType, the **version** list in the **moduleType** element should include the new version that this Tomcat supports, provided Eclipse has support for it. If not yet supported, it may need to be added at a later time. To check, locate the *webtools.javaee* Git repository. Examine the *plugin.xml* for the *plugins/org.eclipse.jst.jee.web project*. In the **org.eclipse.wst.common.project.facet.core.facets** extension declarations, the *jst.web* facets show the supported versions. Note: It appears that adding the appropriate versions before they are supported in Eclipse is okay.
    2. Add a new **serverType** to the **org.eclipse.wst.server.core.serverTypes** extension. Be sure to update the **runtimeTypeId** to match the id of the runtimeType added in step 1.
    3. Add a new **runtime-component-version** element to the **org.eclipse.wst.common.project.facet.core.runtimes** extension specifying the new Tomcat version. Also add a **supported** element specifying the new version for its **runtime-component**. Under this supported element, add appropriate versions to the list of supported facet versions determined in step 1.
    4. Add a new **runtimeFacetMapping** to the **org.eclipse.jst.server.core.runtimeFacetMappings** extension, specifying the appropriate runtimeTypeId and version.
    5. At some point add a pair of **runtime** entries to the **org.eclipse.wst.server.core.installableRuntimes** extension using existing entries as a template. See the end of this document for some commands to help with the parameters. It is best to add these after the Tomcat version is having official releases as the specific version will become obsolete very quickly prior to that.
3. Modify **Messages.java** and **Messages.properties** to add strings for **errorJRETomcat???** and **errorSpec???**. Update the content of the strings as needed per Tomcat documention, such as the minimum version of the JRE that can be used.
4. Modify **TomcatPlugin.java** to add a new **TOMCAT_???** string constant. Then modify its **getTomcatVersionHandler()** method to return the appropriate **Tomcat???Handler** which will be created next.
5. Create a new **Tomcat???Handler** class by copying an existing one that is for Tomcat 11 or newer.  Tomcat 11 dropped support for running with a security manager and copying the Tomcat 11 or newer version will ensure this change is handled properly by the new class.
    1. Modify the **verifyInstallPath()** method to use the new **TomcatPlugin.TOMCAT_???** string constant.
    2. Modify the **canAddModule()** to specify the appropriate **Messages.errorSpec???** constant. Also add any supported module version comparisons to the **if** statement.
    3. Update the version found in strings and comments to refer to the new version.
6. Create a new **Tomcat???Configuration** class by copying an existing one that is for Tomcat 11 or newer. Update the version found in strings and comments to refer to the new version.  TODO: Add support for HTTP2?
7. Create a new **Tomcat???PublishModuleVisitor** class by copying an existing one. Update the version found in strings and comments to refer to the new version.
8. Modify  **TomcatRuntime.java** to update the **validate()** method to support the new Tomcat version.
    1. Add a comparison of the **id** to the new version in the **if** statement related to the *Eclipse JDT compiler*.
    2. Add  additional **else if** code to the end of this method to handle when the **id** matches the new Tomcat version. Update the **Messages.errorJRETomcat???** appropriately. If a JRE newer than version 8 is needed, update code appropriately.
9. Modify **TomcatRuntimeLocator.java** to update the **runtimeTypes** string array to add the runtime id of the new Tomcat version.
10. Modify **TomcatServer.java** to update the **getTomcatConfiguration()** and **importRuntimeConfiguration()** methods to instantiate a **Tomcat???Configuration** when the **id** matches the new Tomcat version.
11. Modify **TomcatVersionHelper.java** to support the new Tomcat version.
    1. Update the initialization of the **versionStringMap** to add an entry that maps the new **TomcatPlugin.TOMCAT_???** to a version string prefix.
    2. Update the **updateContextsToServeDirectly()** to add an **isTomcat??** boolean and **else if** code to instantiate a **Tomcat???PublishModuleVisitor** when that boolean is true.
    3. Update the **checkCatalinaVersion()** method to include checking the **TomcatPlugin.TOMCAT_???** matching the **serverType**.
12. Modify **TomcatRuntimeClasspathProvider.java** to support the new Tomcat version.
    1. Update the **resolveClasspathContainer()** method appropriately. Likely just add an additional runtimeId check to the first **else if** in the method. Also update the **if** statement to set an appropriate **eeVersion** for the new Tomcat version, if appropriate.  Also ensure the **eeVersion** is set to the latest Jakarta EE when exceptions are thrown.
    2. Modify the **getTomcatJavadocLocation()** method to return the appropriate URL for the new and prior version of Tomcat.
    3. Modify the **getStandardsJavadocLocation()** method to return a URL appropriate for Javadoc related to the version of specifications for the new Tomcat version. Navigate from https://projects.eclipse.org/projects/ee4j.jakartaee-platform to find the subproject versions of the Jakarta EE version supported by the new Tomcat version, if needed.

### Modifications to plugins/org.eclipse.jst.server.tomcat.ui

1. Modify **plugin.xml** to add new configuration elements.
    1. Add two new **image** elements to the **org.eclipse.wst.server.ui.serverImages** extension using an existing elements as a template. Update the **id** and **typeIds** appropriately for the new Tomcat version.
    2. Add a new **fragment** element to the **org.eclipse.wst.server.ui.wizardFragments** extension using an existing element as a template. Update the **id** and **typeIds** appropriately for the new Tomcat version.

### Determining values for installable runtimes:
    1. For ostype win32:  filecount = unzip -l apache-tomcat-10.0.13.zip|grep -v '\/$'|grep 'apache-tomcat-'|grep -v ^Archive|wc -l
                          for A in apache-tomcat*zip ; do ls -l $A ; unzip -l $A|grep -v '\/$'|grep 'apache-tomcat-'|grep -v ^Archive|wc -l ; done
    1. For other ostypes: filecount = tar tvzf apache-tomcat-10.0.13.tar.gz|grep -v '\/$'|wc -l
                          for A in apache-tomcat*.tar.gz ; do ls -l $A ; tar tvzf $A|grep -v '\/$'|wc -l;done
