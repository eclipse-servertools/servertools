/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.tests.wizard;

import junit.framework.TestCase;

import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;

/* Note: These tests may be executed in any order.  Because null is used as most
 * arguments, the order doesn't currently matter.  If non-null arguments are used,
 * it may be necessary to rewrite the tests to make them truly order independent.
 */

public class TaskWizardTestCase extends TestCase {
	protected static TaskWizard wizard;

	protected TaskWizard getTaskWizard() {
		if (wizard == null) {
			wizard = new TaskWizard("title", null);
			// Ensure pages are not null
			wizard.addPages();
		}
		return wizard;
	}
	public void testCreate() {
		new TaskWizard("title", null, null);
	}

	public void testGetContainer() {
		getTaskWizard().getContainer();
	}
	
	public void testSetRootFragment() {
		getTaskWizard().setRootFragment(null);
	}
	
	public void testGetRootFragment() {
		getTaskWizard().getRootFragment();
	}
	
	public void testPerformCancel() {
		try {
			getTaskWizard().performCancel();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void testPerformFinish() {
		try {
			getTaskWizard().performFinish();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void testAddPage() {
		try {
			getTaskWizard().addPage(null);
		} catch (Exception e) {
			// ignore
		}
		// Reset wizard since it now has a null page
		wizard = null;
	}
	
	public void testCanFinish() {
		getTaskWizard().canFinish();
	}
	
	public void testCreatePageControls() {
		getTaskWizard().createPageControls(null);
	}
	
	public void testDispose() {
		getTaskWizard().dispose();
		wizard = null;
	}
	
	public void testGetDefaultPageImage() {
		getTaskWizard().getDefaultPageImage();
	}
	
	public void testGetDialogSettings() {
		getTaskWizard().getDialogSettings();
	}
	
	public void testGetNextPage() {
		getTaskWizard().getNextPage(null);
	}
	
	public void testGetPage() {
		getTaskWizard().getPage(null);
	}
	
	public void test18GetPageCount() {
		getTaskWizard().getPageCount();
	}
	
	public void testGetPages() {
		getTaskWizard().getPages();
	}
	
	public void testGetPreviousPage() {
		getTaskWizard().getPreviousPage(null);
	}
	
	public void testGetStartingPage() {
		getTaskWizard().getStartingPage();
	}
	
	public void testGetTitleBarColor() {
		getTaskWizard().getTitleBarColor();
	}
	
	public void testGetWindowTitle() {
		getTaskWizard().getWindowTitle();
	}
	
	public void testIsHelpAvailable() {
		getTaskWizard().isHelpAvailable();
	}
	
	public void testNeedsPreviousAndNextButtons() {
		getTaskWizard().needsPreviousAndNextButtons();
	}
	
	public void testNeedsProgressMonitor() {
		getTaskWizard().needsProgressMonitor();
	}
	
	public void testSetContainer() {
		getTaskWizard().setContainer(null);
	}
	
	public void testSetDialogSettings() {
		getTaskWizard().setDialogSettings(null);
	}
	
	public void testSetNeedsProgressMonitor() {
		getTaskWizard().setNeedsProgressMonitor(false);
	}
	
	public void testSetForcePreviousAndNextButtons() {
		getTaskWizard().setForcePreviousAndNextButtons(false);
	}
	
	public void testSetWindowTitle() {
		getTaskWizard().setWindowTitle("title");
	}
	
	public void testTestProtected() {
		class MyTaskWizard extends TaskWizard {
			public MyTaskWizard() {
				super(null, null, null);
			}
			
			public void testProtected() {
				try {
					executeTask(null, (byte)0, null);
				} catch (Exception e) {
					// ignore
				}
				getCurrentWizardFragment();
				try {
					switchWizardFragment(null);
				} catch (Exception e) {
					// ignore
				}
				updatePages();
				useJob();
				getJobTitle();
			}
		}
		MyTaskWizard mtw = new MyTaskWizard();
		mtw.testProtected();
	}	
}