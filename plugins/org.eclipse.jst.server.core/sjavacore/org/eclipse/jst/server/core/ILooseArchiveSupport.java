/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.core;

public interface ILooseArchiveSupport {
	/**
	 * Return the loose archives that are contained within this enterprise
	 * application.
	 *
	 * @return ILooseArchive[]
	 */
	public ILooseArchive[] getLooseArchives();

	/**
	 * Returns the URI of the given loose archive within this
	 * enterprise application.
	 *
	 * @param com.ibm.etools.server.j2ee.ILooseArchive
	 * @return java.lang.String
	 */
	public String getURI(ILooseArchive jar);
}