/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
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
package org.eclipse.wst.server.core.tests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import junit.framework.TestCase;
/**
 * Do not run - test was created solely to show a JDT compilation problem (modification
 * stamps do not get updated in the output directory) which WTP has worked around.
 */
public class ModificationStampTestCase extends TestCase {
	public void testModificationStamp() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("Test");
		if (!project.exists())
			project.create(null);
		
		if (!project.isOpen())
			project.open(null);
		
		IFile fileA = project.getFile("testA.txt");
		fileA.create(new ByteArrayInputStream("Initial contents".getBytes()), true, null);
		
		IFile fileB = project.getFile("testB.txt");
		fileA.copy(fileB.getProjectRelativePath(), true, null);
		
		long a1 = fileA.getModificationStamp();
		long b1 = fileB.getModificationStamp();
		
		System.out.println("Modification stamps: " + a1 + " " + b1);
		
		System.out.print("B's initial contents: ");
		InputStream in = fileB.getContents();
		byte[] bb = new byte[50];
		int n = in.read(bb);
		in.close();
		System.out.println(new String(bb, 0, n));
		
		fileA.setContents(new ByteArrayInputStream("New contents".getBytes()), false, false, null);
		fileB.delete(false, null);
		fileA.copy(fileB.getProjectRelativePath(), false, null);
		
		System.out.print("B's new contents: ");
		in = fileB.getContents();
		n = in.read(bb);
		in.close();
		System.out.println(new String(bb, 0, n));
		
		long a2 = fileA.getModificationStamp();
		long b2 = fileB.getModificationStamp();
		
		System.out.println("Modification stamps: " + a2 + " " + b2);
		
		assertFalse(a1 == a2);
		assertFalse(b1 == b2);
	}
}