/*******************************************************************************
 * Copyright (c) 2011 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.cnf;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * 
 * The purpose of this class is proportionate a way to register those UI Decorators that 
 * needs to be updated after a Server has been modified. Basically this handler is a list
 * of IDs of UI Decorators. 
 * 
 * @author israelgd@mx1.ibm.com
 */
public class ServerDecoratorsHandler {
	
	final static String NAVIGATOR_DECORATOR_ID = "org.eclipse.wst.server.ui.navigatorDecorator";
	
	protected static ArrayList <String> UIDecoratorsIDs = new ArrayList<String>();
	
	static {
		UIDecoratorsIDs.add(NAVIGATOR_DECORATOR_ID);
	}
	protected static IDecoratorManager decoratorManager = null;
	
	protected static IDecoratorManager getDecoratorManager(){
		if (decoratorManager == null){
			decoratorManager = PlatformUI.getWorkbench().getDecoratorManager();
		}
		return decoratorManager;
	}
	/**
	 * Used to refresh the Server Decorators previously added and set selection after that. 
	 * Triggers the decoration event of each Decorator.
	 * */
	public static void refresh(final CommonViewer tableViewer) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {		
				IDecoratorManager dm = PlatformUI.getWorkbench().getDecoratorManager();
				ArrayList<String> UIDecoratorsIDsClone = (ArrayList<String>)UIDecoratorsIDs.clone();
				for (String decoratorId: UIDecoratorsIDsClone) {
					dm.update(decoratorId);
				}
				if (tableViewer != null){
						tableViewer.setSelection(tableViewer.getSelection());
				}
			}
		});
	}
	
	/**
	 * Used to refresh the Server Decorators previously added.
	 * @param server
	 * */
	public static void refresh() {
		refresh(null);
	}
	
	/**
	 * Remove a UI Decorator from the Decorator Handler.
	 * @param decoratorID
	 */
	public static void removeUIDecoratorsID(String decoratorID) {
		synchronized (UIDecoratorsIDs) {
			UIDecoratorsIDs.remove(decoratorID);
		}
	}

	/**
	 * Adds a new UI Decorator from the Decorator Handler.
	 * @param decoratorID
	 */
	public static void addUIDecoratorsIDs(String decoratorID) {
		synchronized (UIDecoratorsIDs) {
			if(!UIDecoratorsIDs.contains(decoratorID)){
				UIDecoratorsIDs.add(decoratorID);
			}
		}
	}
}
