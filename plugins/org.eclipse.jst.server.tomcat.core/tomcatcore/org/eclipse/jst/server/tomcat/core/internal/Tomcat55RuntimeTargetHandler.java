package org.eclipse.jst.server.tomcat.core.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
/**
 * 
 */
public class Tomcat55RuntimeTargetHandler extends Tomcat50RuntimeTargetHandler {
	public String getId() {
		return "org.eclipse.jst.server.tomcat.runtimeTarget.v55";
	}

	public String getLabel() {
		return TomcatPlugin.getResource("%target55runtime");
	}
}
