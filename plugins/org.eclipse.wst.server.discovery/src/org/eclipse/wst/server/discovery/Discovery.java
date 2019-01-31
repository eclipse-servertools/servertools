/*******************************************************************************
 * Copyright (c) 2008, 2016 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.discovery.internal.DiscoveryPreferences;
import org.eclipse.wst.server.discovery.internal.ExtensionUtility;

public class Discovery {
	public static boolean launchExtensionWizard(Shell shell, String title, String message) {
		ExtensionWizard wizard = new ExtensionWizard();
		WizardDialog dialog = new WizardDialog(shell, wizard);
		if (dialog.open() != Window.CANCEL)
			return true;
		return false;
	}


	/**
	 * @since 1.1
	 */
	public static List<ServerProxy> getExtensionsWithServer(IProgressMonitor monitor){
		return ExtensionUtility.getExtensionsWithServer(monitor);
		
	}
	
	/**
	 * @since 1.1
	 */
	public static String getLicenseText(String extensionId){
		return ExtensionUtility.getLicenseText(extensionId);
		
	}
	
	/**
	 * @since 1.1
	 */
	public static void installExtension(String extensionId){
		ExtensionUtility.installExtension(extensionId);
	}
	
	/**
	 * @since 1.1
	 */
	public static ErrorMessage  refreshExtension(String extensionId, String uri, IProgressMonitor monitor){
		return ExtensionUtility.refreshExtension(extensionId,uri, monitor);
	}
	
	/**
	 * @since 1.1
	 */
	public static void refreshServerAdapters(IProgressMonitor monitor) {
		ExtensionUtility.refreshServerAdapters(monitor);
	}
	
	/**
	 * @since 1.2
	 */
	public static String getLastUpdatedDate(){
		return DiscoveryPreferences.getInstance().getCacheLastUpdatedDate();
	}
	
	/**
	 * @since 1.2
	 */
	public static int  getCacheUpdateFrequency(){
		return DiscoveryPreferences.getInstance().getCacheFrequency();
	}
	
	/**
	 * @since 1.2
	 */
	public static void  setCacheUpdateFrequency(int frequency){
		DiscoveryPreferences.getInstance().setCacheFrequency(frequency);
	}
}