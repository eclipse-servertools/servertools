/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public abstract class BaseContentProvider implements IStructuredContentProvider {
	public BaseContentProvider() {
		super();
	}

	public void dispose() {
		// do nothing
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}