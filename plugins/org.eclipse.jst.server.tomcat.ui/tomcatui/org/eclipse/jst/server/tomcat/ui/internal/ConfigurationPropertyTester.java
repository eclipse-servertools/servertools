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
package org.eclipse.jst.server.tomcat.ui.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jst.server.tomcat.core.internal.ITomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.wst.server.core.IServerAttributes;
/**
 * 
 */
public class ConfigurationPropertyTester extends PropertyTester {
	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		try {
			IServerAttributes server = (IServerAttributes) receiver;
			TomcatServer tomcatServer = (TomcatServer) server.loadAdapter(ITomcatServer.class, null);
			if (tomcatServer != null)
				return tomcatServer.getServerConfiguration() != null;
		} catch (Exception e) {
			// ignore
		}
		return false;
	}
}