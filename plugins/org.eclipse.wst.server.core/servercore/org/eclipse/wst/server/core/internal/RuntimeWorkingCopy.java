/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * 
 */
public class RuntimeWorkingCopy extends Runtime implements IRuntimeWorkingCopy {
	protected String PROP_ID_SET = "id-set";
	protected Runtime runtime;
	protected WorkingCopyHelper wch;
	
	protected RuntimeDelegate workingCopyDelegate;
	
	// from existing runtime
	public RuntimeWorkingCopy(Runtime runtime) {
		super(runtime.getFile());
		this.runtime = runtime;
		
		runtimeType = runtime.getRuntimeType();
		
		map = new HashMap(runtime.map);
		wch = new WorkingCopyHelper(this);
	}
	
	// new runtime
	public RuntimeWorkingCopy(IFile file, String id, IRuntimeType runtimeType) {
		super(file, id, runtimeType);
		wch = new WorkingCopyHelper(this);
		wch.setDirty(true);
		
		if (id == null || id.length() == 0) {
			id = ServerPlugin.generateId();
			map.put(PROP_ID, id);
		} else
			setAttribute(PROP_ID_SET, true);
		
		// throw CoreException if the id already exists
	}

	public boolean isWorkingCopy() {
		return true;
	}

	public IRuntimeWorkingCopy createWorkingCopy() {
		return this;
	}

	public void setAttribute(String attributeName, int value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, boolean value) {
		wch.setAttribute(attributeName, value);
	}
	
	public void setAttribute(String attributeName, String value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, List value) {
		wch.setAttribute(attributeName, value);
	}

	public void setAttribute(String attributeName, Map value) {
		wch.setAttribute(attributeName, value);
	}

	public void setName(String name) {
		wch.setName(name);
		boolean set = getAttribute(PROP_ID_SET, false);
		if (runtime == null && !set)
			setAttribute("id", name);
	}

	public void setTestEnvironment(boolean b) {
		setAttribute(PROP_TEST_ENVIRONMENT, b);
	}

	public boolean isDirty() {
		return wch.isDirty();
	}
	
	public IRuntime getOriginal() {
		return runtime;
	}

	public void setLocked(boolean b) {
		wch.setLocked(b);
	}

	public void setPrivate(boolean b) {
		wch.setPrivate(b);
	}
	
	public void setLocation(IPath path) {
		if (path == null)
			setAttribute(PROP_LOCATION, (String)null);
		else
			setAttribute(PROP_LOCATION, path.toString());
	}

	public IRuntime save(boolean force, IProgressMonitor monitor) throws CoreException {
		monitor = ProgressUtil.getMonitorFor(monitor);
		monitor.subTask(ServerPlugin.getResource("%savingTask", getName()));
		
		if (!force)
			wch.validateTimestamp(getOriginal());
		
		IRuntime origRuntime = runtime;
		if (runtime == null)
			runtime = new Runtime(file);
		
		String oldId = getId();
		String name = getName();
		boolean set = getAttribute(PROP_ID_SET, false);
		if (!oldId.equals(name) && !set) {
			setAttribute("id", name);
		} else
			oldId = null;
		
		runtime.setInternal(this);
		runtime.saveToMetadata(monitor);
		wch.setDirty(false);
		
		if (oldId != null)
			updateRuntimeReferences(oldId, name, origRuntime);
		
		return runtime;
	}

	protected void updateRuntimeReferences(final String oldId, final String newId, final IRuntime origRuntime) {
		class UpdateRuntimeReferencesJob extends Job {
			public UpdateRuntimeReferencesJob() {
				super(ServerPlugin.getResource("%savingTask", newId));
			}

			public IStatus run(IProgressMonitor monitor) {
				// fix .runtime files
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				if (projects != null) {
					int size = projects.length;
					for (int i = 0; i < size; i++) {
						ProjectProperties props = (ProjectProperties) ServerCore.getProjectProperties(projects[i]);
						if (oldId.equals(props.getRuntimeTargetId())) {
							try {
								props.setRuntimeTargetId(newId, monitor);
							} catch (Exception e) {
								// ignore
							}
						}
					}
				}
				
				// save servers
				if (runtime != null) {
					ResourceManager rm = ResourceManager.getInstance();
					IServer[] servers = rm.getServers();
					if (servers != null) {
						int size = servers.length;
						for (int i = 0; i < size; i++) {
							if (oldId.equals(((Server)servers[i]).getRuntimeId())) {
								try {
									ServerWorkingCopy wc = (ServerWorkingCopy) servers[i].createWorkingCopy();
									wc.setRuntimeId(newId);
									wc.save(false, monitor);
								} catch (Exception e) {
									// ignore
								}
							}
						}
					}
				}
				
				return new Status(IStatus.OK, ServerPlugin.PLUGIN_ID, 0, "", null);
			}
		}
		UpdateRuntimeReferencesJob job = new UpdateRuntimeReferencesJob();
		job.schedule();
	}
	
	/**
	 * Rebuild any projects that are targetted to this runtime.
	 * 
	 * @param id
	 */
	protected static void rebuildRuntime(final IRuntime runtime, final boolean add) {
		if (runtime == null)
			return;

		class RebuildRuntimeReferencesJob extends Job {
			public RebuildRuntimeReferencesJob() {
				super(ServerPlugin.getResource("%taskPerforming"));
			}

			public IStatus run(IProgressMonitor monitor) {
				String id = runtime.getId();
				
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				if (projects != null) {
					int size = projects.length;
					for (int i = 0; i < size; i++) {
						ProjectProperties props = (ProjectProperties) ServerCore.getProjectProperties(projects[i]);
						if (id.equals(props.getRuntimeTargetId())) {
							try {
								if (add)
									props.setRuntimeTarget(null, runtime, false, monitor);
								else
									props.setRuntimeTarget(runtime, null, false, monitor);
								projects[i].build(IncrementalProjectBuilder.FULL_BUILD, monitor);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
				
				return new Status(IStatus.OK, ServerPlugin.PLUGIN_ID, 0, "", null);
			}
		}
		RebuildRuntimeReferencesJob job = new RebuildRuntimeReferencesJob();
		job.schedule();
	}

	public RuntimeDelegate getWorkingCopyDelegate(IProgressMonitor monitor) {
		if (workingCopyDelegate != null)
			return workingCopyDelegate;
		
		synchronized (this) {
			if (workingCopyDelegate == null) {
				try {
					long time = System.currentTimeMillis();
					RuntimeType runtimeType2 = (RuntimeType) runtimeType;
					workingCopyDelegate = (RuntimeDelegate) runtimeType2.getElement().createExecutableExtension("class");
					workingCopyDelegate.initialize(this);
					Trace.trace(Trace.PERFORMANCE, "RuntimeWorkingCopy.getWorkingCopyDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getRuntimeType().getId());
				} catch (Exception e) {
					Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), e);
				}
			}
		}
		return workingCopyDelegate;
	}

	public void dispose() {
		super.dispose();
		if (workingCopyDelegate != null)
			workingCopyDelegate.dispose();
	}

	/**
	 * Add a property change listener to this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		wch.addPropertyChangeListener(listener);
	}
	
	/**
	 * Remove a property change listener from this server.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		wch.removePropertyChangeListener(listener);
	}
	
	/**
	 * Fire a property change event.
	 */
	public void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		wch.firePropertyChangeEvent(propertyName, oldValue, newValue);
	}
	
	public void setDefaults(IProgressMonitor monitor) {
		try {
			getWorkingCopyDelegate(monitor).setDefaults();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate setDefaults() " + toString(), e);
		}
	}
}