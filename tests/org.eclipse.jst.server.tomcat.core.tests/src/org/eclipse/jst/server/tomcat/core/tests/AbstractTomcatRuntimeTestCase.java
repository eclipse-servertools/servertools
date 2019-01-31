/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests;

import junit.framework.TestSuite;

import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.tests.ext.AbstractRuntimeTestCase;

public abstract class AbstractTomcatRuntimeTestCase extends AbstractRuntimeTestCase {
	protected abstract String getRuntimeTypeId();

	public IRuntime createRuntime() throws Exception {
		try {
			IRuntimeWorkingCopy wc = createRuntime(getRuntimeTypeId());
			return wc.save(true, null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void deleteRuntime(IRuntime runtime2) throws Exception {
		runtime2.delete();
	}

	protected static IRuntimeWorkingCopy createRuntime(String runtimeTypeId) throws Exception {
		IRuntimeType rt = ServerCore.findRuntimeType(runtimeTypeId);
		IRuntimeWorkingCopy wc = rt.createRuntime(null, null);
		wc.setLocation(new Path(RuntimeLocation.runtimeLocation));
		return wc;
	}

	public static void addOrderedTests(Class testClass,TestSuite suite) {
		AbstractRuntimeTestCase.addOrderedTests(testClass, suite);
		AbstractRuntimeTestCase.addFinalTests(testClass, suite);
	}
}
