/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.wst.internet.monitor.core.IRequest;
import org.eclipse.wst.internet.monitor.core.IRequestListener;
import org.eclipse.wst.internet.monitor.ui.internal.view.MonitorView;
/**
 * Open the monitor view if there is new activity.
 */
public class RequestListener implements IRequestListener {
	public void requestAdded(IRequest rr) {
		if (MonitorUIPlugin.getShowOnActivityPreference()) {
			MonitorView.open(rr);
		}
	}

	public void requestChanged(IRequest rr) {
		// do nothing
	}

	public void requestRemoved(IRequest rr) {
		// do nothing
	}
}