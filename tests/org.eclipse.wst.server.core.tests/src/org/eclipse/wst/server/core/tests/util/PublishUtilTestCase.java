/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests.util;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ModuleFile;
import org.eclipse.wst.server.core.util.ModuleFolder;
import org.eclipse.wst.server.core.util.PublishUtil;

public class PublishUtilTestCase extends TestCase {
	private static IProject testProject;
	private static IModuleResource[] testResources;
	private static IModuleResource[] testResources2;
	private static IPath[] preservePaths;
	private static IPath destination;
	
	private static void setFileContents(IFile file, String contents, boolean touch) throws CoreException {
		InputStream is = new ByteArrayInputStream(contents.getBytes());
		if (file.exists()) {
			if (touch) {
				file.setContents(is, IResource.FORCE, null);
			} else {
				long ts = file.getLocation().toFile().lastModified();
				file.setContents(is, IResource.FORCE, null);
				file.getLocation().toFile().setLastModified(ts);
			}
			file.refreshLocal(IResource.DEPTH_ZERO, null);
		} else
			file.create(is, true, null);
	}
	
	private static IProject getTestProject() throws CoreException {
		if (testProject == null) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("PublishTest");
			if (!project.exists())
				project.create(null);
			
			if (!project.isOpen())
				project.open(null);

			// Create a project with "A" and "B" files in the root of the
			// project and in "A" and "B" folders.  Create two module resource
			// arrays, one including all resources and one omitting the "B" files.
			// Prepare an array of IPaths for the "B" files to use in testing
			// preservation.
			testResources = new IModuleResource[4];
			testResources2 = new IModuleResource[3];
			preservePaths = new IPath[3];

			IFile rootFileA = project.getFile("rootFileA.txt");
			setFileContents(rootFileA, "rootFileA contents", true);
			testResources[0] = new ModuleFile(rootFileA, "rootFileA.txt", rootFileA.getProjectRelativePath());
			testResources2[0] = testResources[0];
			
			IFile rootFileB = project.getFile("rootFileB.txt");
			setFileContents(rootFileB, "rootFileB contents", true);
			testResources[1] = new ModuleFile(rootFileB, "rootFileB.txt", rootFileB.getProjectRelativePath());
			preservePaths[0] = rootFileB.getProjectRelativePath();
			
			IFolder folderA = project.getFolder("FolderA");
			if (!folderA.exists())
				folderA.create(true, true, null);
			ModuleFolder moduleFolder = new ModuleFolder(folderA, "FolderA", folderA.getProjectRelativePath());
			IModuleResource[] folderFiles = new IModuleResource[2];
			moduleFolder.setMembers(folderFiles);
			testResources[2] = moduleFolder;

			IFile folderAFileA = folderA.getFile("folderAFileA.txt");
			setFileContents(folderAFileA, "folderAFileA contents", true);
			folderFiles[0] = new ModuleFile(folderAFileA, "folderAFileA.txt", folderAFileA.getProjectRelativePath());
			
			IFile folderAFileB = folderA.getFile("folderAFileB.txt");
			setFileContents(folderAFileB, "folderAFileB contents", true);
			folderFiles[1] = new ModuleFile(folderAFileB, "folderAFileB.txt", folderAFileB.getProjectRelativePath());
			preservePaths[1] = folderAFileB.getProjectRelativePath();

			moduleFolder = new ModuleFolder(folderA, "FolderA", folderA.getProjectRelativePath());
			moduleFolder.setMembers(new IModuleResource[] {folderFiles[0]});
			testResources2[1] = moduleFolder;
			
			IFolder folderB = project.getFolder("FolderB");
			if (!folderB.exists())
				folderB.create(true, true, null);
			moduleFolder = new ModuleFolder(folderB, "FolderB", folderB.getProjectRelativePath());
			folderFiles = new IModuleResource[2];
			moduleFolder.setMembers(folderFiles);
			testResources[3] = moduleFolder;
			
			IFile folderBFileA = folderB.getFile("folderBFileA.txt");
			setFileContents(folderBFileA, "folderBFileA contents", true);
			folderFiles[0] = new ModuleFile(folderBFileA, "folderBFileA.txt", folderBFileA.getProjectRelativePath());
			
			IFile folderBFileB = folderB.getFile("folderBFileB.txt");
			setFileContents(folderBFileB, "folderBFileB contents", true);
			folderFiles[1] = new ModuleFile(folderBFileB, "folderBFileB.txt", folderBFileB.getProjectRelativePath());
			preservePaths[2] = folderBFileB.getProjectRelativePath();

			moduleFolder = new ModuleFolder(folderB, "FolderB", folderB.getProjectRelativePath());
			moduleFolder.setMembers(new IModuleResource[] {folderFiles[0]});
			testResources2[2] = moduleFolder;
			
			testProject = project;
		}
		return testProject;
	}

	private static IPath getDestination() {
		if (destination == null) {
			destination = ServerPlugin.getInstance().getTempDirectory("publish-destination");
		}
		return destination;
	}

	private static IModuleResource[] getModuleResources() {
		return testResources;
	}

	private static IModuleResource[] getModuleResources2() {
		return testResources2;
	}

	private static IPath[] getPreservePaths() {
		return preservePaths;
	}

	private static String getContents(IPath file) {
		char[] cBuffer = new char[1024];
		FileReader fr;
		try {
			fr = new FileReader(file.toFile());
			int count = fr.read(cBuffer);
			fr.close();
			return new String(cBuffer, 0, count);
		} catch (FileNotFoundException e) {
			return e.getMessage();
		} catch (IOException e) {
			return e.getMessage();
		}
	}

	public void test00FullCopy() throws Exception {
		IProject project = getTestProject();
		assertNotNull(project);
		IPath dest = getDestination();
		assertNotNull(dest);
		IModuleResource[] resources = getModuleResources();
		IStatus[] result = PublishUtil.publishSmart(resources, dest, null, null);
		assertNotNull(result);
		assertTrue(result.length == 0);

		// Verify file contents following initial copy of all resources
		String contents = getContents(dest.append(resources[0].getModuleRelativePath()));
		assertEquals("rootFileA contents", contents);
		contents = getContents(dest.append(resources[1].getModuleRelativePath()));
		assertEquals("rootFileB contents", contents);
		
		IModuleFolder mf = (IModuleFolder)resources[2];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderAFileA contents", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderAFileB contents", contents);
		
		mf = (IModuleFolder)resources[3];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderBFileA contents", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderBFileB contents", contents);
	}

	public void test01FullCopy() throws Exception {
		Thread.sleep(1000);	// wait to ensure time stamps differ

		IProject project = getTestProject();

		// Modify all file contents and time stamps
		IFile rootFileA = project.getFile("rootFileA.txt");
		setFileContents(rootFileA, "rootFileA contents 2", true);
		IFile rootFileB = project.getFile("rootFileB.txt");
		setFileContents(rootFileB, "rootFileB contents 2", true);

		IFile folderAFileA = project.getFile("FolderA/folderAFileA.txt");
		setFileContents(folderAFileA, "folderAFileA contents 2", true);
		IFile folderAFileB = project.getFile("FolderA/folderAFileB.txt");
		setFileContents(folderAFileB, "folderAFileB contents 2", true);
		
		IFile folderBFileA = project.getFile("FolderB/folderBFileA.txt");
		setFileContents(folderBFileA, "folderBFileA contents 2", true);
		IFile folderBFileB = project.getFile("FolderB/folderBFileB.txt");
		setFileContents(folderBFileB, "folderBFileB contents 2", true);

		// Publish all resources
		IPath dest = getDestination();
		assertNotNull(dest);
		IModuleResource[] resources = getModuleResources();
		IStatus[] result = PublishUtil.publishSmart(resources, dest, null, null);
		assertNotNull(result);
		assertTrue(result.length == 0);

		// Verify all files were copied
		String contents = getContents(dest.append(resources[0].getModuleRelativePath()));
		assertEquals("rootFileA contents 2", contents);
		contents = getContents(dest.append(resources[1].getModuleRelativePath()));
		assertEquals("rootFileB contents 2", contents);
		
		IModuleFolder mf = (IModuleFolder)resources[2];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderAFileA contents 2", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderAFileB contents 2", contents);
		
		mf = (IModuleFolder)resources[3];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderBFileA contents 2", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderBFileB contents 2", contents);
	}	

	public void test02PartialCopy() throws Exception {
		Thread.sleep(1000);	// wait to ensure time stamps differ
		
		IProject project = getTestProject();

		// Modify all file contents, but not the time stamps on the "B" files
		IFile rootFileA = project.getFile("rootFileA.txt");
		setFileContents(rootFileA, "rootFileA contents", true);
		IFile rootFileB = project.getFile("rootFileB.txt");
		setFileContents(rootFileB, "rootFileB contents", false);

		IFile folderAFileA = project.getFile("FolderA/folderAFileA.txt");
		setFileContents(folderAFileA, "folderAFileA contents", true);
		IFile folderAFileB = project.getFile("FolderA/folderAFileB.txt");
		setFileContents(folderAFileB, "folderAFileB contents", false);
		
		IFile folderBFileA = project.getFile("FolderB/folderBFileA.txt");
		setFileContents(folderBFileA, "folderBFileA contents", true);
		IFile folderBFileB = project.getFile("FolderB/folderBFileB.txt");
		setFileContents(folderBFileB, "folderBFileB contents", false);

		// Publish all resources
		IPath dest = getDestination();
		assertNotNull(dest);
		IModuleResource[] resources = getModuleResources();
		IStatus[] result = PublishUtil.publishSmart(resources, dest, null, null);
		assertNotNull(result);
		assertTrue(result.length == 0);

		// Verify the "A" files were copied, but not the "B" files since the time stamps didn't change
		String contents = getContents(dest.append(resources[0].getModuleRelativePath()));
		assertEquals("rootFileA contents", contents);
		contents = getContents(dest.append(resources[1].getModuleRelativePath()));
		assertEquals("rootFileB contents 2", contents);
		
		IModuleFolder mf = (IModuleFolder)resources[2];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderAFileA contents", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderAFileB contents 2", contents);
		
		mf = (IModuleFolder)resources[3];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderBFileA contents", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderBFileB contents 2", contents);
	}

	public void test03PreserveFiles() throws Exception {
		// Publish all resources except the B resources, preserving them them rather than delete them
		IModuleResource[] resources = getModuleResources();
		IModuleResource[] resources2 = getModuleResources2();
		IPath dest = getDestination();
		assertNotNull(dest);
		IStatus[] result = PublishUtil.publishSmart(resources2, dest, getPreservePaths(), null);
		assertNotNull(result);
		assertTrue(result.length == 0);

		// Verify "A" files are unchanged and "B" files have not been deleted
		String contents = getContents(dest.append(resources[0].getModuleRelativePath()));
		assertEquals("rootFileA contents", contents);
		contents = getContents(dest.append(resources[1].getModuleRelativePath()));
		assertEquals("rootFileB contents 2", contents);
		
		IModuleFolder mf = (IModuleFolder)resources[2];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderAFileA contents", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderAFileB contents 2", contents);
		
		mf = (IModuleFolder)resources[3];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderBFileA contents", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderBFileB contents 2", contents);
	}	

	public void test04PreserveFolder() throws Exception {
		// Create resource array including all resources except the "B" folder
		IModuleResource[] resources = getModuleResources();
		IModuleResource[] resources2 = new IModuleResource [3];
		System.arraycopy(resources, 0, resources2, 0, 3);
		IPath[] paths = new IPath[] { resources[3].getModuleRelativePath() };

		// Publish all resources except the B folder and preserve just the "B" folder
		IPath dest = getDestination();
		assertNotNull(dest);
		IStatus[] result = PublishUtil.publishSmart(resources2, dest, paths, null);
		assertNotNull(result);
		assertTrue(result.length == 0);

		// Verify non-"B" folder files are unchanged and "B" folder files have not been deleted
		String contents = getContents(dest.append(resources[0].getModuleRelativePath()));
		assertEquals("rootFileA contents", contents);
		contents = getContents(dest.append(resources[1].getModuleRelativePath()));
		assertEquals("rootFileB contents 2", contents);
		
		IModuleFolder mf = (IModuleFolder)resources[2];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderAFileA contents", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderAFileB contents 2", contents);
		
		mf = (IModuleFolder)resources[3];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderBFileA contents", contents);
		contents = getContents(dest.append(mf.members()[1].getModuleRelativePath()));
		assertEquals("folderBFileB contents 2", contents);
	}	

	public void test05DeleteFiles() throws Exception {
		// Publish all resources except the "B" files and "B" folder and don't preserve them
		IModuleResource[] resources = getModuleResources();
		IModuleResource[] resources2 = getModuleResources2();
		IModuleResource[] resources3 = new IModuleResource[2];
		System.arraycopy(resources2, 0, resources3, 0, 2);
		IPath dest = getDestination();
		assertNotNull(dest);
		IStatus[] result = PublishUtil.publishSmart(resources3, dest, null, null);
		assertNotNull(result);
		assertTrue(result.length == 0);
		
		IPath[] paths = getPreservePaths();
		
		// Verify "A" files are unchanged and "B" files and "B" folder have been deleted
		String contents = getContents(dest.append(resources[0].getModuleRelativePath()));
		assertEquals("rootFileA contents", contents);
		assertFalse(dest.append(paths[0]).toFile().exists());
		
		IModuleFolder mf = (IModuleFolder)resources[2];
		contents = getContents(dest.append(mf.members()[0].getModuleRelativePath()));
		assertEquals("folderAFileA contents", contents);
		assertFalse(dest.append(paths[1]).toFile().exists());
		
		mf = (IModuleFolder)resources[3];
		assertFalse(dest.append(mf.getModuleRelativePath()).toFile().exists());
	}

	public void test06DeleteProject() throws Exception {
		IProject project = getTestProject();
		project.delete(true, null);
		ServerPlugin.getInstance().removeTempDirectory("publish-destination");
	}
}