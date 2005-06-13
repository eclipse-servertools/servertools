/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.webbrowser;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.internal.browser.BrowserManager;
import org.eclipse.ui.internal.browser.IBrowserDescriptor;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Action to open the Web browser.
 */
public class SwitchDefaultBrowserAction extends Action {
	protected IBrowserDescriptor webbrowser;
	protected BrowserManager browserManager;

	/**
	 * SwitchDefaultBrowserAction constructor comment.
	 * 
	 * @param webbrowser a browser
	 * @param current true if this is the current browser
	 * @param manager the browser manager
	 */
	public SwitchDefaultBrowserAction(IBrowserDescriptor webbrowser, boolean current, BrowserManager manager) {
		super();
		
		this.webbrowser = webbrowser;
		this.browserManager = manager;
		if (webbrowser == null)
			setText(Messages.internalWebBrowserName);
		else
			setText(webbrowser.getName());
		
		if (current)
			setChecked(true);
	}

	/**
	 * Implementation of method defined on <code>IAction</code>.
	 */
	public void run() {
		if (webbrowser == null)
			WebBrowserPreference.setBrowserChoice(WebBrowserPreference.INTERNAL);
		else {
			WebBrowserPreference.setBrowserChoice(WebBrowserPreference.EXTERNAL);
			browserManager.setCurrentWebBrowser(webbrowser);
		}
	}
}