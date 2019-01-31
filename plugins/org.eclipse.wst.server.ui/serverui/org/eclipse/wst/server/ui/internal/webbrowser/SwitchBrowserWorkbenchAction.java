/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.webbrowser;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.*;
import org.eclipse.ui.internal.browser.BrowserManager;
import org.eclipse.ui.internal.browser.IBrowserDescriptor;
import org.eclipse.ui.internal.browser.WebBrowserPreference;
import org.eclipse.ui.internal.browser.WebBrowserUtil;
/**
 * Action to open the Web broswer.
 */
public class SwitchBrowserWorkbenchAction implements IWorkbenchWindowPulldownDelegate2 {
	/**
	 * The menu created by this action
	 */
	private Menu fMenu;
		
	protected boolean recreateMenu = false;

	/**
	 * SwitchBrowserWorkbenchAction constructor comment.
	 */
	public SwitchBrowserWorkbenchAction() {
		super();
	}

	public void dispose() {
		setMenu(null);
	}
	
	/**
	 * Sets this action's drop-down menu, disposing the previous menu.
	 * 
	 * @param menu the new menu
	 */
	private void setMenu(Menu menu) {
		if (fMenu != null) {
			fMenu.dispose();
		}
		fMenu = menu;
	}

	public void init(IWorkbenchWindow window) {
		// do nothing
	}
	
	/**
	 * Adds the given action to the specified menu with an accelerator specified
	 * by the given number.
	 * 
	 * @param menu the menu to add the action to
	 * @param action the action to add
	 * @param accelerator the number that should appear as an accelerator
	 */
	protected void addToMenu(Menu menu, IAction action, int accelerator) {
		StringBuffer label= new StringBuffer();
		if (accelerator >= 0 && accelerator < 10) {
			//add the numerical accelerator
			label.append('&');
			label.append(accelerator);
			label.append(' ');
		}
		label.append(action.getText());
		action.setText(label.toString());
		ActionContributionItem item= new ActionContributionItem(action);
		item.fill(menu, -1);
	}

	/**
	 * Fills the drop-down menu with favorites and launch history,
	 * launch shortcuts, and an action to open the launch configuration dialog.
	 *
	 * @param menu the menu to fill
	 */
	protected void fillMenu(Menu menu) {
		int i = 0;
		if (WebBrowserUtil.canUseInternalWebBrowser()) {
			addToMenu(menu, new SwitchDefaultBrowserAction(null, WebBrowserPreference.getBrowserChoice() == WebBrowserPreference.INTERNAL, null), i++);
		}
		IBrowserDescriptor current = BrowserManager.getInstance().getCurrentWebBrowser();
		BrowserManager browserManager = BrowserManager.getInstance();
		Iterator iterator = browserManager.getWebBrowsers().iterator();
		while (iterator.hasNext()) {
			IBrowserDescriptor browser = (IBrowserDescriptor) iterator.next();
			addToMenu(menu, new SwitchDefaultBrowserAction(browser, WebBrowserPreference.getBrowserChoice() != WebBrowserPreference.INTERNAL && browser.equals(current), browserManager), i++);
		}
	}

	/**
	 * Creates the menu for the action
	 */
	private void initMenu() {
		// Add listener to repopulate the menu each time
		// it is shown because of dynamic history list
		fMenu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				//if (recreateMenu) {
					Menu m = (Menu) e.widget;
					MenuItem[] items = m.getItems();
					for (int i = 0; i < items.length; i++) {
						items[i].dispose();
					}
					fillMenu(m);
					recreateMenu = false;
				//}
			}
		});
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing
	}
	
	public void run(IAction action) {
		// do nothing
	}

	public Menu getMenu(Menu parent) {
		setMenu(new Menu(parent));
		//fillMenu(fMenu);
		initMenu();
		return fMenu;
	}

	public Menu getMenu(Control parent) {
		setMenu(new Menu(parent));
		//fillMenu(fMenu);
		initMenu();
		return fMenu;
	}
}