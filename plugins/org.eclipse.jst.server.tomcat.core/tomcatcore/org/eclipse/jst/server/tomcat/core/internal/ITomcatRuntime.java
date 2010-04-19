/*******************************************************************************
 * Copyright (c) 2004, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jst.server.core.IJavaRuntime;
/**
 * 
 */
public interface ITomcatRuntime extends IJavaRuntime {
	/**
	 * Returns the runtime classpath that is used by this runtime.
	 * 
	 * @return the runtime classpath
	 */
	public List getRuntimeClasspath(IPath configPath);
}