----------------------------------------------------------------------------------
--						Generating JavaDoc and Ext-Pt Docs for 					--
--						org.eclipse.servertools.doc.isv plugin						--
--								05/14/08	(AV)								--
----------------------------------------------------------------------------------

Intro:
	This tool and readme was extracted from an example done by the JSF team. It was
	then adopted to generate the JavaDocs and Ext-Pt for servertools.
	
	
JavaDoc Generation Task
	Intro:
		The javadocBuild.xml file is used to create all the javadocs.   It uses the
		wst-server-core.map file to locate all of our non-test plugins, and then calls a custom
		Ant task to gather all of the non-internal, non-x-friend exported packages from 
		each of the plugins.  The discovered source and API packages are fed to the 
		Javadoc Ant task.
		
		** The packages should be verified as being API.  The MANIFEST.MF is the single 
		source of truth.
		
	
	Setup:
		- ensure that all Servertools plugins for which you intended to create JavaDocs (Test plugins not required) 
		  are present in the workspace
		- ensure releng.servetools project is also present
		- ensure ant-contrib-0.6.jar (or equivalent) and parseAPIPackages.jar is in lib directory
		- if there are API packages to be excluded from the javadoc (deprecated API), 
		set in javadocBuild.properties as comma delimited list in api.exceptions
		
	Generate:
		1) select javadocBuild.xml file in project explorer
		2) Run as "Ant Build..." --> JRE Tab, ensure that "Run in same JRE as Workspace" is 
		selected
		3) check apiPackages.txt for accuracy
	
Extension-point References Task
	Intro:
		The listed-ext-points property in buildExtDocs.properties file is used to generate 
		the HTML documentation from the schemas.   This list must be verified for accuracy 
		and is not generated like the Javadoc API packages are.
		
	Setup:
		- same as for Javadoc above
		
	Generate:
		1) select buildExtDocs.xml file in project explorer
		2) Run as "Ant Build..." --> JRE Tab, ensure that "Run in same JRE as Workspace" is 
		selected

Checking-in:
	Checkin:
	- any changes made in the HTML directory
	- javadoctoc.xml
	- tocreference.xml
	- topics_ExtPoint_Reference.xml

		
