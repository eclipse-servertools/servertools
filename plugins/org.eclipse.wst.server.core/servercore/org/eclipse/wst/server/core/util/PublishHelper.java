/*******************************************************************************
 * Copyright (c) 2008,2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.internal.Messages;
import org.eclipse.wst.server.core.internal.ProgressUtil;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleFolder;
import org.eclipse.wst.server.core.model.IModuleResource;
/**
 * Utility class with an assortment of useful publishing file methods.
 *
 * @since 3.0
 */
public final class PublishHelper {
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
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorSaving, path.toOSString(), e.getLocalizedMessage()), null));
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
						Trace.trace(Trace.SEVERE, "Error moving file: " + file.toString(), status.getException());
						MultiStatus status2 = new MultiStatus(ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorSaving, file.toString()), null);
						status2.add(status);
						throw new CoreException(status2);
					}
					return;
				} catch (FileNotFoundException e) {
					// shouldn't occur
				} finally {
					tempFile.delete();
				}
			}
		}
		if (!safeRename(tempFile, file, 10)){
			Trace.trace(Trace.SEVERE, "Error moving temp file: " + tempFile.toString());
			throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorSaving, tempFile.toString()), null));
		}
			
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
	
	private void copyFile(IModuleFile mf, IPath path) throws CoreException {
		Trace.trace(Trace.FINEST, "Copying: " + mf.getName() + " to " + path.toString());
		
		IFile file = (IFile) mf.getAdapter(IFile.class);
		if (file != null)
			copyFile(file.getContents(), path, file.getLocalTimeStamp(), mf);
		else {
			File file2 = (File) mf.getAdapter(File.class);
			InputStream in = null;
			try {
				in = new FileInputStream(file2);
			} catch (IOException e) {
				Trace.trace(Trace.SEVERE, "Error copying file: " + file2.getAbsolutePath() , e);
				throw new CoreException(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorSaving, file2.getAbsolutePath()), e));
			}
			copyFile(in, path, file2.lastModified(), mf);
		}
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
		
		List status = new ArrayList(2);
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
		Trace.trace(Trace.FINEST, "Copying: " + name + " to " + path.toString());
		List status = new ArrayList(2);
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
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0, NLS.bind(Messages.errorSaving, new String[] {to, e.getLocalizedMessage()}), e);
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

	private static void addArrayToList(List list, IStatus[] a) {
		if (list == null || a == null || a.length == 0)
			return;
		
		int size = a.length;
		for (int i = 0; i < size; i++)
			list.add(a[i]);
	}
}
