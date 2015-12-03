/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.server.core.internal.IMemento;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.RuntimeClasspathContainer;
import org.eclipse.jst.server.core.internal.Trace;
import org.eclipse.jst.server.core.internal.XMLMemento;
import org.eclipse.wst.server.core.IRuntime;
/**
 * A runtime classpath provider provides the classpath for a Java server runtime.
 * This provider is scoped by runtime type and may provide the classpath for multiple
 * runtime instances.
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
 * @plannedfor 3.0
 */
public abstract class RuntimeClasspathProviderDelegate {
	protected class SourceAttachmentUpdate {
		String runtimeId;
		IPath entry;
		IPath sourceAttachmentPath;
		IPath sourceAttachmentRootPath;
		IClasspathAttribute[] attributes;
	}

	private volatile List<SourceAttachmentUpdate> sourceAttachments;

	private String extensionId;

	private Map<String, IPath> runtimePathMap = Collections.synchronizedMap(new HashMap<String, IPath>());

	private Map<String, IClasspathEntry[]> previousClasspath = Collections.synchronizedMap(new HashMap<String, IClasspathEntry[]>());

	public RuntimeClasspathProviderDelegate() {
		// default constructor
	}

	/**
	 * Initializes this classpath provider with its life-long id.
	 * <p>
	 * This method is called by the framework.
	 * Clients should never call this method.
	 * </p>
	 * @param id the extension id
	 */
	public final void initialize(String id) {
		extensionId = id;
	}

	/**
	 * Resolves (creates the classpath entries for) the classpath container with
	 * the given runtime and the given classpath container id (returned from
	 * getClasspathEntryIds()). If the classpath container cannot be resolved
	 * (for instance, if the runtime does not exist), return null.
	 * 
	 * @param runtime the runtime to resolve the container for
	 * @return an array of classpath entries for the container, or null if the
	 *   container could not be resolved
	 * @deprecated use resolveClasspathContainer(IProject, IRuntime) instead
	 */
	public IClasspathEntry[] resolveClasspathContainer(IRuntime runtime) {
		return null;
	}

	/**
	 * Resolves (creates the classpath entries for) the classpath container with
	 * the given runtime and the given classpath container id (returned from
	 * getClasspathEntryIds()). If the classpath container cannot be resolved
	 * (for instance, if the runtime does not exist), return null.
	 * 
	 * @param project the project to resolve
	 * @param runtime the runtime to resolve the container for
	 * @return an array of classpath entries for the container, or null if the
	 *   container could not be resolved
	 */
	public IClasspathEntry[] resolveClasspathContainer(IProject project, IRuntime runtime) {
		return null;
	}

	/**
	 * Resolve the classpath container.
	 * 
	 * @param runtime a runtime
	 * @return a possibly empty array of classpath entries
	 * @deprecated should use resolveClasspathContainerImpl(IProject, IRuntime) instead
	 */
	public IClasspathEntry[] resolveClasspathContainerImpl(IRuntime runtime) {
		return resolveClasspathContainerImpl(null, runtime);
	}

