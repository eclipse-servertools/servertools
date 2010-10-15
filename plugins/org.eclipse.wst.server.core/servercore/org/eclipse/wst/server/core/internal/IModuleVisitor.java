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
package org.eclipse.wst.server.core.internal;

import org.eclipse.wst.server.core.IModule;
/**
 * A visitor on a server's modules.
 */
public interface IModuleVisitor {
	/**
	 * Visit a single module. Returns true to keep visiting, and
	 * false if it should stop visiting the module. 
	 * 
	 * @param module a module on the server
	 * @return boolean <code>true</code> to visit the next module, or
	 *    <code>false</code> to stop visiting
	 */
	public boolean visit(IModule[] module);
}