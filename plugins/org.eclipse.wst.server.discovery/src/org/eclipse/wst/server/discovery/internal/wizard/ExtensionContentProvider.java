/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal.wizard;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
/**
 * Extension content provider.
 */
public class ExtensionContentProvider implements IStructuredContentProvider {
	protected List list;

	public ExtensionContentProvider(List list) {
		super();
		this.list = list;
	}

	public void dispose() {
		// do nothing
	}

	public Object[] getElements(Object inputElement) {
		return list.toArray();
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}