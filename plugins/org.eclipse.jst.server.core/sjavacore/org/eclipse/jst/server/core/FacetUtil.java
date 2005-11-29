/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
/**
 * Utility class for converting between facet runtimes and server runtimes.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 */
public class FacetUtil {
	/**
	 * Returns the server runtime that corresponds to a facet runtime, or null
	 * if none could be found.
	 * 
	 * @param runtime a facet runtime
	 * @return the server runtime that corresponds to the facet runtime, or
	 *    <code>null</code> if none could be found.
	 */
	public static IRuntime getRuntime(org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime) {
		if (runtime == null)
			throw new IllegalArgumentException();
		
		String id = runtime.getProperty("id");
		
		IRuntime[] runtimes = ServerCore.getRuntimes();
		int size = runtimes.length;
		for (int i = 0; i < size; i++) {
			if (id.equals(runtimes[i].getId()))
				return runtimes[i];
		}
		
		return null;
	}

	/**
	 * Returns the facet runtime that corresponds to a server runtime, or null
	 * if none could be found.
	 * 
	 * @param runtime a server runtime
	 * @return the facet runtime that corresponds to the server runtime, or
	 *    <code>null</code> if none could be found.
	 */
	public static org.eclipse.wst.common.project.facet.core.runtime.IRuntime getRuntime(IRuntime runtime) {
		if (runtime == null)
			throw new IllegalArgumentException();
		
		String id = runtime.getId();
		
		Set runtimes = RuntimeManager.getRuntimes();
		Iterator iterator = runtimes.iterator();
		while (iterator.hasNext()) {
			org.eclipse.wst.common.project.facet.core.runtime.IRuntime runtime2 = (org.eclipse.wst.common.project.facet.core.runtime.IRuntime) iterator.next();
			if (id.equals(runtime2.getProperty("id")))
				return runtime2;
		}
		return null;
	}
}