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
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * Utility class for dealing with J2EE modules.
 */
public class J2EEUtil {
	private static final String EAR_MODULE = "jst.ear";
	/**
	 * Returns the enterprise applications that the module is contained within.
	 * 
	 * @param module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly empty array of enterprise applications
	 */
	public static IModule[] getEnterpriseApplications(IJ2EEModule module, IProgressMonitor monitor) {
		List list = new ArrayList();
		IModule[] modules = ServerUtil.getModules(EAR_MODULE);
		if (modules != null) {
			int size = modules.length;
			for (int i = 0; i < size; i++) {
				IModule module2 = modules[i];
				IEnterpriseApplication ear = (IEnterpriseApplication) module2.loadAdapter(IEnterpriseApplication.class, monitor);
				if (ear != null) {
					IModule[] modules2 = ear.getModules();
					if (modules2 != null) {
						int size2 = modules2.length;
						for (int j = 0; j < size2; j++) {
							if (module.equals(modules2[j].loadAdapter(IJ2EEModule.class, monitor)))
								list.add(module2);
						}
					}
				}
			}
		}
		IModule[] ears = new IModule[list.size()];
		list.toArray(ears);
		return ears;
	}
}