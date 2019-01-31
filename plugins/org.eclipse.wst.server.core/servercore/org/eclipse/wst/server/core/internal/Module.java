/*******************************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IModule2;
import org.eclipse.wst.server.core.model.ModuleDelegate;
/**
 * 
 */
public class Module implements IModule2 {
	protected String id;
	protected String name;
	protected ModuleFactory factory;
	protected ModuleType moduleType;
	protected IProject project;
	protected ModuleDelegate delegate;
	protected String id2;

	protected Map<String, String> properties;
	
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
		this(factory, id, name, type, version, project, null);
	}
	
	/**
	 * Module constructor with properties
	 * 
	 * @param factory
	 * @param id
	 * @param name
	 * @param type
	 * @param version
	 * @param project
	 */
	public Module(ModuleFactory factory, String id, String name, String type, String version, IProject project, Map<String, String> properties) {
		super();
		this.factory = factory;
		this.project = project;
		this.moduleType = ModuleType.getModuleType(type, version);
		this.id = id;
		this.name = name;
		if (factory != null)
			id2 = factory.getId() + ":";
		else
			id2 = ":";
		id2 += id;
		this.properties = properties;
	}

	/**
	 * Returns the id of this module.
	 *
	 * @return the id
	 */
	public String getId() {
		return id2;
	}

	/**
	 * Returns the internal (partial) id of this module.
	 *
	 * @return the partial id
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
		return moduleType;
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

	/**
	 * Returns the <code>ModuleFactory</code> that created this module.
	 * 
	 * @return the module factory that created this module
	 */
	public ModuleFactory getModuleFactory() {
		return factory;
	}

	protected ModuleDelegate getDelegate(IProgressMonitor monitor) {
		if (delegate != null)
			return delegate;
		
		synchronized (this) {
			if (delegate == null) {
				try {
					long time = System.currentTimeMillis();
					delegate = factory.getDelegate(monitor).getModuleDelegate(this);
					delegate.initialize(this);
					if (Trace.PERFORMANCE) {
						Trace.trace(Trace.STRING_PERFORMANCE, "Module.getDelegate(): <"
								+ (System.currentTimeMillis() - time) + " " + factory.getId());
					}
				} catch (Throwable t) {
					if (Trace.WARNING) {
						Trace.trace(Trace.STRING_WARNING, "Could not create delegate " + toString(), t);
					}
				}
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
			return getDelegate(monitor).getChildModules();
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate getChildModules() " + toString(), e);
			}
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
	 * @param monitor a progress monitor, or <code>null</code> if no
	 *    progress reporting is required
	 * @return a status object with code <code>IStatus.OK</code> if the given
	 *    module is valid, otherwise a status object indicating what is
	 *    wrong with it
	 */
	public IStatus validate(IProgressMonitor monitor) {
		try {
			return getDelegate(monitor).validate();
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate validate() " + toString(), e);
			}
			return null;
		}
	}

	/**
	 * @see IModule#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		if (delegate != null) {
			if (adapter.isInstance(delegate))
				return delegate;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * @see IModule#loadAdapter(Class, IProgressMonitor)
	 */
	public Object loadAdapter(Class adapter, IProgressMonitor monitor) {
		getDelegate(monitor);
		if (adapter.isInstance(delegate))
			return delegate;
		return Platform.getAdapterManager().loadAdapter(this, adapter.getName());
	}

	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (!(obj instanceof IModule))
			return false;
		
		IModule m = (IModule) obj;
		if (!id2.equals(m.getId()))
			return false;
		if (project != null && m.getProject() != null && !project.equals(m.getProject()))
			return false;
		//if (!getModuleType().equals(m.getModuleType()))
		//	return false;
		
		return true;
	}

	public int hashCode() {
		int result = id2.hashCode() + 17;
		if (project != null)
			result += project.hashCode() * 37;
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IModule#isExternal()
	 */
	public boolean isExternal() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IModule#exists()
	 */
	public boolean exists() {
		return true;
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "Module[" + name + "," + id2 + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IModule2#getProperty(java.lang.String)
	 */
	public String getProperty(String key) {
		return properties == null || key == null ? null : properties.get(key);
	}
}