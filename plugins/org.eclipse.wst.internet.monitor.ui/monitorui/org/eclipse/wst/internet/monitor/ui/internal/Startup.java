/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
