/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests.wizard;

import junit.framework.TestCase;

import org.eclipse.wst.server.ui.wizard.WizardFragment;

/* Note: These tests may be executed in any order.  Because null is used as most
 * arguments, the order doesn't currently matter.  If non-null arguments are used,
 * it may be necessary to rewrite the tests to make them truly order independent.
 */

public class WizardFragmentTestCase extends TestCase {
	protected static WizardFragment fragment;

	protected WizardFragment getWizardFragment() {
		if (fragment == null) {
			fragment = new WizardFragment() {
				// do nothing
			};
		}
		return fragment;
	}
	
	public void testHasComposite()  {
		getWizardFragment().hasComposite();
	}
	
	public void testCreateComposite()  {
		getWizardFragment().createComposite(null, null); 
	}
	
	public void testSetTaskModel()  {
		getWizardFragment().setTaskModel(null);
	}
	
	public void testGetTaskModel()  {
		getWizardFragment().getTaskModel();
	}
	
	public void testEnter()  {
		getWizardFragment().enter();
	}
	
	public void testExit()  {
		getWizardFragment().exit();
	}
	
	public void testGetChildFragments()  {
		getWizardFragment().getChildFragments();
	}
	
	public void testIsComplete()  {
		getWizardFragment().isComplete();
	}
	
	public void testTestProtected()  {
		class MyWizardFragment extends WizardFragment {
			 public void testProtected() {
				 updateChildFragments();
				 createChildFragments(null);
				 setComplete(false);
			 }
		}
		MyWizardFragment mwf = new MyWizardFragment();
		mwf.testProtected();
	}
}