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

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.Messages;
import org.eclipse.jst.server.core.internal.ProgressUtil;
import org.eclipse.jst.server.core.internal.Trace;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
/**
 * Utility class with an assortment of useful file methods.
 * 
 * <p>
 * <b>Note:</b> This class/interface is part of an interim API that is still under development and expected to
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken
 * (repeatedly) as the API evolves.
 * </p>
 * @plannedfor 1.5
 */
public class PublishUtil {
	// size of the buffer
	private static final int BUFFER = 10240;

	// the buffer
	private static byte[] buf = new byte[BUFFER];

	/**
	 * FileUtil cannot be created. Use static methods.
	 */
	private PublishUtil() {
		super();
	}

	/**
	 * Copy a file from a to b. Closes the input stream after use.
	 *
	 * @param in java.io.InputStream
	 * @param to java.lang.String
	 * @return a status
	 */
	public static IStatus copyFile(InputStream in, String to) {
		OutputStream out = null;
		
		try {
			out = new FileOutputStream(to);
	
			int avail = in.read(buf);
			while (avail > 0) {
				out.write(buf, 0, avail);
				avail = in.read(buf);
			}
			return Status.OK_STATUS;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error copying file", e);
			return new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCopyingFile, new String[] {to, e.getLocalizedMessage()}), e);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (Exception ex) {
				// ignore
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
				// ignore
			}
		}
	}

	/**
	 * Copy a file from a to b. Closes the input stream after use.
	 *
	 * @param in an input stream
	 * @param to a path to copy to. the directory must already exist
	 * @param ts timestamp
	 * @throws CoreException if anything goes wrong
	 */
	private static void copyFile(InputStream in, IPath to, long ts) throws CoreException {
		OutputStream out = null;
		
		File tempFile = null;
		try {
			File file = to.toFile();
			File tempDir = JavaServerPlugin.getInstance().getStateLocation().toFile();
			tempFile = File.createTempFile("tmp", "." + to.getFileExtension(), tempDir);
			
			out = new FileOutputStream(tempFile);
	
			int avail = in.read(buf);
			while (avail > 0) {
				out.write(buf, 0, avail);
				avail = in.read(buf);
			}
			
			out.close();
			out = null;
			
			int i = 0;
			//System.out.print("file: " + file.exists() + " " + tempFile.exists());
			if (file.exists()) {
				i += 1;
				if (!file.delete()) {
					i += 2;
					tempFile.delete();
					throw new Exception(NLS.bind(Messages.errorDelete, file.toString()));
				}
			}
			i += 4;
			//System.out.println(" " + i + " " + file.exists() + " " + tempFile.exists());
			if (!tempFile.renameTo(file)) {
				throw new Exception(NLS.bind(Messages.errorRename, tempFile.toString(), file.toString()));
			}
			
			if (ts != IResource.NULL_STAMP && ts != 0)
				file.setLastModified(ts);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error copying file", e);
			throw new CoreException(new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCopyingFile, to.toOSString(), e.getLocalizedMessage()), e));
		} finally {
			if (tempFile != null && tempFile.exists())
				tempFile.deleteOnExit();
			try {
				if (in != null)
					in.close();
			} catch (Exception ex) {
				// ignore
			}
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
				// ignore
			}
		}
	}

	private static void copyFile(IModuleFile mf, IPath path) throws CoreException {
		IFile file = (IFile) mf.getAdapter(IFile.class);
		if (file != null) {
			copyFile(file.getContents(), path, file.getLocalTimeStamp());
		} else {
			File file2 = (File) mf.getAdapter(File.class);
			InputStream in = null;
			try {
				in = new FileInputStream(file2);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorReading, file2.getAbsolutePath()), e));
			}
			copyFile(in, path, file2.lastModified());
		}
	}

	/**
	 * Recursively deletes a directory.
	 *
	 * @param dir java.io.File
	 * @param monitor a progress monitor, or <code>null</code>
	 */
	public static void deleteDirectory(File dir, IProgressMonitor monitor) {
		try {
			if (!dir.exists() || !dir.isDirectory())
				return;
	
			File[] files = dir.listFiles();
			int size = files.length;
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(NLS.bind(Messages.deletingTask, new String[] { dir.getAbsolutePath() }), size * 10);
	
			// cycle through files
			for (int i = 0; i < size; i++) {
				File current = files[i];
				if (current.isFile()) {
					current.delete();
					monitor.worked(10);
				} else if (current.isDirectory()) {
					monitor.subTask(NLS.bind(Messages.deletingTask, new String[] {current.getAbsolutePath()}));
					deleteDirectory(current, ProgressUtil.getSubMonitorFor(monitor, 10));
				}
			}
			dir.delete();
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error deleting directory " + dir.getAbsolutePath(), e);
		}
	}
	
	public static void smartCopy(IModuleResource[] resources, IPath path, IProgressMonitor monitor) throws CoreException {
		if (resources == null)
			return;
		
		File toDir = path.toFile();
		File[] toFiles = toDir.listFiles();
		int fromSize = resources.length;
		
		if (toDir.exists() && toDir.isDirectory()) {
			int toSize = toFiles.length;
			// check if this exact file exists in the new directory
			for (int i = 0; i < toSize; i++) {
				String name = toFiles[i].getName();
				boolean isDir = toFiles[i].isDirectory();
				boolean found = false;
				for (int j = 0; j < fromSize; j++) {
					if (name.equals(resources[j].getName()) && isDir == resources[j] instanceof IModuleFolder)
						found = true;
				}
	
				// delete file if it can't be found or isn't the correct type
				if (!found) {
					if (isDir)
						deleteDirectory(toFiles[i], null);
					else
						toFiles[i].delete();
				}
				if (monitor.isCanceled())
					return;
			}
		} else {
			if (toDir.isFile())
				toDir.delete();
			toDir.mkdir();
		}
			
		monitor.worked(50);
		
		// cycle through files and only copy when it doesn't exist
		// or is newer
		toFiles = toDir.listFiles();
		int toSize = toFiles.length;
		int dw = 0;
		if (toSize > 0)
			dw = 500 / toSize;

		for (int i = 0; i < fromSize; i++) {
			IModuleResource current = resources[i];

			// check if this is a new or newer file
			boolean copy = true;
			boolean currentIsDir = current instanceof IModuleFolder;
			if (!currentIsDir) {
				//String name = current.getName();
				//IModuleFile mf = (IModuleFile) current;
				
				//long mod = mf.getModificationStamp();
				// TODO
				/*for (int j = 0; j < toSize; j++) {
					if (name.equals(toFiles[j].getName()) && mod <= toFiles[j].lastModified())
						copy = false;
				}*/
			}

			if (copy) {
				//String fromFile = current.getAbsolutePath();
				IPath toPath = path.append(current.getName());
				if (!currentIsDir) {
					IModuleFile mf = (IModuleFile) current;
					copyFile(mf, toPath);
					monitor.worked(dw);
				} else { //if (currentIsDir) {
					IModuleFolder folder = (IModuleFolder) current;
					IModuleResource[] children = folder.members();
					monitor.subTask(NLS.bind(Messages.copyingTask, new String[] {resources[i].getName(), current.getName()}));
					smartCopy(children, toPath, ProgressUtil.getSubMonitorFor(monitor, dw));
				}
			}
			if (monitor.isCanceled())
				return;
		}
		monitor.worked(500 - dw * toSize);
		monitor.done();
	}

	public static void handleDelta(int kind, IPath path, IModuleResourceDelta delta) throws CoreException {
		IModuleResource resource = delta.getModuleResource();
		int kind2 = delta.getKind();
		
		if (resource instanceof IModuleFile) {
			IModuleFile file = (IModuleFile) resource;
			if (kind2 == IModuleResourceDelta.REMOVED)
				deleteFile(path, file);
			else
				copyFile(path, file);
			return;
		}
		
		if (kind2 == IModuleResourceDelta.ADDED) {
			IPath path2 = path.append(resource.getModuleRelativePath()).append(resource.getName());
			path2.toFile().mkdirs();
		} else if (kind == IModuleResourceDelta.REMOVED) {
			IPath path2 = path.append(resource.getModuleRelativePath()).append(resource.getName());
			path2.toFile().delete();
		}
		IModuleResourceDelta[] childDeltas = delta.getAffectedChildren();
		int size = childDeltas.length;
		for (int i = 0; i < size; i++) {
			handleDelta(kind, path, childDeltas[i]);
		}
	}

	protected static void deleteFile(IPath path, IModuleFile file) {
		IPath path2 = path.append(file.getModuleRelativePath()).append(file.getName());
		path2.toFile().delete();
	}

	private static void copyFile(IPath path, IModuleFile mf) throws CoreException {
		IPath path3 = path.append(mf.getModuleRelativePath()).append(mf.getName());
		File f = path3.toFile().getParentFile();
		if (!f.exists())
			f.mkdirs();
		
		IFile file = (IFile) mf.getAdapter(IFile.class);
		if (file != null)
			copyFile(file.getContents(), path3, file.getLocalTimeStamp());
		else {
			File file2 = (File) mf.getAdapter(File.class);
			InputStream in = null;
			try {
				in = new FileInputStream(file2);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorReading, file2.getAbsolutePath()), e));
			}
			copyFile(in, path3, file2.lastModified());
		}
	}

	public static void copy(IModuleResource[] resources, IPath path) throws CoreException {
		if (resources == null)
			return;
		
		int size = resources.length;
		for (int i = 0; i < size; i++) {
			copy(resources[i], path);
		}
	}

	private static void copy(IModuleResource resource, IPath path) throws CoreException {
		if (resource instanceof IModuleFolder) {
			IModuleFolder folder = (IModuleFolder) resource;
			copy(folder.members(), path);
		} else {
			IModuleFile mf = (IModuleFile) resource;
			IPath path3 = path.append(mf.getModuleRelativePath()).append(mf.getName());
			File f = path3.toFile().getParentFile();
			if (!f.exists())
				f.mkdirs();
			IFile file = (IFile) mf.getAdapter(IFile.class);
			if (file != null)
				copyFile(file.getContents(), path3, file.getLocalTimeStamp());
			else {
				File file2 = (File) mf.getAdapter(File.class);
				InputStream in = null;
				try {
					in = new FileInputStream(file2);
				} catch (IOException e) {
					throw new CoreException(new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorReading, file2.getAbsolutePath()), e));
				}
				copyFile(in, path3, file2.lastModified());
			}
		}
	}

	/**
	 * Creates a new zip file containing the given module resources. Deletes the existing file
	 * (and doesn't create a new one) if resources is null or empty.
	 * 
	 * @param resources
	 * @param zipPath
	 * @throws CoreException
	 */
	public static void createZipFile(IModuleResource[] resources, IPath zipPath) throws CoreException {
		if (resources == null || resources.length == 0) {
			// should also check if resources consists of all empty directories
			File file = zipPath.toFile();
			if (file.exists())
				file.delete();
			return;
		}
		
		File tempFile = null;
		try {
			File file = zipPath.toFile();
			File tempDir = JavaServerPlugin.getInstance().getStateLocation().toFile();
			tempFile = File.createTempFile("tmp", "." + zipPath.getFileExtension(), tempDir);
			
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(tempFile));
			ZipOutputStream zout = new ZipOutputStream(bout);
			addZipEntries(zout, resources);
			zout.close();
			
			if (file.exists()) {
				if (!file.delete()) {
					tempFile.delete();
					throw new Exception(NLS.bind(Messages.errorDelete, file.toString()));
				}
			}
			if (!tempFile.renameTo(file))
				throw new Exception(NLS.bind(Messages.errorRename, tempFile.toString(), file.toString()));
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error zipping", e);
			throw new CoreException(new Status(IStatus.ERROR, JavaServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCreatingZipFile, zipPath.lastSegment(), e.getLocalizedMessage()), e));
		} finally {
			if (tempFile != null && tempFile.exists())
				tempFile.deleteOnExit();
		}
	}

	private static void addZipEntries(ZipOutputStream zout, IModuleResource[] resources) throws Exception {
		int size = resources.length;
		for (int i = 0; i < size; i++) {
			if (resources[i] instanceof IModuleFolder) {
				IModuleFolder mf = (IModuleFolder) resources[i];
				IModuleResource[] res = mf.members();
				addZipEntries(zout, res);
				continue;
			}
			
			IModuleFile mf = (IModuleFile) resources[i];
			IPath path = mf.getModuleRelativePath().append(mf.getName());
			
			ZipEntry ze = new ZipEntry(path.toPortableString());
			
			InputStream in = null;
			long ts = 0;
			IFile file = (IFile) mf.getAdapter(IFile.class);
			if (file != null) {
				ts = file.getLocalTimeStamp();
				in = file.getContents();
			} else {
				File file2 = (File) mf.getAdapter(File.class);
				ts = file2.lastModified();
				in = new FileInputStream(file2);
			}
			
			if (ts != IResource.NULL_STAMP && ts != 0)
				ze.setTime(ts);
			
			zout.putNextEntry(ze);
			
			try {
				int n = 0;
				while (n > -1) {
					n = in.read(buf);
					if (n > 0)
						zout.write(buf, 0, n);
				}
			} finally {
				in.close();
			}
			
			zout.closeEntry();
		}
	}
}