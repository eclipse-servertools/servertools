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
package org.eclipse.jst.server.tomcat.core.tests;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatRuntime;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatRuntimeWorkingCopy;
import org.eclipse.wst.server.core.*;

import junit.framework.TestCase;

public class TomcatRuntimeTestCase extends TestCase {
	private static final String RUNTIME_TYPE_ID_32 = "org.eclipse.jst.server.tomcat.runtime.32";
	private static final String RUNTIME_TYPE_ID_40 = "org.eclipse.jst.server.tomcat.runtime.40";
	private static final String RUNTIME_TYPE_ID_41 = "org.eclipse.jst.server.tomcat.runtime.41";
	private static final String RUNTIME_TYPE_ID_50 = "org.eclipse.jst.server.tomcat.runtime.50";
	private static final String RUNTIME_TYPE_ID_55 = "org.eclipse.jst.server.tomcat.runtime.55";

	protected static IRuntime runtime;
	protected static ITomcatRuntime tomcatRuntime;

	protected IRuntimeWorkingCopy createRuntime(String runtimeTypeId) throws Exception {
		IRuntimeType rt = ServerCore.findRuntimeType(runtimeTypeId);
		IRuntimeWorkingCopy wc = rt.createRuntime("a", null);
		wc.setLocation(new Path("c://test"));
		return wc;
	}
	
	protected void validateRuntime() throws Exception {
		IStatus status = runtime.validate(null);
		assertTrue(!status.isOK());
	}
	
	protected void adaptRuntime() throws Exception {
		tomcatRuntime = (ITomcatRuntime) runtime.loadAdapter(ITomcatRuntime.class, null);
		assertNotNull(tomcatRuntime);
		assertNotNull(tomcatRuntime.getVMInstall());
		assertNotNull(tomcatRuntime.getRuntimeClasspath(null));
	}
	
	protected void modifyRuntime() throws Exception {
		IRuntimeWorkingCopy wc = runtime.createWorkingCopy();
		ITomcatRuntimeWorkingCopy trwc = (ITomcatRuntimeWorkingCopy) wc.loadAdapter(ITomcatRuntimeWorkingCopy.class, null);
		trwc.setVMInstall(null);
		wc.save(true, null);
		tomcatRuntime = (ITomcatRuntime) runtime.loadAdapter(ITomcatRuntime.class, null);
		assertNotNull(tomcatRuntime.getVMInstall());
	}
	
	protected void deleteRuntime() throws Exception {
		runtime.delete();
		runtime = null;
		tomcatRuntime = null;
	}

	public void testAll() throws Exception {
		runtime = createRuntime(RUNTIME_TYPE_ID_32).save(false, null);
		assertTrue(!runtime.isWorkingCopy());

		validateRuntime();

		adaptRuntime();

		modifyRuntime();

		deleteRuntime();

		runtime = createRuntime(RUNTIME_TYPE_ID_40).save(false, null);
		assertTrue(!runtime.isWorkingCopy());

		validateRuntime();

		adaptRuntime();

		modifyRuntime();

		deleteRuntime();

		runtime = createRuntime(RUNTIME_TYPE_ID_41).save(false, null);
		assertTrue(!runtime.isWorkingCopy());

		validateRuntime();

		adaptRuntime();

		modifyRuntime();

		deleteRuntime();

		runtime = createRuntime(RUNTIME_TYPE_ID_50).save(false, null);
		assertTrue(!runtime.isWorkingCopy());

		validateRuntime();

		adaptRuntime();

		modifyRuntime();

		deleteRuntime();

		runtime = createRuntime(RUNTIME_TYPE_ID_55).save(false, null);
		assertTrue(!runtime.isWorkingCopy());

		validateRuntime();

		adaptRuntime();

		modifyRuntime();

		deleteRuntime();
	}
}
