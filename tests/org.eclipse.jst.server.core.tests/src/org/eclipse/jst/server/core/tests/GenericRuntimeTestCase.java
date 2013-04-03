/*******************************************************************************
 * Copyright (c) 2004, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.core.internal.GenericRuntimeUtil;
import org.eclipse.jst.server.core.internal.IGenericRuntime;
import org.eclipse.jst.server.core.internal.IGenericRuntimeWorkingCopy;
import org.eclipse.wst.server.core.*;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/* Note: These tests may be executed in any order.  Because null is used as most
 * arguments, the order doesn't currently matter.  If non-null arguments are used,
 * it may be necessary to rewrite the tests to make them truly order independent.
 */

public class GenericRuntimeTestCase extends TestCase {
	private static final String RUNTIME_TYPE_ID = "org.eclipse.jst.server.core.runtimeType";

	protected static IRuntime runtime;

	protected IRuntime getRuntime() throws Exception {
		if (runtime == null) {
			IRuntimeType rt = ServerCore.findRuntimeType(RUNTIME_TYPE_ID);
			IRuntimeWorkingCopy wc = rt.createRuntime("a", null);
			wc.setLocation(new Path("c://test"));
			runtime = wc.save(false, null);
		}
		return runtime;
	}

	protected IGenericRuntime getGenericRuntime() throws Exception {
		return (IGenericRuntime)getRuntime().getAdapter(IGenericRuntime.class);
	}

	protected IRuntime getRuntimeWC() throws Exception {
		return getRuntime().createWorkingCopy();
	}

	protected IGenericRuntimeWorkingCopy getGenericRuntimeWC() throws Exception {
		return (IGenericRuntimeWorkingCopy)getRuntimeWC().loadAdapter(IGenericRuntimeWorkingCopy.class, null);
	}

	public static void addOrderedTests(TestSuite suite) {
		suite.addTest(TestSuite.createTest(GenericRuntimeTestCase.class, "deleteRuntime"));
	}

	public void testAll() throws Exception {
		assertTrue(!getRuntime().isWorkingCopy());

		IStatus status = getRuntime().validate(null);
		assertTrue(!status.isOK());

		assertTrue(GenericRuntimeUtil.isGenericJ2EERuntime(getRuntime()));

		assertNotNull(getGenericRuntime());

		assertNotNull(getRuntime().getAdapter(IGenericRuntimeWorkingCopy.class));

		assertNotNull(getGenericRuntime().getVMInstall());

		assertNotNull(getGenericRuntimeWC());

		assertNotNull(getRuntimeWC().loadAdapter(IGenericRuntime.class, null));

		assertNotNull(getGenericRuntimeWC().getVMInstall());
		assertNotNull(getGenericRuntimeWC().getVMInstall());
	}

	public void deleteRuntime() throws Exception {
		getRuntime().delete();
		runtime = null;
	}
}