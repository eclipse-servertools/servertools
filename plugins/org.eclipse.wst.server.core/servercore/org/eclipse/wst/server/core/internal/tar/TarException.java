/*******************************************************************************
 * Copyright (c) 2004, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal.tar;

import java.io.IOException;
/**
 * Exception generated upon encountering corrupted tar files.
 * <p>
 * Copied from org.eclipse.ui.internal.wizards.datatransfer.
 * </p>
 */
public class TarException extends IOException {
	/**
	 * Generated serial version UID for this class.
	 */
	private static final long serialVersionUID = 2886671254518853528L;

    /**
     * Constructs a TarException without a detail string.
     */
    public TarException() {
    	super();
    }
	
	/**
     * Constructs a TarException with the specified detail string.
     *
     * @param s the detail string
     */
    public TarException(String s) {
    	super(s);
    }
}