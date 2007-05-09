/*******************************************************************************
 * Copyright (c) 2005, 2007 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel R. Somerfield - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal.cactus;

import org.eclipse.jst.server.core.internal.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;

public class WebTestableResource extends ModuleArtifactDelegate {
	private boolean fServletIsConfigured;
	private String fClassName;
	private String fTestName;
	private String fProjectName;

	public WebTestableResource(IModule module, boolean servletIsConfigured,
			String projectName, String className, String testName) {
		super(module);
		fServletIsConfigured = servletIsConfigured;
		fClassName = className;
		fTestName = testName;
		fProjectName = projectName;
	}

	public WebTestableResource() {
		super();
	}

	public String getProjectName() {
		return fProjectName;
	}

	public boolean isServletConfigured() {
		return fServletIsConfigured;
	}

	public String getClassName() {
		return fClassName;
	}

	public String getTestName() {
		return fTestName;
	}

	/*
	 * @see ModuleArtifactDelegate#getName()
	 */
	public String getName() {
		return NLS.bind(Messages.artifactCactusTest, fTestName);
	}

	/*
	 * @see ModuleArtifactDelegate#deserialize(String)
	 */
	public void deserialize(String s) {
		int ind = s.indexOf("//");
		super.deserialize(s.substring(0, ind));
		s = s.substring(ind+2);
		ind = s.indexOf("//");
		fProjectName = s.substring(0, ind);
		
		s = s.substring(ind+2);
		ind = s.indexOf("//");
		fClassName = s.substring(0, ind);
		
		if ('T' == s.charAt(ind+2))
			fServletIsConfigured = true;
		else
			fServletIsConfigured = false;
		fTestName = s.substring(ind+3);
	}

	/*
	 * @see ModuleArtifactDelegate#serialize()
	 */
	public String serialize() {
		StringBuffer sb = new StringBuffer(super.serialize());
		sb.append("//");
		sb.append(fProjectName);
		sb.append("//");
		sb.append(fClassName);
		sb.append("//");
		if (fServletIsConfigured)
			sb.append("T");
		else
			sb.append("F");
		sb.append(fTestName);
		return sb.toString();
	}
}