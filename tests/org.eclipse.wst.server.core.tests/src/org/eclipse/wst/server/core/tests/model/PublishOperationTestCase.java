/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.wst.server.core.model.PublishOperation;

import junit.framework.TestCase;

public class PublishOperationTestCase extends TestCase {
	protected static PublishOperation task;

	public void testCreate() throws Exception {
		task = new PublishOperation() {
			public void execute(IProgressMonitor monitor, IAdaptable info) throws CoreException {
				// ignore
			}

			public int getOrder() {
				return 0;
			}
		};
		task.getLabel();
		task.getDescription();
		task.getTaskModel();
		task.setTaskModel(null);
		task.execute(null, null);
		task.getKind();
		task.getOrder();
	}
}