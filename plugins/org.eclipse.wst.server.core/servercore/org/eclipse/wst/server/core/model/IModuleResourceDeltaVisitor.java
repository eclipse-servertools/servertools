/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.model;

import org.eclipse.core.runtime.CoreException;
/**
 * An objects that visits module resource deltas.
 * <p> 
 * Usage:
 * <pre>
 * class Visitor implements IModuleResourceDeltaVisitor {
 *     public boolean visit(IModuleResourceDelta delta) {
 *       switch (delta.getKind()) {
 *         case IModuleResourceDelta.ADDED :
 *             // handle added resource
 *             break;
 *         case IModuleResourceDelta.REMOVED :
 *             // handle removed resource
 *             break;
 *         case IModuleResourceDelta.CHANGED :
 *             // handle changed resource
 *             break;
 *         }
 *       return true;
 *     }
 * }
 * IModuleResourceDelta rootDelta = ...;
 * rootDelta.accept(new Visitor());
 * </pre>
 * </p>
 * <p>
 * Clients may implement this interface.
 * </p>
 *
 * @see IModuleResourceDelta#accept(IModuleResourceVisitor)
 */
public interface IModuleResourceDeltaVisitor {
	/** 
	 * Visits the given module resource delta.
	 * 
	 * @return <code>true</code> if the resource delta's children should
	 *		be visited; <code>false</code> if they should be skipped.
	 * @exception CoreException if the visit fails for some reason.
	 */
	public boolean visit(IModuleResourceDelta delta) throws CoreException;
}