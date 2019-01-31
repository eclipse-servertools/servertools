/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;
/**
 * A protocol adapter enables a monitor to support a particular network
 * protocol used to communicate between a client and server. All supported
 * protocols will be based on TCP/IP.
 * <p>
 * Protocol adapters are registered via the <code>protocolAdapaters</code>
 * extension point in the <code>org.eclipse.wst.internet.monitor.core</code>
 * plug-in. The global list of known protocol adapters is available via
 * {@link MonitorPlugin#getProtocolAdapters()}. Standard protocol
 * adapters for {@linkplain #HTTP_PROTOCOL_ID HTTP} and
 * {@linkplain #TCPIP_PROTOCOL_ID TCP/IP} are built-in.
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IProtocolAdapter {
	/**
	 * Protocol adapter id (value {@value}) for TCP/IP.
	 * The TCP/IP protocol adapter is standard.
	 */
	public static String TCPIP_PROTOCOL_ID = "TCP/IP";

	/**
	 * Protocol adapter id (value {@value}) for HTTP.
	 * The HTTP protocol adapter is standard.
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
