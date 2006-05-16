/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeLifecycleListener;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.osgi.framework.BundleContext;
/**
 * The main server tooling plugin class.
 */
public class JavaServerPlugin extends Plugin {
	/**
	 * Java server plugin id
	 */
	public static final String PLUGIN_ID = "org.eclipse.jst.server.core";

	// singleton instance of this class
	private static JavaServerPlugin singleton;

	//	cached copy of all runtime classpath providers
	private static List runtimeClasspathProviders;

	//	cached copy of all runtime facet mappings
	private static List runtimeFacetMappings;

	// runtime listener
	private static IRuntimeLifecycleListener runtimeListener;

    private static final Set messagesLogged = new HashSet();
    
	/**
	 * Create the JavaServerPlugin.
	 */
	public JavaServerPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 *
	 * @return a singleton instance
	 */
	public static JavaServerPlugin getInstance() {
		return singleton;
	}

	/**
	 * @see Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		runtimeListener = new IRuntimeLifecycleListener() {
			public void runtimeAdded(IRuntime runtime) {
				handleRuntimeChange(runtime);
			}

			public void runtimeChanged(IRuntime runtime) {
				handleRuntimeChange(runtime);
			}

			public void runtimeRemoved(IRuntime runtime) {
				handleRuntimeChange(runtime);
			}
		};
		
		ServerCore.addRuntimeLifecycleListener(runtimeListener);
	}

	/**
	 * @see Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ServerCore.removeRuntimeLifecycleListener(runtimeListener);
		super.stop(context);
	}

	/**
	 * Handle a runtime change by potentially updating the classpath container.
	 * 
	 * @param runtime a runtime
	 */
	protected void handleRuntimeChange(final IRuntime runtime) {
		if (runtime == null)
			throw new IllegalArgumentException();
		
		Trace.trace(Trace.FINEST, "Possible runtime change: " + runtime);
		
		final RuntimeClasspathProviderWrapper rcpw = findRuntimeClasspathProvider(runtime.getRuntimeType());
		if (rcpw != null && rcpw.hasRuntimeClasspathChanged(runtime)) {
			final IPath serverContainerPath = new Path(RuntimeClasspathContainer.SERVER_CONTAINER)
				.append(rcpw.getId()).append(runtime.getId());
			
			class RebuildRuntimeReferencesJob extends Job {
				public RebuildRuntimeReferencesJob() {
					super(NLS.bind(Messages.updateClasspathContainers, runtime.getName()));
				}

				public boolean belongsTo(Object family) {
					return ServerPlugin.PLUGIN_ID.equals(family);
				}

				public IStatus run(IProgressMonitor monitor) {
					IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
					if (projects != null) {
						int size = projects.length;
						for (int i = 0; i < size; i++) {
							if (projects[i].isAccessible()) {
								try {
									IJavaProject javaProject = JavaCore.create(projects[i]);
									
									boolean found = false;
									IClasspathEntry[] ce = javaProject.getRawClasspath();
									for (int j = 0; j < ce.length; j++) {
										if (ce[j].getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
											if (serverContainerPath.isPrefixOf(ce[j].getPath()))
												found = true;
										}
									}
									
									Trace.trace(Trace.FINEST, "Classpath change on: " + projects[i] + " " + found);
									
									if (found) {
										RuntimeClasspathContainer container = new RuntimeClasspathContainer(
												serverContainerPath, rcpw, runtime);
										JavaCore.setClasspathContainer(serverContainerPath, new IJavaProject[] { javaProject },
												new IClasspathContainer[] {container}, null);
									}
								} catch (Exception e) {
									Trace.trace(Trace.SEVERE, "Could not update classpath container", e);
								}
							}
						}
					}
					
					return Status.OK_STATUS;
				}
			}
			RebuildRuntimeReferencesJob job = new RebuildRuntimeReferencesJob();
			job.schedule();
		}
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status a status
	 */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}
	
