/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.IOException;
import java.net.Socket;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.wst.internet.monitor.core.IMonitor;
import org.eclipse.wst.internet.monitor.core.IProtocolAdapter;
import org.eclipse.wst.internet.monitor.core.ProtocolAdapterDelegate;
/**
 * 
 */
public class ProtocolAdapter implements IProtocolAdapter {
	protected IConfigurationElement element;
	protected ProtocolAdapterDelegate delegate;
	
	protected ProtocolAdapter(IConfigurationElement element) {
		this.element = element;
	}

	public String getId() {
		return element.getAttribute("id");
	}
	
	public String getName() {
		return element.getAttribute("name");
	}
	
	public void parse(IMonitor monitor, Socket in, Socket out) throws IOException {
		if (delegate == null) {
			try {
				delegate = (ProtocolAdapterDelegate) element.createExecutableExtension("class");
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not create protocol adapter delegate: " + getId(), e);
				return;
			}
		}
		delegate.parse(monitor, in, out);
	}
}