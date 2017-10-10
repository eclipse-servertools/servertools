/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.tests;

import org.eclipse.wst.server.ui.internal.provisional.AbstractServerLabelProvider;

import junit.framework.TestCase;

/**
 * Test for API coverage and changes
 * @author arvera
 *
 */
public class AbstractServerLabelProviderTestCase extends TestCase{

	
	public void testAbstractServerLabelProvider(){
		AbstractServerLabelProvider lp = new AbstractServerLabelProvider();
		String serverState = lp.getServerStateLabel(null);
		assertNull(serverState);
	}

}
