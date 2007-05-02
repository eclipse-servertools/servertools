/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

public class VerifyResourceSpec {
	private String spec;
	private String [] paths;
	private String otherNames;
	private int lastSuccess = -1;
	private IStatus errorStatus;
	
	public VerifyResourceSpec(String spec) {
		this.spec = spec;
		int altIndex = spec.indexOf('|');
		if (altIndex < 0) {
			paths = new String[1];
			paths[0] = spec.trim();
		}
		else {
			String file1 = spec.substring(0, altIndex).trim();
			String altSpec = spec.substring(altIndex + 1);
			if (altSpec.length() > 0) {
				int index = file1.lastIndexOf(File.separatorChar);
				String dir = index >= 0 ? file1.substring(0, index + 1) : "";
				String [] altNames = altSpec.split("\\|");
				paths = new String[altNames.length + 1];
				paths[0] = file1;
				for (int i = 0; i < altNames.length; i++) {
					paths[i + 1] = dir + altNames[i].trim();
				}
				otherNames = altSpec.replace('|', ',');
			}
			else {
				paths = new String[1];
				paths[0] = file1;
			}
		}
	}
	
	public String [] getPaths() {
		return paths;
	}
	
	public IStatus checkResource(String installDir) {
		if (lastSuccess >= 0 ) {
			File file = new File(installDir, paths[lastSuccess]);
			if (file.exists())
				return Status.OK_STATUS;
		}
		for (int i = 0; i < paths.length; i++) {
			if (i != lastSuccess) {
				File file = new File(installDir, paths[i]);
				if (file.exists()) {
					lastSuccess = i;
					return Status.OK_STATUS;
				}
			}
		}
		// Resource was not found, return error status
		if (errorStatus == null) {
			// Construct error status on first use
			if (paths.length == 1 || otherNames == null) {
				errorStatus = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorInstallDirMissingFile, paths[0]), null);
			}
			else if (paths.length == 2) {
				errorStatus = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorInstallDirMissingFile2, paths[0],  otherNames), null);
			}
			else {
				errorStatus = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorInstallDirMissingFile3, paths[0],  otherNames), null);
			}
		}
		return errorStatus;
	}
	
	public String toString() {
		return spec;
	}
}
