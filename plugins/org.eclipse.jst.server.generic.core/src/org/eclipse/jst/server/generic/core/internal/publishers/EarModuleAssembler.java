/***************************************************************************************************
 * Copyright (c) 2009, 2010 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal.publishers;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.IJ2EEModule;
import org.eclipse.jst.server.generic.core.internal.CorePlugin;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.core.internal.Trace;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.util.ProjectModule;

/**
 * Utility for EAR module assembly.
 */
public class EarModuleAssembler extends AbstractModuleAssembler {

	protected EarModuleAssembler(IModule module, GenericServer server, IPath assembleRoot) {
		super(module, server, assembleRoot);
	}

	public IPath assemble(IProgressMonitor monitor) throws CoreException {
		// copy ear root to the temporary assembly directory
		IPath parent = fAssembleRoot;
		final IModule[] rootMod = { fModule };
		boolean shouldCopy = (IServer.PUBLISH_STATE_NONE != fServer.getServer().getModulePublishState(rootMod));
		if (shouldCopy)
			copyModule(fModule, monitor);
		IEnterpriseApplication earModule = (IEnterpriseApplication) fModule.loadAdapter(IEnterpriseApplication.class, monitor);
		IModule[] childModules = earModule.getModules();
		for (int i = 0; i < childModules.length; i++) {
			IModule module = childModules[i];
			String uri = earModule.getURI(module);
			if (uri == null) { // The bad memories of WTP 1.0
				IStatus status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, "unable to assemble module null uri", null); //$NON-NLS-1$
				throw new CoreException(status);
			}
			IJ2EEModule jeeModule = (IJ2EEModule) module.loadAdapter(IJ2EEModule.class, monitor);
			if (jeeModule != null && jeeModule.isBinary()) {// Binary module
															// just copy
				ProjectModule pm = (ProjectModule) module.loadAdapter(ProjectModule.class, null);
				IModuleResource[] resources = pm.members();
				publishHelper.publishToPath(resources, parent.append(uri), monitor);
				continue;// done! no need to go further
			}
			if (shouldRepack(module)) {
				packModule(module, uri, parent);
			}
		}
		return parent;
	}

	/**
	 * Checks if the publish state of the child module has changed.
	 * 
	 * @param module
	 * @return module changed
	 */
	private boolean shouldRepack(IModule module) {
		final IModule[] rootMod = { fModule };
		final IModule[] modules = { fModule, module };
		boolean repack = (IServer.PUBLISH_STATE_NONE != fServer.getServer().getModulePublishState(modules));
		repack |= (IServer.PUBLISH_STATE_NONE != fServer.getServer().getModulePublishState(rootMod));
		Trace.trace(Trace.FINEST, "EarModuleAssembler should repack returns " + Boolean.toString(repack) + " for module " + module); //$NON-NLS-1$//$NON-NLS-2$
		return repack;
	}

	protected void packModule(IModule module, String deploymentUnitName, IPath destination) throws CoreException {
		if (module.getModuleType().getId().equals("jst.web")) //$NON-NLS-1$
		{
			AbstractModuleAssembler assembler = AbstractModuleAssembler.Factory.getModuleAssembler(module, fServer);
			IPath webAppPath = assembler.assemble(new NullProgressMonitor());
			String realDestination = destination.append(deploymentUnitName).toString();
			ModulePackager packager = null;
			try {
				packager = new ModulePackager(realDestination, false);
				packager.pack(webAppPath.toFile(), webAppPath.toOSString());
			} catch (IOException e) {
				IStatus status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, "unable to assemble module", e); //$NON-NLS-1$
				throw new CoreException(status);
			} finally {
				if (packager != null) {
					try {
						packager.finished();
					} catch (IOException e) {
						// Unhandled
					}
				}

			}
		} else {
			super.packModule(module, deploymentUnitName, destination);
		}
	}
}
