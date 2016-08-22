/**********************************************************************
 * Copyright (c) 2016 SAS Institute and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    SAS Institute - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.JarResources;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.PostResources;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.PreResources;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;

public class Tomcat85PublishModuleVisitor extends TomcatPublishModuleVisitor {

	/**
	 * Instantiate a new Tomcat85PublishModuleVisitor
	 * 
	 * @param baseDir catalina base path
	 * @param tomcatVersion tomcat version
	 * @param serverInstance ServerInstance containing server.xml contents
	 * @param sharedLoader string value for shared.loader catalina configuration property
	 * @param enableMetaInfResources flag to indicate if Servlet 3.0 "META-INF/resources" feature should be supported
	 */
	Tomcat85PublishModuleVisitor(IPath baseDir, String tomcatVersion, ServerInstance serverInstance, String sharedLoader, boolean enableMetaInfResources) {
		super(baseDir, tomcatVersion, serverInstance, sharedLoader, enableMetaInfResources);
	}

    /**
     * {@inheritDoc}
     */
	@Override
    public void endVisitWebComponent(IVirtualComponent component)
            throws CoreException {

        // track context changes, don't rewrite if not needed
        boolean dirty = false;

        IModule module = ServerUtil.getModule(component.getProject());

        // we need this for the user-specified context path
        Context context = findContext(module);
        if (context == null) {
        	String name = module != null ? module.getName() : component.getName();
    		Trace.trace(Trace.SEVERE, "Could not find context for module " + name);
    		throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
    				NLS.bind(Messages.errorPublishContextNotFound, name), null));
        }

		dirty = includeProjectContextXml(component, context);
		dirty = updateDocBaseAndPath(component, context);

		// Add WEB-INF/classes elements as PreResources
		for (Iterator iterator = virtualClassClasspathElements.iterator();
				iterator.hasNext();) {
			Object virtualClassClasspathElement = iterator.next();
			PreResources preResources = (PreResources)context.getResources().createElement("PreResources");
			preResources.setClassName("org.apache.catalina.webresources.DirResourceSet");
			preResources.setBase(virtualClassClasspathElement.toString());
			preResources.setWebAppMount("/WEB-INF/classes");
			preResources.setInternalPath("/");
			preResources.setClassLoaderOnly("false");
		}
		virtualClassClasspathElements.clear();

		// Add Jars as JarResources if a jar, or as PostResources if a utility project
		for (Iterator iterator = virtualJarClasspathElements.iterator();
				iterator.hasNext();) {
			Object virtualJarClassClasspathElement = iterator.next();
			String jarPath = virtualJarClassClasspathElement.toString();
			if (jarPath.endsWith(".jar")) {
				JarResources jarResources = (JarResources)context.getResources().createElement("JarResources");
				jarResources.setClassName("org.apache.catalina.webresources.JarResourceSet");
				jarResources.setBase(jarPath);
				jarResources.setWebAppMount("/WEB-INF/classes");
				jarResources.setInternalPath("/");
				jarResources.setClassLoaderOnly("true");
			}
			else {
				PostResources postResources = (PostResources)context.getResources().createElement("PostResources");
				postResources.setClassName("org.apache.catalina.webresources.DirResourceSet");
				postResources.setBase(jarPath);
				postResources.setWebAppMount("/WEB-INF/classes");
				postResources.setInternalPath("/");
				postResources.setClassLoaderOnly("false");
				// Map META-INF tld files to WEB-INF
				File metaInfDir = new File(jarPath + "/META-INF");
				if (metaInfDir.isDirectory() && metaInfDir.exists()) {
					// Map META-INF directory directly to /META-INF
					postResources = (PostResources)context.getResources().createElement("PostResources");
					postResources.setClassName("org.apache.catalina.webresources.DirResourceSet");
					postResources.setBase(metaInfDir.getPath());
					postResources.setWebAppMount("/META-INF");
					postResources.setInternalPath("/");
					postResources.setClassLoaderOnly("false");

					File [] tldFiles = metaInfDir.listFiles(new FileFilter() {
							public boolean accept(File file) {
								if (file.isFile() && file.getName().endsWith(".tld")) {
									return true;
								}
								return false;
							}
						});
					for (int i = 0; i < tldFiles.length; i++) {
						postResources = (PostResources)context.getResources().createElement("PostResources");
						postResources.setClassName("org.apache.catalina.webresources.FileResourceSet");
						postResources.setBase(tldFiles[0].getPath());
						postResources.setWebAppMount("/WEB-INF/" + tldFiles[0].getName());
						postResources.setInternalPath("/");
						postResources.setClassLoaderOnly("false");
					}
				}
			}
		}
		virtualJarClasspathElements.clear();

		Set<String> rtPathsProcessed = new HashSet<String>();
		Set<String> locationsIncluded = new HashSet<String>();
		String docBase = context.getDocBase();
		locationsIncluded.add(docBase);
		Map<String, String> retryLocations = new HashMap<String, String>();
		IVirtualResource [] virtualResources = component.getRootFolder().getResources("");
		// Loop over the module's resources
		for (int i = 0; i < virtualResources.length; i++) {
			String rtPath = virtualResources[i].getRuntimePath().toString();
			// Note: The virtual resources returned only know their runtime path.
			// Asking for the project path for this resource performs a lookup
			// that will only return the path for the first mapping for the
			// runtime path.  Thus use of getUnderlyingResources() is necessary.
			// However, this returns matching resources from all mappings so
			// we have to try to keep only those that are mapped directly
			// to the runtime path in the .components file.

			// If this runtime path has not yet been processed
			if (!rtPathsProcessed.contains(rtPath)) {
				// If not a Java related resource
				if (!"/WEB-INF/classes".equals(rtPath)) {
					// Get all resources for this runtime path
					IResource[] underlyingResources = virtualResources[i].getUnderlyingResources();
					// If resource is mapped to "/", then we know it corresponds directly
					// to a mapping in the .components file
					if ("/".equals(rtPath)) {
						for (int j = 0; j < underlyingResources.length; j++) {
							IPath resLoc = underlyingResources[j].getLocation();
							String location = resLoc.toOSString();
							if (!location.equals(docBase)) {
								PreResources preResources = (PreResources)context.getResources().createElement("PreResources");
								preResources.setClassName("org.apache.catalina.webresources.DirResourceSet");
								preResources.setBase(location);
								preResources.setWebAppMount("/");
								preResources.setInternalPath("/");
								preResources.setClassLoaderOnly("false");
								// Add to the set of locations included
								locationsIncluded.add(location);
							}
						}
					}
					// Else this runtime path is something other than "/"
					else {
						int idx = rtPath.lastIndexOf('/');
						// If a "normal" runtime path
						if (idx >= 0) {
							// Get the name of the last segment in the runtime path
							String lastSegment = rtPath.substring(idx + 1);
							// Check the underlying resources to determine which correspond to mappings
							for (int j = 0; j < underlyingResources.length; j++) {
								IPath resLoc = underlyingResources[j].getLocation();
								String location = resLoc.toOSString();
								// If the last segment of the runtime path doesn't match the
								// the last segment of the location, then we have a direct mapping
								// from the .contents file.
								if (!lastSegment.equals(resLoc.lastSegment())) {
									PreResources preResources = (PreResources)context.getResources().createElement("PreResources");
									preResources.setClassName("org.apache.catalina.webresources.DirResourceSet");
									preResources.setBase(location);
									preResources.setWebAppMount(rtPath);
									preResources.setInternalPath("/");
									preResources.setClassLoaderOnly("false");
									// Add to the set of locations included
									locationsIncluded.add(location);
								}
								// Else last segment of runtime path did match the last segment
								// of the location.  We likely have a subfolder of a mapping
								// that matches a portion of the runtime path.
								else {
									// Since we can't be sure, save so it can be check again later
									retryLocations.put(location, rtPath);
								}
							}
						}
					}
				}
				// Add the runtime path to those already processed
				rtPathsProcessed.add(rtPath);
			}
		}
		// If there are locations to retry, add any not yet included in extra paths setting
		if (!retryLocations.isEmpty()) {
			// Remove retry locations already included in the extra paths
			for (Iterator iterator = retryLocations.keySet().iterator(); iterator.hasNext();) {
				String location = (String)iterator.next();
				for (Iterator iterator2 = locationsIncluded.iterator(); iterator2.hasNext();) {
					String includedLocation = (String)iterator2.next();
					if (location.equals(includedLocation) || location.startsWith(includedLocation + File.separator)) {
						iterator.remove();
						break;
					}
				}
			}
			// If any entries are left, include them in the extra paths
			if (!retryLocations.isEmpty()) {
				for (Iterator iterator = retryLocations.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry entry = (Map.Entry)iterator.next();
					String location = (String)entry.getKey();
					String rtPath = (String)entry.getValue();
					PreResources preResources = (PreResources)context.getResources().createElement("PreResources");
					preResources.setClassName("org.apache.catalina.webresources.DirResourceSet");
					preResources.setBase(location);
					preResources.setWebAppMount(rtPath);
					preResources.setInternalPath("/");
					preResources.setClassLoaderOnly("false");
				}
			}
		}
		if (!virtualDependentResources.isEmpty()) {
			for (Map.Entry<String, List<String>> entry : virtualDependentResources.entrySet()) {
				String rtPath = entry.getKey();
				List<String> locations = entry.getValue();
				for (String location : locations) {
					PostResources postResources = (PostResources)context.getResources().createElement("PostResources");
					postResources.setClassName("org.apache.catalina.webresources.DirResourceSet");
					postResources.setBase(location);
					postResources.setWebAppMount(rtPath.length() > 0 ? rtPath : "/");
					postResources.setInternalPath("/");
					postResources.setClassLoaderOnly("false");
				}
			}
		}
		virtualDependentResources.clear();

		if (dirty) {
			//TODO If writing to separate context XML files, save "dirty" status for later use
		}
	}
}
