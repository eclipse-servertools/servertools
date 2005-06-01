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
package org.eclipse.jst.server.core;
/**
 * A J2EE web module.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @since 1.0
 */
public interface IWebModule extends IJ2EEModule {
	/**
	 * Returns a version number in the form "x.y.z".
	 * 
	 * @return java.lang.String
	 */
	public String getServletSpecificationVersion();

	/**
	 * Returns a version number in the form "x.y.z".
	 * 
	 * @return java.lang.String
	 */
	public String getJSPSpecificationVersion();

	/**
	 * Returns the context root of the module.
	 * 
	 * @return java.lang.String
	 */
	public String getContextRoot();
	
	/**
	 * Returns false if the files in this module are placed in the
	 * correct structure for testing before publishing. Returns true
	 * if the file system resources must be published before they will
	 * work.
	 * 
	 * @return boolean
	 */
	public boolean isPublishRequired();
}