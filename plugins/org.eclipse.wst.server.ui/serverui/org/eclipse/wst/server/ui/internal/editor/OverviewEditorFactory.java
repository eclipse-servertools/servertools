/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.editor;

import org.eclipse.ui.IEditorPart;

import org.eclipse.wst.server.ui.editor.ServerEditorPartFactoryDelegate;
/**
 *
 */
public class OverviewEditorFactory extends ServerEditorPartFactoryDelegate {
	/*
	 * @see ServerEditorPartFactoryDelegate#createPage()
	 */
	public IEditorPart createPage() {
		return new OverviewEditorPart();
	}
}