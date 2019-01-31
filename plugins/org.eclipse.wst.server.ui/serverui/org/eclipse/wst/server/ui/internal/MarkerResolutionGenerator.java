/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator2 {
	public boolean hasResolutions(IMarker marker) {
		return true;
	}

	public IMarkerResolution[] getResolutions(IMarker marker) {
		return new IMarkerResolution[] { new RuntimeMarkerResolution() };
	}
}