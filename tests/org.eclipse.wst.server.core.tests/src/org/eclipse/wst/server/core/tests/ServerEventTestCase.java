/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.core.tests;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.util.ServerEvent;

import junit.framework.Test;
import junit.framework.TestCase;

public class ServerEventTestCase extends TestCase {
	private static ServerEvent event;
	
	private static int SAMPLE_KIND = ServerEvent.STATE_CHANGE;
	private static IServer SAMPLE_SERVER = createSampleServer(); 
	private static IModule[] SAMPLE_MODULE_TREE = new IModule[0];
	private static int SAMPLE_STATE = 1;
	private static int SAMPLE_PUBLISHING_STATE = 2;
	private static boolean SAMPLE_RESTART_STATE = true;
	
	public static Test suite() {
		return new OrderedTestSuite(ServerEventTestCase.class, "ServerEventTestCase");
	}

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
			public boolean shouldPublish() {
				return false;
			}
			public IStatus publish(IProgressMonitor monitor) {
				return null;
			}
			public IStatus publish(int kind, IProgressMonitor monitor) {
				return null;
			}
			public IStatus canStart(String launchMode) {
				return null;
			}
			public ILaunchConfiguration getLaunchConfiguration(boolean create, IProgressMonitor monitor) throws CoreException {
				return null;
			}
			public ILaunch start(String launchMode, IProgressMonitor monitor) throws CoreException {
				return null;
			}
			public ILaunch synchronousStart(String launchMode, IProgressMonitor monitor) throws CoreException {
				return null;
			}
			public IStatus canRestart(String mode) {
				return null;
			}
			public boolean getServerRestartState() {
				return false;
			}
			public void restart(String mode) {
				// do nothing.
			}
			public void synchronousRestart(String launchMode, IProgressMonitor monitor) throws CoreException {
				// do nothing.
			}
			public IStatus canStop() {
			return null;
			}
			public void stop(boolean force) {
				// do nothing.
			}
			public void synchronousStop(boolean force) {
				// do nothing.
			}
			public IStatus canRestartModule(IModule[] module) {
				return null;
			}
			public boolean getModuleRestartState(IModule[] module) {
				return false;
			}
			public void restartModule(IModule[] module, IProgressMonitor monitor) throws CoreException {
				// do nothing.
			}
			public void synchronousRestartModule(IModule[] module, IProgressMonitor monitor) throws CoreException {
				// do nothing.
			}
			public int getModuleState(IModule[] module) {
				return 0;
			}
			public IModule[] getServerModules(IProgressMonitor monitor) {
				return null;
			}
			public String getName() {
				return null;
			}
			public String getId() {
				return null;
			}
			public void delete() throws CoreException {
				// do nothing.
			}
			public boolean isReadOnly() {
				return false;
			}
			public boolean isPrivate() {
				return false;
			}
			public boolean isWorkingCopy() {
				return false;
			}
			public boolean isDelegateLoaded() {
				return false;
			}
			public IStatus validateEdit(Object context) {
				return null;
			}
			public int getTimestamp() {
				return 0;
			}
			public String getHost() {
				return null;
			}
			public IFile getFile() {
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
			public ServerPort[] getServerPorts() {
				return null;
			}
			public Object getAdapter(Class adapter) {
				return null;
			}
		};
	}
	
	public static ServerEvent createSampleServerEvent() {
		return new ServerEvent(SAMPLE_KIND, SAMPLE_SERVER, SAMPLE_STATE, SAMPLE_PUBLISHING_STATE, SAMPLE_RESTART_STATE);
	}
	
	public void test010CreateServerEvent() {
		event = ServerEventTestCase.createSampleServerEvent();
	}
	
	public void test011ServerGetKind() {
		assertTrue((event.getKind() & (ServerEvent.SERVER_CHANGE | SAMPLE_KIND)) != 0);
	}
	
	public void test013ServerGetPublishingState() {
		assertEquals(SAMPLE_PUBLISHING_STATE, event.getPublishingState());
	}
	
	public void test014ServerGetRestartState() {
		assertEquals(SAMPLE_RESTART_STATE, event.getRestartState());
	}
	
	public void test015ServerGetServer() {
		assertEquals(SAMPLE_SERVER, event.getServer());
	}
	
	public void test016ServerGetState() {
		assertEquals(SAMPLE_STATE,event.getState());
	}
	
	public void test110CreateModuleEvent() {
		event = new ServerEvent(SAMPLE_KIND, SAMPLE_SERVER, SAMPLE_MODULE_TREE, SAMPLE_STATE, SAMPLE_PUBLISHING_STATE, SAMPLE_RESTART_STATE);
	}
	
	public void test111ModuleGetKind() {
		assertTrue((event.getKind() & (ServerEvent.MODULE_CHANGE | SAMPLE_KIND)) != 0);
	}
	
	public void test112ModuleGetModuleTree() {
		assertEquals(SAMPLE_MODULE_TREE, event.getModuleTree());
	}

	public void test113ModuleGetPublishingState() {
		assertEquals(SAMPLE_PUBLISHING_STATE, event.getPublishingState());
	}
	
	public void test114ModuleGetRestartState() {
		assertEquals(SAMPLE_RESTART_STATE, event.getRestartState());
	}
	
	public void test115ModuleGetServer() {
		assertEquals(SAMPLE_SERVER, event.getServer());
	}
	
	public void test116ModuleGetState() {
		assertEquals(SAMPLE_STATE,event.getState());
	}
}
