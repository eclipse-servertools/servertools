/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.wst.internet.monitor.core.internal.IStartup;
/**
 * Startup hook.
 */
public class Startup implements IStartup {
	/* (non-Javadoc)
	 * @see org.eclipse.wst.internet.monitor.core.IStartup#startup()
	 */
	public void startup() {
		// do nothing - plugin loading is enough
	}
}
