/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.LaunchableAdapterDelegate;
import org.eclipse.wst.server.core.util.NullModuleArtifact;
/**
 * A client delegate that does nothing. Application will be launched
 * for the user, but no client application will open.
 */
public class NullLaunchableAdapterDelegate extends LaunchableAdapterDelegate {
	public class NullLaunchable {
		// class is used just for tagging
		public String toString() {
			return "NullLaunchable";
		}
	}

	public Object NULL_LAUNCHABLE = new NullLaunchable();

	public Object getLaunchable(IServer server, IModuleArtifact moduleArtifact) throws CoreException {
		if (moduleArtifact instanceof NullModuleArtifact)
			return NULL_LAUNCHABLE;
		return null;
	}
}