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

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.model.IRuntimeDelegate;
import org.eclipse.wst.server.core.model.IRuntimeWorkingCopyDelegate;
/**
 * 
 */
public class RuntimeWorkingCopy extends Runtime implements IRuntimeWorkingCopy {
	protected Runtime runtime;
	protected WorkingCopyHelper wch;
	
	protected IRuntimeWorkingCopyDelegate workingCopyDelegate;
	
	// from existing runtime
	public RuntimeWorkingCopy(Runtime runtime) {
		super(runtime.getFile());
		this.runtime = runtime;
		
		runtimeType = runtime.getRuntimeType();
		
		map = new HashMap(runtime.map);
		wch = new WorkingCopyHelper(this);
	}
	
	// new runtime
	public RuntimeWorkingCopy(IFile file, String id, IRuntimeType runtimeType) {
		super(file, id, runtimeType);
		//runtime = this;
		wch = new WorkingCopyHelper(this);
		// throw CoreException if the id already exists
	}
	
	public boolean isWorkingCopy() {
		return true;
	}
	
	public IRuntimeWorkingCopy getWorkingCopy() {
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
	
	public void setAttribute(String attributeName, List value) {
		wch.setAttribute(attributeName, value);
	}
	
	public void setAttribute(String attributeName, Map value) {
		wch.setAttribute(attributeName, value);
	}
	
	public void setName(String name) {
		wch.setName(name);
	}
	
	public void setTestEnvironment(boolean b) {
		setAttribute(PROP_TEST_ENVIRONMENT, b);
	}

	public boolean isDirty() {
		return wch.isDirty();
	}

	public void release() {
		wch.release();
		dispose();
		if (runtime != null)
			runtime.release(this);
	}
	
	public IRuntime getOriginal() {
		return runtime;
	}

	public void setLocked(boolean b) {
		wch.setLocked(b);
	}

	public void setPrivate(boolean b) {
		wch.setPrivate(b);
	}
	
	public void setLocation(IPath path) {
		if (path == null)
			setAttribute(PROP_LOCATION, (String)null);
		else
			setAttribute(PROP_LOCATION, path.toString());
	}

	public IRuntime save(IProgressMonitor monitor) {
		if (wch.isReleased())
			return null;
		if (runtime == null)
			runtime = new Runtime(file);
		getWorkingCopyDelegate().handleSave(IRuntimeWorkingCopyDelegate.PRE_SAVE, monitor);
		runtime.setInternal(this);
		runtime.saveToMetadata(monitor);
		wch.setDirty(false);
		release();
		getWorkingCopyDelegate().handleSave(IRuntimeWorkingCopyDelegate.POST_SAVE, monitor);
		return runtime;
	}
	
	public IRuntimeDelegate getDelegate() {
		return getWorkingCopyDelegate();
	}
	
	public IRuntimeWorkingCopyDelegate getWorkingCopyDelegate() {
		if (workingCopyDelegate == null) {
			try {
				RuntimeType runtimeType2 = (RuntimeType) runtimeType;
				workingCopyDelegate = (IRuntimeWorkingCopyDelegate) runtimeType2.getElement().createExecutableExtension("workingCopyClass");
				workingCopyDelegate.initialize((IRuntime) this);
				workingCopyDelegate.initialize(this);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), e);
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
		wch.addPropertyChangeListener(listener);
	}
	
	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		wch.removePropertyChangeListener(listener);
	}
	
	/**
	 * Fire a property change event.
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		wch.firePropertyChangeEvent(propertyName, oldValue, newValue);
	}
	
	public void setDefaults() {
		try {
			getWorkingCopyDelegate().setDefaults();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate setDefaults() " + toString(), e);
		}
	}
}