	/**
	 * Convenience method for logging.
	 *
	 * @param t a throwable
	 */
	public static void log(Throwable t) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Internal error", t)); //$NON-NLS-1$
	}

    public static void log( final String msg )
    {
        log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, null ) );
    }
    
    public static void logError( final String msg )
    {
        logError( msg, false );
    }
    
    public static void logError( final String msg,
                                 final boolean suppressDuplicates )
    {
        if( suppressDuplicates && messagesLogged.contains( msg ) )
        {
            return;
        }
        
        messagesLogged.add( msg );
        
        log( new Status( IStatus.ERROR, PLUGIN_ID, IStatus.OK, msg, null ) );
    }

    public static void logWarning( final String msg )
    {
        logWarning( msg, false );
    }
    
    public static void logWarning( final String msg,
                                   final boolean suppressDuplicates )
    {
        if( suppressDuplicates && messagesLogged.contains( msg ) )
        {
            return;
        }
        
        messagesLogged.add( msg );
        
        log( new Status( IStatus.WARNING, PLUGIN_ID, IStatus.OK, msg, null ) );
    }
    
	/**
	 * Returns an array of all known runtime classpath provider instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime classpath provider instances
	 *    {@link RuntimeClasspathProviderWrapper}
	 */
	public static RuntimeClasspathProviderWrapper[] getRuntimeClasspathProviders() {
		if (runtimeClasspathProviders == null)
			loadRuntimeClasspathProviders();
		
		RuntimeClasspathProviderWrapper[] rth = new RuntimeClasspathProviderWrapper[runtimeClasspathProviders.size()];
		runtimeClasspathProviders.toArray(rth);
		return rth;
	}

	/**
	 * Returns the runtime classpath provider that supports the given runtime type, or <code>null</code>
	 * if none. This convenience method searches the list of known runtime
	 * classpath providers ({@link #getRuntimeClasspathProviders()}) for the one with
	 * a matching runtime type.
	 * The runtimeType may not be null.
	 *
	 * @param runtimeType a runtime type
	 * @return the runtime classpath provider instance, or <code>null</code> if
	 *   there is no runtime classpath provider with the given id
	 */
	public static RuntimeClasspathProviderWrapper findRuntimeClasspathProvider(IRuntimeType runtimeType) {
		if (runtimeType == null)
			throw new IllegalArgumentException();

		if (runtimeClasspathProviders == null)
			loadRuntimeClasspathProviders();
		
		Iterator iterator = runtimeClasspathProviders.iterator();
		while (iterator.hasNext()) {
			RuntimeClasspathProviderWrapper runtimeClasspathProvider = (RuntimeClasspathProviderWrapper) iterator.next();
			if (runtimeClasspathProvider.supportsRuntimeType(runtimeType))
				return runtimeClasspathProvider;
		}
		return null;
	}

	/**
	 * Returns the runtime classpath provider with the given id, or <code>null</code>
	 * if none. This convenience method searches the list of known runtime
	 * classpath providers ({@link #getRuntimeClasspathProviders()}) for the one with
	 * a matching runtime classpath provider id ({@link RuntimeClasspathProviderWrapper#getId()}).
	 * The id may not be null.
	 *
	 * @param id the runtime classpath provider id
	 * @return the runtime classpath provider instance, or <code>null</code> if
	 *   there is no runtime classpath provider with the given id
	 */
	public static RuntimeClasspathProviderWrapper findRuntimeClasspathProvider(String id) {
		if (id == null)
			throw new IllegalArgumentException();

		if (runtimeClasspathProviders == null)
			loadRuntimeClasspathProviders();
		
		Iterator iterator = runtimeClasspathProviders.iterator();
		while (iterator.hasNext()) {
			RuntimeClasspathProviderWrapper runtimeClasspathProvider = (RuntimeClasspathProviderWrapper) iterator.next();
			if (id.equals(runtimeClasspathProvider.getId()))
				return runtimeClasspathProvider;
		}
		return null;
	}

	/**
	 * Load the runtime classpath providers.
	 */
	private static synchronized void loadRuntimeClasspathProviders() {
		if (runtimeClasspathProviders != null)
			return;
		Trace.trace(Trace.CONFIG, "->- Loading .runtimeClasspathProviders extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(JavaServerPlugin.PLUGIN_ID, "runtimeClasspathProviders");

		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new RuntimeClasspathProviderWrapper(cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded runtimeClasspathProviders: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeClasspathProviders: " + cf[i].getAttribute("id"), t);
			}
		}
		runtimeClasspathProviders = list;
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .runtimeClasspathProviders extension point -<-");
	}
	
	/**
	 * Returns an array of all known runtime classpath provider instances.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return a possibly-empty array of runtime classpath provider instances
	 *    {@link RuntimeClasspathProviderWrapper}
	 */
	public static RuntimeFacetMapping[] getRuntimeFacetMapping() {
		if (runtimeFacetMappings == null)
			loadRuntimeFacetMapping();
		
		RuntimeFacetMapping[] rfm = new RuntimeFacetMapping[runtimeFacetMappings.size()];
		runtimeFacetMappings.toArray(rfm);
		return rfm;
	}

	/**
	 * Load the runtime facet mappings.
	 */
	private static synchronized void loadRuntimeFacetMapping() {
		if (runtimeFacetMappings != null)
			return;
		Trace.trace(Trace.CONFIG, "->- Loading .runtimeFacetMapping extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(JavaServerPlugin.PLUGIN_ID, "runtimeFacetMappings");

		int size = cf.length;
		List list = new ArrayList(size);
		for (int i = 0; i < size; i++) {
			try {
				list.add(new RuntimeFacetMapping(cf[i]));
				Trace.trace(Trace.CONFIG, "  Loaded runtimeFacetMapping: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load runtimeFacetMapping: " + cf[i].getAttribute("id"), t);
			}
		}
		runtimeFacetMappings = list;
		
		Trace.trace(Trace.CONFIG, "-<- Done loading .runtimeFacetMapping extension point -<-");
	}
}