/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.actions;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Dummy IProcess so that a Run on Server launch can be terminated.
 */
public class RunOnServerProcess implements IProcess {
	protected ILaunch launch;
	protected boolean isTerminated;

	public RunOnServerProcess(ILaunch launch) {
		this.launch = launch;
	}

	public String getAttribute(String arg0) {
		return null;
	}

	public int getExitValue() throws DebugException {
		return 0;
	}

	public String getLabel() {
		return Messages.processName;
	}

	public ILaunch getLaunch() {
		return launch;
	}

	public IStreamsProxy getStreamsProxy() {
		return null;
	}

	public void setAttribute(String arg0, String arg1) {
		// ignore
	}

	public Object getAdapter(Class arg0) {
		return null;
	}

	public boolean canTerminate() {
		return true;
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void terminate() throws DebugException {
		isTerminated = true;
	}
}