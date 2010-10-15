/*******************************************************************************
 * Copyright (c) 2007, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.internal.Messages;
import org.eclipse.wst.server.core.internal.ProgressUtil;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
/**
 * Utility class with an assortment of useful publishing file methods.
 *
 * @since 1.1
 */
public class PublishHelper {
	// size of the buffer
	private static final int BUFFER = 65536;

	// the buffer
	private static byte[] buf = new byte[BUFFER];

	private static final IStatus[] EMPTY_STATUS = new IStatus[0];

	private static final File defaultTempDir = ServerPlugin.getInstance().getStateLocation().toFile();

	private static final String TEMPFILE_PREFIX = "tmp";

	private File tempDir;

	/**
	 * Create a new PublishHelper.
	 * 
	 * @param tempDirectory a temporary directory to use during publishing, or <code>null</code>
	 *    to use the default. If it does not exist, the folder will be created
	 */
	public PublishHelper(File tempDirectory) {
		this.tempDir = tempDirectory;
		if (tempDir == null)
			tempDir = defaultTempDir;
		else if (!tempDir.exists())
			tempDir.mkdirs();
	}

	/**
	 * Copy a file from a to b. Closes the input stream after use.
	 * 
	 * @param in an input stream
	 * @param to a path to copy to. the directory must already exist
	 * @param ts timestamp
	 * @throws CoreException if anything goes wrong
	 */
	private void copyFile(InputStream in, IPath to, long ts, IModuleFile mf) throws CoreException {
		OutputStream out = null;
		
		File tempFile = null;
		try {
			File file = to.toFile();
			tempFile = File.createTempFile(TEMPFILE_PREFIX, "." + to.getFileExtension(), tempDir);
			
			out = new FileOutputStream(tempFile);
			
			int avail = in.read(buf);
			while (avail > 0) {
				out.write(buf, 0, avail);
				avail = in.read(buf);
			}
			
			out.close();
			out = null;
			
			moveTempFile(tempFile, file);
			
			if (ts != IResource.NULL_STAMP && ts != 0)
				file.setLastModified(ts);
		} catch (CoreException e) {
			throw e;
		} catch (Exception e) {
			IPath path = mf.getModuleRelativePath().append(mf.getName());
			Trace.trace(Trace.SEVERE, "Error copying file: " + path.toOSString() + " to " + to.toOSString(), e);
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCopyingFile, path.toOSString(), e.getLocalizedMessage()), null));
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

	/**
	 * Utility method to recursively delete a directory.
	 *
	 * @param dir a directory
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public static IStatus[] deleteDirectory(File dir, IProgressMonitor monitor) {
		if (!dir.exists() || !dir.isDirectory())
			return new IStatus[] { new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorNotADirectory, dir.getAbsolutePath()), null) };
		
		List<IStatus> status = new ArrayList<IStatus>(2);
		
		try {
			File[] files = dir.listFiles();
			int size = files.length;
			monitor = ProgressUtil.getMonitorFor(monitor);
			monitor.beginTask(NLS.bind(Messages.deletingTask, new String[] { dir.getAbsolutePath() }), size * 10);
			
			// cycle through files
			boolean deleteCurrent = true;
			for (int i = 0; i < size; i++) {
				File current = files[i];
				if (current.isFile()) {
					if (!current.delete()) {
						status.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, files[i].getAbsolutePath()), null));
						deleteCurrent = false;
					}
					monitor.worked(10);
				} else if (current.isDirectory()) {
					monitor.subTask(NLS.bind(Messages.deletingTask, new String[] {current.getAbsolutePath()}));
					IStatus[] stat = deleteDirectory(current, ProgressUtil.getSubMonitorFor(monitor, 10));
					if (stat != null && stat.length > 0) {
						deleteCurrent = false;
						addArrayToList(status, stat);
					}
				}
			}
			if (deleteCurrent && !dir.delete())
				status.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, dir.getAbsolutePath()), null));
			monitor.done();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error deleting directory " + dir.getAbsolutePath(), e);
			status.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, e.getLocalizedMessage(), null));
		}
		
		IStatus[] stat = new IStatus[status.size()];
		status.toArray(stat);
		return stat;
	}

	/**
	 * Smart copy the given module resources to the given path.
	 * 
	 * @param resources an array of module resources
	 * @param path an external path to copy to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of error and warning status 
	 */
	public IStatus[] publishSmart(IModuleResource[] resources, IPath path, IProgressMonitor monitor) {
		return publishSmart(resources, path, null, monitor);
	}

	/**
	 * Smart copy the given module resources to the given path.
	 * 
	 * @param resources an array of module resources
	 * @param path an external path to copy to
	 * @param ignore an array of paths relative to path to ignore, i.e. not delete or copy over
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of error and warning status 
	 */
	public IStatus[] publishSmart(IModuleResource[] resources, IPath path, IPath[] ignore, IProgressMonitor monitor) {
		if (resources == null)
			return EMPTY_STATUS;
		
		monitor = ProgressUtil.getMonitorFor(monitor);
		
		List<IStatus> status = new ArrayList<IStatus>(2);
		File toDir = path.toFile();
		int fromSize = resources.length;
		String[] fromFileNames = new String[fromSize];
		for (int i = 0; i < fromSize; i++)
			fromFileNames[i] = resources[i].getName();
		List<String> ignoreFileNames = new ArrayList<String>();
		if (ignore != null) {
			for (int i = 0; i < ignore.length; i++) {
				if (ignore[i].segmentCount() == 1) {
					ignoreFileNames.add(ignore[i].toOSString());
				}
			}
		}
		
		//	cache files and file names for performance
		File[] toFiles = null;
		String[] toFileNames = null;
		
		boolean foundExistingDir = false;
		if (toDir.exists()) {
			if (toDir.isDirectory()) {
				foundExistingDir = true;
				toFiles = toDir.listFiles();
				int toSize = toFiles.length;
				toFileNames = new String[toSize];
				
				// check if this exact file exists in the new directory
				for (int i = 0; i < toSize; i++) {
					toFileNames[i] = toFiles[i].getName();
					boolean isDir = toFiles[i].isDirectory();
					boolean found = false;
					for (int j = 0; j < fromSize; j++) {
						if (toFileNames[i].equals(fromFileNames[j]) && isDir == resources[j] instanceof IModuleFolder) {
							found = true;
							break;
						}
					}
					
					// delete file if it can't be found or isn't the correct type
					if (!found) {
						boolean delete = true;
						// if should be preserved, don't delete and don't try to copy
						for (String preserveFileName : ignoreFileNames) {
							if (toFileNames[i].equals(preserveFileName)) {
								delete = false;
								break;
							}
						}
						if (delete) {
							if (isDir) {
								IStatus[] stat = deleteDirectory(toFiles[i], null);
								addArrayToList(status, stat);
							} else {
								if (!toFiles[i].delete())
									status.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, toFiles[i].getAbsolutePath()), null));
							}
						}
						toFiles[i] = null;
						toFileNames[i] = null;
					}
				}
			} else { //if (toDir.isFile())
				if (!toDir.delete()) {
					status.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, toDir.getAbsolutePath()), null));
					IStatus[] stat = new IStatus[status.size()];
					status.toArray(stat);
					return stat;
				}
			}
		}
		if (!foundExistingDir && !toDir.mkdirs()) {
			status.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorMkdir, toDir.getAbsolutePath()), null));
			IStatus[] stat = new IStatus[status.size()];
			status.toArray(stat);
			return stat;
		}
		
		if (monitor.isCanceled())
			return new IStatus[] { Status.CANCEL_STATUS };
		
		monitor.worked(50);
		
		// cycle through files and only copy when it doesn't exist
		// or is newer
		if (toFiles == null) {
			toFiles = toDir.listFiles();
			if (toFiles == null)
				toFiles = new File[0];
		}
		int toSize = toFiles.length;
		
		int dw = 0;
		if (toSize > 0)
			dw = 500 / toSize;
		
		// cache file names and last modified dates for performance
		if (toFileNames == null)
			toFileNames = new String[toSize];
		long[] toFileMod = new long[toSize];
		for (int i = 0; i < toSize; i++) {
			if (toFiles[i] != null) {
				if (toFileNames[i] != null)
					toFileNames[i] = toFiles[i].getName();
				toFileMod[i] = toFiles[i].lastModified();
			}
		}
		
		for (int i = 0; i < fromSize; i++) {
			IModuleResource current = resources[i];
			String name = fromFileNames[i];
			boolean currentIsDir = current instanceof IModuleFolder;
			
			if (!currentIsDir) {
				// check if this is a new or newer file
				boolean copy = true;
				IModuleFile mf = (IModuleFile) current;
				
				long mod = -1;
				IFile file = (IFile) mf.getAdapter(IFile.class);
				if (file != null) {
					mod = file.getLocalTimeStamp();
				} else {
					File file2 = (File) mf.getAdapter(File.class);
					mod = file2.lastModified();
				}
				
				for (int j = 0; j < toSize; j++) {
					if (name.equals(toFileNames[j]) && mod == toFileMod[j]) {
						copy = false;
						break;
					}
				}
				
				if (copy) {
					try {
						copyFile(mf, path.append(name));
					} catch (CoreException ce) {
						status.add(ce.getStatus());
					}
				}
				monitor.worked(dw);
			} else { //if (currentIsDir) {
				IModuleFolder folder = (IModuleFolder) current;
				IModuleResource[] children = folder.members();

				// build array of ignored Paths that apply to this folder
				IPath[] ignoreChildren = null;
				if (ignore != null) {
					List<IPath> ignoreChildPaths = new ArrayList<IPath>();
					for (int j = 0; j < ignore.length; j++) {
						IPath preservePath = ignore[j];
						if (preservePath.segment(0).equals(name)) {
							ignoreChildPaths.add(preservePath.removeFirstSegments(1));
						}
					}
					if (ignoreChildPaths.size() > 0)
						ignoreChildren = ignoreChildPaths.toArray(new Path[ignoreChildPaths.size()]);
				}
				monitor.subTask(NLS.bind(Messages.copyingTask, new String[] {name, name}));
				IStatus[] stat = publishSmart(children, path.append(name), ignoreChildren, ProgressUtil.getSubMonitorFor(monitor, dw));
				addArrayToList(status, stat);
			}
		}
		if (monitor.isCanceled())
			return new IStatus[] { Status.CANCEL_STATUS };
		
		monitor.worked(500 - dw * toSize);
		monitor.done();
		
		IStatus[] stat = new IStatus[status.size()];
		status.toArray(stat);
		return stat;
	}

	/**
	 * Handle a delta publish.
	 * 
	 * @param delta a module resource delta
	 * @param path the path to publish to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishDelta(IModuleResourceDelta[] delta, IPath path, IProgressMonitor monitor) {
		if (delta == null)
			return EMPTY_STATUS;
		
		monitor = ProgressUtil.getMonitorFor(monitor);
		
		List<IStatus> status = new ArrayList<IStatus>(2);
		int size2 = delta.length;
		for (int i = 0; i < size2; i++) {
			IStatus[] stat = publishDelta(delta[i], path, monitor);
			addArrayToList(status, stat);
		}
		
		IStatus[] stat = new IStatus[status.size()];
		status.toArray(stat);
		return stat;
	}

	/**
	 * Handle a delta publish.
	 * 
	 * @param delta a module resource delta
	 * @param path the path to publish to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishDelta(IModuleResourceDelta delta, IPath path, IProgressMonitor monitor) {
		List<IStatus> status = new ArrayList<IStatus>(2);
		
		IModuleResource resource = delta.getModuleResource();
		int kind2 = delta.getKind();
		
		if (resource instanceof IModuleFile) {
			IModuleFile file = (IModuleFile) resource;
			try {
				if (kind2 == IModuleResourceDelta.REMOVED)
					deleteFile(path, file);
				else {
					IPath path2 = path.append(file.getModuleRelativePath()).append(file.getName());
					File f = path2.toFile().getParentFile();
					if (!f.exists())
						f.mkdirs();
					
					copyFile(file, path2);
				}
			} catch (CoreException ce) {
				status.add(ce.getStatus());
			}
			IStatus[] stat = new IStatus[status.size()];
			status.toArray(stat);
			return stat;
		}
		
		if (kind2 == IModuleResourceDelta.ADDED) {
			IPath path2 = path.append(resource.getModuleRelativePath()).append(resource.getName());
			File file = path2.toFile();
			if (!file.exists() && !file.mkdirs()) {
				status.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorMkdir, path2), null));
				IStatus[] stat = new IStatus[status.size()];
				status.toArray(stat);
				return stat;
			}
		}
		
		IModuleResourceDelta[] childDeltas = delta.getAffectedChildren();
		int size = childDeltas.length;
		for (int i = 0; i < size; i++) {
			IStatus[] stat = publishDelta(childDeltas[i], path, monitor);
			addArrayToList(status, stat);
		}
		
		if (kind2 == IModuleResourceDelta.REMOVED) {
			IPath path2 = path.append(resource.getModuleRelativePath()).append(resource.getName());
			File file = path2.toFile();
			if (file.exists() && !file.delete()) {
				status.add(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, path2), null));
			}
		}
		
		IStatus[] stat = new IStatus[status.size()];
		status.toArray(stat);
		return stat;
	}

	private static void deleteFile(IPath path, IModuleFile file) throws CoreException {
		Trace.trace(Trace.PUBLISHING, "Deleting: " + file.getName() + " from " + path.toString());
		IPath path2 = path.append(file.getModuleRelativePath()).append(file.getName());
		if (path2.toFile().exists() && !path2.toFile().delete())
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, path2), null));
	}

	private void copyFile(IModuleFile mf, IPath path) throws CoreException {
		Trace.trace(Trace.PUBLISHING, "Copying: " + mf.getName() + " to " + path.toString());
		
		if(!isCopyFile(mf, path)){
			return;
		}
		
		IFile file = (IFile) mf.getAdapter(IFile.class);
		if (file != null)
			copyFile(file.getContents(), path, file.getLocalTimeStamp(), mf);
		else {
			File file2 = (File) mf.getAdapter(File.class);
			InputStream in = null;
			try {
				in = new FileInputStream(file2);
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorReading, file2.getAbsolutePath()), e));
			}
			copyFile(in, path, file2.lastModified(), mf);
		}
	}
	
	/**
	 * Returns <code>true<code/> if the module file should be copied to the destination, <code>false</codre> otherwise.
	 * @param moduleFile the module file
	 * @param toPath destination.
	 * @return <code>true<code/>, if the module file should be copied
	 */
	protected boolean isCopyFile(IModuleFile moduleFile, IPath toPath){
		return true;
	}

	/**
	 * Publish the given module resources to the given path.
	 * 
	 * @param resources an array of module resources
	 * @param path a path to publish to
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishFull(IModuleResource[] resources, IPath path, IProgressMonitor monitor) {
		if (resources == null)
			return EMPTY_STATUS;
		
		monitor = ProgressUtil.getMonitorFor(monitor);
		
		List<IStatus> status = new ArrayList<IStatus>(2);
		int size = resources.length;
		for (int i = 0; i < size; i++) {
			IStatus[] stat = copy(resources[i], path, monitor);
			addArrayToList(status, stat);
		}
		
		IStatus[] stat = new IStatus[status.size()];
		status.toArray(stat);
		return stat;
	}

	private IStatus[] copy(IModuleResource resource, IPath path, IProgressMonitor monitor) {
		String name = resource.getName();
		Trace.trace(Trace.PUBLISHING, "Copying: " + name + " to " + path.toString());
		List<IStatus> status = new ArrayList<IStatus>(2);
		if (resource instanceof IModuleFolder) {
			IModuleFolder folder = (IModuleFolder) resource;
			IStatus[] stat = publishFull(folder.members(), path, monitor);
			addArrayToList(status, stat);
		} else {
			IModuleFile mf = (IModuleFile) resource;
			path = path.append(mf.getModuleRelativePath()).append(name);
			File f = path.toFile().getParentFile();
			if (!f.exists())
				f.mkdirs();
			try {
				copyFile(mf, path);
			} catch (CoreException ce) {
				status.add(ce.getStatus());
			}
		}
		IStatus[] stat = new IStatus[status.size()];
		status.toArray(stat);
		return stat;
	}

	/**
	 * Creates a new zip file containing the given module resources. Deletes the existing file
	 * (and doesn't create a new one) if resources is null or empty.
	 * 
	 * @param resources an array of module resources
	 * @param path the path where the zip file should be created 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishZip(IModuleResource[] resources, IPath path, IProgressMonitor monitor) {
		if (resources == null || resources.length == 0) {
			// should also check if resources consists of all empty directories
			File file = path.toFile();
			if (file.exists())
				file.delete();
			return EMPTY_STATUS;
		}
		
		monitor = ProgressUtil.getMonitorFor(monitor);
		
		File tempFile = null;
		try {
			File file = path.toFile();
			tempFile = File.createTempFile(TEMPFILE_PREFIX, "." + path.getFileExtension(), tempDir);
			
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(tempFile));
			ZipOutputStream zout = new ZipOutputStream(bout);
			addZipEntries(zout, resources);
			zout.close();
			
			moveTempFile(tempFile, file);
		} catch (CoreException e) {
			return new IStatus[] { e.getStatus() };
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error zipping", e);
			return new Status[] { new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCreatingZipFile, path.lastSegment(), e.getLocalizedMessage()), e) };
		} finally {
			if (tempFile != null && tempFile.exists())
				tempFile.deleteOnExit();
		}
		return EMPTY_STATUS;
	}

	private static void addZipEntries(ZipOutputStream zout, IModuleResource[] resources) throws Exception {
		if (resources == null)
			return;
		
		int size = resources.length;
		for (int i = 0; i < size; i++) {
			if (resources[i] instanceof IModuleFolder) {
				IModuleFolder mf = (IModuleFolder) resources[i];
				IModuleResource[] res = mf.members();
				
				IPath path = mf.getModuleRelativePath().append(mf.getName());
				String entryPath = path.toPortableString();
				if (!entryPath.endsWith("/"))
					entryPath += '/';
				
				ZipEntry ze = new ZipEntry(entryPath);
				
				long ts = 0;
				IContainer folder = (IContainer) mf.getAdapter(IContainer.class);
				if (folder != null)
					ts = folder.getLocalTimeStamp();
				
				if (ts != IResource.NULL_STAMP && ts != 0)
					ze.setTime(ts);
				
				zout.putNextEntry(ze);
				zout.closeEntry();
				
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

	/**
	 * Accepts an IModuleResource array which is expected to contain a single
	 * IModuleFile resource and copies it to the specified path, which should
	 * include the name of the file to write.  If the array contains more than
	 * a single resource or the resource is not an IModuleFile resource, the
	 * file is not created.  Currently no error is returned, but error handling
	 * is recommended since that is expected to change in the future.
	 * 
	 * @param resources an array containing a single IModuleFile resource
	 * @param path the path, including file name, where the file should be created
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishToPath(IModuleResource[] resources, IPath path, IProgressMonitor monitor) {
		if (resources == null || resources.length == 0) {
			// should also check if resources consists of all empty directories
			File file = path.toFile();
			if (file.exists())
				file.delete();
			return EMPTY_STATUS;
		}
		
		monitor = ProgressUtil.getMonitorFor(monitor);

		if (resources.length == 1 && resources[0] instanceof IModuleFile) {
			try {
				copyFile((IModuleFile) resources[0], path);
			}
			catch (CoreException e) {
				return new IStatus[] { e.getStatus() };
			}
		}

		return EMPTY_STATUS;
	}

	/**
	 * Utility method to move a temp file into position by deleting the original and
	 * swapping in a new copy.
	 *  
	 * @param tempFile
	 * @param file
	 * @throws CoreException
	 */
	private void moveTempFile(File tempFile, File file) throws CoreException {
		if (file.exists()) {
			if (!safeDelete(file, 2)) {
				// attempt to rewrite an existing file with the tempFile contents if
				// the existing file can't be deleted to permit the move
				try {
					InputStream in = new FileInputStream(tempFile);
					IStatus status = copyFile(in, file.getPath());
					if (!status.isOK()) {
						MultiStatus status2 = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, file.toString()), null);
						status2.add(status);
						throw new CoreException(status2);
					}
					return;
				} catch (FileNotFoundException e) {
					// shouldn't occur
				} finally {
					tempFile.delete();
				}
				/*if (!safeDelete(file, 8)) {
					tempFile.delete();
					throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, file.toString()), null));
				}*/
			}
		}
		if (!safeRename(tempFile, file, 10))
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorRename, tempFile.toString()), null));
	}

	/**
	 * Copy a file from a to b. Closes the input stream after use.
	 *
	 * @param in an InputStream
	 * @param to the file to copy to
	 * @return a status
	 */
	private IStatus copyFile(InputStream in, String to) {
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
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorCopyingFile, new String[] {to, e.getLocalizedMessage()}), e);
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
	 * Safe delete. Tries to delete multiple times before giving up.
	 * 
	 * @param f
	 * @return <code>true</code> if it succeeds, <code>false</code> otherwise
	 */
	private static boolean safeDelete(File f, int retrys) {
		int count = 0;
		while (count < retrys) {
			if (!f.exists())
				return true;
			
			f.delete();
			
			if (!f.exists())
				return true;
			
			count++;
			// delay if we are going to try again
			if (count < retrys) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					// ignore
				}
			}
		}
		return false;
	}

	/**
	 * Safe rename. Will try multiple times before giving up.
	 * 
	 * @param from
	 * @param to
	 * @param retrys number of times to retry
	 * @return <code>true</code> if it succeeds, <code>false</code> otherwise
	 */
	private static boolean safeRename(File from, File to, int retrys) {
		// make sure parent dir exists
		File dir = to.getParentFile();
		if (dir != null && !dir.exists())
			dir.mkdirs();
		
		int count = 0;
		while (count < retrys) {
			if (from.renameTo(to))
				return true;
			
			count++;
			// delay if we are going to try again
			if (count < retrys) {
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					// ignore
				}
			}
		}
		return false;
	}

	private static void addArrayToList(List<IStatus> list, IStatus[] a) {
		if (list == null || a == null || a.length == 0)
			return;
		
		int size = a.length;
		for (int i = 0; i < size; i++)
			list.add(a[i]);
	}
}