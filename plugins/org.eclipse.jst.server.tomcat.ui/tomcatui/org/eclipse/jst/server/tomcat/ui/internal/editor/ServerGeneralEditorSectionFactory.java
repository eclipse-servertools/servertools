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
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.ui.editor.*;
/**
 *
 */
public class ServerGeneralEditorSectionFactory implements IServerEditorPageSectionFactoryDelegate {
	/*
	 * @see IServerEditorPartFactoryDelegate#shouldDisplay(IServer, IServerConfiguration)
	 */
	public boolean shouldCreateSection(IServer server, IServerConfiguration configuration) {
		return (server != null && server.getServerType().getId().indexOf("tomcat") >= 0);
	}

	/*
	 * @see IServerEditorPartFactoryDelegate#createPage()
	 */
	public IServerEditorSection createSection() {
		return new ServerGeneralEditorSection();
	}
}
