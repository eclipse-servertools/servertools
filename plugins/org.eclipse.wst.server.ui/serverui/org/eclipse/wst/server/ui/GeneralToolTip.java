/**********************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServer;

public class GeneralToolTip implements IServerToolTip {

	public GeneralToolTip() {
		// do nothing
	}

	public Composite createContent(Composite parent,IServer server) {
		Text text = new Text(parent,SWT.NONE);
		text.setBackground(parent.getBackground());
		text.setText("The WebTools project</a>");
		text.setCapture(true);
		
		return parent;
	}
}