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
package org.eclipse.wst.internet.monitor.core.internal;

import org.eclipse.wst.internet.monitor.core.IMonitorWorkingCopy;
import org.eclipse.wst.internet.monitor.core.MonitorCore;
/**
 * A protocol adapter enables a monitor to support a particular network
 * protocol used to communicate between a client and server.
 * <p>
 * Protocol adapters are registered via the <code>protocolAdapaters</code>
 * extension point in the <code>org.eclipse.wst.internet.monitor.core</code>
 * plug-in. The global list of known protocol adapters is available via
 * {@link MonitorCore.getProtocolAdapters()}. Standard protocol
 * adapters for {@linkplain #HTTP_PROTOCOL_ID HTTP} and
 * {@linkplain #TCPIP_PROTOCOL_ID TCP/IP} are built-in.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * [issue: Would it be fair to say that we're only talking about other protocols
 * that are based on TCP/IP? IRequest does seem pretty TCP/IP specific.]
 * </p>
 * 
 * @see IMonitorWorkingCopy#setProtocolAdapter(IProtocolAdapter)
 * @since 1.0
 */
public interface IProtocolAdapter {
	/**
	 * Protocol adapter id (value {@value}) for TCP/IP.
	 * The TCP/IP protocol adapter is standard.
	 * 
	 * @see MonitorCore#findProtocolAdapter(String)
	 */
	public static String TCPIP_PROTOCOL_ID = "TCP/IP";

	/**
	 * Protocol adapter id (value {@value}) for HTTP.
	 * The HTTP protocol adapter is standard.
	 * 
	 * @see MonitorCore#findProtocolAdapter(String)
	 */
	public static String HTTP_PROTOCOL_ID = "HTTP";

	/**
	 * Returns the id of this adapter.
	 * Each adapter has a distinct, fixed id. Ids are intended to be used
	 * internally as keys; they are not intended to be shown to end users.
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