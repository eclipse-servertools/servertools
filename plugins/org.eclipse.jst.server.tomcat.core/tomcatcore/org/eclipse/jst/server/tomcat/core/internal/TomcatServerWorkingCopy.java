package org.eclipse.jst.server.tomcat.core.internal;
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
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.jst.server.tomcat.core.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.ITomcatServerWorkingCopy;
import org.eclipse.jst.server.tomcat.core.WebModule;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.model.IModule;
/**
 * 
 */
public class TomcatServerWorkingCopy extends TomcatServer implements ITomcatServerWorkingCopy {
	protected IServerWorkingCopy workingCopy;

	public TomcatServerWorkingCopy() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IServerWorkingCopyDelegate#initializeWorkingCopy(org.eclipse.wst.server.core.IServerWorkingCopy)
	 */
	public void initialize(IServerWorkingCopy workingCopy2) {
		this.workingCopy = workingCopy2;
	}
	
	public void setDefaults() {
		setTestEnvironment(true);
	}
	
	/**
	 * Sets this process to debug mode. This feature only works
	 * with Tomcat v4.0.
	 *
	 * @param b boolean
	 */
	public void setDebug(boolean b) {
		workingCopy.setAttribute(PROPERTY_DEBUG, b);
	}

	/**
	 * Sets this process to secure mode.
	 * @param b boolean
	 */
	public void setSecure(boolean b) {
		workingCopy.setAttribute(PROPERTY_SECURE, b);
	}
	
	/**
	 * Sets this server to test environment mode.
	 * 
	 * @param b boolean
	 */
	public void setTestEnvironment(boolean b) {
		workingCopy.setAttribute(PROPERTY_TEST_ENVIRONMENT, b);
	}
	
	/**
	 * Add the given project to this configuration. The project
	 * has already been verified using isSupportedProject() and
	 * does not already exist in the configuration.
	 *
	 * @param ref java.lang.String
	 */
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
		IStatus status = canModifyModules(add, remove);
		if (status == null || !status.isOK())
			throw new CoreException(status);
		
		IServerConfigurationWorkingCopy scwc = server.getServerConfiguration().getWorkingCopy();
		// TODO
		ITomcatConfigurationWorkingCopy wc = (ITomcatConfigurationWorkingCopy) scwc.getWorkingCopyDelegate();
		boolean change = false;

		if (add != null) {
			int size = add.length;
			for (int i = 0; i < size; i++) {
				IModule module3 = add[i];
				IWebModule module = (IWebModule) module3;
				String contextRoot = module.getContextRoot();
				if (contextRoot != null && !contextRoot.startsWith("/"))
					contextRoot = "/" + contextRoot;
				WebModule module2 = new WebModule(contextRoot,
						module.getLocation().toOSString(), module.getFactoryId() + ":" + module.getId(), true);
				wc.addWebModule(-1, module2);
				change = true;
			}
		}
		
		if (remove != null) {
			int size2 = remove.length;
			for (int j = 0; j < size2; j++) {
				IModule module3 = remove[j];
				String memento = module3.getFactoryId() + ":" + module3.getId();
				List modules = getTomcatConfiguration().getWebModules();
				int size = modules.size();
				for (int i = 0; i < size; i++) {
					WebModule module = (WebModule) modules.get(i);
					if (memento.equals(module.getMemento())) {
						wc.removeWebModule(i);
						change = true;
					}
				}
			}
		}
		if (!change)
			scwc.release();
		else
			scwc.save(new NullProgressMonitor());
		server.setConfigurationSyncState(IServer.SYNC_STATE_DIRTY);
	}
	
	public void handleSave(byte id, IProgressMonitor monitor) { }
}