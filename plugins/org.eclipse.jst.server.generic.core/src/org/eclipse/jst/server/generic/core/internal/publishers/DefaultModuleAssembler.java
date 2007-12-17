/***************************************************************************************************
 * Copyright (c) 2005, 2007 Eteration A.S. and Gorkem Ercan.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation 
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal.publishers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.wst.server.core.IModule;
/**
 * Default module assembler that basically copies the contents.
 * @author Gorkem Ercan
 */
public class DefaultModuleAssembler extends AbstractModuleAssembler {
	
	protected DefaultModuleAssembler(IModule module, GenericServer server, IPath assembleRoot) {
		super(module, server, assembleRoot);
	}
	
	public IPath assemble(IProgressMonitor monitor) throws CoreException {
		return copyModule(fModule,monitor);		
	}
}