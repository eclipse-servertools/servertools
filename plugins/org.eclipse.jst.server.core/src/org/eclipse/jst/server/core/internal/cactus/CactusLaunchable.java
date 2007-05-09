/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel R. Somerfield - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal.cactus;

import java.net.URL;
/**
 *
 */
public class CactusLaunchable {
	private String fProjectname;
	private String fClassName;
	private String fTestName;
	private URL fCactusURL;

	public CactusLaunchable(String projectName, String className, String testName, URL cactusURL) {
		fProjectname = projectName;
		fClassName = className;
		fTestName = testName;
		fCactusURL = cactusURL;
	}

	public String getTestClassName() {
		return fClassName;
	}

	public String getTestName() {
		return fTestName;
	}

	public String getProjectName() {
		return fProjectname;
	}

	public URL getCactusURL() {
		return fCactusURL;
	}
}