/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal.operations;

import java.util.ArrayList;
import java.util.List;
/**
 * A server state.
 */
public class ServerState {
	private int state;
	private boolean publish;
	private List moduleStates = new ArrayList(2);
	
	public ServerState(int state, boolean publish) {
		this.state = state;
		this.publish = publish;
	}
	
	public void addModuleState(ModuleState moduleState) {
		if (moduleStates.contains(moduleState))
			return;
		moduleStates.add(moduleState);
	}

	public int getState() {
		return state;
	}

	public boolean getPublish() {
		return publish;
	}

	public ModuleState[] getModuleStates() {
		ModuleState[] ms = new ModuleState[moduleStates.size()];
		moduleStates.toArray(ms);
		return ms;
	}
}