/*******************************************************************************
 * Copyright (c) 2003, 2021 IBM Corporation and others.
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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.InternalInitializer;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class Runtime extends Base implements IRuntime {
	protected static final String PROP_RUNTIME_TYPE_ID = "runtime-type-id";
	protected static final String PROP_LOCATION = "location";
	protected static final String PROP_TEST_ENVIRONMENT = "test-environment";
	protected static final String PROP_STUB = "stub";

	protected IRuntimeType runtimeType;
	protected RuntimeDelegate delegate;

	/**
	 * Create a new runtime.
	 * 
	 * @param file
	 */
	public Runtime(IFile file) {
		super(file);
	}

	/**
	 * Create a new runtime.
	 * 
	 * @param file
	 * @param id
	 * @param runtimeType
	 */
	public Runtime(IFile file, String id, IRuntimeType runtimeType) {
		super(file, id);
		this.runtimeType = runtimeType;
		map.put(PROP_NAME, runtimeType.getName());
	}

	/**
	 * @see IRuntime#getRuntimeType()
	 */
	public IRuntimeType getRuntimeType() {
		return runtimeType;
	}

	/**
	 * @see IRuntime#validate(IProgressMonitor)
	 */
	public IStatus validate(IProgressMonitor monitor) {
		try {
			return getDelegate(monitor).validate();
		} catch (Exception e) {
			ServerPlugin.logExtensionFailure(toString(), e);
			return new Status(IStatus.ERROR, getClass(), "Exception validating runtime");
		}
	}

	protected RuntimeDelegate getDelegate(IProgressMonitor monitor) {
		if (delegate != null)
			return delegate;
		
		synchronized (this) {
			if (delegate == null) {
				try {
					long time = System.currentTimeMillis();
					delegate = ((RuntimeType) runtimeType).createRuntimeDelegate();
					if (delegate != null)
						InternalInitializer.initializeRuntimeDelegate(delegate, this, monitor);
					if (Trace.PERFORMANCE) {
						Trace.trace(Trace.STRING_PERFORMANCE, "Runtime.getDelegate(): <"
								+ (System.currentTimeMillis() - time) + "> " + getRuntimeType().getId());
					}
				} catch (Throwable t) {
					ServerPlugin.logExtensionFailure(toString(), t);
				}
			}
		}
		return delegate;
	}

	public void dispose() {
		if (delegate != null) {
			delegate.dispose();
			delegate = null;
		}
	}

	/**
	 * @see IRuntime#createWorkingCopy()
	 */
	public IRuntimeWorkingCopy createWorkingCopy() {
		return new RuntimeWorkingCopy(this); 
	}

	/**
	 * @see IRuntime#isWorkingCopy()
	 */
	public boolean isWorkingCopy() {
		return false;
	}

	/**
	 * @see IRuntime#getLocation()
	 */
	public IPath getLocation() {
		String temp = getAttribute(PROP_LOCATION, (String)null);
		if (temp == null)
			return null;
		return new Path(temp);
	}
	
	protected void deleteFromMetadata() {
		ResourceManager.getInstance().removeRuntime(this);
	}

	protected void saveToMetadata(IProgressMonitor monitor) {
		super.saveToMetadata(monitor);
		ResourceManager.getInstance().addRuntime(this);
	}

	protected String getXMLRoot() {
		return "runtime";
	}

	public boolean isTestEnvironment() {
		return getAttribute(PROP_TEST_ENVIRONMENT, false);
	}

	/**
	 * @see IRuntime#isStub()
	 */
	public boolean isStub() {
		return getAttribute(PROP_STUB, false);
	}

	protected void setInternal(RuntimeWorkingCopy wc) {
		map = wc.map;
		runtimeType = wc.runtimeType;
		file = wc.file;
		delegate = wc.delegate;
	}

	protected void loadState(IMemento memento) {
		resolve();
	}

	protected void resolve() {
		String runtimeTypeId = getAttribute(PROP_RUNTIME_TYPE_ID, (String) null);
		if (runtimeTypeId != null)
			runtimeType = ServerCore.findRuntimeType(runtimeTypeId);
		else
			runtimeType = null;
	}

	protected void saveState(IMemento memento) {
		if (runtimeType != null)
			memento.putString(PROP_RUNTIME_TYPE_ID, runtimeType.getId());
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof Runtime))
			return false;
		
		Runtime runtime = (Runtime) obj;
		return runtime.getId().equals(getId());
	}

	/**
	 * @see IRuntime#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		if (delegate != null) {
			if (adapter.isInstance(delegate))
				return delegate;
		}
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/**
	 * @see IRuntime#loadAdapter(Class, IProgressMonitor)
	 */
	public Object loadAdapter(Class adapter, IProgressMonitor monitor) {
		getDelegate(monitor);
		if (adapter.isInstance(delegate))
			return delegate;
	
		return Platform.getAdapterManager().loadAdapter(this, adapter.getName());
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return "Runtime[" + getId() + ", " + getName() + ", " + getLocation() + ", " + getRuntimeType() + "]";
	}
}
