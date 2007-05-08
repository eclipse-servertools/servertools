/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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

public class WizardFragmentTestCase extends TestCase {
	protected static WizardFragment fragment;

	public void test00CreateFragment() {
		fragment = new WizardFragment() {
			// do nothing
		};
	}
	
	public void test01HasComposite()  {
		fragment.hasComposite();
	}
	
	public void test02CreateComposite()  {
		fragment.createComposite(null, null); 
	}
	
	public void test03SetTaskModel()  {
		fragment.setTaskModel(null);
	}
	
	public void test04GetTaskModel()  {
		fragment.getTaskModel();
	}
	
	public void test05Enter()  {
		fragment.enter();
	}
	
	public void test06Exit()  {
		fragment.exit();
	}
	
	public void test09GetChildFragments()  {
		fragment.getChildFragments();
	}
	
	public void test10IsComplete()  {
		fragment.isComplete();
	}
	
	public void test11TestProtected()  {
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