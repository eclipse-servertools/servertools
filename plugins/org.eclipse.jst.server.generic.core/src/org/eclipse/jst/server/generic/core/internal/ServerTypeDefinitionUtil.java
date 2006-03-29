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
import org.eclipse.jst.server.generic.servertype.definition.ArchiveType;
import org.eclipse.jst.server.generic.servertype.definition.Classpath;
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
		Classpath cp = definition.getClasspath(ref);
		Iterator archives = cp.getArchive().iterator();
		ArrayList entryList = new ArrayList();
		while (archives.hasNext()) {
			ArchiveType archive = (ArchiveType) archives.next();
			String item = definition.getResolver().resolveProperties(archive.getPath());
			IClasspathEntry entry = JavaCore.newLibraryEntry(new Path(item),null,null );
			entryList.add(entry);
		}
		return (IClasspathEntry[])entryList.toArray(new IClasspathEntry[entryList.size()]);
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
