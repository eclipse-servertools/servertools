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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IModuleListener;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.util.ModuleEvent;
/**
 * 
 */
public class Module implements IModule {
	protected String id;
	protected String name;
	protected IModuleFactory factory;
	protected String type;
	protected String version;
	protected IProject project;
	protected ModuleDelegate delegate;

	// change listeners
	private transient List listeners;

	/**
	 * Module constructor comment.
	 */
	public Module() {
		super();
	}

	/**
	 * Returns the id of this module.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return factory.getId() + ":" + id;
	}

	/**
	 * Returns the type of this module.
	 * 
	 * @return
	 */
	public IModuleType2 getModuleType() {
		return new ModuleType2(type, version);
	}

	/**
	 * Returns the workbench project that this module is contained in,
	 * or null if the module is outside of the workspace.
	 * 
	 * @return org.eclipse.core.resources.IProject
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * @see IModule#getName()
	 */
	public String getName() {
		return name;
	}

	public IServerExtension getExtension(IProgressMonitor monitor) {
		return getDelegate(monitor);
	}

	public ModuleDelegate getDelegate(IProgressMonitor monitor) {
		if (delegate == null) {
			try {
				delegate = null; // TODO
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create delegate" + toString() + ": " + e.getMessage());
			}
		}
		return delegate;
	}

	/**
	 * Returns the child modules of this module.
	 *
	 * @return org.eclipse.wst.server.core.model.IModule[]
	 */
	public IModule[] getChildModules(IProgressMonitor monitor) {
		try {
			return getDelegate(monitor).getChildModules();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getChildModules() " + toString(), e);
			return null;
		}
	}

	/**
	 * Return the validation status of the module.
	 * 
	 * @return
	 */
	public IStatus validate(IProgressMonitor monitor) {
		try {
			return getDelegate(monitor).validate();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate validate() " + toString(), e);
			return null;
		}
	}
	
	/**
	 * Add a listener for the module.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void addModuleListener(IModuleListener listener) {
		Trace.trace(Trace.FINEST, "Adding module listener " + listener + " to " + this);
	
		if (listeners == null)
			listeners = new ArrayList();
		else if (listeners.contains(listener))
			return;
		listeners.add(listener);
	}
	
	/**
	 * Add a listener for the module.
	 *
	 * @param listener org.eclipse.wst.server.core.model.IModuleListener
	 */
	public void removeModuleListener(IModuleListener listener) {
		Trace.trace(Trace.FINEST, "Removing module listener " + listener + " from " + this);
	
		if (listeners != null)
			listeners.remove(listener);
	}
	
	/**
	 * Fire a module change event.
	 */
	protected void fireModuleChangeEvent(boolean isChange, IModule[] added, IModule[] changed, IModule[] removed) {
		Trace.trace(Trace.FINEST, "->- Firing module change event: " + getName() + " (" + isChange + ") ->-");
	
		if (listeners == null || listeners.isEmpty())
			return;
	
		int size = listeners.size();
		IModuleListener[] dcl = new IModuleListener[size];
		listeners.toArray(dcl);
		
		ModuleEvent event = new ModuleEvent(this, isChange, added, changed, removed);
	
		for (int i = 0; i < size; i++) {
			try {
				Trace.trace(Trace.FINEST, "  Firing module change event to: " + dcl[i]);
				dcl[i].moduleChanged(event);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "  Error firing module change event", e);
			}
		}
		Trace.trace(Trace.FINEST, "-<- Done firing module change event -<-");
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "Module[" + getId() + "]";
	}
}