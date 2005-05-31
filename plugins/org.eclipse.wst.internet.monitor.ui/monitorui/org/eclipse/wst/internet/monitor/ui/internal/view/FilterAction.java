/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.wst.internet.monitor.core.internal.IContentFilter;
/**
 * 
 */
public class FilterAction extends Action {
	protected ViewerManager vm;
	protected IContentFilter filter;
	protected boolean enabled;

	public FilterAction(ViewerManager vm, IContentFilter filter) {
		super(filter.getName(), IAction.AS_CHECK_BOX);
		this.vm = vm;
		this.filter = filter;
	}
	
	public void run() {
		if (!isChecked())
			vm.removeFilter(filter);
		else
			vm.addFilter(filter);
	}
}