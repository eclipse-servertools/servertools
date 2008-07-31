/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.ResourceManager;
import org.eclipse.wst.server.core.internal.facets.FacetUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Class used to sort categories, runtime types, and server types in the
 * New wizards.
 */
public class InitialSelectionProvider extends ViewerSorter {

	public Object getInitialSelection(Object[] obj) {
		if (obj == null || obj.length == 0)
			return null;
		
		if (obj[0] instanceof IRuntimeType) {
			int size = obj.length;
			IRuntimeType[] rt = new IRuntimeType[size];
			for (int i = 0; i < size; i++)
				rt[i] = (IRuntimeType) obj[i];
			return getInitialSelection(rt);
		}
		
		if (obj[0] instanceof IServerType) {
			int size = obj.length;
			IServerType[] st = new IServerType[size];
			for (int i = 0; i < size; i++)
				st[i] = (IServerType) obj[i];
			return getInitialSelection(st);
		}
		
		if (obj[0] instanceof IServer) {
			int size = obj.length;
			IServer[] st = new IServer[size];
			for (int i = 0; i < size; i++)
				st[i] = (IServer) obj[i];
			return getInitialSelection(st);
		}
		
		return null;
	}

	/**
	 * 
	 * @param serverTypes
	 * @return the initial selection
	 */
	public IServerType getInitialSelection(IServerType[] serverTypes) {
		if (serverTypes == null)
			return null;
		
		int size = serverTypes.length;
		for (int i = 0; i < size; i++) {
			if (hasRuntime(serverTypes[i]))
				return serverTypes[i];
		}
		return getDefaultServerType(serverTypes);
	}

	/**
	 * 
	 * @param servers
	 * @return the initial selection
	 */
	public IServer getInitialSelection(IServer[] servers) {
		return getInitialSelection(servers,null);
	}
	
	/**
	 * Allows adopters to provide an initial selection out of a list of items.
	 * <p>
	 * The <code>IProject</code> can be null, in cases where a project selection was not available (ie: New Server Wizard)
	 * </p><p>
	 * Returning <code>null</code> means no object is applicable to be selected. 
	 * </p>
	 * 
	 * @param servers
	 * @param project
	 * @return
	 */
	public IServer getInitialSelection(IServer[] servers, IProject project){
		if (servers == null)
			return null;
		
		IServer rval = servers[0];
		
		if (project != null){
			try{
				// check for the targeted runtime of the project
				IFacetedProject facetedProject = ProjectFacetsManager.create(project);
				if (facetedProject != null){
					org.eclipse.wst.common.project.facet.core.runtime.IRuntime facetedRuntime = facetedProject.getPrimaryRuntime();
						if (facetedRuntime != null){
							IRuntime runtime = FacetUtil.getRuntime(facetedRuntime);
							IServer server = findServerFromRuntime(runtime.getId());
							if (server != null){
								rval = server;
							}
						}
					}
				}
				catch (CoreException ce){
					Trace.trace(Trace.WARNING,"Could not create a faceted project",ce);
				}
			}
		return rval;
	}

	/**
	 * 
	 * @param runtimeTypes
	 * @return the initial selection
	 */
	public IRuntimeType getInitialSelection(IRuntimeType[] runtimeTypes) {
		if (runtimeTypes == null)
			return null;
		
		int size = runtimeTypes.length;
		for (int i = 0; i < size; i++) {
			if (hasRuntime(runtimeTypes[i]))
				return runtimeTypes[i];
		}
		return getDefaultRuntimeType(runtimeTypes);
	}

	protected boolean hasRuntime(IServerType serverType) {
		return hasRuntime(serverType.getRuntimeType());
	}

	protected boolean hasRuntime(IRuntimeType runtimeType) {
		if (runtimeType == null)
			return false;
		IRuntime[] runtimes = ServerUIPlugin.getRuntimes(runtimeType);
		return runtimes != null && runtimes.length > 0;
	}

	/**
	 * Returns a default server type, typically the 'first' one sorted
	 * alphabetically by name.
	 * 
	 * @param serverTypes
	 * @return the default server type
	 */
	protected IServerType getDefaultServerType(IServerType[] serverTypes) {
		if (serverTypes == null)
			return null;
		
		int size = serverTypes.length;
		if (size == 1)
			return serverTypes[0];
		
		IServerType first = serverTypes[0];
		for (int i = 1; i < size; i++) {
			if (DefaultViewerSorter.compareServerTypes(first, serverTypes[i]) > 0)
				first = serverTypes[i];
		}
		return first;
	}

	/**
	 * Returns a default runtime type, typically the 'first' one sorted
	 * alphabetically by name.
	 * 
	 * @param runtimeTypes
	 * @return the default runtime type
	 */
	protected IRuntimeType getDefaultRuntimeType(IRuntimeType[] runtimeTypes) {
		if (runtimeTypes == null)
			return null;
		
		int size = runtimeTypes.length;
		if (size == 1)
			return runtimeTypes[0];
		
		IRuntimeType first = runtimeTypes[0];
		for (int i = 1; i < size; i++) {
			if (DefaultViewerSorter.compareRuntimeTypes(first, runtimeTypes[i]) > 0)
				first = runtimeTypes[i];
		}
		return first;
	}
	
	/**
	 * Allows adopters to provide an initial selection out of a list of items.
	 * <p>
	 * The <code>IProject</code> can be null, in cases where a project selection was not available (ie: New Server Wizard)
	 * </p><p>
	 * Returning <code>null</code> means no object is applicable to be selected. 
	 * </p>
	 * 
	 * @param obj Contains an array of all the possible object to be selected. 
	 * @param project
	 * @return The object to be selected from the <code>obj[]</code> 
	 */
	public Object getInitialSelection(Object [] obj, IProject project){
		if (obj == null || obj.length == 0)
			return null;
		
		if (obj[0] instanceof IRuntimeType) {
			int size = obj.length;
			IRuntimeType[] rt = new IRuntimeType[size];
			for (int i = 0; i < size; i++)
				rt[i] = (IRuntimeType) obj[i];
			return getInitialSelection(rt);
		}
		
		if (obj[0] instanceof IServerType) {
			int size = obj.length;
			IServerType[] st = new IServerType[size];
			for (int i = 0; i < size; i++)
				st[i] = (IServerType) obj[i];
			return getInitialSelection(st);
		}
		
		if (obj[0] instanceof IServer) {
			int size = obj.length;
			IServer[] st = new IServer[size];
			for (int i = 0; i < size; i++)
				st[i] = (IServer) obj[i];
			return getInitialSelection(st,project);
		}
		
		return null;		
	}
	
	/**
	 * Returns the server with the given runtime id, or <code>null</code> 
	 * if none. This convenience method searches the list of registered servers
	 * for the matching runtime id. The id may not be null.
	 * 
	 * @param runtimeId
	 * @return
	 */
	private static IServer findServerFromRuntime(String runtimeId){
		if (runtimeId == null)
			throw new IllegalArgumentException();
			
		IServer [] servers = ResourceManager.getInstance().getServers();
		for (IServer server:servers){
			if (runtimeId == server.getRuntime().getId()){
				return server;
			}
		}
		return null;
	}
	
}