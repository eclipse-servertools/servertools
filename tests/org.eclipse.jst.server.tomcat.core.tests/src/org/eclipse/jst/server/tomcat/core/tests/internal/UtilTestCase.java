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
		assertEquals("", TomcatServerBehaviour.mergeArguments("", new String[] { }, null, false));
		assertEquals("", TomcatServerBehaviour.mergeArguments("", new String[] { }, null, true));
		assertEquals("", TomcatServerBehaviour.mergeArguments("", new String[] { }, new String[] { "a" } , true));
	}
	
	public void testArgMerge2() {
		assertEquals("a=b", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b"}, null, false));
		assertEquals("a=b", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b"}, null, true));
		// new arguments aren't removed
		assertEquals("a=b", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b"}, new String[] { "a" }, true));
	}
	
	public void testArgMerge3() {
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b", "c=d" }, null, false));
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b", "c=d" }, null, true));
		// new arguments aren't removed
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b", "c=d" }, new String[] { "a" }, true));
	}
	
	public void testArgMerge4() {
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "c=d" }, null, false));
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "c=d" }, null, true));
		// old argument is removed
		assertEquals("c=d", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "c=d" }, new String[] { "a" }, true));
	}
	
	public void testArgMerge5() {
		assertEquals("a=c", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "a=c" }, null, false));
		assertEquals("a=c", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "a=c" }, null, true));
		// replaced argument is removed
		assertEquals("", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "a=c" }, new String[] { "a" }, true));
	}
	
	public void testArgMerge6() {
		assertEquals("a b=2 d e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, null, false));
		assertEquals("a b=2 d e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, null, true));
		// old argument is removed
		assertEquals("b=2 d e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, new String[] { "a" }, true));
		assertEquals("a e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, new String[] { "b", "d" }, true));
		assertEquals("a e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, new String[] { "b=", "d" }, true));
	}
	
	public void testArgMerge7() {
		assertEquals("a bb=c d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" }, null, false));
		assertEquals("a bb=c d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" }, null, true));
		// remove expected wrong argument
		assertEquals("a d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" },  new String[] { "b" }, true));
		// avoid removing wrong argument
		assertEquals("a bb=c d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" },  new String[] { "b=" }, true));
	}
	
	public void testArgMerge8() {
		assertEquals("a", TomcatServerBehaviour.mergeArguments("a", new String[] { }, null, false));
		assertEquals("a", TomcatServerBehaviour.mergeArguments("a", new String[] { }, null, true));
		// remove single exiting argument
		assertEquals("", TomcatServerBehaviour.mergeArguments("a", new String[] { }, new String[] { "a" }, true));
	}
	
	public void testArgMerge9() {
		assertEquals("a b", TomcatServerBehaviour.mergeArguments("a", new String[] { "b" }, null, false));
		assertEquals("a b", TomcatServerBehaviour.mergeArguments("a", new String[] { "b" }, null, true));
		// remove single exiting argument
		assertEquals("b", TomcatServerBehaviour.mergeArguments("a", new String[] { "b" }, new String[] { "a" }, true));
	}
	
	public void testArgMerge10() {
		assertEquals("a b c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, null, false));
		assertEquals("a b c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, null, true));
		// remove argumets
		assertEquals("b c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, new String[] { "a" }, true));
		assertEquals("a c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, new String[] { "b" }, true));
		assertEquals("c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, new String[] { "a", "b" }, true));
	}
	
	public void testArgMerge11() {
		assertEquals("a=b c=\"e\"", TomcatServerBehaviour.mergeArguments("a=b c=d", new String[] { "c=\"e\"" }, null, false));
		assertEquals("a=b c=\"e\"", TomcatServerBehaviour.mergeArguments("a=b c=d", new String[] { "c=\"e\"" }, null, true));
		// remove argument
		assertEquals("c=\"e\"", TomcatServerBehaviour.mergeArguments("a=b c=d", new String[] { "c=\"e\"" }, new String[] { "a=" }, true));
	}
	
	public void testArgMerge12() {
		assertEquals("a=b c=\"e f\"", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "c=\"e f\"" }, null, false));
		assertEquals("a=b c=\"e f\"", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "c=\"e f\"" }, null, true));
		// remove argument
		assertEquals("c=\"e f\"", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "c=\"e f\"" }, new String[] { "a=" }, true));
		assertEquals("a=g ", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "a=g" }, new String[] { "c=" }, true));
	}
	
	public void testArgMerge13() {
		assertEquals("a=b -c \"e\"", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "-c \"e\"" }, null, false));
		assertEquals("a=b -c \"e\"", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "-c \"e\"" }, null, true));
		// remove argument
		assertEquals("-c \"e\"", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "-c \"e\"" }, new String[] { "a=" }, true));
		assertEquals("a=g ", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "a=g" }, new String[] { "-c " }, true));
	}
	
	public void testArgMerge14() {
		assertEquals("a=b -c \"e f\"", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "-c \"e f\"" }, null, false));
		assertEquals("a=b -c \"e f\"", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "-c \"e f\"" }, null, true));
		// remove argument
		assertEquals("-c \"e f\"", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "-c \"e f\"" }, new String[] { "a=" }, true));
		assertEquals("a=g ", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "a=g" }, new String[] { "-c " }, true));
	}

	public void testArgMerge15() {
		assertEquals("b a", TomcatServerBehaviour.mergeArguments("b", new String[] { "a", "b" }, null, false));
		assertEquals("a b", TomcatServerBehaviour.mergeArguments("b", new String[] { "a", "b" }, null, true));
	}
	
	public void testArgMerge16() {
		assertEquals("c b a", TomcatServerBehaviour.mergeArguments("c b", new String[] { "a", "b" }, null, false));
		assertEquals("c a b", TomcatServerBehaviour.mergeArguments("c b", new String[] { "a", "b" }, null, true));
	}
	
	public void testArgMerge17() {
		// remove middle arguments
		assertEquals("a e f", TomcatServerBehaviour.mergeArguments("a -b c e", new String[] { "f" }, new String[] { "-b " }, false));
		assertEquals("a e f", TomcatServerBehaviour.mergeArguments("a -b \"c d\" e", new String[] { "f" }, new String[] { "-b " }, false));
		assertEquals("a e f", TomcatServerBehaviour.mergeArguments("a b=c e", new String[] { "f" }, new String[] { "b=" }, false));
		assertEquals("a e f", TomcatServerBehaviour.mergeArguments("a b=\"c d\" e", new String[] { "f" }, new String[] { "b=" }, false));
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