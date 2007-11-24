/***************************************************************************************************
 * Copyright (c) 2007 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal.editor;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.ui.internal.GenericUiPlugin;
import org.eclipse.wst.server.core.IServerWorkingCopy;

public class UpdateServerPropertyOperation extends AbstractOperation {
	private GenericServer fGenericServer;
	private String  fPropertyName;
	private String fPRopertyValue;
	private String fOldValue;
	
	public UpdateServerPropertyOperation(IServerWorkingCopy server, String name, String propertyName, String propertyValue) {
		super(name);
		if(server!=null){
			fGenericServer = (GenericServer)server.loadAdapter(GenericServer.class, new NullProgressMonitor());
		}
		fPropertyName=propertyName;
		fPRopertyValue=propertyValue;	
	}
	
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Map map = new HashMap( fGenericServer.getServerInstanceProperties() );
		if(map.containsKey(fPropertyName))
		{
            fOldValue = ( String )map.get( fPropertyName );
			map.put(fPropertyName, fPRopertyValue);
            fGenericServer.setServerInstanceProperties( map );
			return null;
		}
		return new Status(IStatus.ERROR,GenericUiPlugin.PLUGIN_ID,0,"Property can not be found",null); //$NON-NLS-1$
	}

	
	
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		return execute(monitor,info);
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Map map = new HashMap(fGenericServer.getServerInstanceProperties());
		map.put(fPropertyName, fOldValue);
        fGenericServer.setServerInstanceProperties(map);
		return null;
		}
}