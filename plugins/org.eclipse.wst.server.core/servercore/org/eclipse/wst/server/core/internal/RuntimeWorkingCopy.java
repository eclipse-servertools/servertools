/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.InternalInitializer;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class RuntimeWorkingCopy extends Runtime implements IRuntimeWorkingCopy {
	protected Runtime runtime;
	protected WorkingCopyHelper wch;
	
	protected RuntimeDelegate workingCopyDelegate;

	/**
	 * Create a new runtime working copy from existing runtime.
	 * 
	 * @param runtime
	 */
	public RuntimeWorkingCopy(Runtime runtime) {
		super(runtime.getFile());
		this.runtime = runtime;
		
		runtimeType = runtime.getRuntimeType();
		
		map = new HashMap<String, Object>(runtime.map);
		wch = new WorkingCopyHelper(this);
	}

	/**
	 * Create a new runtime working copy for a new runtime.
	 * 
	 * @param file
	 * @param id
	 * @param runtimeType
	 */
	public RuntimeWorkingCopy(IFile file, String id, IRuntimeType runtimeType) {
		super(file, id, runtimeType);
		wch = new WorkingCopyHelper(this);
		wch.setDirty(true);
	}

	/**
	 * @see IRuntime#isWorkingCopy()
	 */
	public boolean isWorkingCopy() {
		return true;
	}

	/**
	 * @see IRuntime#createWorkingCopy()
	 */
	public IRuntimeWorkingCopy createWorkingCopy() {
		return this;
	}

	public void setAttribute(String attributeName, int value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, boolean value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, String value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, List<String> value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, Map value) {
		wch.setAttribute(attributeName, value);
	}

	/**
	 * @see IRuntimeWorkingCopy#setName(String)
	 */
	public void setName(String name) {
		wch.setName(name);
		boolean set = getAttribute(PROP_ID_SET, false);
		if (runtime == null && !set)
			setAttribute(PROP_ID, name);
	}

	public void setTestEnvironment(boolean b) {
		setAttribute(PROP_TEST_ENVIRONMENT, b);
	}

	/**
	 * @see IRuntimeWorkingCopy#setStub(boolean)
	 */
	public void setStub(boolean b) {
		setAttribute(PROP_STUB, b);
	}

	/**
	 * @see IRuntimeWorkingCopy#isDirty()
	 */
	public boolean isDirty() {
		return wch.isDirty();
	}
	
	/**
	 * @see IRuntimeWorkingCopy#getOriginal()
	 */
	public IRuntime getOriginal() {
		return runtime;
	}

	/**
	 * @see IRuntimeWorkingCopy#setReadOnly(boolean)
	 */
	public void setReadOnly(boolean b) {
		wch.setLocked(b);
	}

	public void setPrivate(boolean b) {
		wch.setPrivate(b);
	}

	/**
	 * @see IRuntimeWorkingCopy#setLocation(IPath)
	 */
	public void setLocation(IPath path) {
		if (path == null)
			setAttribute(PROP_LOCATION, (String)null);
		else
			setAttribute(PROP_LOCATION, path.toString());
	}

	/**
	 * @see IRuntimeWorkingCopy#save(boolean, IProgressMonitor)
	 */
	public IRuntime save(boolean force, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.subTask(NLS.bind(Messages.savingTask, getName()));
		
		if (!force && getOriginal() != null)
			wch.validateTimestamp(((Runtime) getOriginal()).getTimestamp());
		
		int timestamp = getTimestamp();
		map.put(PROP_TIMESTAMP, Integer.toString(timestamp+1));
		
		if (runtime == null)
			runtime = new Runtime(file);
		
		String oldId = getId();
		String name = getName();
		boolean set = getAttribute(PROP_ID_SET, false);
		if (!oldId.equals(name) && !set) {
			setAttribute(PROP_ID, name);
		} else
			oldId = null;
		
		runtime.setInternal(this);
		runtime.saveToMetadata(monitor);
		wch.setDirty(false);
		
		return runtime;
	}

	protected RuntimeDelegate getWorkingCopyDelegate(IProgressMonitor monitor) {
		if (workingCopyDelegate != null)
			return workingCopyDelegate;
		
		synchronized (this) {
			if (workingCopyDelegate == null) {
				try {
					long time = System.currentTimeMillis();
					workingCopyDelegate = ((RuntimeType) runtimeType).createRuntimeDelegate();
					InternalInitializer.initializeRuntimeDelegate(workingCopyDelegate, this, monitor);
					Trace.trace(Trace.PERFORMANCE, "RuntimeWorkingCopy.getWorkingCopyDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getRuntimeType().getId());
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), e);
				}
			}
		}
		return workingCopyDelegate;
	}

	public void dispose() {
		super.dispose();
		if (workingCopyDelegate != null)
			workingCopyDelegate.dispose();
	}

	/**
	 * Add a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		wch.addPropertyChangeListener(listener);
	}
	
	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		wch.removePropertyChangeListener(listener);
	}
	
	/**
	 * Fire a property change event.
	 * 
	 * @param propertyName a property name
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		wch.firePropertyChangeEvent(propertyName, oldValue, newValue);
	}
	
	/**
	 * Set the defaults for this runtime, including the name.
	 * 
	 * @param monitor a progress monitor, or null
	 */
	protected void setDefaults(IProgressMonitor monitor) {
		try {
			ServerUtil.setRuntimeDefaultName(this);
			getWorkingCopyDelegate(monitor).setDefaults(monitor);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate setDefaults() " + toString(), e);
		}
	}
}