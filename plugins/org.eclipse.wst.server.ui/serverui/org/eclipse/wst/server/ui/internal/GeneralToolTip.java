/**********************************************************************
 * Copyright (c) 2007,2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.provisional.IServerToolTip;

public class GeneralToolTip implements IServerToolTip {
	public GeneralToolTip() {
		// do nothing
	}

	public void createContent(Composite parent, IServer server) {
		Text text = new Text(parent,SWT.NONE);
		text.setBackground(parent.getBackground());
		text.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		text.setEditable(false);
		String s = "";
		if (server.getRuntime() != null)
			s += server.getRuntime().getName() + " - ";
		s += NLS.bind(Messages.modules, server.getModules().length + "");
		text.setText(s);
	}
}