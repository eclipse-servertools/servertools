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
package org.eclipse.wst.server.ui.internal.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
/**
 * An abstract Wizard that contains helper methods.
 */
public abstract class AbstractWizard extends Wizard {
	protected IWorkbench workbench;
	protected IStructuredSelection selection;

	/**
	 * AbstractWizard constructor comment.
	 */
	public AbstractWizard() {
		super();
	}

	/**
	 * Return the current workbench.
	 * 
	 * @return org.eclipse.ui.IWorkbench
	 */
	public IWorkbench getWorkbench() {
		return workbench;
	}

	/**
	 * Initialize the workbench and current selection.
	 * 
	 * @param org.eclipse.ui.IWorkbench
	 * @param org.eclipse.jface.viewers.IStructuredSelection
	 */
	public void init(IWorkbench newWorkbench, IStructuredSelection newSelection) {
		workbench = newWorkbench;
		selection = newSelection;
	}
}
