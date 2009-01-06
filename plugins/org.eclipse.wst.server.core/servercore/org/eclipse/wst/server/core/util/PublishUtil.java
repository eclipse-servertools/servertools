/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
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

import org.eclipse.core.runtime.*;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
/**
 * Utility class with an assortment of useful file methods.
 * <p>
 * This class provides all its functionality through static members.
 * It is not intended to be subclassed or instantiated.
 * </p>
 * @since 2.0
 */
public final class PublishUtil {
	private static PublishHelper publishHelper = new PublishHelper(null);

	/**
	 * PublishUtil cannot be created. Use static methods.
	 */
	private PublishUtil() {
		// can't create
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
		return PublishHelper.deleteDirectory(dir, monitor);
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
		return publishHelper.publishSmart(resources, path, monitor);
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
	 * @since 1.1
	 */
	public static IStatus[] publishSmart(IModuleResource[] resources, IPath path, IPath[] ignore, IProgressMonitor monitor) {
		return publishHelper.publishSmart(resources, path, ignore, monitor);
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
		return publishHelper.publishDelta(delta, path, monitor);
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
		return publishHelper.publishDelta(delta, path, monitor);
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
		return publishHelper.publishFull(resources, path, monitor);
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
		return publishHelper.publishZip(resources, path, monitor);
	}
}