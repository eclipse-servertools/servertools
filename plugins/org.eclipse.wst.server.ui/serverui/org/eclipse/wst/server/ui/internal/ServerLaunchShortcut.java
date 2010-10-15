/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.ILaunchShortcut2;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;

import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.actions.RunOnServerActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
/**
 * 
 */
public class ServerLaunchShortcut implements ILaunchShortcut2 {
	/* (non-Javadoc)
	 * @see ILaunchShortcut#launch(ISelection, String)
	 */
	public void launch(ISelection selection, final String mode) {
		RunOnServerActionDelegate ros = new RunOnServerActionDelegate();
		ros.setLaunchMode(mode);
		IAction action = new Action() {
			// dummy action
		};
		ros.selectionChanged(action, selection);
		ros.run(action);
	}

	/* (non-Javadoc)
	 * @see ILaunchShortcut#launch(IEditorPart, String)
	 */
	public void launch(IEditorPart editor, String mode) {
		if (editor == null)
			return;
		
		// check if the editor input itself can be run. Otherwise, check if
		// the editor has a file input that can be run
		IEditorInput input = editor.getEditorInput();
		
		if (ServerPlugin.hasModuleArtifact(input)) {
			launch(new StructuredSelection(input), mode);
		} else if (input instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) input;
			IFile file = fei.getFile();
			if (ServerPlugin.hasModuleArtifact(file))
				launch(new StructuredSelection(file), mode);
		}
	}

	/**
	 * Given the specified <code>ISelection</code> this method returns an array of 
	 * <code>ILaunchConfiguration</code>s that apply to the current selection, 
	 * i.e. all of the launch configurations that could be used to launch the given 
	 * selection.
	 * @param selection the current selection
	 * @return an array of <code>ILaunchConfiguration</code>s that could be 
	 * used to launch the given selection, or an empty array, never <code>null</code>
	 */
	public ILaunchConfiguration[] getLaunchConfigurations(ISelection selection) {
		return new ILaunchConfiguration[0];
	}

	/* (non-Javadoc)
	 * @see ILaunchShortcut2#getLaunchConfigurations(IEditorPart)
	 */
	public ILaunchConfiguration[] getLaunchConfigurations(IEditorPart editor) {
		if (editor == null)
			return new ILaunchConfiguration[0];
		
		// check if the editor input itself can be run. Otherwise, check if
		// the editor has a file input that can be run
		IEditorInput input = editor.getEditorInput();
		
		if (ServerPlugin.hasModuleArtifact(input)) {
			return getLaunchConfigurations(new StructuredSelection(input));
		} else if (input instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) input;
			IFile file = fei.getFile();
			if (ServerPlugin.hasModuleArtifact(file))
				return getLaunchConfigurations(new StructuredSelection(file));
		}
		return new ILaunchConfiguration[0];
	}

	/**
	 * Given the specified <code>ISelection</code> this method returns an
	 * <code>IResource</code> that directly maps to the current selection.
	 * This mapping is then leveraged by the context launching framework
	 * to try and launch the resource. 
	 * @param selection the current selection
	 * @return an <code>IResource</code> that would be used during context
	 * sensitive launching or <code>null</code> if one is not to be provided or does not exist.
	 */
	public IResource getLaunchableResource(ISelection selection) {
		return null;
	}

	/* (non-Javadoc)
	 * @see ILaunchShortcut2#getLaunchableResource(IEditorPart)
	 */
	public IResource getLaunchableResource(IEditorPart editor) {
		if (editor == null)
			return null;
		
		// check if the editor input itself can be run. Otherwise, check if
		// the editor has a file input that can be run
		IEditorInput input = editor.getEditorInput();
		
		if (ServerPlugin.hasModuleArtifact(input)) {
			return getLaunchableResource(new StructuredSelection(input));
		} else if (input instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) input;
			IFile file = fei.getFile();
			if (ServerPlugin.hasModuleArtifact(file))
				return getLaunchableResource(new StructuredSelection(file));
		}
		return null;
	}
}