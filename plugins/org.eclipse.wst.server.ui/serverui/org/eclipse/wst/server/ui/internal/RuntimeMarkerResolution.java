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
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class RuntimeMarkerResolution implements IMarkerResolution2 {
	public String getDescription() {
		return Messages.wizNewRuntimeDescription;
	}

	public Image getImage() {
		return ImageResource.getImage(ImageResource.IMG_SERVER);
	}

	public String getLabel() {
		return Messages.wizNewServerRuntimeCreate;
	}

	public void run(IMarker marker) {
		showPreferencePage();
	}

	protected boolean showPreferencePage() {
		String id = "org.eclipse.wst.server.ui.preferencePage";
		String id2 = "org.eclipse.wst.server.ui.runtime.preferencePage";
		final PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, id2, new String[] { id, id2 }, null);
		return (dialog.open() == Window.OK);
	}
}