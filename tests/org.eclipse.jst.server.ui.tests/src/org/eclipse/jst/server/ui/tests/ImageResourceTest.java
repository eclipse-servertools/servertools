/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.tests;

import org.eclipse.jst.server.ui.internal.ImageResource;

import junit.framework.TestCase;

public class ImageResourceTest extends TestCase {
	public void testImageResource2() {
		ImageResource.getImage(ImageResource.IMG_WIZ_RUNTIME_TYPE);
	}
}