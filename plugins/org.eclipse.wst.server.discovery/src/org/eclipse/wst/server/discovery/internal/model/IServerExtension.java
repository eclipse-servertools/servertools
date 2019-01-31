/*******************************************************************************
 * Copyright (c) 2015, 2016 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery.internal.model;

import org.eclipse.equinox.p2.metadata.Version;

public interface IServerExtension {
	public String getName() ;

	public String getDescription() ;

	public String getProvider() ;

	public String getId() ;
	
	public String getURI() ;
	
	public Version getVersion() ;
	
	public String getRuntimeVendor() ;
}
