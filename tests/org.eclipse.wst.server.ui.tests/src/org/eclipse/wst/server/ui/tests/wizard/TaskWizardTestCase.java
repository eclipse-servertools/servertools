/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.wizard;

import org.eclipse.wst.server.ui.tests.OrderedTestSuite;
import org.eclipse.wst.server.ui.wizard.TaskWizard;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import junit.framework.Test;
import junit.framework.TestCase;

public class TaskWizardTestCase extends TestCase {
	protected static TaskWizard wizard;
	
	public static Test suite() {
		return new OrderedTestSuite(TaskWizardTestCase.class, "TaskWizardTestCase");
	}

	public void test00Create() {
		wizard = new TaskWizard();
	}
	
	public void test01Create() {
		wizard = new TaskWizard("title");
	}
	
	public void test02Create() {
		wizard = new TaskWizard("title", null);
	}
	
	public void test03Create() {
		wizard = new TaskWizard((WizardFragment)null);
	}

	public void test04GetContainer() {
		wizard.getContainer();
	}
	
	public void test05SetRootFragment() {
		wizard.setRootFragment(null);
	}
	
	public void test06GetRootFragment() {
		wizard.getRootFragment();
	}
	
	public void test07PerformCancel() {
		try {
			wizard.performCancel();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test08PerformFinish() {
		try {
			wizard.performFinish();
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test09AddPage() {
		try {
			wizard.addPage(null);
		} catch (Exception e) {
			// ignore
		}
	}
	
	public void test10AddPages() {
		wizard.addPages();
	}
	
	public void test11CanFinish() {
		wizard.canFinish();
	}
	
	public void test12CreatePageControls() {
		wizard.createPageControls(null);
	}
	
	public void test13Dispose() {
		wizard.dispose();
	}
	
	public void test14GetDefaultPageImage() {
		wizard.getDefaultPageImage();
	}
	
	public void test15GetDialogSettings() {
		wizard.getDialogSettings();
	}
	
	public void test16GetNextPage() {
		wizard.getNextPage(null);
	}
	
	public void test17GetPage() {
		wizard.getPage(null);
	}
	
	public void test18GetPageCount() {
		wizard.getPageCount();
	}
	
	public void test19GetPages() {
		wizard.getPages();
	}
	
	public void test20GetPreviousPage() {
		wizard.getPreviousPage(null);
	}
	
	public void test21GetStartingPage() {
		wizard.getStartingPage();
	}
	
	public void test22GetTitleBarColor() {
		wizard.getTitleBarColor();
	}
	
	public void test23GetWindowTitle() {
		wizard.getWindowTitle();
	}
	
	public void test24IsHelpAvailable() {
		wizard.isHelpAvailable();
	}
	
	public void test25NeedsPreviousAndNextButtons() {
		wizard.needsPreviousAndNextButtons();
	}
	
	public void test26NeedsProgressMonitor() {
		wizard.needsProgressMonitor();
	}
	
	public void test27SetContainer() {
		wizard.setContainer(null);
	}
	
	public void test28SetDialogSettings() {
		wizard.setDialogSettings(null);
	}
	
	public void test29SetNeedsProgressMonitor() {
		wizard.setNeedsProgressMonitor(false);
	}
	
	public void test30SetForcePreviousAndNextButtons() {
		wizard.setForcePreviousAndNextButtons(false);
	}
	
	public void test31SetWindowTitle() {
		wizard.setWindowTitle("title");
	}
	
	public void test32TestProtected() {
		class MyTaskWizard extends TaskWizard {
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