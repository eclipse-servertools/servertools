/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.*;
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

	public Runtime(IFile file) {
		super(file);
	}

	public Runtime(IFile file, String id, IRuntimeType runtimeType) {
		super(file, id);
		this.runtimeType = runtimeType;
		map.put(PROP_NAME, runtimeType.getName());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntime#getRuntimeType()
	 */
	public IRuntimeType getRuntimeType() {
		return runtimeType;
	}

	/**
	 * Return the validation status of the runtime.
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

	public RuntimeDelegate getDelegate(IProgressMonitor monitor) {
		if (delegate != null)
			return delegate;
		
		synchronized (this) {
			if (delegate == null) {
				try {
					long time = System.currentTimeMillis();
					RuntimeType runtimeType2 = (RuntimeType) runtimeType;
					delegate = (RuntimeDelegate) runtimeType2.getElement().createExecutableExtension("class");
					delegate.initialize(this);
					Trace.trace(Trace.PERFORMANCE, "Runtime.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getRuntimeType().getId());
				} catch (Throwable t) {
					Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), t);
				}
			}
		}
		return delegate;
	}
	
	/**
	 * Returns true if the delegate has been loaded.
	 * 
	 * @return
	 */
	public boolean isDelegateLoaded() {
		return delegate != null;
	}
	
	public void dispose() {
		if (delegate != null)
			delegate.dispose();
	}
	
	public IRuntimeWorkingCopy createWorkingCopy() {
		return new RuntimeWorkingCopy(this); 
	}

	public boolean isWorkingCopy() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntime#getLocation()
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

	public boolean isStub() {
		return getAttribute(PROP_STUB, false);
	}

	protected void setInternal(RuntimeWorkingCopy wc) {
		map = wc.map;
		runtimeType = wc.runtimeType;
		file = wc.file;
		delegate = wc.delegate;
		
		int timestamp = wc.getTimestamp();
		map.put("timestamp", Integer.toString(timestamp+1));
	}

	protected void loadState(IMemento memento) {
		String runtimeTypeId = memento.getString(PROP_RUNTIME_TYPE_ID);
		if (runtimeTypeId != null)
			runtimeType = ServerCore.findRuntimeType(runtimeTypeId);
		else
			runtimeType = null;
	}

	protected void saveState(IMemento memento) {
		if (runtimeType != null)
			memento.putString(PROP_RUNTIME_TYPE_ID, runtimeType.getId());
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Runtime))
			return false;
		
		Runtime runtime = (Runtime) obj;
		return runtime.getId().equals(getId());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		RuntimeDelegate delegate2 = getDelegate(null);
		if (adapter.isInstance(delegate2))
			return delegate;
		return null;
	}
	
	public String toString() {
		return "Runtime[" + getId() + ", " + getName() + ", " + getLocation() + ", " + getRuntimeType() + "]";
	}
}