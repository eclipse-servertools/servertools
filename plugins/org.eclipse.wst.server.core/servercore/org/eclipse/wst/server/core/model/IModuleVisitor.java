/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.model;

import java.util.List;
/**
 * A visitor on a server's modules.
 */
public interface IModuleVisitor {
	/**
	 * Visit a single module. Returns true to keep visiting, and
	 * false if it should stop visiting the module. 
	 * 
	 * @param parents java.util.List
	 * @param module org.eclipse.wst.server.core.model.IModule
	 * @return boolean
	 */
	public boolean visit(List parents, IModule module);
}
