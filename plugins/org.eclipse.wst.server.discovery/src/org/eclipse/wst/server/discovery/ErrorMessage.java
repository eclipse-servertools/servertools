/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery;

/**
 * @since 1.1
 */
public class ErrorMessage {
	
	private String errorTitle;
	private String errorDescription;
	public ErrorMessage(String errorTitle, String errorDescription) {
		super();
		this.errorTitle = errorTitle;
		this.errorDescription = errorDescription;
	}
	public String getErrorTitle() {
		return errorTitle;
	}
	
	public String getErrorDescription() {
		return errorDescription;
	}
	
}
