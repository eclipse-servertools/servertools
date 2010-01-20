/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.tests.j2ee;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jst.server.core.tests.TestsPlugin;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.*;
import org.eclipse.wst.server.core.util.ProjectModule;
import java.io.IOException;
import java.net.URL;

public class ModuleHelper {
	protected static IPath getLocalPath(IPath path) {
		try {
			URL url = FileLocator.find(TestsPlugin.instance.getBundle(), path, null);
			url = FileLocator.toFileURL(url);
			return new Path(url.getPath());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean importProject(String zipName, String[] projectNames) {
		IPath zipPath = getLocalPath(new Path("data").append(zipName));
		ProjectUnzipUtil util = new ProjectUnzipUtil(zipPath, projectNames);
		return util.createProjects();
	}

	public static void deleteProject(final String projectName) throws Exception {
		class DeleteJob extends Job {
			public DeleteJob() {
				super("Deleting project");
				
				IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
				setRule(ruleFactory.createRule(ResourcesPlugin.getWorkspace().getRoot()));
			}

			protected IStatus run(IProgressMonitor monitor) {
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				try {
					project.delete(true, null);
				} catch (CoreException ce) {
					return ce.getStatus();
				}
				return Status.OK_STATUS;
			}
		}
		
		Job job = new DeleteJob();
		job.schedule();
		job.join();
		//IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		//project.delete(true, null);
	}

	public static void buildIncremental() throws CoreException {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		boolean interrupted = true;
		while (interrupted) {
			try {
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD,
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
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD,
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
				Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD,
						new NullProgressMonitor());
				interrupted = false;
			} catch (InterruptedException e) {
				// 
			}
		}
	}

	public static IModule getModuleFromProject(String name) throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(name);
		IModule module = ServerUtil.getModule(project);
		if (module == null)
			throw new Exception("No module in project");
		
		return module;
	}

	public static IModule getModule(String type, String name) throws Exception {
		IModule[] modules = ServerUtil.getModules(type);
		for (IModule module : modules) {
			if (module.getName().equals(name))
				return module;
		}
		
		return null;
	}

	public static int countFiles(IModule module) throws CoreException {
		ModuleDelegate pm = (ModuleDelegate)module.loadAdapter(ModuleDelegate.class, null);
		IModuleResource[] mr = pm.members();
		
		int count = 0;
		for (IModuleResource m : mr) {
			if (m instanceof IModuleFolder)
				count += countFiles((IModuleFolder) m);
			else
				count++;
		}
		
		return count;
	}

	protected static int countFiles(IModuleFolder mf) {
		int count = 0;
		IModuleResource[] mr = mf.members();
		if (mr == null)
			return 0;
		
		for (IModuleResource m : mr) {
			if (m instanceof IModuleFolder)
				count += countFiles((IModuleFolder) m);
			else
				count++;
		}
		
		return count;
	}

	public static int countFolders(IModule module) throws CoreException {
		ModuleDelegate pm = (ModuleDelegate) module.loadAdapter(ModuleDelegate.class, null);
		IModuleResource[] mr = pm.members();
		
		int count = 0;
		for (IModuleResource m : mr) {
			if (m instanceof IModuleFolder) {
				count++;
				count += countFolders((IModuleFolder) m);
			}
		}
		
		return count;
	}

	protected static int countFolders(IModuleFolder mf) {
		int count = 0;
		IModuleResource[] mr = mf.members();
		if (mr == null)
			return 0;
		
		for (IModuleResource m : mr) {
			if (m instanceof IModuleFolder) {
				count ++;
				count += countFolders((IModuleFolder) m);
			}
		}
		
		return count;
	}

	public static int countFilesInDelta(IModuleResourceDelta delta) throws CoreException {
		int count = 0;
		if (delta.getModuleResource() instanceof IModuleFile)
			count++;
		
		IModuleResourceDelta[] children = delta.getAffectedChildren();
		for (IModuleResourceDelta child : children)
			count += countFilesInDelta(child);
		
		return count;
	}

