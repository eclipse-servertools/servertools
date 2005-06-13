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
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.wst.server.core.internal.ServerPlugin;
/**
 * 
 */
public class ServerPropertyTester extends PropertyTester {
	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.IPropertyTester#test(java.lang.Object, java.lang.String, java.lang.Object[], java.lang.Object)
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean b = ServerPlugin.hasModuleArtifact(receiver);
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

		if (receiver instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) receiver;
			IFile file = fei.getFile();
			b = ServerPlugin.hasModuleArtifact(file);
			if (b)
				return true;
		}
		return false;
	}
}