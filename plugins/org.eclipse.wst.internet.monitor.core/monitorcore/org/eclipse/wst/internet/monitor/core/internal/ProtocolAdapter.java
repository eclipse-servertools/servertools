/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.IOException;
import java.net.Socket;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
/**
 * 
 */
public class ProtocolAdapter implements IProtocolAdapter {
	protected IConfigurationElement element;
	protected ProtocolAdapterDelegate delegate;
	
	protected ProtocolAdapter(IConfigurationElement element) {
		this.element = element;
	}

	/**
	 * @see IProtocolAdapter#getId()
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * @see IProtocolAdapter#getName()
	 */
	public String getName() {
		return element.getAttribute("name");
	}
	
	protected ProtocolAdapterDelegate getDelegate() {
		if (delegate != null)
			return delegate;
		
		try {
			delegate = (ProtocolAdapterDelegate) element.createExecutableExtension("class");
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not create protocol adapter delegate: " + getId(), e);
		}
		return delegate;
	}
	
	/**
	 * Connect with the protocol.
	 * 
	 * @param monitor a monitor
	 * @param in an inbound socket
	 * @param out an outbound socket
	 * @throws IOException
	 */
	public void connect(IMonitor monitor, Socket in, Socket out) throws IOException {
		getDelegate().connect(monitor, in, out);
	}
	
	/**
	 * Disconnect from the sockets.
	 * 
	 * @param monitor a monitor
	 * @throws IOException
	 */
	public void disconnect(IMonitor monitor) throws IOException {
		getDelegate().disconnect(monitor);
	}
}