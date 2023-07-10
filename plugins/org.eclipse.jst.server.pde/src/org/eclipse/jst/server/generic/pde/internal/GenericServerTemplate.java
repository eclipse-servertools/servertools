/********************************************************************************
 * Copyright (c) 2005, 2023 Eteration A.S., Gorkem Ercan, and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 *******************************************************************************/
package org.eclipse.jst.server.generic.pde.internal;

import java.net.URL;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jst.server.generic.pde.ServerPdePlugin;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginElement;
import org.eclipse.pde.core.plugin.IPluginExtension;
import org.eclipse.pde.core.plugin.IPluginModelFactory;
import org.eclipse.pde.core.plugin.IPluginReference;
import org.eclipse.pde.ui.templates.PluginReference;
import org.eclipse.pde.ui.templates.OptionTemplateSection;
import org.eclipse.pde.ui.templates.TemplateOption;
/**
 * Simple generic server plug-in template
 * 
 * @author Gorkem Ercan
 *
 */
public class GenericServerTemplate extends OptionTemplateSection {

	private static final String KEY_SERVER_VERSION = "server_version"; //$NON-NLS-1$
	private static final String KEY_SERVER_START_BEFORE_PUBLISH = "server_start_before_publish"; //$NON-NLS-1$
	private static final String KEY_SERVER_LAUNCHTYPE = "server_launchtype"; //$NON-NLS-1$
	private static final String KEY_SERVER_VENDOR = "server_vendor"; //$NON-NLS-1$
	private static final String KEY_SERVER_DESCRIPTION = "server_description"; //$NON-NLS-1$
	private static final String KEY_SERVER_NAME = "server_name"; //$NON-NLS-1$
	private static final String[][] launchOpts = {{"java",Messages.labelJavaLaunchConfiguration},{"external",Messages.labelExternalLaunchConfiguration}}; //$NON-NLS-1$ //$NON-NLS-3$
	
	private TemplateOption fServerNameOption;
	private TemplateOption fServerDescriptionOption;
	private TemplateOption fServerVendorOption;
	private TemplateOption fServerVersionOption;
	private TemplateOption fLaunchTypeOption;
	private TemplateOption fStartBeforPublishOption;
	
	public GenericServerTemplate() {
		setPageCount(1);
		createOptions();
	}
	
	
	private void createOptions(){
	
		fServerNameOption= addOption(KEY_SERVER_NAME,Messages.labelServerName,"your server's name",0); //$NON-NLS-2$
		fServerDescriptionOption = addOption(KEY_SERVER_DESCRIPTION,"Description","User description of your server",0); //$NON-NLS-1$ //$NON-NLS-2$
		fServerVendorOption= addOption(KEY_SERVER_VENDOR,Messages.labelServerVendor,"Server vendor' s name",0); //$NON-NLS-2$
		fServerVersionOption= addOption(KEY_SERVER_VERSION,Messages.labelServerVesion,"0",0); //$NON-NLS-2$
		fLaunchTypeOption= addOption(KEY_SERVER_LAUNCHTYPE,Messages.labelLaunchType,launchOpts,"java",0); //$NON-NLS-2$
		fStartBeforPublishOption= addOption(KEY_SERVER_START_BEFORE_PUBLISH,Messages.labelStartBeforePublish,false,0);
		
		fServerNameOption.setRequired(true);
		fServerDescriptionOption.setRequired(true);
		fServerVendorOption.setRequired(true);
		fServerVersionOption.setRequired(true);
	}
	
	public String getSectionId() {
		return "genericserver"; //$NON-NLS-1$
	}

	protected void updateModel(IProgressMonitor monitor) throws CoreException{
		IPluginBase plugin = model.getPluginBase();
		IPluginModelFactory factory = model.getPluginFactory();		
		addRuntimeType(plugin, factory);
		addServerType(plugin, factory);
		addWizardFragment(plugin, factory);
		addServerImage(plugin, factory);
		addRuntimeTargetHandler(plugin, factory);
		addServerDefinition(plugin, factory);
	}

