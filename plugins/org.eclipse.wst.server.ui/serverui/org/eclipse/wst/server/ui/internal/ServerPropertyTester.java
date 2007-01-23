/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
/**
 * 
 */
public class ServerPropertyTester extends PropertyTester {
	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (expectedValue instanceof String)
			return checkProperty(receiver, property, (String) expectedValue);
		if (expectedValue != null)
			return checkProperty(receiver, property, expectedValue.toString());
		
		return checkProperty(receiver, property, null);
	}

	protected static boolean checkProperty(Object target, String property, String value) {
		if ("isRunnable".equals(property)) {
			// check if project has a module associated with it
			if (target instanceof IProject)
				return ServerUtil.getModule((IProject) target) != null;
			
			// check for runnable object
			boolean b = ServerPlugin.hasModuleArtifact(target);
			if (b)
				return true;
			
			/*if (!(receiver instanceof IEditorPart))
				return false;
			
			//	 check if the editor input itself can be run. Otherwise, check if
			// the editor has a file input that can be run
			IEditorPart editor = (IEditorPart) receiver;
			IEditorInput input = editor.getEditorInput();
			
			b = ServerPlugin.hasModuleArtifact(input);
			if (b)
				return true;*/
	
			if (target instanceof IFileEditorInput) {
				IFileEditorInput fei = (IFileEditorInput) target;
				IFile file = fei.getFile();
				b = ServerPlugin.hasModuleArtifact(file);
				if (b)
					return true;
			}
			return false;
		} else if ("serverType".equals(property)) {
			IServer server = null;
			if (target instanceof IServer) {
				server = (IServer) target;
			} else if (target instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) target;
				server = ms.server;
			}
			if (server == null || server.getServerType() == null)
				return false;
			
			String[] typeIds = ServerPlugin.tokenize(value, ",");
			return matches(server.getServerType().getId(), typeIds);
		} else if ("moduleType".equals(property)) {
			IModule[] module = null;
			if (target instanceof IModule[]) {
				module = (IModule[]) target;
			} else if (target instanceof IModule) {
				module = new IModule[] {(IModule) target};
			} else if (target instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) target;
				module = ms.module;
			}
			if (module == null)
				return false;
			if (module.length == 0)
				return false;
			
			String[] values = ServerPlugin.tokenize(value, ",");
			IModule m = module[module.length - 1];
			return matches(m.getModuleType().getId(), values);
		} else if ("moduleVersion".equals(property)) {
			IModule[] module = null;
			if (target instanceof IModule[]) {
				module = (IModule[]) target;
			} else if (target instanceof IModule) {
				module = new IModule[] {(IModule) target};
			} else if (target instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) target;
				module = ms.module;
			}
			if (module == null)
				return false;
			if (module.length == 0)
				return false;
			
			String[] values = ServerPlugin.tokenize(value, ",");
			IModule m = module[module.length - 1];
			return matches(m.getModuleType().getVersion(), values);
		}
		return false;
	}
	
	/**
	 * Returns true if the given type (given by the id) can use this action.
	 *
	 * @return boolean
	 */
	protected static boolean matches(String id, String[] typeIds) {
		if (id == null || id.length() == 0)
			return false;

		if (typeIds == null)
			return false;
		
		int size = typeIds.length;
		for (int i = 0; i < size; i++) {
			if (typeIds[i].endsWith("*")) {
				if (id.length() >= typeIds[i].length() && id.startsWith(typeIds[i].substring(0, typeIds[i].length() - 1)))
					return true;
			} else if (id.equals(typeIds[i]))
				return true;
		}
		return false;
	}
}