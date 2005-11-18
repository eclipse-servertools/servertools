/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.core.internal.IMemento;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.Trace;
import org.eclipse.jst.server.core.internal.XMLMemento;
import org.eclipse.wst.server.core.IRuntime;
/**
 * A runtime classpath provider provides the classpath for a Java server runtime.
 * <p>
 * This abstract class is intended to be extended only by clients
 * to extend the <code>runtimeClasspathProviders</code> extension point.
 * </p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @plannedfor 1.5
 */
public abstract class RuntimeClasspathProviderDelegate {
	private class SourceAttachmentUpdate {
		String runtimeId;
		String id;
		IPath entry;
		IPath sourceAttachmentPath;
		IPath sourceAttachmentRootPath;
		IClasspathAttribute[] attributes;
	}

	private List sourceAttachments;

	private String extensionId;

	public final void initialize(String id) {
		extensionId = id;
	}

	/**
	 * Returns the classpath container label for the given runtime and the given
	 * classpath container id (returned from getClasspathEntryIds()). This method
	 * must not return null.
	 * 
	 * @param runtime the runtime to resolve the container label for
	 * @param id the classpath entry id
	 * @return a classpath container label
	 */
	public abstract String getClasspathContainerLabel(IRuntime runtime, String id);

	/**
	 * Resolves (creates the classpath entries for) the classpath container with
	 * the given runtime and the given classpath container id (returned from
	 * getClasspathEntryIds()). If the classpath container cannot be resolved
	 * (for instance, if the runtime does not exist), return null.
	 * 
	 * @param runtime the runtime to resolve the container for
	 * @param id the classpath entry id
	 * @return an array of classpath entries for the container, or null if the
	 *   container could not be resolved
	 */
	public abstract IClasspathEntry[] resolveClasspathContainer(IRuntime runtime, String id);

	private void load() {
		sourceAttachments = new ArrayList();
		
		String id = extensionId;
		String filename = JavaServerPlugin.getInstance().getStateLocation().append(id + ".xml").toOSString();
		
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
			
			IMemento[] children = memento.getChildren("source-attachment");
			int size = children.length;
			
			for (int i = 0; i < size; i++) {
				try {
					SourceAttachmentUpdate sau = new SourceAttachmentUpdate();
					sau.runtimeId = children[i].getString("runtime-id");
					sau.id = children[i].getString("id");
					String temp = children[i].getString("entry");
					if (temp != null)
						sau.entry = new Path(temp);
					temp = children[i].getString("source-attachment-path");
					if (temp != null)
						sau.sourceAttachmentPath = new Path(temp);
					temp = children[i].getString("source-attachment-root-path");
					if (temp != null)
						sau.sourceAttachmentRootPath = new Path(temp);
					IMemento[] attrChildren = children[i].getChildren("attribute");
					if (attrChildren != null) {
						int size2 = attrChildren.length;
						sau.attributes = new IClasspathAttribute[size2];
						for (int j = 0; j < size2; j++) {
							String name = attrChildren[j].getString("name");
							String value = attrChildren[j].getString("value");
							sau.attributes[j] = JavaCore.newClasspathAttribute(name, value);
						}
					}
					sourceAttachments.add(sau);
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, "Could not load monitor: " + e);
				}
			}
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Could not load source path info: " + e.getMessage());
		}
	}

	/**
	 * Resolve the classpath container.
	 * 
	 * @param runtime a runtime
	 * @param id a container id
	 * @return a possibly empty array of classpath entries
	 */
	public IClasspathEntry[] resolveClasspathContainerImpl(IRuntime runtime, String id) {
		IClasspathEntry[] entries = resolveClasspathContainer(runtime, id);
		
		if (entries == null)
			entries = new IClasspathEntry[0];
		
		if (sourceAttachments == null)
			load();
		
		int size = entries.length;
		int size2 = sourceAttachments.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size2; j++) {
				SourceAttachmentUpdate sau = (SourceAttachmentUpdate) sourceAttachments.get(j);
				if ((id != null && sau.id.equals(id)) || (id == null && sau.id == null)) {
					if (sau.runtimeId.equals(runtime.getId()) && sau.entry.equals(entries[i].getPath())) {
						entries[i] = JavaCore.newLibraryEntry(entries[i].getPath(), sau.sourceAttachmentPath, sau.sourceAttachmentRootPath, new IAccessRule[0], sau.attributes, false);
					}
				}
			}
		}
		
		return entries;
	}

	private static void addJarFiles(File dir, List list, boolean includeSubdirectories) {
		int depth = 0;
		if (includeSubdirectories)
			depth = 2;
		addJarFiles(dir, list, depth);
	}

	private static void addJarFiles(File dir, List list, int depth) {
		if (dir == null)
			throw new IllegalArgumentException();
		
		File[] files = dir.listFiles();
		if (files != null) {
			int size = files.length;
			for (int i = 0; i < size; i++) {
				if (files[i].isDirectory() && depth > 0) {
					addJarFiles(files[i], list, depth - 1);
				} else if (files[i].getAbsolutePath().endsWith(".jar") || files[i].getAbsolutePath().endsWith(".zip")) {
					IPath path = new Path(files[i].getAbsolutePath());
					list.add(JavaCore.newLibraryEntry(path, null, null));
				}
			}
		}
	}

	/**
	 * Add library entries to the given list for every jar file found in the
	 * given directory. Optionally search subdirectories as well.
	 * 
	 * @param list a list
	 * @param dir a directory
	 * @param includeSubdirectories <code>true</code> to include subdirectories, and
	 *    <code>false</code> otherwise
	 */
	protected static void addLibraryEntries(List list, File dir, boolean includeSubdirectories) {
		if (dir == null)
			throw new IllegalArgumentException();
		addJarFiles(dir, list, includeSubdirectories);
	}
}