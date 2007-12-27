/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * Utility class for dealing with J2EE modules.
 */
public class J2EEUtil {
	private static final String EAR_MODULE = "jst.ear";
	private static final String WEB_MODULE = "jst.web";

	private static final IModule[] EMPTY_LIST = new IModule[0];
	private static Map<IModule, List<IModule>> earCache;
	private static Map<IJ2EEModule, List<IModule>> earCache2;
	private static Map<IModule, List<IModule>> webCache;
	protected static String cache;

	/**
	 * Returns the enterprise applications that the module is contained within.
	 * 
	 * @param module a J2EE module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly empty array of enterprise applications
	 */
	public static IModule[] getEnterpriseApplications(IJ2EEModule module, IProgressMonitor monitor) {
		if (shouldUseCache()) {
			List<IModule> list = earCache2.get(module);
			if (list == null)
				return EMPTY_LIST;
			return list.toArray(new IModule[list.size()]); 
		}
		
		List<IModule> list = new ArrayList<IModule>();
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
		
		return list.toArray(new IModule[list.size()]);
	}

	/**
	 * Returns the enterprise applications that the module is contained within.
	 * 
	 * @param module a J2EE module or utility module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly empty array of enterprise applications
	 */
	public static IModule[] getEnterpriseApplications(IModule module, IProgressMonitor monitor) {
		if (shouldUseCache()) {
			List<IModule> list = earCache.get(module);
			if (list == null)
				return EMPTY_LIST;
			return list.toArray(new IModule[list.size()]); 
		}
		
		List<IModule> list = new ArrayList<IModule>();
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
							if (module.equals(modules2[j]))
								list.add(module2);
						}
					}
				}
			}
		}
		
		return list.toArray(new IModule[list.size()]);
	}

	/**
	 * Returns the web modules that the utility module is contained within.
	 * 
	 * @param module a utility module
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly empty array of web modules
	 */
	public static IModule[] getWebModules(IModule module, IProgressMonitor monitor) {
		if (shouldUseCache()) {
			List<IModule> list = webCache.get(module);
			if (list == null)
				return EMPTY_LIST;
			return list.toArray(new IModule[list.size()]);
		}
		
		List<IModule> list = new ArrayList<IModule>();
		IModule[] modules = ServerUtil.getModules(WEB_MODULE);
		if (modules != null) {
			int size = modules.length;
			for (int i = 0; i < size; i++) {
				IModule module2 = modules[i];
				IWebModule web = (IWebModule) module2.loadAdapter(IWebModule.class, monitor);
				if (web != null) {
					IModule[] modules2 = web.getModules();
					if (modules2 != null) {
						int size2 = modules2.length;
						for (int j = 0; j < size2; j++) {
							if (module.equals(modules2[j]))
								list.add(module2);
						}
					}
				}
			}
		}
		
		return list.toArray(new IModule[list.size()]);
	}

	private static void fillCache(IProgressMonitor monitor) {
		earCache = new HashMap<IModule, List<IModule>>();
		earCache2 = new HashMap<IJ2EEModule, List<IModule>>();
		webCache = new HashMap<IModule, List<IModule>>();
		
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
							List<IModule> m = earCache.get(modules2[j]);
							if (m == null) {
								m = new ArrayList<IModule>(2);
								earCache.put(modules2[j], m);
							}
							m.add(module2);
							
							IJ2EEModule mod = (IJ2EEModule) modules2[j].loadAdapter(IJ2EEModule.class, monitor);
							if (mod != null) {
								m = earCache2.get(mod);
								if (m == null) {
									m = new ArrayList<IModule>(2);
									earCache2.put(mod, m);
								}
								m.add(module2);
							}
						}
					}
				}
			}
		}
		
		modules = ServerUtil.getModules(WEB_MODULE);
		if (modules != null) {
			int size = modules.length;
			for (int i = 0; i < size; i++) {
				IModule module2 = modules[i];
				IWebModule web = (IWebModule) module2.loadAdapter(IWebModule.class, monitor);
				if (web != null) {
					IModule[] modules2 = web.getModules();
					if (modules2 != null) {
						int size2 = modules2.length;
						for (int j = 0; j < size2; j++) {
							List<IModule> m = webCache.get(modules2[j]);
							if (m == null) {
								m = new ArrayList<IModule>(2);
								webCache.put(modules2[j], m);
							}
							m.add(module2);
						}
					}
				}
			}
		}
	}

	private static boolean shouldUseCache() {
		String s = System.getProperty("J2EEcache");
		if (s == null || "".equals(s) || "false".equals(s)) {
			webCache = null;
			earCache = null;
			return false;
		}
		if (cache == null || !cache.equals(s)) {
			cache = s;
			fillCache(null);
		}
		return true;
	}
}