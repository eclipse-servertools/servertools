/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.adapter.internal.ui;

import org.eclipse.debug.ui.*;

import org.eclipse.wst.server.ui.ServerLaunchConfigurationTab;
/**
 * A debug tab group for launching Tomcat. 
 */
public class PreviewLaunchConfigurationTabGroup extends AbstractLaunchConfigurationTabGroup {
	/*
	 * @see ILaunchConfigurationTabGroup#createTabs(ILaunchConfigurationDialog, String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[2];
		tabs[0] = new ServerLaunchConfigurationTab(new String[] { "org.eclipse.wst.server.preview" });
		tabs[0].setLaunchConfigurationDialog(dialog);
		/*tabs[1] = new SourceLookupTab();
		tabs[1].setLaunchConfigurationDialog(dialog);
		tabs[2] = new EnvironmentTab();
		tabs[2].setLaunchConfigurationDialog(dialog);*/
		tabs[1] = new CommonTab();
		tabs[1].setLaunchConfigurationDialog(dialog);
		setTabs(tabs);
	}
}