/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
/**
 * An event fired when a server change or module changes.
 * 
 * @since 1.0
 */
public class ServerEvent {
	private IServer server;
	private int kind;
	private IModule[] moduleTree;
	private int state;
	private int publishState;
	private boolean restartState;
	
	/**
	 * For notification when the state has changed.
	 * <p>
	 * This kind is mutually exclusive with <code>PUBLISH_STATE_CHANGE</code> and 
	 * <code>RESTART_STATE_CHANGE</code>.
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int STATE_CHANGE = 0x0001;
	/**
	 * Fired when published is needed or no longer needs to be published, 
	 * or it's state has changed.
	 * <p>
	 * This kind is mutually exclusive with <code>STATE_CHANGE</code> and 
	 * <code>RESTART_STATE_CHANGE</code>.
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int PUBLISH_STATE_CHANGE = 0x0002;
	/**
	 * For notification when the server isRestartNeeded() property changes.
	 * <p>
	 * This kind is mutually exclusive with <code>STATE_CHANGE</code> and 
	 * <code>PUBLISH_STATE_CHANGE</code>.
	 * </p>
	 * 
	 * @see #getKind()
	 */
	public static final int RESTART_STATE_CHANGE = 0x0004;
	/**
	 * For event on server changes. This kind is mutually exclusive with <code>MODULE_CHANGE</code>.
	 * 
	 * @see #getKind()
	 */
	public static final int SERVER_CHANGE = 0x0010;
	/**
	 * For event on module changes. This kind is mutually exclusive with <code>SERVER_CHANGE</code>.
	 * 
	 * @see #getKind()
	 */
	public static final int MODULE_CHANGE = 0x0020;

	/**
	 * For server change events.
	 * [issue: should we check the mutually exclusive flags and throw an InstantiationError 
	 * if the mutually exclusive flag condition is not satisify.] 
	 * @param the kind of the change. (<code>XXX_CHANGE</code>). If the kind does not 
	 * include the <code>SERVER_CHANGE</code> kind, the SERVER_CHANGE will be added automatically.  
	 * constants declared on {@link ServerEvent}
	 * @param server the server that the server event takes place.
	 * @param state the server state after the change (<code>STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 * @param publishingState the server publishing state after the 
	 * change (<code>PUBLISH_STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 * @param restartState get the server restart state after the server is restart 
	 * needed property change event.
	 */
	public ServerEvent(int kind, IServer server, int state, int publishingState, boolean restartState) {
		this.kind = kind |= SERVER_CHANGE;
		this.server = server;
		this.state = state;
		this.publishState = publishingState;
		this.restartState = restartState;
	}

	/**
	 * For module change events.
	 * [issue: should we check the mutually exclusive flags and throw an InstantiationError 
	 * if the mutually exclusive flag condition is not satisify. Also, should we check for
	 * moduleTree to make sure it is not null or empty.] 
	 * @param the kind of the change. (<code>XXX_CHANGE</code>). If the kind does not 
	 * include the <code>MODULE_CHANGE</code> kind, the MODULE_CHANGE will be added automatically.  
	 * constants declared on {@link ServerEvent}
	 * @param server the server that the module event takes place.
	 * @param state the module state after the change (<code>STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 * @param publishingState the module publishing state after the 
	 * change (<code>PUBLISH_STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 * @param restartState get the module restart state after the module is restart 
	 * needed property change event.
	 * @exception InstantiationError will be throw if the module tree is null or empty.
	 */
	public ServerEvent(int kind, IServer server, IModule[] moduleTree, int state, int publishingState, boolean restartState) {
		this.kind = kind |= MODULE_CHANGE;
		this.server = server;
		this.moduleTree = moduleTree;
		this.state = state;
		this.publishState = publishingState;
		this.restartState = restartState;
	}

	/**
	 * Returns the kind of the server event.
	 * <p>
	 * This kind can be used to test whether this event is a server event or module event by using
	 * the following code (the example is checking for the server event):
	 * ((getKind() | SERVER_CHANGE) != 0) 
	 * the following code (the example is checking for the module event):
	 * ((getKind() | MODULE_CHANGE) != 0) 
	 * 
	 * @return the kind of the change (<code>XXX_CHANGE</code>
	 * constants declared on {@link ServerEvent}
	 */
	public int getKind() {
		return kind;
	}
	
	/**
	 * Returns the module tree of the module involved in the module change event,
	 * or <code>null</code> if the event is not a module event, i.e. isModuleEvent() is false.
	 *  
	 * @return the module tree of the module involved in the module change event,
	 * or <code>null</code> if the event is not a module event, i.e. isModuleEvent() is false.
	 */
	public IModule[] getModuleTree() {
		return moduleTree;
	}
	
	/**
	 * Get the publish state after the change that triggers this server event. If this event 
	 * is of the SERVER_CHANGE kind, then the publishing state is the server publishing state.
	 * If this event is of the MODULE_CHANGE kind, then the publishing state is the module
	 * publishing state.
	 * @return the publishing state after the change (<code>PUBLISH_STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 */
	public int getPublishingState() {
		return publishState;
	}
	
	/**
	 * Get the restart state after isRestartNeeded() property change event.
	 * If this event is of the SERVER_CHANGE kind, then the restart state is the server 
	 * restart state. If this event is of the MODULE_CHANGE kind, then the restart state 
	 * is the module restart state. 
	 * @return <code>true</code> if restart is needed, and
	 *    <code>false</code> otherwise
	 */
	public boolean getRestartState() {
		return restartState;
	}
	
	/**
	 * Get the state after the change that triggers this server event. If this event 
	 * is of the SERVER_CHANGE kind, then the state is the server state.
	 * If this event is of the MODULE_CHANGE kind, then the state is the module
	 * state.
	 * @return the server state after the change (<code>STATE_XXX</code>)
	 * constants declared on {@link IServer}
	 */
	public int getState() {
		return state;
	}
	
	/**
	 * Returns the server involved in the change event.
	 * @return the server involved in the change event.
	 */
	public IServer getServer() {
		return server;
	}
}
