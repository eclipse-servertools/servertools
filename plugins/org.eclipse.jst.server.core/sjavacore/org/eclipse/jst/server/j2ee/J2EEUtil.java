/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.j2ee;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.IModule;
/**
 *
 */
public class J2EEUtil {
	/**
	 * Returns the enterprise applications that the module is contained within.
	 * 
	 * @param module org.eclipse.jst.server.j2ee.IJ2EEModule
	 * @return org.eclipse.jst.server.j2ee.IEnterpriseApplication
	 */
	public static IEnterpriseApplication[] getEnterpriseApplications(IJ2EEModule module) {
		Iterator iterator = ServerUtil.getModules("j2ee.ear", "*", false).iterator();
		List list = new ArrayList();
		while (iterator.hasNext()) {
			IModule module2 = (IModule) iterator.next();
			if (module2 instanceof IEnterpriseApplication) {
				IEnterpriseApplication ear = (IEnterpriseApplication) module2;
				IJ2EEModule[] modules = ear.getModules();
				if (modules != null) {
					int size = modules.length;
					for (int i = 0; i < size; i++) {
						if (modules[i].equals(module))
							list.add(ear);
					}
				}
			}
		}
		IEnterpriseApplication[] ears = new IEnterpriseApplication[list.size()];
		list.toArray(ears);
		return ears;
	}
}
