package org.eclipse.jst.server.tomcat.internal.core;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.jst.server.tomcat.core.IMimeMapping;
/**
 * A mime mapping.
 */
public class MimeMapping implements IMimeMapping {
	private String extension;
	private String mimeType;

	/**
	 * MimeMapping constructor comment.
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