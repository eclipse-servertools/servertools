package org.eclipse.wst.server.ui.internal.view.servers;
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.ServerStartupListener;
import org.eclipse.swt.widgets.Shell;

/**
 * Start a server.
 */
public class StartAction extends AbstractServerAction {
	protected String launchMode = ILaunchManager.RUN_MODE;
	
	public StartAction(Shell shell, ISelectionProvider selectionProvider, String name, String launchMode) {
		super(shell, selectionProvider, name);
		this.launchMode = launchMode;
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) { }
	}

	/**
	 * Return true if this server can currently be acted on.
	 * @return boolean
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public boolean accept(IServer server) {
		return server.canStart(launchMode);
	}

	/**
	 * Perform action on this server.
	 * @param server org.eclipse.wst.server.core.model.IServer
	 */
	public void perform(final IServer server) {
		if (!ServerUIUtil.promptIfDirty(shell, server))
			return;				
	
		if (!ServerUIUtil.saveEditors())
			return;
		
		if (!ServerUIUtil.publish(server))
			return;
		
		ServerStartupListener listener = new ServerStartupListener(shell, server);
		try {
			EclipseUtil.startServer(shell, server, launchMode, listener);
		} catch (CoreException e) { }
	}
}
