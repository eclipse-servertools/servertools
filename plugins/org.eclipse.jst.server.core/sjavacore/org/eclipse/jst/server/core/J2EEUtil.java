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
package org.eclipse.jst.server.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
/**
 *
 */
public class J2EEUtil {
	/**
	 * Returns the enterprise applications that the module is contained within.
	 * 
	 * @param module
	 * @return
	 */
	public static IEnterpriseApplication[] getEnterpriseApplications(IJ2EEModule module) {
		List list = new ArrayList();
		IModule[] modules = ServerUtil.getModules("j2ee.ear");
		if (modules != null) {
			int size = modules.length;
			for (int i = 0; i < size; i++) {
				IModule module2 = modules[i];
				IEnterpriseApplication ear = (IEnterpriseApplication) module2.getAdapter(IEnterpriseApplication.class);
				if (ear != null) {
					IJ2EEModule[] modules2 = ear.getModules();
					if (modules2 != null) {
						int size2 = modules2.length;
						for (int j = 0; j < size2; j++) {
							if (modules2[j].equals(module))
								list.add(ear);
						}
					}
				}
			}
		}
		IEnterpriseApplication[] ears = new IEnterpriseApplication[list.size()];
		list.toArray(ears);
		return ears;
	}
}