/*******************************************************************************
 * Copyright (c) 2007,2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.Collections;
import java.util.List;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jst.common.project.facet.core.StandardJreRuntimeComponent;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.server.core.IRuntime;

public class JRERuntimeComponentProvider extends RuntimeComponentProviderDelegate {

	public List<IRuntimeComponent> getRuntimeComponents(IRuntime runtime) {
		// define JRE component
		IJavaRuntime javaRuntime = (IJavaRuntime) runtime.loadAdapter(IJavaRuntime.class, null);
		if (javaRuntime != null) {
		    final IVMInstall vmInstall = javaRuntime.getVMInstall();
		    final IRuntimeComponent rc = StandardJreRuntimeComponent.create( vmInstall );
			return Collections.singletonList( rc );
		}
		return null;
	}
}