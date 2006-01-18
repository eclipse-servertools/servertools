/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests.module;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jst.j2ee.datamodel.properties.IJ2EEComponentCreationDataModelProperties;
import org.eclipse.jst.j2ee.internal.web.archive.operations.WebComponentCreationDataModelProvider;
//import org.eclipse.jst.j2ee.internal.web.archive.operations.WebFacetProjectCreationDataModelProvider;
import org.eclipse.wst.common.componentcore.datamodel.properties.IComponentCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.util.ProjectModule;

public class ModuleHelper {
	public static void createModule(String name) throws Exception {
		//IDataModel dataModel = DataModelFactory.createDataModel(new WebFacetProjectCreationDataModelProvider());
		IDataModel dataModel = DataModelFactory.createDataModel(new WebComponentCreationDataModelProvider());
      dataModel.setProperty(IComponentCreationDataModelProperties.COMPONENT_NAME, name);
      dataModel.setBooleanProperty(IJ2EEComponentCreationDataModelProperties.ADD_TO_EAR, false);
      dataModel.setIntProperty(IJ2EEComponentCreationDataModelProperties.COMPONENT_VERSION, 22); //J2EEVersionConstants.12);
		dataModel.getDefaultOperation().execute(new NullProgressMonitor(), null);
		dataModel.dispose();
	}

	public static void createWebContent(String name, int i) throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		IFile file = project.getFile(new Path("WebContent").append("test" + i + ".html"));
		String content = "Hello!";
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
		file.create(in, true, null);
	}

	public static void createXMLContent(String name, int i) throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		IFile file = project.getFile(new Path("WebContent").append("test" + i + ".xml"));
		String content = "<book name='test'><isbn>299827698</isbn></book>";
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
		file.create(in, true, null);
	}

	public static void createJavaContent(String name, int i) throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		IFile file = project.getFile(new Path("JavaSource").append("Test" + i + ".java"));
		String content = "public class Test" + i + " { }";
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
		file.create(in, true, null);
	}

	public static void deleteModule(String name) throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		project.delete(true, null);
	}

	public static void buildIncremental() throws CoreException {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		boolean interrupted = true;
		while (interrupted) {
			try {
				Platform.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD,
						new NullProgressMonitor());
				interrupted = false;
			} catch (InterruptedException e) {
				// 
			}
		}
	}

	public static void buildFull() throws CoreException {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
		boolean interrupted = true;
		while (interrupted) {
			try {
				Platform.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD,
						new NullProgressMonitor());
				interrupted = false;
			} catch (InterruptedException e) {
				// 
			}
		}
	}

	public static void buildClean() throws CoreException {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		boolean interrupted = true;
		while (interrupted) {
			try {
				Platform.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD,
						new NullProgressMonitor());
				interrupted = false;
			} catch (InterruptedException e) {
				// 
			}
		}
	}

	public static IModule getModule(String name) throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		IModule module = ServerUtil.getModule(project);
		if (module == null)
			throw new Exception("No module in Web project");
		
		return module;
	}

	public static int countFilesInModule(IModule module) throws CoreException {
		ProjectModule pm = (ProjectModule) module.loadAdapter(ProjectModule.class, null);
		IModuleResource[] mr = pm.members();
		
		int count = 0;
		int size = mr.length;
		for (int i = 0; i < size; i++) {
			if (mr[i] instanceof IModuleFolder)
				count += countFilesInFolder((IModuleFolder) mr[i]);
			else
				count++;
		}
		
		return count;
	}

	protected static int countFilesInFolder(IModuleFolder mf) {
		int count = 0;
		IModuleResource[] mr = mf.members();
		int size = mr.length;
		for (int i = 0; i < size; i++) {
			if (mr[i] instanceof IModuleFolder)
				count += countFilesInFolder((IModuleFolder) mr[i]);
			else
				count++;
		}
		
		return count;
	}

	public static int countFilesInDelta(IModuleResourceDelta delta) throws CoreException {
		int count = 0;
		if (delta.getModuleResource() instanceof IModuleFile)
			count++;
		
		IModuleResourceDelta[] children = delta.getAffectedChildren();
		int size = children.length;
		for (int i = 0; i < size; i++) {
			count += countFilesInDelta(children[i]);
		}
		
		return count;
	}

	public static IModuleFile getModuleFile(IModule module, IPath path) throws CoreException {
		ProjectModule pm = (ProjectModule) module.loadAdapter(ProjectModule.class, null);
		IModuleResource[] mr = pm.members();
		
		int size = mr.length;
		for (int i = 0; i < size; i++) {
			if (mr[i].getModuleRelativePath().equals(path)) {
				if (mr[i] instanceof IModuleFile)
					return (IModuleFile) mr[i];
				return null;
			} else if (mr[i].getModuleRelativePath().isPrefixOf(path))
				return getModuleFile((IModuleFolder) mr[i], path);
		}
		
		return null;
	}

	protected static IModuleFile getModuleFile(IModuleFolder mf, IPath path) {
		IModuleResource[] mr = mf.members();
		int size = mr.length;
		for (int i = 0; i < size; i++) {
			if (mr[i].getModuleRelativePath().equals(path)) {
				if (mr[i] instanceof IModuleFile)
					return (IModuleFile) mr[i];
				return null;
			} else if (mr[i].getModuleRelativePath().isPrefixOf(path))
				return getModuleFile((IModuleFolder) mr[i], path);
		}
		
		return null;
	}
}