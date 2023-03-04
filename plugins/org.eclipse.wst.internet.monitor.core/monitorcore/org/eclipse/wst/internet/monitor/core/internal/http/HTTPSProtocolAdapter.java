/*******************************************************************************
 * Copyright (c) 2023 Erik Brangs and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Erik Brangs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.http;

import java.io.IOException;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;
public class HTTPSProtocolAdapter extends HTTPProtocolAdapter {

	public Socket createRemoteSocket() throws IOException {
		return SSLSocketFactory.getDefault().createSocket();
	}

}
