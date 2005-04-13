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
package org.eclipse.wst.server.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.IModuleListener;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.model.ModuleEvent;
/**
 * 
 */
public class Module implements IModule {
	protected String id;
	protected String name;
	protected ModuleFactory factory;
	protected String type;
	protected String version;
	protected IProject project;
	protected ModuleDelegate delegate;

	// change listeners
	private transient List listeners;

	/**
	 * Module constructor.
	 * 
	 * @param factory
	 * @param id
	 * @param name
	 * @param type
	 * @param version
	 * @param project
	 */
	public Module(ModuleFactory factory, String id, String name, String type, String version, IProject project) {
		super();
		this.factory = factory;
		this.project = project;
		this.type = type;
		this.version = version;
		this.id = id;
		this.name = name;
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
	 * Returns the internal (partial) id of this module.
	 *
	 * @return the id
	 */
	public String getInternalId() {
		return id;
	}

	/**
	 * Returns the type of this module.
	 * 
	 * @return the module type
	 */
	public IModuleType getModuleType() {
		return new ModuleType(type, version);
	}

	/**
	 * Returns the workbench project that this module is contained in,
	 * or null if the module is outside of the workspace.
	 * 
	 * @return the project that the module is contained in
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

	protected ModuleDelegate getDelegate() {
		if (delegate == null) {
			try {
				delegate = factory.getDelegate().getModuleDelegate(this);
				delegate.initialize(this);
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate" + toString() + ": " + t.getMessage());
			}
		}
		return delegate;
	}

	/**
	 * Returns the child modules of this module.
	 *
	 * @param monitor a progress monitor
	 * @return a possibly empty array of modules
	 */
	public IModule[] getChildModules(IProgressMonitor monitor) {
		try {
			return getDelegate().getChildModules();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate getChildModules() " + toString(), e);
			return null;
		}
	}

	/**
	 * Validates this module.
	 * <p>
	 * [issue: Conjecture: Each different type of module prescribes
	 * legal arrangements of, and the significance of, the files within
	 * it. This would be spelled out in the spec for the particular
	 * module types.
	 * This validate operation is suppose to check the actual
	 * arrangement of files in this module to see whether they
	 * meet expectations.
	 * It's an open question as to how "strenuous" a check this
	 * is.]
	 * </p>
	 * <p>
	 * [issue: Old comment said: "If there is an error
	 * that should block the server from starting (e.g. major errors)
	 * it should be returned from this method. This method can also be used to
	 * return warning for such things as an open (and dirty) editor."]
	 * </p>
	 * <p>
	 * [issue: All existing implementations of this return null,
	 * which is illegal.]
	 * </p>
	 * <p>
	 * [issue: Old comment said: "Returns an IStatus that is used to determine if this object can
	 * be published to the server." Since the same module can
	 * be associated with any number of servers, "the server" is
	 * ill-defined.]
	 * </p>
	 * <p>
	 * [issue: Old comment said: "Should return an error if there
	 * is a major problem with the resources, or can be used to
	 * return warnings on unsaved files, etc." It is usually
	 * difficult in principle for core-level infrastructure to
	 * detect whether there are open editors with unsaved changes.]
	 * </p>
	 *
	 * @return a status object with code <code>IStatus.OK</code> if the given
	 * module is valid, otherwise a status object indicating what is
	 * wrong with it
	 */
	public IStatus validate(IProgressMonitor monitor) {
		try {
			return getDelegate().validate();
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
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		ModuleDelegate delegate2 = getDelegate();
		if (adapter.isInstance(delegate2))
			return delegate;
		return null;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof IModule))
			return false;
		
		IModule m = (IModule) obj;
		if (!getId().equals(m.getId()))
			return false;
		//if (!project.equals(m.getProject()))
		//	return false;
		//if (!getModuleType().equals(m.getModuleType()))
		//	return false;
		
		return true;
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