/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
	 * @return the workbench
	 */
	public IWorkbench getWorkbench() {
		return workbench;
	}

	/**
	 * Initialize the workbench and current selection.
	 * 
	 * @param newWorkbench
	 * @param newSelection
	 */
	public void init(IWorkbench newWorkbench, IStructuredSelection newSelection) {
		workbench = newWorkbench;
		selection = newSelection;
	}
}