	/**
	 * Resolve the classpath container.
	 * 
	 * @param project a project
	 * @param runtime a runtime
	 * @return a possibly empty array of classpath entries
	 */
	public IClasspathEntry[] resolveClasspathContainerImpl(IProject project, IRuntime runtime) {
		if (runtime == null)
			return new IClasspathEntry[0];
		runtimePathMap.put(runtime.getId(), runtime.getLocation());
		IClasspathEntry[] entries = resolveClasspathContainer(project, runtime);
		if (entries == null)
			entries = resolveClasspathContainer(runtime);
		
		if (entries == null)
			entries = new IClasspathEntry[0];
		
		synchronized (this) {
			if (sourceAttachments == null)
				load();
		}
		List<SourceAttachmentUpdate> srcAttachments = sourceAttachments;

		if (srcAttachments != null) {
			int size = entries.length;
			int size2 = srcAttachments.size();
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size2; j++) {
					SourceAttachmentUpdate sau = srcAttachments.get(j);
					if (sau.runtimeId.equals(runtime.getId()) && sau.entry.equals(entries[i].getPath())) {
						IClasspathAttribute[] consolidatedClasspathAttributes = consolidateClasspathAttributes(sau.attributes, entries[i].getExtraAttributes());
						entries[i] = JavaCore.newLibraryEntry(entries[i].getPath(), sau.sourceAttachmentPath, sau.sourceAttachmentRootPath, entries[i].getAccessRules(), consolidatedClasspathAttributes, false);
						break;
					}
				}
			}
		}
		
		String key = project.getName() + "/" + runtime.getId();
		if (!previousClasspath.containsKey(key))
			previousClasspath.put(key, entries);
		else {
			IClasspathEntry[] previousClasspathEntries = previousClasspath.get(key);
			
			if (previousClasspathEntries == null 
					|| previousClasspathEntries.length != entries.length 
					|| entriesChanged(previousClasspathEntries,entries)) {
				if (Trace.FINEST) {
					Trace.trace(Trace.STRING_FINEST, "Classpath update: " + key + " " + entries);
				}
				previousClasspath.put(key, entries);
				
				IPath path = new Path(RuntimeClasspathContainer.SERVER_CONTAINER);
				path = path.append(extensionId).append(runtime.getId());
				try {
					IJavaProject javaProject = JavaCore.create(project);
					JavaCore.setClasspathContainer(path, new IJavaProject[] { javaProject },
							new IClasspathContainer[] { null }, new NullProgressMonitor());
				} catch (Exception e) {
					if (Trace.WARNING) {
						Trace.trace(Trace.STRING_WARNING, "Error updating classpath", e);
					}
				}
			}
		}
		
		return entries;
	}

	private boolean entriesChanged(IClasspathEntry[] previousEntries, IClasspathEntry[] entries) {
		if (previousEntries.length != entries.length) {
			return true;
		}
		for (int i=0; i<previousEntries.length; i++) {
			if ((previousEntries[i] == null && entries[i] != null)
					|| (previousEntries[i].getPath() == null && entries[i].getPath() != null)
					|| !previousEntries[i].getPath().equals(entries[i].getPath())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Returns true if there are any changes in the runtime since the last time that the
	 * classpath was resolved which may affect the classpath, and false otherwise. This
	 * method is used to check projects when a runtime changes and automatically rebuild
	 * them if necessary.
	 * 
	 * @param runtime a runtime
	 * @return <code>true</code> if the classpath may change due to a change in the runtime,
	 *    and <code>false</code> if there are no changes
	 */
	public boolean hasRuntimeClasspathChanged(IRuntime runtime) {
		try {
			IPath path = runtimePathMap.get(runtime.getId());
			return (path != null && !path.equals(runtime.getLocation()));
		} catch (Exception e) {
			// ignore
		}
		return false;
	}

	private static void addJarFiles(File dir, List<IClasspathEntry> list, boolean includeSubdirectories) {
		int depth = 0;
		if (includeSubdirectories)
			depth = 2;
		addJarFiles(dir, list, depth);
	}

	private static void addJarFiles(File dir, List<IClasspathEntry> list, int depth) {
		if (dir == null)
			throw new IllegalArgumentException();
		
		File[] files = dir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory() && depth > 0) {
					addJarFiles(file, list, depth - 1);
				} else if (file.getAbsolutePath().endsWith(".jar") || file.getAbsolutePath().endsWith(".zip")) {
					IPath path = new Path(file.getAbsolutePath());
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
	protected static void addLibraryEntries(List<IClasspathEntry> list, File dir, boolean includeSubdirectories) {
		if (dir == null)
			throw new IllegalArgumentException();
		addJarFiles(dir, list, includeSubdirectories);
	}

	/**
	 * Request that the classpath container for the given runtime and id be updated
	 * with the given classpath container entries.
	 * 
	 * @param runtime a runtime
	 * @param entries an array of classpath entries
	 */
	public void requestClasspathContainerUpdate(IRuntime runtime, IClasspathEntry[] entries) {
		// default behaviour is to save the source path entries
		if (runtime == null || entries == null)
			return;
		
		// find the source attachments
		List<SourceAttachmentUpdate> srcAttachments = new ArrayList<SourceAttachmentUpdate>();
		
		for (IClasspathEntry entry : entries) {
			if (entry.getSourceAttachmentPath() != null || (entry.getExtraAttributes() != null && entry.getExtraAttributes().length > 0)) {
				SourceAttachmentUpdate sau = new SourceAttachmentUpdate();
				sau.runtimeId = runtime.getId();
				sau.entry = entry.getPath();
				sau.sourceAttachmentPath = entry.getSourceAttachmentPath();
				sau.sourceAttachmentRootPath = entry.getSourceAttachmentRootPath();
				sau.attributes = entry.getExtraAttributes();
				srcAttachments.add(sau);
			}
		}
		sourceAttachments = srcAttachments;
		save();
	}

	/**
	 * Load source attachment info.
	 */
	private void load() {
		List<SourceAttachmentUpdate> srcAttachments = new ArrayList<SourceAttachmentUpdate>();
		
		String id = extensionId;
		String filename = JavaServerPlugin.getInstance().getStateLocation().append(id + ".xml").toOSString();
		if (!(new File(filename)).exists())
			return;
		
		try {
			IMemento memento = XMLMemento.loadMemento(filename);
			
			IMemento[] children = memento.getChildren("source-attachment");
			for (IMemento child : children) {
				try {
					SourceAttachmentUpdate sau = new SourceAttachmentUpdate();
					sau.runtimeId = child.getString("runtime-id");
					String temp = child.getString("entry");
					if (temp != null)
						sau.entry = new Path(temp);
					temp = child.getString("source-attachment-path");
					if (temp != null)
						sau.sourceAttachmentPath = new Path(temp);
					temp = child.getString("source-attachment-root-path");
					if (temp != null)
						sau.sourceAttachmentRootPath = new Path(temp);
					IMemento[] attrChildren = child.getChildren("attribute");
					if (attrChildren != null) {
						int size2 = attrChildren.length;
						sau.attributes = new IClasspathAttribute[size2];
						for (int j = 0; j < size2; j++) {
							String name = attrChildren[j].getString("name");
							String value = attrChildren[j].getString("value");
							sau.attributes[j] = JavaCore.newClasspathAttribute(name, value);
						}
					}
					srcAttachments.add(sau);
				} catch (Exception e) {
					if (Trace.WARNING) {
						Trace.trace(Trace.STRING_WARNING, "Could not load source attachment: " + e);
					}
				}
			}
		} catch (Exception e) {
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Could not load source path info", e);
			}
		}
		sourceAttachments = srcAttachments;
	}

	/**
	 * Save source attachment info.
	 */
	private synchronized void save() {
		List<SourceAttachmentUpdate> srcAttachments = sourceAttachments;
		if (srcAttachments == null)
			return;
		String id = extensionId;
		String filename = JavaServerPlugin.getInstance().getStateLocation().append(id + ".xml").toOSString();
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("classpath");

			Iterator iterator = srcAttachments.iterator();
			while (iterator.hasNext()) {
				SourceAttachmentUpdate sau = (SourceAttachmentUpdate) iterator.next();
				IMemento child = memento.createChild("source-attachment");
				child.putString("runtime-id", sau.runtimeId);
				if (sau.entry != null)
					child.putString("entry", sau.entry.toPortableString());
				if (sau.sourceAttachmentPath != null)
					child.putString("source-attachment-path", sau.sourceAttachmentPath.toPortableString());
				if (sau.sourceAttachmentRootPath != null)
					child.putString("source-attachment-root-path", sau.sourceAttachmentRootPath.toPortableString());
				if (sau.attributes != null) {
					for (IClasspathAttribute attr : sau.attributes) {
						IMemento attrChild = child.createChild("attribute");
						attrChild.putString("name", attr.getName());
						attrChild.putString("value", attr.getValue());
					}
				}
			}
			
			memento.saveToFile(filename);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error saving source path info", e);
			}
		}
	}
	
	public IClasspathAttribute[] consolidateClasspathAttributes(IClasspathAttribute[] sourceAttachmentAttributes, IClasspathAttribute[] classpathEntryAttributes) {
		List classpathAttributeList = new ArrayList();
		classpathAttributeList.addAll(Arrays.asList(sourceAttachmentAttributes));
		for (int i = 0; i < classpathEntryAttributes.length; i++) {
			boolean attributeCollision = false;
			for (int j = 0; j < sourceAttachmentAttributes.length; j++) {
				String name = classpathEntryAttributes[i].getName();
				if(name != null && name.equals(sourceAttachmentAttributes[j].getName())) {
					attributeCollision = true;
					break;
				}
			}
			if(!attributeCollision) {
				classpathAttributeList.add(classpathEntryAttributes[i]);
			}
		}
		return (IClasspathAttribute[]) classpathAttributeList.toArray(new IClasspathAttribute[classpathAttributeList.size()]);
	}
}