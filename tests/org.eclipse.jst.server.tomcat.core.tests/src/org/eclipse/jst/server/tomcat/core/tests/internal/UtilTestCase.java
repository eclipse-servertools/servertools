/*******************************************************************************
 * Copyright (c) 2004, 2021 IBM Corporation and others.
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
package org.eclipse.jst.server.tomcat.core.tests.internal;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServerBehaviour;
import org.eclipse.jst.server.tomcat.core.internal.VerifyResourceSpec;
import org.eclipse.jst.server.tomcat.core.tests.RuntimeLocation;

import junit.framework.TestCase;

public class UtilTestCase extends TestCase {
	public void testArgMerge() {
		assertEquals("", TomcatServerBehaviour.mergeArguments("", new String[] { }, null, false));
		assertEquals("", TomcatServerBehaviour.mergeArguments("", new String[] { }, null, true));
		assertEquals("", TomcatServerBehaviour.mergeArguments("", new String[] { }, new String[] { "a" } , true));

		assertEquals("a=b", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b"}, null, false));
		assertEquals("a=b", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b"}, null, true));
		// new arguments aren't removed
		assertEquals("a=b", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b"}, new String[] { "a" }, true));

		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b", "c=d" }, null, false));
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b", "c=d" }, null, true));
		// new arguments aren't removed
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("", new String[] { "a=b", "c=d" }, new String[] { "a" }, true));

		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "c=d" }, null, false));
		assertEquals("a=b c=d", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "c=d" }, null, true));
		// old argument is removed
		assertEquals("c=d", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "c=d" }, new String[] { "a" }, true));

		assertEquals("a=c", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "a=c" }, null, false));
		assertEquals("a=c", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "a=c" }, null, true));
		// replaced argument is removed
		assertEquals("", TomcatServerBehaviour.mergeArguments("a=b", new String[] { "a=c" }, new String[] { "a" }, true));

		assertEquals("a b=2 d e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, null, false));
		assertEquals("a b=2 d e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, null, true));
		// old argument is removed
		assertEquals("b=2 d e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, new String[] { "a" }, true));
		assertEquals("a e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, new String[] { "b", "d" }, true));
		assertEquals("a e=3", TomcatServerBehaviour.mergeArguments("a b=c d", new String[] { "b=2", "e=3" }, new String[] { "b=", "d" }, true));

		assertEquals("a bb=c d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" }, null, false));
		assertEquals("a bb=c d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" }, null, true));
		// remove expected wrong argument
		assertEquals("a d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" },  new String[] { "b" }, true));
		// avoid removing wrong argument
		assertEquals("a bb=c d b=2 e=3", TomcatServerBehaviour.mergeArguments("a bb=c d", new String[] { "b=2", "e=3" },  new String[] { "b=" }, true));

		assertEquals("a", TomcatServerBehaviour.mergeArguments("a", new String[] { }, null, false));
		assertEquals("a", TomcatServerBehaviour.mergeArguments("a", new String[] { }, null, true));
		// remove single exiting argument
		assertEquals("", TomcatServerBehaviour.mergeArguments("a", new String[] { }, new String[] { "a" }, true));

		assertEquals("a b", TomcatServerBehaviour.mergeArguments("a", new String[] { "b" }, null, false));
		assertEquals("a b", TomcatServerBehaviour.mergeArguments("a", new String[] { "b" }, null, true));
		// remove single exiting argument
		assertEquals("b", TomcatServerBehaviour.mergeArguments("a", new String[] { "b" }, new String[] { "a" }, true));

		assertEquals("a b c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, null, false));
		assertEquals("a b c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, null, true));
		// remove argumets
		assertEquals("b c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, new String[] { "a" }, true));
		assertEquals("a c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, new String[] { "b" }, true));
		assertEquals("c d", TomcatServerBehaviour.mergeArguments("a b ", new String[] { "c", "d" }, new String[] { "a", "b" }, true));

		assertEquals("a=b c=\"e\"", TomcatServerBehaviour.mergeArguments("a=b c=d", new String[] { "c=\"e\"" }, null, false));
		assertEquals("a=b c=\"e\"", TomcatServerBehaviour.mergeArguments("a=b c=d", new String[] { "c=\"e\"" }, null, true));
		// remove argument
		assertEquals("c=\"e\"", TomcatServerBehaviour.mergeArguments("a=b c=d", new String[] { "c=\"e\"" }, new String[] { "a=" }, true));

		assertEquals("a=b c=\"e f\"", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "c=\"e f\"" }, null, false));
		assertEquals("a=b c=\"e f\"", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "c=\"e f\"" }, null, true));
		// remove argument
		assertEquals("c=\"e f\"", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "c=\"e f\"" }, new String[] { "a=" }, true));
		assertEquals("a=g ", TomcatServerBehaviour.mergeArguments("a=b c=\"d e\"", new String[] { "a=g" }, new String[] { "c=" }, true));

		assertEquals("a=b -c \"e\"", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "-c \"e\"" }, null, false));
		assertEquals("a=b -c \"e\"", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "-c \"e\"" }, null, true));
		// remove argument
		assertEquals("-c \"e\"", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "-c \"e\"" }, new String[] { "a=" }, true));
		assertEquals("a=g ", TomcatServerBehaviour.mergeArguments("a=b -c d", new String[] { "a=g" }, new String[] { "-c " }, true));

		assertEquals("a=b -c \"e f\"", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "-c \"e f\"" }, null, false));
		assertEquals("a=b -c \"e f\"", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "-c \"e f\"" }, null, true));
		// remove argument
		assertEquals("-c \"e f\"", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "-c \"e f\"" }, new String[] { "a=" }, true));
		assertEquals("a=g ", TomcatServerBehaviour.mergeArguments("a=b -c \"d e\"", new String[] { "a=g" }, new String[] { "-c " }, true));

		assertEquals("b a", TomcatServerBehaviour.mergeArguments("b", new String[] { "a", "b" }, null, false));
		assertEquals("a b", TomcatServerBehaviour.mergeArguments("b", new String[] { "a", "b" }, null, true));

		assertEquals("c b a", TomcatServerBehaviour.mergeArguments("c b", new String[] { "a", "b" }, null, false));
		assertEquals("c a b", TomcatServerBehaviour.mergeArguments("c b", new String[] { "a", "b" }, null, true));

		// remove middle arguments
		assertEquals("a e f", TomcatServerBehaviour.mergeArguments("a -b c e", new String[] { "f" }, new String[] { "-b " }, false));
		assertEquals("a e f", TomcatServerBehaviour.mergeArguments("a -b \"c d\" e", new String[] { "f" }, new String[] { "-b " }, false));
		assertEquals("a e f", TomcatServerBehaviour.mergeArguments("a b=c e", new String[] { "f" }, new String[] { "b=" }, false));
		assertEquals("a e f", TomcatServerBehaviour.mergeArguments("a b=\"c d\" e", new String[] { "f" }, new String[] { "b=" }, false));

		VerifyResourceSpec spec = new VerifyResourceSpec("");
		assertEquals("", spec.toString());
		String [] paths = spec.getPaths();
		assertEquals(1, paths.length);
		assertEquals("", paths[0]);

		spec = new VerifyResourceSpec("file ");
		assertEquals("file ", spec.toString());
		paths = spec.getPaths();
		assertEquals(1, paths.length);
		assertEquals("file", paths[0]);

		spec = new VerifyResourceSpec("dir" + File.separator + "file");
		paths = spec.getPaths();
		assertEquals(1, paths.length);
		assertEquals("dir" + File.separator + "file", paths[0]);

		spec = new VerifyResourceSpec("dir" + File.separator + "file| alt ");
		paths = spec.getPaths();
		assertEquals(2, paths.length);
		assertEquals("dir" + File.separator + "file", paths[0]);
		assertEquals("dir" + File.separator + "alt", paths[1]);

		spec = new VerifyResourceSpec("dir" + File.separator + "file| alt1 | alt2 ");
		paths = spec.getPaths();
		assertEquals(3, paths.length);
		assertEquals("dir" + File.separator + "file", paths[0]);
		assertEquals("dir" + File.separator + "alt1", paths[1]);
		assertEquals("dir" + File.separator + "alt2", paths[2]);

		if (RuntimeLocation.runtimeLocation != null) {
			spec = new VerifyResourceSpec("conf");
			IStatus status = spec.checkResource(RuntimeLocation.runtimeLocation);
			assertTrue(status.isOK());
		}

		if (RuntimeLocation.runtimeLocation != null) {
			spec = new VerifyResourceSpec("conf" + File.separator + "server.xml");
			IStatus status = spec.checkResource(RuntimeLocation.runtimeLocation);
			assertTrue(status.isOK());
		}

		if (RuntimeLocation.runtimeLocation != null) {
			spec = new VerifyResourceSpec("conf" + File.separator + "nofile|server.xml");
			IStatus status = spec.checkResource(RuntimeLocation.runtimeLocation);
			assertTrue(status.isOK());
		}

		if (RuntimeLocation.runtimeLocation != null) {
			spec = new VerifyResourceSpec("conf" + File.separator + "nofile");
			IStatus status = spec.checkResource(RuntimeLocation.runtimeLocation);
			assertFalse(status.isOK());
		}
	}

	public void testArgParsing() {
		String[] parsedArguments = DebugPlugin.parseArguments(String.join(" ", TomcatServerBehaviour.getAllowReflectionArguments()));
		for (int i = 0; i < parsedArguments.length; i++) {
			assertEquals(TomcatServerBehaviour.getAllowReflectionArguments()[i], parsedArguments[i]);
		}
	}

	public void testBug574268() throws IOException {
		Properties props = new Properties();
		props.load(new InputStreamReader(getClass().getResourceAsStream("UtilTestCase.properties"), "utf8"));
		// irrevocably strips the quotes from the argument values
		String[] before = DebugPlugin.parseArguments(props.getProperty("before"));
		String mergedVMArguments = TomcatServerBehaviour.mergeArguments(String.join(" ", before), TomcatServerBehaviour.getAllowReflectionArguments(), null, false);
		for (int i = 0; i < before.length; i++) {
			assertTrue("missing " + before[i], mergedVMArguments.contains(before[i]));
		}
		for (int i = 0; i < TomcatServerBehaviour.getAllowReflectionArguments().length; i++) {
			assertTrue("missing " + TomcatServerBehaviour.getAllowReflectionArguments()[i], mergedVMArguments.contains(TomcatServerBehaviour.getAllowReflectionArguments()[i]));
		}
		assertFalse("still broken", mergedVMArguments.equals(props.get("after")));
	}
}
