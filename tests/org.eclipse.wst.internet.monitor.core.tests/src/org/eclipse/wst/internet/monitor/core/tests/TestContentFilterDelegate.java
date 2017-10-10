/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.tests;

import java.io.IOException;
import org.eclipse.wst.internet.monitor.core.internal.provisional.ContentFilterDelegate;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;

public class TestContentFilterDelegate extends ContentFilterDelegate{
	public byte[] filter(Request request, boolean isRequest, byte[] content) throws IOException {
		return null;
	}
}