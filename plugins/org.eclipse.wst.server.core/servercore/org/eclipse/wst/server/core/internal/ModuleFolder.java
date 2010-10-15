/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
/**
 * @deprecated use org.eclipse.wst.server.core.util.ModuleFolder instead
 */
public class ModuleFolder extends org.eclipse.wst.server.core.util.ModuleFolder {
	public ModuleFolder(IContainer container, String name, IPath path) {
		super(container, name, path);
	}
}