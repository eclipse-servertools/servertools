/*
 * Created on Oct 19, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.jst.server.generic.internal.ui;

import java.util.Map;

import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * 
 *
 * @author Gorkem Ercan
 */
public abstract class ServerDefinitionTypeAwareWizardFragment extends WizardFragment {

	public boolean hasComposite() {
		return true;
	}

	public Composite createComposite(Composite parent, IWizardHandle handle) {
	    
	    Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout grid = new GridLayout(1,false);
		container.setLayout(grid);
		createContent(container,handle);
		return container;
	}

	public abstract void createContent(Composite parent, IWizardHandle handle);

	protected ServerTypeDefinition getServerTypeDefinition(String definitionID, Map properties)
	{
	    return CorePlugin.getDefault().getServerTypeDefinitionManager().getServerRuntimeDefinition(definitionID,properties);
	}
	
	protected ServerTypeDefinition[] getAllServerDefinitionTypes()
	{
	    return CorePlugin.getDefault().getServerTypeDefinitionManager().getServerTypeDefinitions();
	}

}
