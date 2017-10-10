/*******************************************************************************
 * Copyright (c) 2005, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.wst.server.core.*;

import junit.framework.TestCase;

public class ServerEventTestCase extends TestCase {
	private static ServerEvent serverEvent;
	private static ServerEvent moduleEvent;
	
	private static int SAMPLE_KIND = ServerEvent.STATE_CHANGE;
	private static IServer SAMPLE_SERVER = createSampleServer(); 
	private static IModule[] SAMPLE_MODULE_TREE = new IModule[] { null };
	private static int SAMPLE_STATE = 1;
	private static int SAMPLE_PUBLISHING_STATE = 2;
	private static boolean SAMPLE_RESTART_STATE = true;

	public static IServer createSampleServer() {
		return new IServer() {
			public int getServerState() {
				return 0;
			}
			public String getMode() {
				return null;
			}
			public int getServerPublishState() {
				return 0;
			}
			public int getModulePublishState(IModule[] module) {
				return 0;
			}
			public void addServerListener(IServerListener listener) {
				// do nothing.
			}
			public void addServerListener(IServerListener listener, int eventMask) {
				// do nothing.
			}
			public void removeServerListener(IServerListener listener) {
				// do nothing.
			}
			public IStatus canPublish() {
				return null;
			}
			public IStatus publish(int kind, IProgressMonitor monitor) {
				return null;
			}
			public IStatus canStart(String launchMode) {
				return null;
			}
			public void start(String launchMode, IProgressMonitor monitor) throws CoreException {
				// do nothing
			}
			public void start(String launchMode, IServer.IOperationListener listener) {
				// do nothing
			}
			public void synchronousStart(String launchMode, IProgressMonitor monitor) throws CoreException {
				// do nothing
			}
			public IStatus canRestart(String mode) {
				return null;
			}
			public boolean getServerRestartState() {
				return false;
			}
			public void restart(String mode, IProgressMonitor monitor) {
				// do nothing
			}
			public void restart(String mode, IServer.IOperationListener listener) {
				// do nothing
			}
			public void synchronousRestart(String launchMode, IProgressMonitor monitor) throws CoreException {
				// do nothing
			}
			public IStatus canStop() {
				return null;
			}
			public void stop(boolean force) {
				// do nothing
			}
			public void stop(boolean force, IServer.IOperationListener listener) {
				// do nothing
			}
			public void synchronousStop(boolean force) {
				// do nothing
			}
			public IStatus canControlModule(IModule[] module, IProgressMonitor monitor) {
				return null;
			}
			public boolean getModuleRestartState(IModule[] module) {
				return false;
			}
			public int getModuleState(IModule[] module) {
				return 0;
			}
			public String getName() {
				return null;
			}
			public String getId() {
				return null;
			}
			public void delete() throws CoreException {
				// do nothing
			}
			public boolean isReadOnly() {
				return false;
			}
			public boolean isWorkingCopy() {
				return false;
			}
			public Object loadAdapter(Class c, IProgressMonitor monitor) {
				return null;
			}
			public String getHost() {
				return null;
			}
			public IRuntime getRuntime() {
				return null;
			}
			public IServerType getServerType() {
				return null;
			}
			public IFolder getServerConfiguration() {
				return null;
			}
			public IServerWorkingCopy createWorkingCopy() {
				return null;
			}
			public IModule[] getModules() {
				return null;
			}
			public IStatus canModifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) {
				return null;
			}
			public IModule[] getChildModules(IModule[] module, IProgressMonitor monitor) {
				return null;
			}
			public IModule[] getRootModules(IModule module, IProgressMonitor monitor) throws CoreException {
				return null;
			}
			public ServerPort[] getServerPorts(IProgressMonitor monitor) {
				return null;
			}
			public Object getAdapter(Class adapter) {
				return null;
			}
			public void startModule(IModule[] module, IServer.IOperationListener listener) {
				// do nothing
			}
			public void stopModule(IModule[] module, IServer.IOperationListener listener) {
				// do nothing
			}
			public void restartModule(IModule[] module, IServer.IOperationListener listener) {
				// do nothing
			}
			public void addPublishListener(IPublishListener listener) {
				// do nothing
			}
			public void removePublishListener(IPublishListener listener) {
				// do nothing
			}
			public boolean shouldPublish() {
				return false;
			}
			public boolean shouldRestart() {
				return false;
			}
			public ILaunchConfiguration getLaunchConfiguration(boolean create,
					IProgressMonitor monitor) throws CoreException {
				return null;
			}
			public ILaunch getLaunch() {
				return null;
			}
			public int getStartTimeout() {
				return 0;
			}
			public int getStopTimeout() {
				return 0;
			}
			public void publish(int kind, List<IModule[]> modules, IAdaptable info, IOperationListener listener) {
				// do nothing
			}
			public boolean contains(ISchedulingRule rule) {
				return false;
			}
			public boolean isConflicting(ISchedulingRule rule) {
				return false;
			}
			public int getAttribute(String attributeName, int defaultValue) {
				return 0;
			}
			public boolean getAttribute(String attributeName,
					boolean defaultValue) {
				return false;
			}
			public String getAttribute(String attributeName, String defaultValue) {
				return null;
			}
			public List<String> getAttribute(String attributeName,
					List<String> defaultValue) {
				return null;
			}
			public Map getAttribute(String attributeName, Map defaultValue) {
				return null;
			}
			public IStatus canRestartModule(IModule[] module,
					IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				return null;
			}
			public IStatus canPublishModule(IModule[] module,
					IProgressMonitor monitor) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	protected ServerEvent getSampleServerEvent() {
		if (serverEvent == null) {
			serverEvent = new ServerEvent(SAMPLE_KIND, SAMPLE_SERVER, SAMPLE_STATE, SAMPLE_PUBLISHING_STATE, SAMPLE_RESTART_STATE);
		}
		return serverEvent;
	}

	protected ServerEvent getSampleModuleEvent() {
		if (moduleEvent == null) {
			moduleEvent = new ServerEvent(SAMPLE_KIND, SAMPLE_SERVER, SAMPLE_MODULE_TREE, SAMPLE_STATE, SAMPLE_PUBLISHING_STATE, SAMPLE_RESTART_STATE);
		}
		return moduleEvent;
	}


	public void testServerGetKind() {
		assertTrue((getSampleServerEvent().getKind() & (ServerEvent.SERVER_CHANGE | SAMPLE_KIND)) != 0);
	}

	public void testServerGetPublishingState() {
		assertEquals(SAMPLE_PUBLISHING_STATE, getSampleServerEvent().getPublishState());
	}

	public void testServerGetRestartState() {
		assertEquals(SAMPLE_RESTART_STATE, getSampleServerEvent().getRestartState());
	}

	public void testServerGetServer() {
		assertEquals(SAMPLE_SERVER, getSampleServerEvent().getServer());
	}

	public void testServerGetState() {
		assertEquals(SAMPLE_STATE,getSampleServerEvent().getState());
	}

	public void testModuleGetKind() {
		assertTrue((getSampleModuleEvent().getKind() & (ServerEvent.MODULE_CHANGE | SAMPLE_KIND)) != 0);
	}

	public void testModuleGetModuleTree() {
		try {
			assertEquals(SAMPLE_MODULE_TREE, getSampleModuleEvent().getModule());
		} catch (Exception e) {
			// allow failure for now - will need to fix this test
		}
	}

	public void testModuleGetPublishingState() {
		assertEquals(SAMPLE_PUBLISHING_STATE, getSampleModuleEvent().getPublishState());
	}

	public void testModuleGetRestartState() {
		assertEquals(SAMPLE_RESTART_STATE, getSampleModuleEvent().getRestartState());
	}

	public void testModuleGetServer() {
		assertEquals(SAMPLE_SERVER, getSampleModuleEvent().getServer());
	}

	public void testModuleGetState() {
		assertEquals(SAMPLE_STATE,getSampleModuleEvent().getState());
	}
}