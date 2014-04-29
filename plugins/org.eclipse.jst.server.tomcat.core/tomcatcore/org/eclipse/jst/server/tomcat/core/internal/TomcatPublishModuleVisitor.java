/**********************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Igor Fedorenko & Fabrizio Giustina - Initial API and implementation
 *    Matteo TURRA - Support for multiple web resource paths
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jst.server.tomcat.core.internal.wst.IModuleVisitor;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Loader;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.common.componentcore.resources.IVirtualResource;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * Handles "publishing" for servers that can load classes and resources directly
 * from the workspace. Instead of creating and deploying jars to the webapp this
 * simply update the virtual classpath in the context xml file.
 */
public class TomcatPublishModuleVisitor implements IModuleVisitor {

    /**
     * Server base path (Catalina base).
     */
    protected final IPath baseDir;
    
    /**
     * Tomcat version (from "server.info" property in org.apache.catalina.util.ServerInfo.properties).
     */
    protected final String tomcatVersion;
    
    /**
     * Server instance in which to modify the context
     */
    protected final ServerInstance serverInstance;

    /**
     * Catalina.properties loader to add global classpath entries
     */
    protected final String sharedLoader;

    /**
     * 
     */
    protected final boolean enableMetaInfResources;
    
    /**
     * Classpath entries added by ear configurations.
     */
    protected final List<String> earCommonResources = new ArrayList<String>();

    /**
     * List of classpath elements that will be used by the custom tomcat loader.
     * This set should include any class dir from referenced project.
     */
    protected Set<String> virtualClassClasspathElements = new LinkedHashSet<String>();
    protected Set<String> virtualJarClasspathElements = new LinkedHashSet<String>();

    /**
     * Map of resources found in "META-INF/resources" folder of dependent projects
     */
    protected Map<String, List<String>> virtualDependentResources = new LinkedHashMap<String, List<String>>();

    /**
     * Instantiate a new TomcatPublishModuleVisitor
     * 
     * @param baseDir catalina base path
     * @param tomcatVersion tomcat version
     * @param serverInstance ServerInstance containing server.xml contents
     * @param sharedLoader string value for shared.loader catalina configuration property
     * @param enableMetaInfResources flag to indicate if Servlet 3.0 "META-INF/resources" feature should be supported
     */
    TomcatPublishModuleVisitor(IPath baseDir, String tomcatVersion, ServerInstance serverInstance, String sharedLoader, boolean enableMetaInfResources) {
        this.baseDir = baseDir;
        this.tomcatVersion = tomcatVersion;
        this.serverInstance = serverInstance;
        this.sharedLoader = sharedLoader;
        this.enableMetaInfResources = enableMetaInfResources;
    }

    /**
     * @see IModuleVisitor#visitWebComponent(IVirtualComponent)
     */
    public void visitWebComponent(IVirtualComponent component)
            throws CoreException {
        // nothing to do, everything is done in endVisitWebComponent
    }

    /**
     * @see IModuleVisitor#visitArchiveComponent(IPath, IPath)
     */
    public void visitArchiveComponent(IPath runtimePath, IPath workspacePath) {
        addVirtualJarResource(runtimePath, workspacePath);
    }

    /**
     * @see IModuleVisitor#visitDependentJavaProject(IJavaProject javaProject)
     */
    public void visitDependentJavaProject(IJavaProject javaProject) {
        // Useful for finding source folders, so do nothing.
    }

    /**
     * @see IModuleVisitor#visitDependentComponent(IPath, IPath)
     */
    public void visitDependentComponent(IPath runtimePath, IPath workspacePath) {
        addVirtualJarResource(runtimePath, workspacePath);
    }

    /**
     * @see IModuleVisitor#visitWebResource(IPath, IPath)
     */
    public void visitWebResource(IPath runtimePath, IPath workspacePath) {
        addVirtualClassResource(runtimePath, workspacePath);
    }

    /**
     * @see IModuleVisitor#visitDependentContentResource(IPath, IPath)
     */
    public void visitDependentContentResource(IPath runtimePath, IPath workspacePath) {
    	// Currently, only handle "META-INF/resources" folders if supported
    	if (enableMetaInfResources) {
        	addContentResource(runtimePath, workspacePath);
    	}
    }

