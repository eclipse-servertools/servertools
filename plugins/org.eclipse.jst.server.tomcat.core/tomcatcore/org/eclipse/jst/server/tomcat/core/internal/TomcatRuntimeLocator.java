package org.eclipse.jst.server.tomcat.core.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.tomcat.core.ITomcatRuntimeWorkingCopy;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.model.IRuntimeLocatorDelegate;
import org.eclipse.wst.server.core.model.IRuntimeLocatorListener;
import org.eclipse.wst.server.core.model.IRuntimeWorkingCopyDelegate;
/**
 * 
 */
public class TomcatRuntimeLocator implements IRuntimeLocatorDelegate {
	protected static final String[] runtimeTypes = new String[] {
		"org.eclipse.jst.server.tomcat.32.runtime",
		"org.eclipse.jst.server.tomcat.40.runtime",
		"org.eclipse.jst.server.tomcat.41.runtime",
		"org.eclipse.jst.server.tomcat.50.runtime",
		"org.eclipse.jst.server.tomcat.55.runtime"};

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntimeFactoryDelegate#getKnownRuntimes()
	 */
	public void searchForRuntimes(IRuntimeLocatorListener listener, IProgressMonitor monitor) {
		File[] files = File.listRoots();
		if (files != null) {
			int size = files.length;
			int work = 100 / size;
			int workLeft = 100 - (work * size);
			for (int i = 0; i < size; i++) {
				if (monitor.isCanceled())
					return;
				if (files[i] != null && files[i].isDirectory())
					searchDir(listener, files[i], 4, monitor);
				monitor.worked(work);
			}
			monitor.worked(workLeft);
		} else
			monitor.worked(100);
	}

	protected void searchDir(IRuntimeLocatorListener listener, File dir, int depth, IProgressMonitor monitor) {
		if ("conf".equals(dir.getName())) {
			IRuntimeWorkingCopy runtime = getRuntimeFromDir(dir.getParentFile());
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
			int size = files.length;
			for (int i = 0; i < size; i++) {
				if (monitor.isCanceled())
					return;
				searchDir(listener, files[i], depth - 1, monitor);
			}
		}
	}

	protected IRuntimeWorkingCopy getRuntimeFromDir(File dir) {
		for (int i = 0; i < runtimeTypes.length; i++) {
			try {
				IRuntimeType runtimeType = ServerCore.getRuntimeType(runtimeTypes[i]);
				IRuntimeWorkingCopy runtime = runtimeType.createRuntime(dir.getAbsolutePath());
				runtime.setName(dir.getName());
				runtime.setLocation(new Path(dir.getAbsolutePath()));
				IRuntimeWorkingCopyDelegate delegate = runtime.getWorkingCopyDelegate();
				ITomcatRuntimeWorkingCopy wc = (ITomcatRuntimeWorkingCopy) delegate;
				IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
				wc.setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
				IStatus status = wc.validate();
				if (status == null || status.isOK())
					return runtime;
				
				Trace.trace(Trace.FINER, "False runtime found at " + dir.getAbsolutePath() + ": " + status.getMessage());
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Could not find runtime", e);
			}
		}
		return null;
	}
}