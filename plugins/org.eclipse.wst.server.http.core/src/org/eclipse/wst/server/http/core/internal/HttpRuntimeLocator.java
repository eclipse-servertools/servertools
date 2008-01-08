/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.http.core.internal;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.RuntimeLocatorDelegate;
/**
 * 
 */
public class HttpRuntimeLocator extends RuntimeLocatorDelegate {
	protected static final String[] runtimeTypes = new String[] { HttpRuntime.ID };

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IRuntimeFactoryDelegate#getKnownRuntimes()
	 */
	public void searchForRuntimes(IPath path, IRuntimeSearchListener listener,
			IProgressMonitor monitor) {
		searchForRuntimes2(path, listener, monitor);
	}

	protected static void searchForRuntimes2(IPath path, IRuntimeSearchListener listener,
			IProgressMonitor monitor) {
		File[] files = null;
		if (path != null) {
			File f = path.toFile();
			if (f.exists())
				files = f.listFiles();
			else
				return;
		} else
			files = File.listRoots();

		if (files != null) {
			int size = files.length;
			int work = 100 / size;
			int workLeft = 100 - (work * size);
			for (File file : files) {
				if (monitor.isCanceled())
					return;
				if (file != null && file.isDirectory())
					searchDir(listener, file, 4, monitor);
				monitor.worked(work);
			}
			monitor.worked(workLeft);
		} else
			monitor.worked(100);
	}

	protected static void searchDir(IRuntimeSearchListener listener, File dir, int depth, IProgressMonitor monitor) {
		if ("htdocs".equals(dir.getName())) {
			IRuntimeWorkingCopy runtime = getRuntimeFromDir(dir.getParentFile(), monitor);
			if (runtime != null) {
				listener.runtimeFound(runtime);
				return;
			}
		}

		if (depth == 0)
			return;

		File[] files = dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		if (files != null) {
			for (File file : files) {
				if (monitor.isCanceled())
					return;
				searchDir(listener, file, depth - 1, monitor);
			}
		}
	}

	protected static IRuntimeWorkingCopy getRuntimeFromDir(File dir, IProgressMonitor monitor) {
		for (String rt : runtimeTypes) {
			try {
				IRuntimeType runtimeType = ServerCore.findRuntimeType(rt);
				String absolutePath = dir.getAbsolutePath();
				String id = absolutePath.replace(File.separatorChar, '_').replace(':', '-');
				IRuntimeWorkingCopy runtime = runtimeType.createRuntime(id, monitor);
				runtime.setName(dir.getName());
				runtime.setLocation(new Path(absolutePath).append("htdocs"));
				IStatus status = runtime.validate(monitor);
				if (status == null || status.getSeverity() != IStatus.ERROR)
					return runtime;
				
				Trace.trace(Trace.FINER, "False runtime found at " + dir.getAbsolutePath()
						+ ": " + status.getMessage());
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not find runtime", e);
			}
		}
		return null;
	}
}