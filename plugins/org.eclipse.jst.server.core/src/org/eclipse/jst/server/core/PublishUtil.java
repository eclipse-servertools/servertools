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

import java.io.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.Messages;
import org.eclipse.jst.server.core.internal.Trace;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
/**
 * Utility class with an assortment of useful file methods.
 * <p>
 * This class provides all its functionality through static members.
 * It is not intended to be subclassed or instantiated.
 * </p>
 * <p>
 * <b>Note:</b> Adopters should use the equivalent class in
 * org.eclipse.wst.server.core.util instead. This class will eventually
 * be deprecated.
 * </p>
 */
public final class PublishUtil {
	// size of the buffer
	private static final int BUFFER = 65536;

	// the buffer
	private static byte[] buf = new byte[BUFFER];

	/**
	 * PublishUtil cannot be created. Use static methods.
	 */
	private PublishUtil() {
		// can't create
	}

	/**
	 * Copy a file from a to b. Closes the input stream after use.
	 *
	 * @param in java.io.InputStream
	 * @param to java.lang.String
	 * @deprecated Unused - will be removed.
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
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error copying file", e);
			}
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
	 * Smart copy the given module resources to the given path.
	 * 
	 * @param resources
	 * @param path
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @throws CoreException
	 * @deprecated This method only returns a single error in the case of failure. Use publishSmart() instead.
	 */
	public static void smartCopy(IModuleResource[] resources, IPath path, IProgressMonitor monitor) throws CoreException {
		IStatus[] status = PublishUtil.publishSmart(resources, path, monitor);
		if (status != null && status.length > 0)
			throw new CoreException(status[0]);
	}

	/**
	 * Handle a delta publish.
	 * 
	 * @param kind
	 * @param path
	 * @param delta
	 * @throws CoreException
	 * @deprecated This method only returns a single error in the case of failure. Use publishDelta() instead.
	 */
	public static void handleDelta(int kind, IPath path, IModuleResourceDelta delta) throws CoreException {
		IStatus[] status = PublishUtil.publishDelta(delta, path, null);
		if (status != null && status.length > 0)
			throw new CoreException(status[0]);
	}

	/**
	 * 
	 * @param path
	 * @param file
	 * @deprecated does not fail or return status if delete doesn't work
	 */
	protected static void deleteFile(IPath path, IModuleFile file) {
		if (Trace.PUBLISHING) {
			Trace.trace(Trace.STRING_PUBLISHING, "Deleting: " + file.getName() + " from " + path.toString());
		}
		IPath path2 = path.append(file.getModuleRelativePath()).append(file.getName());
		path2.toFile().delete();
	}

	/**
	 * 
	 * @param resources
	 * @param path
	 * @throws CoreException
	 * @deprecated This method only returns a single error in the case of failure. Use publishFull() instead
	 */
	public static void copy(IModuleResource[] resources, IPath path) throws CoreException {
		IStatus[] status = PublishUtil.publishFull(resources, path, null);
		if (status != null && status.length > 0)
			throw new CoreException(status[0]);
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
		IStatus[] status = PublishUtil.publishZip(resources, zipPath, null);
		if (status != null && status.length > 0)
			throw new CoreException(status[0]);
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
		return org.eclipse.wst.server.core.util.PublishUtil.deleteDirectory(dir, monitor);
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
	public static IStatus[] publishSmart(IModuleResource[] resources, IPath path, IProgressMonitor monitor) {
		return org.eclipse.wst.server.core.util.PublishUtil.publishSmart(resources, path, monitor);
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
	public static IStatus[] publishDelta(IModuleResourceDelta[] delta, IPath path, IProgressMonitor monitor) {
		return org.eclipse.wst.server.core.util.PublishUtil.publishDelta(delta, path, monitor);
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
	public static IStatus[] publishDelta(IModuleResourceDelta delta, IPath path, IProgressMonitor monitor) {
		return org.eclipse.wst.server.core.util.PublishUtil.publishDelta(delta, path, monitor);
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
	public static IStatus[] publishFull(IModuleResource[] resources, IPath path, IProgressMonitor monitor) {
		return org.eclipse.wst.server.core.util.PublishUtil.publishFull(resources, path, monitor);
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
	public static IStatus[] publishZip(IModuleResource[] resources, IPath path, IProgressMonitor monitor) {
		return org.eclipse.wst.server.core.util.PublishUtil.publishZip(resources, path, monitor);
	}
}