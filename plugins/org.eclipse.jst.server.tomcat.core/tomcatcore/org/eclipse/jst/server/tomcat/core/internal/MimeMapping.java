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
package org.eclipse.jst.server.tomcat.core.internal;

/**
 * A mime mapping.
 */
public class MimeMapping implements IMimeMapping {
	private String extension;
	private String mimeType;

	/**
	 * MimeMapping constructor comment.
	 * 
	 * @param extension an extension
	 * @param mimeType a mime type
	 */
	public MimeMapping(String extension, String mimeType) {
		super();
		this.extension = extension;
		this.mimeType = mimeType;
	}

	/**
	 * Returns the extension.
	 * @return java.lang.String
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * Returns the mime type.
	 * @return java.lang.String
	 */
	public String getMimeType() {
		return mimeType;
	}
}