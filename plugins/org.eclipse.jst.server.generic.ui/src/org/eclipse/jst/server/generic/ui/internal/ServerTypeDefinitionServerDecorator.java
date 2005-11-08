/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * Server properties decorator.
 * @author Gorkem Ercan
 */
public class ServerTypeDefinitionServerDecorator extends
		ServerTypeDefinitionDecorator {
	
	private GenericServer fServer;
	public ServerTypeDefinitionServerDecorator(ServerRuntime definition, Map initialProperties,IWizardHandle wizard,GenericServer server) {
		super(definition, initialProperties,CONTEXT_SERVER, wizard);
		fServer=server;
	}

	public boolean validate() {
		if(fServer!=null)
			fServer.setServerInstanceProperties(getValues());
		IStatus status = fServer.validate();
		if(status==null || status.isOK())
		{
			fWizard.setMessage(null, IMessageProvider.NONE);
			fWizard.update();
		}
		else
		{
			fWizard.setMessage(status.getMessage(), IMessageProvider.ERROR);
			fWizard.update();
			return true;
		}
		return false;
	}

}
