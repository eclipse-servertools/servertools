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
 * 
 * @since 1.0
 */
public interface ILooseArchiveSupport {
	/**
	 * Return the loose archives that are contained within this enterprise
	 * application.
	 *
	 * @return a possibly empty array of modules contained within this application
	 */
	public ILooseArchive[] getLooseArchives();

	/**
	 * Returns the URI of the given loose archive within this
	 * enterprise application.
	 *
	 * @param archive a loose archive
	 * @return the URI of the given archive, or <code>null</code> if the URI could
	 *    not be found
	 */
	public String getURI(ILooseArchive archive);
}