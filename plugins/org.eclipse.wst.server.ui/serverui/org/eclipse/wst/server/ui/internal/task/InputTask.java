/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.util.Task;
/**
 * 
 */
public class InputTask extends Task {
	protected String[] ids;
	protected Object[] values;
	
	public InputTask(String id, Object value) {
		this(new String[] { id }, new Object[] { value });
	}

	public InputTask(String[] ids, Object[] values) {
		this.ids = ids;
		this.values = values;
	}
	
	public void execute(IProgressMonitor monitor) {
		int size = ids.length;
		for (int i = 0; i < size; i++) {
			getTaskModel().putObject(ids[i], values[i]);
		}
	}
}