/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests.internal;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServerBehaviour;
import org.eclipse.jst.server.tomcat.core.internal.VerifyResourceSpec;
import org.eclipse.jst.server.tomcat.core.tests.RuntimeLocation;

import junit.framework.TestCase;

public class UtilTestCase extends TestCase {
	public void testArgMerge() {
		assertEquals("", TomcatServerBehaviour.mergeArguments("", new String[] { }));
	}
	
	public void testArgMerge2() {
		assertEquals("a=b", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b"}));
	}
	
	public void testArgMerge3() {
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b", "c=d" }));
	}
	
	public void testArgMerge4() {
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "c=d" }));
	}
	
	public void testArgMerge5() {
		assertEquals("a=c", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "a=c" }));
	}
	
	public void testArgMerge6() {
		assertEquals("a b=2 d e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }));
	}
	
	public void testArgMerge7() {
		assertEquals("a bb=c d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" }));
	}
	
	public void testArgMerge8() {
		assertEquals("a", TomcatServerBehaviour.mergeArguments("a", new String[] { }));
	}
	
	public void testArgMerge9() {
		assertEquals("a b", TomcatServerBehaviour.mergeArguments("a", new String[] { "b" }));
	}
	
	public void testArgMerge10() {
		assertEquals("a b c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }));
	}
	
	public void testArgMerge11() {
		assertEquals("a=b c=\"e\"", TomcatServerBehaviour.mergeArguments("a=b c=d", new String[] { "c=\"e\"" }));
	}
	
	public void testArgMerge12() {
		assertEquals("a=b c=\"e f\"", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "c=\"e f\"" }));
	}
	
	public void testArgMerge13() {
		assertEquals("a=b -c \"e\"", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "-c \"e\"" }));
	}
	
	public void testArgMerge14() {
		assertEquals("a=b -c \"e f\"", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "-c \"e f\"" }));
	}
	
	public void testVerifySpec() {
		VerifyResourceSpec spec = new VerifyResourceSpec("");
		assertEquals("", spec.toString());
		String [] paths = spec.getPaths();
		assertEquals(1, paths.length);
		assertEquals("", paths[0]);
	}

	public void testVerifySpec2() {
		VerifyResourceSpec spec = new VerifyResourceSpec("file ");
		assertEquals("file ", spec.toString());
		String [] paths = spec.getPaths();
		assertEquals(1, paths.length);
		assertEquals("file", paths[0]);
	}
	
	public void testVerifySpec3() {
		VerifyResourceSpec spec = new VerifyResourceSpec("dir" + File.separator + "file");
		String [] paths = spec.getPaths();
		assertEquals(1, paths.length);
		assertEquals("dir" + File.separator + "file", paths[0]);
	}

	public void testVerifySpec4() {
		VerifyResourceSpec spec = new VerifyResourceSpec("dir" + File.separator + "file| alt ");
		String [] paths = spec.getPaths();
		assertEquals(2, paths.length);
		assertEquals("dir" + File.separator + "file", paths[0]);
		assertEquals("dir" + File.separator + "alt", paths[1]);
	}

	public void testVerifySpec5() {
		VerifyResourceSpec spec = new VerifyResourceSpec("dir" + File.separator + "file| alt1 | alt2 ");
		String [] paths = spec.getPaths();
		assertEquals(3, paths.length);
		assertEquals("dir" + File.separator + "file", paths[0]);
		assertEquals("dir" + File.separator + "alt1", paths[1]);
		assertEquals("dir" + File.separator + "alt2", paths[2]);
	}
	
	public void testVerifySpec6() {
		if (RuntimeLocation.runtimeLocation != null) {
			VerifyResourceSpec spec = new VerifyResourceSpec("conf");
			IStatus status = spec.checkResource(RuntimeLocation.runtimeLocation);
			assertTrue(status.isOK());
		}
	}
	
	public void testVerifySpec7() {
		if (RuntimeLocation.runtimeLocation != null) {
			VerifyResourceSpec spec = new VerifyResourceSpec("conf" + File.separator + "server.xml");
			IStatus status = spec.checkResource(RuntimeLocation.runtimeLocation);
			assertTrue(status.isOK());
		}
	}

	public void testVerifySpec8() {
		if (RuntimeLocation.runtimeLocation != null) {
			VerifyResourceSpec spec = new VerifyResourceSpec("conf" + File.separator + "nofile|server.xml");
			IStatus status = spec.checkResource(RuntimeLocation.runtimeLocation);
			assertTrue(status.isOK());
		}
	}

	public void testVerifySpec9() {
		if (RuntimeLocation.runtimeLocation != null) {
			VerifyResourceSpec spec = new VerifyResourceSpec("conf" + File.separator + "nofile");
			IStatus status = spec.checkResource(RuntimeLocation.runtimeLocation);
			assertFalse(status.isOK());
		}
	}
}