    /**
     * @see IModuleVisitor#visitEarResource(IPath, IPath)
     */
    public void visitEarResource(IPath runtimePath, IPath workspacePath) {
        earCommonResources.add(workspacePath.toOSString());
    }

    /**
     * @see IModuleVisitor#endVisitEarComponent(IVirtualComponent)
     */
    public void endVisitEarComponent(IVirtualComponent component)
            throws CoreException {
        if (earCommonResources.size() > 0) {
            try {
                CatalinaPropertiesUtil.addGlobalClasspath(baseDir.append(
                		"conf/catalina.properties").toFile(), sharedLoader,
                		earCommonResources.toArray(new String[earCommonResources.size()]));
            } catch (IOException e) {
                Trace.trace(Trace.WARNING, "Unable to add ear path entries to catalina.properties", e);
            } finally {
                earCommonResources.clear();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
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

        context.getResources().setClassName(
                "org.eclipse.jst.server.tomcat.loader.WtpDirContext");

        Loader loader = context.getLoader();

        loader.setClassName("org.eclipse.jst.server.tomcat.loader.WtpWebappLoader");

        // required for tomcat 5.5.20 due to the change in
        // http://issues.apache.org/bugzilla/show_bug.cgi?id=39704
        loader.setUseSystemClassLoaderAsParent(Boolean.FALSE.toString());

        // Build the virtual classPath setting
		StringBuffer vcBuffer = new StringBuffer(); // Filled with classes entries first, then jar entries added
		StringBuffer vcJarBuffer = new StringBuffer(); // Filled with jar enteries only
		// Build list of additional resource paths and check for additional jars
		StringBuffer rpBuffer = new StringBuffer();

		boolean isTomcat7 = tomcatVersion.startsWith("7.");
		// Add WEB-INF/classes elements to both settings
		for (Iterator iterator = virtualClassClasspathElements.iterator();
				iterator.hasNext();) {
			Object element = iterator.next();
			if (vcBuffer.length() > 0) {
				vcBuffer.append(";");
			}
			vcBuffer.append(element);
			if (isTomcat7) {
				if (rpBuffer.length() > 0) {
					rpBuffer.append(";");
				}
				// Add to resource paths too, so resource artifacts can be found
				rpBuffer.append("/WEB-INF/classes").append("|").append(element);
			}
        }
        for (Iterator iterator = virtualJarClasspathElements.iterator();
        		iterator.hasNext();) {
			vcJarBuffer.append(iterator.next());
        	if (iterator.hasNext()) {
				vcJarBuffer.append(";");
        	}
        }
        virtualClassClasspathElements.clear();
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
								if (rpBuffer.length() != 0) {
									rpBuffer.append(";");
								}
								// Add this location to extra paths setting
								rpBuffer.append(location);
								// Add to the set of locations included
								locationsIncluded.add(location);
								// If a "WEB-INF/classes" exists and is a directory, add to virtual classpath
								File webInfClasses = resLoc.append("WEB-INF/classes").toFile();
								if (webInfClasses.exists() && webInfClasses.isDirectory()) {
									if (vcBuffer.length() != 0) {
										vcBuffer.append(";");
									}
									vcBuffer.append(webInfClasses.getPath());
								}
								// Check if this extra content location contains jars
								File webInfLib = resLoc.append("WEB-INF/lib").toFile();
								// If a "WEB-INF/lib" exists and is a directory, add
								// its jars to the virtual classpath
								if (webInfLib.exists() && webInfLib.isDirectory()) {
									String [] jars = webInfLib.list(new FilenameFilter() {
											public boolean accept(File dir, String name) {
												File f = new File(dir, name);
												return f.isFile() && name.endsWith(".jar");
											}
										});
									for (int k = 0; k < jars.length; k++) {
										if (vcJarBuffer.length() != 0) {
											vcJarBuffer.append(";");
										}
										vcJarBuffer.append(webInfLib.getPath() + File.separator + jars[k]);
									}
								}
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
									if (rpBuffer.length() != 0) {
										rpBuffer.append(";");
									}
									// Add this location to extra paths setting
									rpBuffer.append(rtPath).append("|").append(location);
									// Add to the set of locations included
									locationsIncluded.add(location);
									// Check if this extra content location contains jars
									File webInfLib = null;
									File webInfClasses = null;
									if ("/WEB-INF".equals(rtPath)) {
										webInfLib = resLoc.append("lib").toFile();
										webInfClasses = resLoc.append("classes").toFile();
									}
									else if ("/WEB-INF/lib".equals(rtPath)) {
										webInfLib = resLoc.toFile();
									}
									else if ("/WEB-INF/classes".equals(rtPath)) {
										webInfClasses = resLoc.toFile();
									}
									// If a "WEB-INF/classes" exists and is a directory, add to virtual classpath
									if (webInfClasses != null && webInfClasses.exists() && webInfClasses.isDirectory()) {
										if (vcBuffer.length() != 0) {
											vcBuffer.append(";");
										}
										vcBuffer.append(webInfClasses.getPath());
									}
									// If this "WEB-INF/lib" exists and is a directory, add
									// its jars to the virtual classpath
									if (webInfLib != null && webInfLib.exists() && webInfLib.isDirectory()) {
										String [] jars = webInfLib.list(new FilenameFilter() {
												public boolean accept(File dir, String name) {
													File f = new File(dir, name);
													return f.isFile() && name.endsWith(".jar");
												}
											});
										for (int k = 0; k < jars.length; k++) {
											if (vcJarBuffer.length() != 0) {
												vcJarBuffer.append(";");
											}
											vcJarBuffer.append(webInfLib.getPath() + File.separator + jars[k]);
										}
									}
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
					if (rpBuffer.length() != 0) {
						rpBuffer.append(";");
					}
					rpBuffer.append(rtPath).append("|").append(location);
					// Check if this extra content location contains jars
					File webInfLib = null;
					File webInfClasses = null;
					if ("/WEB-INF".equals(rtPath)) {
						webInfLib = new File(location, "lib");
						webInfClasses = new File(location, "classes");
					}
					else if ("/WEB-INF/lib".equals(rtPath)) {
						webInfLib = new File(location);
					}
					else if ("/WEB-INF/classes".equals(rtPath)) {
						webInfClasses = new File(location);
					}
					// If a "WEB-INF/classes" exists and is a directory, add to virtual classpath
					if (webInfClasses != null && webInfClasses.exists() && webInfClasses.isDirectory()) {
						if (vcBuffer.length() != 0) {
							vcBuffer.append(";");
						}
						vcBuffer.append(webInfClasses.getPath());
					}
					// If a "WEB-INF/lib" exists and is a directory, add
					// its jars to the virtual classpath
					if (webInfLib != null && webInfLib.exists() && webInfLib.isDirectory()) {
						String [] jars = webInfLib.list(new FilenameFilter() {
								public boolean accept(File dir, String name) {
									File f = new File(dir, name);
									return f.isFile() && name.endsWith(".jar");
								}
							});
						for (int k = 0; k < jars.length; k++) {
							if (vcJarBuffer.length() != 0) {
								vcJarBuffer.append(";");
							}
							vcJarBuffer.append(webInfLib.getPath() + File.separator + jars[k]);
						}
					}
				}
			}
		}
		if (!virtualDependentResources.isEmpty()) {
			for (Iterator iterator = virtualDependentResources.entrySet().iterator(); iterator.hasNext();) {
				Map.Entry entry = (Map.Entry)iterator.next();
				String rtPath = (String)entry.getKey();
				List locations = (List)entry.getValue();
				for (Iterator iterator2 = locations.iterator(); iterator2.hasNext();) {
					String location = (String)iterator2.next();
					if (rpBuffer.length() != 0) {
						rpBuffer.append(";");
					}
					if (rtPath.length() > 0) {
						rpBuffer.append(entry.getKey()).append("|").append(location);
					}
					else {
						rpBuffer.append(location);
					}
				}
			}			
		}
		virtualDependentResources.clear();

