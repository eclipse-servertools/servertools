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
package org.eclipse.wst.internet.monitor.core;
/**
 * A protocol adapter allows the monitor to support a new protocol between a client
 * and server, and manages the message passing between the two.
 * The global list of known protocol adapters is available via
 * {@link MonitorCore.getProtocolAdapters()}.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * @since 1.0
 */
public interface IProtocolAdapter {
	/**
	 * Returns the id of this adapter.
	 * Each adapter has a distinct, fixed id. Ids are intended to be used internally as keys;
	 * they are not intended to be shown to end users.
	 * 
	 * @return the element id
	 */
	public String getId();

	/**
	 * Returns the displayable (translated) name for this adapter.
	 *
	 * @return a displayable name
	 */
	public String getName();
}