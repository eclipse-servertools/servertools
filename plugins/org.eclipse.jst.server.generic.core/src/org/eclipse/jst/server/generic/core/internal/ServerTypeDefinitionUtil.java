/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/

package org.eclipse.jst.server.generic.core.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.generic.internal.files.DirectoryScanner;
import org.eclipse.jst.server.generic.servertype.definition.ArchiveType;
import org.eclipse.jst.server.generic.servertype.definition.Classpath;
import org.eclipse.jst.server.generic.servertype.definition.ExcludeType;
import org.eclipse.jst.server.generic.servertype.definition.FilesetType;
import org.eclipse.jst.server.generic.servertype.definition.IncludeType;
import org.eclipse.jst.server.generic.servertype.definition.Module;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;

/**
 * Utilities for ServerRuntime definition files.
 *
 * @author Gorkem Ercan
 */
public class ServerTypeDefinitionUtil 
{
	/**
	 * Returns the server definition for runtime.
	 * @param runtime
	 * @return serverRuntime
	 */
	public static ServerRuntime getServerTypeDefinition(IRuntime runtime)
	{
		if(runtime==null)
			return null;
	    GenericServerRuntime delegate = (GenericServerRuntime)runtime.loadAdapter(GenericServerRuntime.class,null);
	    if(delegate==null)
	    	return null;
		String serverType = delegate.getRuntime().getRuntimeType().getId();
		Map properties = delegate.getServerInstanceProperties();
		ServerRuntime definition = 
			CorePlugin.getDefault().getServerTypeDefinitionManager().getServerRuntimeDefinition(serverType,properties);
		return definition;
	}
	/**
	 * Extracts the server classpath entry array.
	 *
	 * @param runtime
	 * @return classpathEntry
	 */
	public static IClasspathEntry[] getServerClassPathEntry(IRuntime runtime)
	{
		ServerRuntime definition = getServerTypeDefinition(runtime);
		if(definition==null)
			return null;
		String ref = definition.getProject().getClasspathReference();
		ArrayList entryList = getClasspathEntries(ref,definition,false);
		return (IClasspathEntry[])entryList.toArray(new IClasspathEntry[entryList.size()]);
	}
	
	public  static ArrayList getClasspathEntries(String ref, ServerRuntime definition, boolean isLaunch) {
		Classpath cp = definition.getClasspath(ref);
		Iterator archives = cp.getArchive().iterator();
		ArrayList entryList = new ArrayList();
		while (archives.hasNext()) {
			ArchiveType archive = (ArchiveType) archives.next();
			String item = definition.getResolver().resolveProperties(archive.getPath());
			IClasspathEntry entry = null;
			if(isLaunch)
				entryList.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(item)));
			else
				entryList.add(JavaCore.newLibraryEntry(new Path(item),null,null ));
		}
		Iterator fileSets = cp.getFileset().iterator();
		while (fileSets.hasNext()) {
			FilesetType fileset = (FilesetType) fileSets.next();
			String dir = definition.getResolver().resolveProperties(fileset.getDir());
			Iterator includes = fileset.getInclude().iterator();
			String[] inclstr = new String[fileset.getInclude().size()];
			int i=0;
			while (includes.hasNext()) {
				IncludeType incl = (IncludeType) includes.next();
				inclstr[i++] =definition.getResolver().resolveProperties(incl.getName());
			}
			Iterator excludes = fileset.getExclude().iterator();
			String[] exclstr = new String[fileset.getExclude().size()];
			i=0;
			while (excludes.hasNext()) {
				ExcludeType excl = (ExcludeType) excludes.next();
				exclstr[i++] =definition.getResolver().resolveProperties(excl.getName());
			}
			DirectoryScanner scanner = new DirectoryScanner();
			scanner.setBasedir(dir);
			scanner.setIncludes(inclstr);
			scanner.setExcludes(exclstr);
			scanner.scan();
			String[] filesetFiles = scanner.getIncludedFiles();
			for (int j = 0; j < filesetFiles.length; j++) {
				String item = dir+"/"+filesetFiles[j];
				if(isLaunch)
					entryList.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(item)));
				else
					entryList.add(JavaCore.newLibraryEntry(new Path(item),null,null ));

			}
		
		}
		return entryList;
	}
	/**
	 * Given the serverDefinition and module returns the publisher id 
	 * that handles the publishing of module type for this serverDefinition.
	 * @param module
	 * @param serverDefinition
	 * @return publisher id
	 */
	public static String getPublisherID(IModule module, ServerRuntime serverDefinition)
	{
		if(module==null || module.getModuleType()== null)
			return null;
		Module m = serverDefinition.getModule(module.getModuleType().getId());
		return m.getPublisherReference();
	}
	
}