		// Combine the classes and jar virtual classpaths
		if (vcJarBuffer.length() > 0) {
			if (vcBuffer.length() > 0) {
				vcBuffer.append(';');
			}
			vcBuffer.append(vcJarBuffer);
		}

        String vcp = vcBuffer.toString();
        String oldVcp = loader.getVirtualClasspath();
        if (!vcp.equals(oldVcp)) {
            // save only if needed
            dirty = true;
            loader.setVirtualClasspath(vcp);
            context.getResources().setVirtualClasspath(vcp);
        }

		String resPaths = rpBuffer.toString();
		String oldResPaths = context.getResources().getExtraResourcePaths();
		if (!resPaths.equals(oldResPaths)) {
			dirty = true;
			context.getResources().setExtraResourcePaths(resPaths);
		}
		
		if (enableMetaInfResources) {
			context.findElement("JarScanner").setAttributeValue("scanAllDirectories", "true");
		}

        if (dirty) {
        	//TODO If writing to separate context XML files, save "dirty" status for later use
        }
    }

    private void addVirtualClassResource(IPath runtimePath, IPath workspacePath) {
        virtualClassClasspathElements.add(workspacePath.toOSString());
    }

    private void addVirtualJarResource(IPath runtimePath, IPath workspacePath) {
        virtualJarClasspathElements.add(workspacePath.toOSString());
    }
    
    private void addContentResource(IPath runtimePath, IPath workspacePath) {
    	String rtPath = runtimePath.toString(); 
    	List<String> locations = virtualDependentResources.get(rtPath);
    	if (locations == null) {
    		locations = new ArrayList<String>();
    		virtualDependentResources.put(rtPath, locations);
    	}
    	locations.add(workspacePath.toOSString());
    }
    
    /**
     * Load a META-INF/context.xml file from project, if available
     * 
     * @param component web component containing the context.xml
     * @return context element containing the context.xml
     * @throws CoreException
     */
    protected Context getProjectContextXml(IVirtualComponent component)
            throws CoreException {

        // load or create module's context.xml document
        IVirtualFile contextFile = (IVirtualFile) component.getRootFolder()
                .findMember("META-INF/context.xml");

        Context contextElement = null;

        if (contextFile != null && contextFile.exists()) {

            Factory factory = new Factory();
            factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");

            InputStream fis = null;
            try {
                fis = contextFile.getUnderlyingFile().getContents();
                contextElement = (Context) factory.loadDocument(fis);
            } catch (Exception e) {
                Trace.trace(Trace.SEVERE, "Exception reading " + contextFile, e);
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return contextElement;
    }

    /**
     * Returns the given module from the config.
     * 
     * @param module a web module
     * @return a web module
     */
    protected Context findContext(IModule module) {
        if (module == null) {
            return null;
        }

        String source = module.getId();
        
        Context [] contexts = serverInstance.getContexts();
        for (int i = 0; i < contexts.length; i++) {
        	if (source.equals(contexts[i].getSource()))
        		return contexts[i];
		}
        return null;
    }

	String getContextName(Context context) {
		String contextName = context.getPath();

		// now strip initial /
		if (contextName.startsWith("/")) {
			contextName = contextName.substring(1);
		}

		// root context is deployed with the "ROOT" name in tomcat
		if ("".equals(contextName)) {
			contextName = "ROOT";
		}
		return contextName;
	}

	boolean includeProjectContextXml(IVirtualComponent component, Context context) throws CoreException {
		boolean dirty = false;
		// handle project context.xml
		Context projectContext = getProjectContextXml(component);

		if (projectContext != null) {
			// copy configuration to server context
			projectContext.copyChildrenTo(context);

			Map attrs = projectContext.getAttributes();
			Iterator iter = attrs.keySet().iterator();
			while (iter.hasNext()) {
				String name = (String) iter.next();
				if (!name.equalsIgnoreCase("path")
						&& !name.equalsIgnoreCase("docBase")
						&& !name.equalsIgnoreCase("source")) {
					String value = (String) attrs.get(name);
					String actualValue = context.getAttributeValue(name);
					if (!value.equals(actualValue)) {
						context.setAttributeValue(name, value);
						dirty = true;
					}
				}
			}
		}
		return dirty;
	}

	boolean updateDocBaseAndPath(IVirtualComponent component, Context context) {
		boolean dirty = false;
		// handle changes in docBase
		String docBase = component.getRootFolder().getUnderlyingFolder().getLocation().toOSString();
		if (!docBase.equals(context.getDocBase())) {
			dirty = true;
			context.setDocBase(docBase);
		}

		String contextName = getContextName(context);
		String path = (contextName.equals("ROOT") ? "" : "/" + contextName);
		// handle changes in the path
		// PATH is required for tomcat 5.0, but ignored in tomcat 5.5
		if (!path.equals(context.getPath())) {
			dirty = true;
			context.setPath(path);
		}
		return dirty;
	}
}
