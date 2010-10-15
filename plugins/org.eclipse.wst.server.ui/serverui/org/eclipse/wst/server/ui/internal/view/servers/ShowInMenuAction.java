/*******************************************************************************
 * Copyright (c) 2007, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * "ShowInMenuAction" menu action.
 */
public class ShowInMenuAction extends Action {
	/**
	 * ShowInMenuAction constructor.
	 * 
	 * @param sp a selection provider
	 */
	public ShowInMenuAction(final ISelectionProvider sp) {
		super(Messages.actionShowIn);
		
		setActionDefinitionId("org.eclipse.ui.navigate.showInQuickMenu");
		setMenuCreator(new IMenuCreator() {
			private MenuManager menuManager;
			public void dispose() {
				if (menuManager != null)
					menuManager.dispose();
			}

			public Menu getMenu(Control parent) {
				return getMenuManager().createContextMenu(parent);
			}

			public Menu getMenu(Menu parent) {
				//return getMenuManager().createContextMenu(parent.getShell());
				return null;
			}
			
			private MenuManager getMenuManager() {
				if (menuManager != null)
					return menuManager;
				
				menuManager = new MenuManager(Messages.actionShowIn);
				menuManager.add(new ShowInConsoleAction(sp));
				menuManager.add(new ShowInDebugAction(sp));
				return menuManager;
			}
		});
	}
}