/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.*;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.*;
/**
 * Links builder.
 */
public class ServerBuilder extends IncrementalProjectBuilder {
	// attribute to tag the server core broken link markers
	private static final String SERVER_CORE_MARKER = "server-core-marker";

	/**
	 * @see IncrementalProjectBuilder#build
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
		Trace.trace(Trace.FINEST, "->- Link builder running on: " + getProject().getName() + " " + kind + " " + args + " ->-");
		try {
			IServerProject nature = (IServerProject) getProject().getNature(IServerProject.NATURE_ID);
			Iterator iterator = nature.getServers().iterator();
			while (iterator.hasNext()) {
				IServer server = (IServer) iterator.next();
				updateServerMarkers(server);
			}
			iterator = nature.getServerConfigurations().iterator();
			while (iterator.hasNext()) {
				IServerConfiguration configuration = (IServerConfiguration) iterator.next();
				updateConfigurationMarkers(configuration);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error in link builder", e);
		}
		Trace.trace(Trace.FINEST, "-<- Done link builder running -<-");
		return null;
	}

	/**
	 * startupOnInitialize method comment.
	 */
	protected void startupOnInitialize() {
	}

	/**
	 * Update the "broken link" markers on a specific configuration.
	 */
	protected void updateConfigurationMarkers(IServerConfiguration configuration) {
		/*IResource resource = ServerCore.getResourceManager().getServerResourceLocation(configuration);
	
		List missingProjects = new ArrayList();
	
	//FIX-ME
		// just do immediate projects for now
		String[] projectRefs = configuration.getProjectRefs();
		if (projectRefs != null) {
			int size = projectRefs.length;
			for (int i = 0; i < size; i++) {
				IProject project = Reference.getProjectByRef(projectRefs[i]);
				if (project == null) {
					missingProjects.add(projectRefs[i]);
				}
			}
		}
	
		if (Trace.isTracing())
			Trace.trace("Updating markers on " + configuration + " " + resource.getLocation().toString() + " " + missingProjects);
	
		try {
			boolean found = false;
			IMarker[] marker = resource.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
			if (marker != null) {
				int size = marker.length;
				for (int i = 0; i < size; i++) {
					if (marker[i].getAttribute(SERVER_CORE_MARKER, false)) {
						Trace.trace("marker found");
						try {
							String projectRef = marker[i].getAttribute("projectRef", null);
							if (projectRef != null && missingProjects.contains(projectRef)) {
								// update marker
								Trace.trace("Updating marker: " + marker[i].getId());
								marker[i].setAttribute(IMarker.MESSAGE, ServerPlugin.getResource("%errorMissingProjectTask", new String[] {ServerUtil.getName(configuration), Reference.getProjectNameFromRef(projectRef)}));
								marker[i].setAttribute(IMarker.LOCATION, resource.getLocation().toString());
								marker[i].setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
							} else {
								// remove
								Trace.trace("Removing marker: " + marker[i].getId());
								marker[i].delete();
							}
							missingProjects.remove(projectRef);
						} catch (Exception e) {
							Trace.trace("Error updating markers 1", e);
						}
					}
				}
			}
	
			if (!missingProjects.isEmpty()) {
				Iterator iterator = missingProjects.iterator();
				while (iterator.hasNext()) {
					String projectRef = (String) iterator.next();		
					try {
						// add new marker
						Trace.trace("Adding new marker");
						IMarker newMarker = resource.createMarker(IMarker.PROBLEM);
						newMarker.setAttribute(SERVER_CORE_MARKER, true);
						newMarker.setAttribute(IMarker.MESSAGE, ServerPlugin.getResource("%errorMissingProjectTask", new String[] {ServerUtil.getName(configuration), Reference.getProjectNameFromRef(projectRef)} ));
						newMarker.setAttribute(IMarker.LOCATION, resource.getLocation().toString());
						newMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					} catch (Exception e) {
						Trace.trace("Error updating markers 2", e);
					}
				}
			}
		} catch (Exception e) {
			Trace.trace("Error updating markers", e);
		}
		Trace.trace("Done updating markers");
		*/
	}

	/**
	 * Update the "broken link" markers on a specific server.
	 */
	protected void updateServerMarkers(IServer server) {
		IFile file = server.getFile();
		if (file == null || !file.exists())
			return;
		
		if (!server.getServerType().hasServerConfiguration())
			return;
	
		IServerConfiguration configuration = server.getServerConfiguration();
		boolean isProblem = (configuration == null);
	
		Trace.trace(Trace.FINEST, "Updating markers on " + server + " " + file.getLocation().toString() + " " + isProblem);
	
		try {
			boolean found = false;
			IMarker[] marker = file.findMarkers(IMarker.PROBLEM, false, IResource.DEPTH_ZERO);
			if (marker != null) {
				int size = marker.length;
				for (int i = 0; i < size; i++) {
					if (marker[i].getAttribute(SERVER_CORE_MARKER, false)) {
						try {
							if (isProblem && !found) {
								// update marker
								Trace.trace(Trace.FINEST, "Updating marker: " + marker[i].getId());
								marker[i].setAttribute(IMarker.MESSAGE, ServerPlugin.getResource("%errorMissingConfigurationTask", new String[] {server.getName()}));
								marker[i].setAttribute(IMarker.LOCATION, file.getLocation().toString());
								marker[i].setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
							} else {
								// remove
								Trace.trace(Trace.FINEST, "Removing marker: " + marker[i].getId());
								marker[i].delete();
							}
						} catch (Exception e) {
							Trace.trace(Trace.SEVERE, "Error updating markers 1", e);
						}
						found = true;
					}
				}
			}
	
			if (!found && isProblem) {
				try {
					// add new marker
					Trace.trace(Trace.FINEST, "Adding new marker");
					IMarker newMarker = file.createMarker(IMarker.PROBLEM);
					newMarker.setAttribute(SERVER_CORE_MARKER, true);
					newMarker.setAttribute(IMarker.MESSAGE, ServerPlugin.getResource("%errorMissingConfigurationTask", new String[] {server.getName()}));
					newMarker.setAttribute(IMarker.LOCATION, file.getLocation().toString());
					newMarker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Error updating markers 2", e);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error updating markers", e);
		}
		Trace.trace(Trace.FINEST, "Done updating markers");
	}
}