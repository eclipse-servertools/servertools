/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.server.core.model.*;
/**
 * A server configuration. Server configurations usually contain
 * directories (the resources to be run on the server) and configuration
 * information. (i.e. mime types, data sources, etc.)
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerConfiguration extends IElement {
	public static final String FILE_EXTENSION = "config";

	public IServerConfigurationType getServerConfigurationType();
	
	public IFile getFile();
	
	public IServerConfigurationDelegate getDelegate();
	
	public IServerConfigurationWorkingCopy getWorkingCopy();
	
	public IFolder getConfigurationDataFolder();
	
	public IPath getConfigurationDataPath();
}