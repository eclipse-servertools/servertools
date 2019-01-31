/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;
/**
 * 
 */
public interface IMimeMapping {
	/**
	 * Returns the extension.
	 * 
	 * @return the extension
	 */
	public String getExtension();

	/**
	 * Returns the mime type.
	 * 
	 * @return the mime type
	 */
	public String getMimeType();
}