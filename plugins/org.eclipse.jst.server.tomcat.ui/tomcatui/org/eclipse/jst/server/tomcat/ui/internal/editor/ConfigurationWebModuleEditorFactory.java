package org.eclipse.jst.server.tomcat.ui.internal.editor;
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
import org.eclipse.ui.IEditorPart;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.ui.editor.IServerEditorPartFactoryDelegate;
/**
 *
 */
public class ConfigurationWebModuleEditorFactory implements IServerEditorPartFactoryDelegate {
	/*
	 * @see IServerEditorPartFactoryDelegate#shouldDisplay(IServer, IServerConfiguration)
	 */
	public boolean shouldCreatePage(IServer server, IServerConfiguration configuration) {
		return (server != null && configuration != null);
	}

	/*
	 * @see IServerEditorPartFactoryDelegate#createPage()
	 */
	public IEditorPart createPage() {
		return new ConfigurationWebModuleEditorPart();
	}
}
