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
package org.eclipse.wst.server.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IRuntimeType extends IOrdered {
	/**
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 
	 * @return
	 */
	public String getDescription();
	
	public String getVendor();
	
	public String getVersion();

	/**
	 * 
	 * @return
	 */
	public List getModuleTypes();
	
	public boolean canCreate();

	public IRuntimeWorkingCopy createRuntime(String id) throws CoreException;
}