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

import org.eclipse.wst.server.core.IModule;
/**
 * A module state.
 */
public class ModuleState {
	private IModule module;
	private boolean publish;
	private int state;
	
	public ModuleState(IModule module, int state, boolean publish) {
		this.module = module;
		this.state = state;
		this.publish = publish;
	}
	
	public IModule getModule() {
		return module;
	}
	
	public int getState() {
		return state;
	}
	
	public boolean getPublish() {
		return publish;
	}
}