	public static IModuleFile getModuleFile(IModule module, IPath path) throws CoreException {
		ProjectModule pm = (ProjectModule) module.loadAdapter(ProjectModule.class, null);
		IModuleResource[] mr = pm.members();
		
		for (IModuleResource m : mr) {
			if (m.getModuleRelativePath().append(m.getName()).equals(path)) {
				if (m instanceof IModuleFile)
					return (IModuleFile) m;
				return null;
			} else if (m.getModuleRelativePath().isPrefixOf(path) && m instanceof IModuleFolder) {
				IModuleFile mf2 = getModuleFile((IModuleFolder) m, path);
				if (mf2 != null)
					return mf2;
			}
		}
		
		return null;
	}

	protected static IModuleFile getModuleFile(IModuleFolder mf, IPath path) {
		IModuleResource[] mr = mf.members();
		if (mr == null)
			return null;
		
		for (IModuleResource m : mr) {
			if (m.getModuleRelativePath().append(m.getName()).equals(path)) {
				if (m instanceof IModuleFile)
					return (IModuleFile) m;
				return null;
			} else if (m.getModuleRelativePath().isPrefixOf(path) && m instanceof IModuleFolder) {
				IModuleFile mf2 = getModuleFile((IModuleFolder) m, path);
				if (mf2 != null)
					return mf2;
			}
		}
		
		return null;
	}

	public static boolean fileExists(IModule module, IPath path) throws CoreException {
		return getModuleFile(module, path) != null;
	}

	public static boolean fileExists(IModule module, String path) throws CoreException {
		return fileExists(module, new Path(path));
	}

	public static IModuleFolder getModuleFolder(IModule module, IPath path) throws CoreException {
		ProjectModule pm = (ProjectModule) module.loadAdapter(ProjectModule.class, null);
		IModuleResource[] mr = pm.members();
		
		for (IModuleResource m : mr) {
			if (m.getModuleRelativePath().append(m.getName()).equals(path)) {
				if (m instanceof IModuleFolder)
					return (IModuleFolder) m;
				return null;
			} else if (m.getModuleRelativePath().isPrefixOf(path) && m instanceof IModuleFolder) {
				IModuleFolder mf2 = getModuleFolder((IModuleFolder) m, path);
				if (mf2 != null)
					return mf2;
			}
		}
		
		return null;
	}

	protected static IModuleFolder getModuleFolder(IModuleFolder mf, IPath path) {
		IModuleResource[] mr = mf.members();
		if (mr == null)
			return null;
		
		for (IModuleResource m : mr) {
			if (m.getModuleRelativePath().append(m.getName()).equals(path)) {
				if (m instanceof IModuleFolder)
					return (IModuleFolder) m;
				return null;
			} else if (m.getModuleRelativePath().isPrefixOf(path) && m instanceof IModuleFolder) {
				IModuleFolder mf2 = getModuleFolder((IModuleFolder) m, path);
				if (mf2 != null)
					return mf2;
			}
		}
		
		return null;
	}

	public static boolean folderExists(IModule module, IPath path) throws CoreException {
		return getModuleFile(module, path) != null;
	}

	public static boolean folderExists(IModule module, String path) throws CoreException {
		return folderExists(module, new Path(path));
	}

	public static void listModule(IModule module) throws CoreException {
		System.out.println("--- Contents of " + module.getName() + "/" + module.getId() + "---");
		ProjectModule pm = (ProjectModule) module.loadAdapter(ProjectModule.class, null);
		IModuleResource[] mr = pm.members();
		
		for (IModuleResource m : mr) {
			if (m instanceof IModuleFile) {
				System.out.println(m.getName());
			} else {
				System.out.println(m.getName() + "/");
				listFolder((IModuleFolder) m, "  ");
			}
		}
		System.out.println("------");
	}

	protected static void listFolder(IModuleFolder mf, String pad) {
		IModuleResource[] mr = mf.members();
		if (mr == null)
			return;
		
		for (IModuleResource m : mr) {
			if (m instanceof IModuleFile) {
				System.out.println(pad + m.getName());
			} else {
				System.out.println(pad + m.getName() + "/");
				listFolder((IModuleFolder) m, pad + "  ");
			}
		}
	}
}