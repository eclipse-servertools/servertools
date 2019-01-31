/*******************************************************************************
 * Copyright (c) 2005, 2019 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.websphere.core;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class AntPublisher extends org.eclipse.jst.server.generic.core.internal.publishers.AntPublisher {
	public static final String PUBLISHER_ID="org.eclipse.jst.server.generic.websphere.antpublisher"; //$NON-NLS-1$

	protected void setupAntLaunchConfiguration(ILaunchConfigurationWorkingCopy wc) {
		String wasProfile = (String)this.getServerRuntime().getServerInstanceProperties().get("wasProfile");
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,"-Duser.install.root="+wasProfile);

	}
}
