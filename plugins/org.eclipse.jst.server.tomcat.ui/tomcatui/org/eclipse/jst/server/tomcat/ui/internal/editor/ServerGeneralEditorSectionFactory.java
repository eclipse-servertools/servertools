/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import org.eclipse.wst.server.ui.editor.*;
/**
 *
 */
public class ServerGeneralEditorSectionFactory extends ServerEditorPageSectionFactoryDelegate {
	/*
	 * @see ServerEditorPartFactoryDelegate#createPage()
	 */
	public IServerEditorSection createSection() {
		return new ServerGeneralEditorSection();
	}
}