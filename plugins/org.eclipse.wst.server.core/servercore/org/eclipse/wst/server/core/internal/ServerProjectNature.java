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
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.eclipse.wst.server.core.*;
/**
 * A nature for projects that contain servers and server configurations.
 */
public class ServerProjectNature implements IServerProject, IProjectNature {
	public static final String BUILDER_ID = ServerPlugin.PLUGIN_ID + ".builder";

	// the project that contains this nature
	protected IProject project;
	
	protected static List serverProjects = new ArrayList();

	/**
	 * ServerProjectNature constructor.
	 */
	public ServerProjectNature() {
		super();
	}
	
	/**
	 * Adds a builder to the build spec for the given project.
	 */
	private void addToBuildSpec(String builderID) throws CoreException {
		if (builderID == null)
			return;

		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
	
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(builderID))
				return;
		}
		// add builder to project
		ICommand command = description.newCommand();
		command.setBuilderName(builderID);

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		newCommands[newCommands.length - 1] = command;

		description.setBuildSpec(newCommands);
		getProject().setDescription(description, new NullProgressMonitor());
	}

	/**
	 * Nothing to configure in this nature.
	 */
	public void configure() throws CoreException {
		Trace.trace(Trace.FINEST, "Server project configured");

		// add link builder
		addToBuildSpec(BUILDER_ID);
	}

	/**
	 * Nothing to deconfigure in this nature.
	 */
	public void deconfigure() {	}

	/**
	 * Returns a list of the available folders.
	 * @return java.util.List
	 */
	public List getAvailableFolders() {
		List list = new ArrayList();
		list.add(getProject());
	
		int t = 0;
		while (t < list.size()) {
			try {
				IContainer container = (IContainer) list.get(t);
				IResource[] members = container.members();
				if (members != null) {
					for (int j = 0; j < members.length; j++) {
						if (members[j] instanceof IFolder)
							list.add(members[j]);
					}
				}
			} catch (Exception e) { }
			t++;
		}
		return list;
	}

	/**
	 * Returns the project that this nature is associated with.
	 *
	 * @return org.eclipse.core.resources.IProject
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Returns the server configurations that are located in
	 * this project.
	 *
	 * @return java.util.List
	 */
	public List getServerConfigurations() {
		List list = new ArrayList();
		IResourceManager rm = ServerCore.getResourceManager();
		IServerConfiguration[] configs = rm.getServerConfigurations();
		if (configs != null) {
			int size = configs.length;
			for (int i = 0; i < size; i++) {
				IFile file = configs[i].getFile();
				if (file != null && file.getProject().equals(project))
					list.add(configs[i]);
			}
		}
		return list;
	}

	/**
	 * Returns the servers that are located in
	 * this project.
	 *
	 * @return java.util.List
	 */
	public List getServers() {
		List list = new ArrayList();
		IResourceManager rm = ServerCore.getResourceManager();
		IServer[] servers = rm.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				IFile file = servers[i].getFile();
				if (file != null && file.getProject().equals(project))
					list.add(servers[i]);
			}
		}
		return list;
	}

	/**
	 * Load all of the servers and server configurations.
	 */
	public void loadFiles() {
		Trace.trace(Trace.FINER, "Initial server resource load for " + project.getName(), null);
		final ResourceManager rm = (ResourceManager) ServerCore.getResourceManager();
	
		try {
			getProject().accept(new IResourceVisitor() {
				public boolean visit(IResource resource) {
					try {
						if (resource instanceof IFile) {
							IFile file = (IFile) resource;
							rm.handleNewFile(file, new NullProgressMonitor());
							return false;
						}
						return true;
						//return !rm.handleNewServerResource(resource, new NullProgressMonitor());
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Error during initial server resource load", e);
					}
					return true;
				}
			});
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load server project " + project.getName(), e);
		}
	}

	/**
	 * Sets the project that this nature belongs to.
	 *
	 * @param project org.eclipse.core.resources.IProject
	 */
	public void setProject(IProject project) {
		Trace.trace(Trace.FINER, "Server project: " + project);
		this.project = project;
		loadFiles();
		serverProjects.add(project);
	}
}