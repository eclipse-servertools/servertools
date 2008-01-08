/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and others.
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
			for (IModule module2 : modules) {
				IEnterpriseApplication ear = (IEnterpriseApplication) module2.loadAdapter(IEnterpriseApplication.class, monitor);
				if (ear != null) {
					IModule[] modules2 = ear.getModules();
					if (modules2 != null) {
						for (IModule m : modules2) {
							if (module.equals(m.loadAdapter(IJ2EEModule.class, monitor)))
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
			for (IModule module2 : modules) {
				IEnterpriseApplication ear = (IEnterpriseApplication) module2.loadAdapter(IEnterpriseApplication.class, monitor);
				if (ear != null) {
					IModule[] modules2 = ear.getModules();
					if (modules2 != null) {
						for (IModule m : modules2) {
							if (module.equals(m))
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
			for (IModule module2 : modules) {
				IWebModule web = (IWebModule) module2.loadAdapter(IWebModule.class, monitor);
				if (web != null) {
					IModule[] modules2 = web.getModules();
					if (modules2 != null) {
						for (IModule m : modules2) {
							if (module.equals(m))
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
			for (IModule module2 : modules) {
				IEnterpriseApplication ear = (IEnterpriseApplication) module2.loadAdapter(IEnterpriseApplication.class, monitor);
				if (ear != null) {
					IModule[] modules2 = ear.getModules();
					if (modules2 != null) {
						for (IModule mm : modules2) {
							List<IModule> m = earCache.get(mm);
							if (m == null) {
								m = new ArrayList<IModule>(2);
								earCache.put(mm, m);
							}
							m.add(module2);
							
							IJ2EEModule mod = (IJ2EEModule) mm.loadAdapter(IJ2EEModule.class, monitor);
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
			for (IModule module2 : modules) {
				IWebModule web = (IWebModule) module2.loadAdapter(IWebModule.class, monitor);
				if (web != null) {
					IModule[] modules2 = web.getModules();
					if (modules2 != null) {
						for (IModule mm : modules2) {
							List<IModule> m = webCache.get(mm);
							if (m == null) {
								m = new ArrayList<IModule>(2);
								webCache.put(mm, m);
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