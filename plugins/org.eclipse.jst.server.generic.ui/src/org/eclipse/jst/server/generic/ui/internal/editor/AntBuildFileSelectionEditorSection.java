/***************************************************************************************************
 * Copyright (c) 2007,2017 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Locale;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.ui.internal.GenericServerUIMessages;
import org.eclipse.jst.server.generic.ui.internal.GenericUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

public class AntBuildFileSelectionEditorSection extends ServerEditorSection{
	protected GenericServer fServer;
	private PropertyChangeListener fPropertyChangeListener;
	protected Text buildFileText;
	protected boolean fUpdating;
   
	private class BuildFileFilter extends ViewerFilter{

		public boolean select(Viewer viewer, Object parentElement,
				Object element) {

			if (element instanceof IFile) {
				IFile file = ((IFile)element);
				if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".xml")){ //$NON-NLS-1$
					IContentType contentType = IDE.getContentType(file);
					if( contentType != null){
						return "org.eclipse.ant.core.antBuildFile".equals(contentType.getId()); //$NON-NLS-1$
					}	
				}
				return false;
			}

			if (element instanceof IProject && !((IProject)element).isOpen())
				return false;
			
			if (element instanceof IContainer){ // i.e. IProject, IFolder
				try {
					IResource[] resources = ((IContainer)element).members();
					for (int i = 0; i < resources.length; i++){
						if (select(viewer, parentElement, resources[i]))
							return true;
					}
				} catch (CoreException e) {
					//Ignored
				}
			}
			return false;
		
		}
		
	}
    
	public void init(final IEditorSite site, IEditorInput input) {
		super.init(site, input);
		if( server != null ){
			fServer = (GenericServer)server.loadAdapter(GenericServer.class, new NullProgressMonitor());
		}
		fPropertyChangeListener = new PropertyChangeListener(){
			public void propertyChange( PropertyChangeEvent evt ) {
				if(evt.getPropertyName().equals( GenericServerRuntime.SERVER_INSTANCE_PROPERTIES )) 
                {
                    if ( !fUpdating ){
                        fUpdating = true;
              			Object value = fServer.getServerInstanceProperties().get(GenericServer.PROP_CUSTOM_BUILD_SCRIPT);
              			buildFileText.setText(value == null? "": (String)value );         //$NON-NLS-1$
                        fUpdating = false;
                    }

                }
			}
		};
		server.addPropertyChangeListener( fPropertyChangeListener );

		
	}
    

    public void createSection(final Composite parent) {
		super.createSection(parent);
		FormToolkit formToolkit = getFormToolkit(parent.getDisplay());
		Section section = formToolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED
				| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
			section.setText(GenericServerUIMessages.AntPublisherSectionHeading);
			section.setDescription(GenericServerUIMessages.AntPublisherSectionDescription);
			section.setLayoutData(new GridData(SWT.FILL,SWT.NONE,true,false));
			
		Composite composite = formToolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns=3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(SWT.FILL,SWT.NONE,true,false));
		
		
		Label buildFileLabel = formToolkit.createLabel(composite, GenericServerUIMessages.LabelBuildFile);
		buildFileLabel.setForeground(formToolkit.getColors().getColor(IFormColors.TITLE));
		buildFileText = formToolkit.createText(composite, ""); //$NON-NLS-1$
		String oldValue = fServer.getServerInstanceProperties().get(GenericServer.PROP_CUSTOM_BUILD_SCRIPT)==null ?"": //$NON-NLS-1$
			(String)fServer.getServerInstanceProperties().get(GenericServer.PROP_CUSTOM_BUILD_SCRIPT) ;
		buildFileText.setText(oldValue);
		
		buildFileText.addListener(SWT.Modify, new Listener() {		
			public void handleEvent(Event event) {
				executeUpdateOperation( GenericServer.PROP_CUSTOM_BUILD_SCRIPT, buildFileText.getText() );
				
			}
		
		});
		
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		buildFileText.setLayoutData(gridData);
		Button buildFileBtn = formToolkit.createButton(composite, GenericServerUIMessages.ButtonBrowseForBuildFile, SWT.PUSH);
		buildFileBtn.addListener(SWT.Selection, new Listener() {
		
			public void handleEvent(Event event) {
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						parent.getShell(), new WorkbenchLabelProvider(),
						new WorkbenchContentProvider());

				// dialog.setValidator(new FileValidator());
				dialog.setAllowMultiple(false);
				dialog.setTitle(GenericServerUIMessages.AntBuildDialogTitle);
				dialog.setMessage(GenericServerUIMessages.AntBuildDialogDescription);
				dialog.addFilter(new BuildFileFilter());
				dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());

				if (dialog.open() == Window.OK) {
					IFile file = (IFile) dialog.getFirstResult();
					buildFileText.setText(VariablesPlugin.getDefault().getStringVariableManager().generateVariableExpression("workspace_loc",file.getFullPath().toString())); //$NON-NLS-1$
				} 

			}
		
		});

		formToolkit.paintBordersFor(composite);
		section.setClient(composite);
	}


	public void dispose() {
	    super.dispose();
	       if( server!= null )
	            server.removePropertyChangeListener( fPropertyChangeListener );
	  
	}
	
	public IStatus[] getSaveStatus() {
		try {
			String filename = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(buildFileText.getText());
			if ( filename!= null && filename.length()>0  )
			{
				File f = new File(filename);
				if ( !f.exists() ){
					IStatus[] st = new Status[1];	
					st[0] = new Status(IStatus.ERROR,GenericUiPlugin.PLUGIN_ID,GenericServerUIMessages.ErrorNoAntBuildFile);
					return st;
				}
			}
		} catch (CoreException e) {
			GenericUiPlugin.getDefault().getLog().log(e.getStatus());
		}
		return super.getSaveStatus();
	}

	protected void executeUpdateOperation(String propertyName, String propertyValue)
	{
		if (!fUpdating) {
			fUpdating = true;
			execute(new UpdateServerPropertyOperation(server,
					GenericServerUIMessages.UpdateOperationDescription,
					propertyName, propertyValue));
			fUpdating = false;
		}
	}
} 
