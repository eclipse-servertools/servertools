/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.tests.performance;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.test.performance.PerformanceTestCase;
import org.eclipse.wst.server.core.internal.IMemento;
import org.eclipse.wst.server.core.internal.XMLMemento;

public class MementoTestCase extends PerformanceTestCase {
	protected static IPath getLocalPath(IPath path2) {
		try {
			URL url = FileLocator.find(TestsPlugin.instance.getBundle(), path2, null);
			url = FileLocator.toFileURL(url);
			return new Path(url.getPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void testMementoLoad() throws Exception {
		IPath path = getLocalPath(new Path("data").append("publish.xml"));
		for (int i = 0; i < 5; i++) { 
			startMeasuring();	
			XMLMemento.loadMemento(path.toOSString());
			stopMeasuring();
		}
		commitMeasurements();
		assertPerformance();
	}

	public void testMementoSave() throws Exception {
		IPath path = getLocalPath(new Path("data").append("publish.xml"));
		File f = File.createTempFile("perftest", null);
		XMLMemento memento = (XMLMemento) XMLMemento.loadMemento(path.toOSString());
		
		for (int i = 0; i < 5; i++) { 
			startMeasuring();	
			memento.saveToFile(f.getAbsolutePath());
			stopMeasuring();
			f.delete();
		}
		commitMeasurements();
		assertPerformance();
	}

	public void testMementoCreateSave() throws Exception {
		IPath path = getLocalPath(new Path("data").append("publish.xml"));
		File f = File.createTempFile("perftest", null);
		XMLMemento memento = (XMLMemento) XMLMemento.loadMemento(path.toOSString());
		
		for (int i = 0; i < 5; i++) { 
			startMeasuring();	
			XMLMemento memento2 = XMLMemento.createWriteRoot("server");
			
			copy(memento, memento2);
			
			memento2.saveToFile(f.getAbsolutePath());
			stopMeasuring();
			f.delete();
		}
		commitMeasurements();
		assertPerformance();
	}

	protected void copy(IMemento a, IMemento b) {
		String s = a.getString("name");
		if (s != null)
			b.putString("name", s);
		s = a.getString("module-ids");
		if (s != null)
			b.putString("module-ids", s);
		s = a.getString("module-type-id");
		if (s != null)
			b.putString("module-type-id", s);
		s = a.getString("module-type-version");
		if (s != null)
			b.putString("module-type-version", s);
		s = a.getString("stamp");
		if (s != null)
			b.putString("stamp", s);
		
		IMemento[] children = a.getChildren("module");
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				IMemento child = b.createChild("module");
				copy(children[i], child);
			}
		}
		children = a.getChildren("folder");
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				IMemento child = b.createChild("folder");
				copy(children[i], child);
			}
		}
		children = a.getChildren("file");
		if (children != null) {
			int size = children.length;
			for (int i = 0; i < size; i++) {
				IMemento child = b.createChild("file");
				copy(children[i], child);
			}
		}
	}
}
