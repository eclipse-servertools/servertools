/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.provisional.IServerToolTip;
import org.eclipse.wst.server.ui.internal.ServerToolTip;

public class ServerTooltipTestCase extends TestCase {
	public ServerTooltipTestCase() {
		super();
	}

	protected static ServerToolTip tooltip;
	protected static IServerToolTip exTooltip;

	protected ServerToolTip getServerToolTip() {
		if (tooltip == null) {
			Tree tree = new Tree(new Shell(),SWT.SINGLE);
			TreeItem tItem = new TreeItem(tree,SWT.NONE);
			tItem.setText("Item 1");
			tree.setTopItem(tItem);
			tooltip = new ServerToolTip(tree);
		}
		return tooltip;
	}

	public void test00CreateExtensionToolTip(){
		exTooltip = new IServerToolTip(){
			public void createContent(Composite parent, IServer server) {
				// do nothing
			}
		};
	}
	
	public void test01ActivateToolTip(){
		getServerToolTip().activate();		
	}
	
	public void test02DeactivateToolTip(){
		getServerToolTip().deactivate();
	}	
}