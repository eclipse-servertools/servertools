/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import org.eclipse.jst.server.tomcat.core.ITomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.*;
/**
 *
 */
public class ConfigurationMimeEditorSectionFactory extends ServerEditorPageSectionFactoryDelegate {
	/*
	 * @see ServerEditorPartFactoryDelegate#shouldDisplay(IServer)
	 */
	public boolean shouldCreateSection(IServerWorkingCopy server) {
		TomcatServer tomcatServer = (TomcatServer) server.getAdapter(ITomcatServer.class);
		return tomcatServer.getServerConfiguration() != null;
	}

	/*
	 * @see ServerEditorPartFactoryDelegate#createPage()
	 */
	public IServerEditorSection createSection() {
		return new ConfigurationMimeEditorSection();
	}
}