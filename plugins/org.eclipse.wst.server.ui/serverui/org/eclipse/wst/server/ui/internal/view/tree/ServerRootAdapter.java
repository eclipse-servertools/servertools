/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.view.tree;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.model.IWorkbenchAdapter;
/**
 * 
 */
public class ServerRootAdapter implements IAdaptable, IWorkbenchAdapter {
	protected Object[] children;

	public ServerRootAdapter() {
		// do nothing
	}
	
	public void setChildren(Object[] obj) {
		children = obj;
	}

	/*
	 * @see IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter.equals(IAdaptable.class))
			return this;
		else if (adapter.equals(IWorkbenchAdapter.class))
			return this;
		else
			return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/*
	 * @see IWorkbenchAdapter#getChildren(Object)
	 */
	public Object[] getChildren(Object o) {
		return children;
	}

	/*
	 * @see IWorkbenchAdapter#getImageDescriptor(Object)
	 */
	public ImageDescriptor getImageDescriptor(Object object) {
		return null;
	}

	/*
	 * @see IWorkbenchAdapter#getLabel(Object)
	 */
	public String getLabel(Object o) {
		return "Server Root";
	}

	/*
	 * @see IWorkbenchAdapter#getParent(Object)
	 */
	public Object getParent(Object o) {
		return null;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof ServerRootAdapter))	
			return false;
		
		ServerRootAdapter adapter = (ServerRootAdapter) obj;
		return (adapter.children.equals(children));
	}
	
	public int hashCode() {
		return 0;
	}
}