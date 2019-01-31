/***************************************************************************************************
 * Copyright (c) 2005, 2006 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import java.util.Map;

import org.eclipse.jst.server.generic.core.internal.CorePlugin;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * A wizard fragment that provides support for serverdef files.
 *
 * @author Gorkem Ercan
 */
public abstract class ServerDefinitionTypeAwareWizardFragment extends WizardFragment {

    private IWizardHandle fWizard;
	
    public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle handle) {
	    
	    this.fWizard = handle;
	    Composite container = new Composite(parent, SWT.NONE);
		GridLayout grid = new GridLayout(1,false);
		grid.marginWidth=0;
		container.setLayout(grid);
	    container.setLayoutData(new GridData(GridData.FILL_BOTH));
		handle.setImageDescriptor(GenericUiPlugin.getDefault().imageDescriptor(GenericUiPlugin.WIZBAN_IMAGE));
		handle.setTitle(title());
		handle.setDescription(description());
		createContent(container,handle);
		return container;
	}
	public IWizardHandle getWizard(){
	    return fWizard;
	}
	/**
	 * Returns the description to be displayed on the wizard head.
	 * @return
	 */
	public abstract String description();
	/**
	 * Returns the title of the wizard.
	 * @return
	 */
	public abstract String title();
	/**
	 * Create the real content
	 * @param parent
	 * @param handle
	 */
	public abstract void createContent(Composite parent, IWizardHandle handle);

	/**
	 * Retuns the ServerRuntime.
	 * @param definitionID
	 * @param properties
	 * @return
	 */	
	protected ServerRuntime getServerTypeDefinition(String definitionID, Map properties)
	{
	    return CorePlugin.getDefault().getServerTypeDefinitionManager().getServerRuntimeDefinition(definitionID,properties);
	}

    /**
     * Retuns the ServerRuntime.
     * @param serverTypeId
     * @param runtimeTypeId
     * @param properties
     * @return
     */ 
    protected ServerRuntime getServerTypeDefinition(String serverTypeId, String runtimeTypeId, Map properties)
    {
        return CorePlugin.getDefault().getServerTypeDefinitionManager().getServerRuntimeDefinition(serverTypeId, runtimeTypeId, properties);
    }

}