	private void addServerDefinition(IPluginBase plugin, IPluginModelFactory factory) throws CoreException {
		IPluginExtension definitionExtension = createExtension("org.eclipse.jst.server.generic.core.serverdefinition",true); //$NON-NLS-1$
		IPluginElement serverDef = factory.createElement(definitionExtension);
		serverDef.setName("serverdefinition"); //$NON-NLS-1$
		serverDef.setAttribute("id",getRuntimeId()); //$NON-NLS-1$
		serverDef.setAttribute("definitionfile","/servers/"+getStringOption(KEY_SERVER_NAME)+".serverdef"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		definitionExtension.add(serverDef);
		if(!definitionExtension.isInTheModel())
			plugin.add(definitionExtension);
	}


	private void addRuntimeTargetHandler(IPluginBase plugin, IPluginModelFactory factory) throws CoreException {
		IPluginExtension handlerExtension = createExtension("org.eclipse.wst.server.core.runtimeTargetHandlers",true); //$NON-NLS-1$
		IPluginElement handler= factory.createElement(handlerExtension);
		handler.setName("runtimeTargetHandler"); //$NON-NLS-1$
		handler.setAttribute("id",getNamespace()+".runtimeTarget"); //$NON-NLS-1$ //$NON-NLS-2$
		handler.setAttribute("runtimeTypeIds",getRuntimeId()); //$NON-NLS-1$
		handler.setAttribute("class","org.eclipse.jst.server.generic.core.internal.GenericServerRuntimeTargetHandler"); //$NON-NLS-1$ //$NON-NLS-2$
		handlerExtension.add(handler);
		if(!handlerExtension.isInTheModel())
			plugin.add(handlerExtension);
	}


	private void addServerImage(IPluginBase plugin, IPluginModelFactory factory) throws CoreException {
		IPluginExtension imageExtension = createExtension("org.eclipse.wst.server.ui.serverImages",true); //$NON-NLS-1$
		IPluginElement serverImage = factory.createElement(imageExtension);
		serverImage.setName("image"); //$NON-NLS-1$
		serverImage.setAttribute("id",getNamespace()+".serverImage"); //$NON-NLS-1$ //$NON-NLS-2$
		serverImage.setAttribute("icon","icons/server.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		serverImage.setAttribute("typeIds",getServerId()); //$NON-NLS-1$
		imageExtension.add(serverImage);
		
		IPluginElement runtimeImage = factory.createElement(imageExtension);
		runtimeImage.setName("image"); //$NON-NLS-1$
		runtimeImage.setAttribute("id",getNamespace()+".runtimeImage"); //$NON-NLS-1$ //$NON-NLS-2$
		runtimeImage.setAttribute("icon","icons/server.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		runtimeImage.setAttribute("typeIds",getRuntimeId()); //$NON-NLS-1$
		imageExtension.add(runtimeImage);
		if(!imageExtension.isInTheModel())
			plugin.add(imageExtension);
	}


	private void addWizardFragment(IPluginBase plugin, IPluginModelFactory factory) throws CoreException {
		IPluginExtension wizardExtension = createExtension("org.eclipse.wst.server.ui.wizardFragments",true); //$NON-NLS-1$
		IPluginElement runtimeWiz = factory.createElement(wizardExtension);
		runtimeWiz.setName("fragment"); //$NON-NLS-1$
		runtimeWiz.setAttribute("id",getNamespace()+".runtimeWizard"); //$NON-NLS-1$ //$NON-NLS-2$
		runtimeWiz.setAttribute("class","org.eclipse.jst.server.generic.ui.internal.GenericServerRuntimeWizardFragment"); //$NON-NLS-1$ //$NON-NLS-2$
		runtimeWiz.setAttribute("typeIds",getRuntimeId()); //$NON-NLS-1$
		wizardExtension.add(runtimeWiz);
		if(!wizardExtension.isInTheModel())
			plugin.add(wizardExtension);
		
		IPluginElement serverWiz = factory.createElement(wizardExtension);
		serverWiz.setName("fragment"); //$NON-NLS-1$
		serverWiz.setAttribute("id",getNamespace()+".serverWizard"); //$NON-NLS-1$ //$NON-NLS-2$
        serverWiz.setAttribute("class","org.eclipse.jst.server.generic.ui.internal.GenericServerWizardFragment");    //$NON-NLS-1$ //$NON-NLS-2$
		serverWiz.setAttribute("typeIds",getServerId()); //$NON-NLS-1$
		wizardExtension.add(serverWiz);
	}


	private void addServerType(IPluginBase plugin, IPluginModelFactory factory) throws CoreException {
		IPluginExtension serverExtension =createExtension("org.eclipse.wst.server.core.serverTypes",true); //$NON-NLS-1$
		IPluginElement serverType = factory.createElement(serverExtension);
		serverType.setName("serverType"); //$NON-NLS-1$
		serverType.setAttribute("runtime","true"); //$NON-NLS-1$ //$NON-NLS-2$
		serverType.setAttribute("class","org.eclipse.jst.server.generic.core.internal.GenericServer"); //$NON-NLS-1$ //$NON-NLS-2$
		serverType.setAttribute("id",getServerId()); //$NON-NLS-1$
		serverType.setAttribute("initialState","stopped"); //$NON-NLS-1$ //$NON-NLS-2$
		serverType.setAttribute("supportsRemoteHosts","false"); //$NON-NLS-1$ //$NON-NLS-2$
		serverType.setAttribute("runtimeTypeId",getRuntimeId()); //$NON-NLS-1$
		serverType.setAttribute("description",getStringOption(KEY_SERVER_DESCRIPTION)); //$NON-NLS-1$
		serverType.setAttribute("launchConfigId",getSelectedConfigType()); //$NON-NLS-1$
		serverType.setAttribute("behaviourClass",getSelectedBehaviourClass()); //$NON-NLS-1$
		serverType.setAttribute("name",getStringOption(KEY_SERVER_NAME)); //$NON-NLS-1$
		serverType.setAttribute("startTimeout","75000"); //$NON-NLS-1$ //$NON-NLS-2$
		serverType.setAttribute("stopTimeout","30000"); //$NON-NLS-1$ //$NON-NLS-2$
		serverType.setAttribute("hasConfiguration","false"); //$NON-NLS-1$ //$NON-NLS-2$
		serverType.setAttribute("launchModes","run,debug"); //$NON-NLS-1$ //$NON-NLS-2$
		serverType.setAttribute("startBeforePublish", Boolean.toString(getBooleanOption(KEY_SERVER_START_BEFORE_PUBLISH))); //$NON-NLS-1$
		serverExtension.add(serverType);
		if(!serverExtension.isInTheModel())
			plugin.add(serverExtension);
	}


	private void addRuntimeType(IPluginBase plugin, IPluginModelFactory factory) throws CoreException {
		IPluginExtension extension =createExtension("org.eclipse.wst.server.core.runtimeTypes",true); //$NON-NLS-1$
		IPluginElement runtimeType = factory.createElement(extension);
		runtimeType.setName("runtimeType"); //$NON-NLS-1$
		runtimeType.setAttribute("id",getRuntimeId()); //$NON-NLS-1$
		runtimeType.setAttribute("name",getStringOption(KEY_SERVER_NAME)); //$NON-NLS-1$
		runtimeType.setAttribute("description",getStringOption(KEY_SERVER_DESCRIPTION)); //$NON-NLS-1$
		runtimeType.setAttribute("vendor",getStringOption(KEY_SERVER_VENDOR)); //$NON-NLS-1$
		runtimeType.setAttribute("version",getStringOption(KEY_SERVER_VERSION)); //$NON-NLS-1$
		runtimeType.setAttribute("class","org.eclipse.jst.server.generic.core.internal.GenericServerRuntime"); //$NON-NLS-1$ //$NON-NLS-2$
		
		IPluginElement moduleType = factory.createElement(runtimeType);
		moduleType.setName("moduleType"); //$NON-NLS-1$
		moduleType.setAttribute("types","jst.web"); //$NON-NLS-1$ //$NON-NLS-2$
        moduleType.setAttribute("versions","1.2, 1.3"); //$NON-NLS-1$ //$NON-NLS-2$
        runtimeType.add(moduleType);
        
		extension.add(runtimeType);
		if(!extension.isInTheModel())
			plugin.add(extension);
	}
	
	private String getRuntimeId(){
		return getNamespace()+".runtime"; //$NON-NLS-1$
	}
	
	private String getServerId(){
		return getNamespace()+".server"; //$NON-NLS-1$
	}
	private String getNamespace()
	{
		return model.getPluginBase().getId();
	}
	private String getSelectedBehaviourClass() {
		if(isExternalSelected())
			return "org.eclipse.jst.server.generic.core.internal.ExternalServerBehaviour"; //$NON-NLS-1$
		return "org.eclipse.jst.server.generic.core.internal.GenericServerBehaviour"; //$NON-NLS-1$
	}

	private boolean isExternalSelected(){
		String selectedType = getStringOption(KEY_SERVER_LAUNCHTYPE);
		return "external".equals(selectedType); //$NON-NLS-1$
	}

	private String getSelectedConfigType(){
		if(isExternalSelected())
			return "org.eclipse.jst.server.generic.core.ExternalLaunchConfigurationType"; //$NON-NLS-1$
		return "org.eclipse.jst.server.generic.core.launchConfigurationType"; //$NON-NLS-1$
	}
	
	protected ResourceBundle getPluginResourceBundle(){
		return null;
	}
	
	public String[] getNewFiles(){
		return new String[]{"icons/", "server/","buildfiles/"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public String getUsedExtensionPoint(){
		return null;
	}
	
	public void validateOptions(TemplateOption changed){
		if(changed.isRequired() && changed.isEmpty()){
			flagMissingRequiredOption(changed);
			return;
		}
		TemplateOption[] options = getOptions(0);
		for (int i = 0; i < options.length; i++) {
			if(options[i].isRequired() && options[i].isEmpty()){
				flagMissingRequiredOption(options[i]);
				return;
			}
		}
		resetPageState();
	}
	
	
	
	public void addPages(Wizard wizard) {
		WizardPage page = createPage(0);
		page.setTitle(Messages.pageTitle);
		page.setDescription(Messages.pageDescription);
		wizard.addPage(page);
		markPagesAdded();
	}
	
	
 
	protected URL getInstallURL() {
		return ServerPdePlugin.getDefault().getBundle().getEntry("/"); //$NON-NLS-1$
	}
	/* (non-Javadoc)
	 * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#getDependencies(java.lang.String)
	 */
	public IPluginReference[] getDependencies(String schemaVersion) {
		IPluginReference[] dep = new IPluginReference[4];
		dep[0] = new PluginReference("org.eclipse.core.runtime", null, 0); //$NON-NLS-1$
		dep[1] = new PluginReference("org.eclipse.ui", null, 0); //$NON-NLS-1$
		dep[2] = new PluginReference("org.eclipse.jst.server.generic.core", null, 0); //$NON-NLS-1$
		dep[3] = new PluginReference("org.eclipse.jst.server.generic.ui", null, 0); //$NON-NLS-1$
		return dep;
	}

	public Object getValue(String variable) {
		return super.getValue(variable);
	}
	
}
