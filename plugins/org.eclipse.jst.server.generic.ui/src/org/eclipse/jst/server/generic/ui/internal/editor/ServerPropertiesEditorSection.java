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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.core.internal.GenericServerBehaviour;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.ui.internal.GenericServerUIMessages;
import org.eclipse.jst.server.generic.ui.internal.GenericUiPlugin;
import org.eclipse.jst.server.generic.ui.internal.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.wst.server.ui.editor.ServerEditorSection;

public class ServerPropertiesEditorSection extends ServerEditorSection{
	private static final String MESSAGE_ID_SERVER_RUNNING = "server_running"; //$NON-NLS-1$
	protected GenericServer fServer;
	private PropertyChangeListener fPropertyChangeListener;
	private ILaunchesListener2 fLaunchListener;
	private Map fControls = new HashMap();
    protected boolean fUpdating;


	public void init(final IEditorSite site, IEditorInput input) {
		super.init(site, input);
		if(server!=null){
			fServer = (GenericServer)server.loadAdapter(GenericServer.class, new NullProgressMonitor());
		}
		fPropertyChangeListener = new PropertyChangeListener(){

			public void propertyChange( PropertyChangeEvent evt ) {
				if(evt.getPropertyName().equals( GenericServerRuntime.SERVER_INSTANCE_PROPERTIES ))
                {
                    if ( !fUpdating ){
                        fUpdating = true;
                        updateControls();
                        fUpdating = false;
                    }
                }
			}
		};
		server.addPropertyChangeListener( fPropertyChangeListener );
		fLaunchListener = new ILaunchesListener2() {

			public void launchesRemoved(ILaunch[] launches) {
				//Nothing to do
			}

			private ILaunchConfiguration getServerLaunchConfig(ILaunch[] launches){
				for (int i=0; i< launches.length; i++)
				{
					ILaunchConfiguration launchConfig = launches[i].getLaunchConfiguration();
						if (launchConfig != null) {
							String serverId;
							try {
								serverId = launchConfig.getAttribute( GenericServerBehaviour.ATTR_SERVER_ID, (String) null);
								if (fServer.getServer().getId().equals(serverId)) {
									return launchConfig;
							}
							} catch (CoreException e) {
								//Ignore
							}

						}
				}
				return null;

			}
			public void launchesChanged(ILaunch[] launches) {
				//Nothing to do
			}

			public void launchesAdded(ILaunch[] launches) {
				ILaunchConfiguration lc = getServerLaunchConfig(launches);
				try {
					if( lc  != null){
						if ("true".equals(lc.getAttribute(GenericServerBehaviour.ATTR_STOP, "false"))){ //$NON-NLS-1$ //$NON-NLS-2$
						site.getWorkbenchWindow().getWorkbench().getDisplay().asyncExec( new Runnable() {
								public void run() {
									IManagedForm managedForm = getManagedForm();
									managedForm.getMessageManager().removeMessage(MESSAGE_ID_SERVER_RUNNING);
									managedForm.getMessageManager().update();

								}

							});
						}
						else{
							site.getWorkbenchWindow().getWorkbench().getDisplay().asyncExec( new Runnable() {
								public void run() {
									getManagedForm().getMessageManager().addMessage(MESSAGE_ID_SERVER_RUNNING,GenericServerUIMessages.serverRunningCanNotSave , null, IMessageProvider.WARNING);

								}

							});

						}
					}
				} catch (CoreException e) {
					GenericUiPlugin.getDefault().getLog().log(e.getStatus());
				}
			}

			public void launchesTerminated(ILaunch[] launches) {
				if(getServerLaunchConfig(launches) != null )
				{
					site.getWorkbenchWindow().getWorkbench().getDisplay().asyncExec( new Runnable() {

						public void run() {
							getManagedForm().getMessageManager().removeMessage(MESSAGE_ID_SERVER_RUNNING);
						}
					});
				}

			}

		};

		getLaunchManager().addLaunchListener(fLaunchListener);

	}

	protected void updateControls() {
        List<Property> props = fServer.getServerDefinition().getProperty();
        for (Iterator<Property> iter = props.iterator(); iter.hasNext();) {
            Property property = iter.next();
            if(property.getContext().equals(Property.CONTEXT_SERVER))
            {
                if( Property.TYPE_BOOLEAN.equals(property.getType()) ){
                    Button b = (Button)fControls.get( property.getId() );
                    b.setSelection( "true".equals(  getPropertyValue( property ) ) ); //$NON-NLS-1$
                }
                else if( Property.TYPE_SELECT.equals( property.getType() ) ||  Property.TYPE_SELECT_EDIT.equals( property.getType() )){
                    Combo c = (Combo)fControls.get( property.getId() );
                    String value = getPropertyValue( property )==null ? "": getPropertyValue( property ); //$NON-NLS-1$
                    //c.setText( getPropertyValue( property ) );
                    // responding to "value not used" msg, I'm assuming value
                    // should be used as in following block.
                    c.setText( value );
                }
                else{
                    Text t = (Text)fControls.get( property.getId() );
                    String value = getPropertyValue( property )==null ? "": getPropertyValue( property ); //$NON-NLS-1$
                    t.setText( value );
                }
            }
        }
    }

    public void createSection(Composite parent) {
		super.createSection(parent);
		FormToolkit formToolkit = getFormToolkit(parent.getDisplay());
		Section section = formToolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED
				| ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
			section.setText(GenericServerUIMessages.ServerEditorSectionTitle);
			section.setDescription(GenericServerUIMessages.ServerEditorSectionDescription);
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

		List<Property> props = fServer.getServerDefinition().getProperty();
		for (Iterator<Property> iter = props.iterator(); iter.hasNext();) {
			Property property = iter.next();
			if(property.getContext().equals(Property.CONTEXT_SERVER))
				createPropertyControl(composite, property,formToolkit);
		}

		formToolkit.paintBordersFor(composite);
		section.setClient(composite);


		if ( getExistingLaunch() != null ){
			getManagedForm().getMessageManager().addMessage(MESSAGE_ID_SERVER_RUNNING,GenericServerUIMessages.serverRunningCanNotSave , null, IMessageProvider.WARNING);
		}
	}

	protected void executeUpdateOperation(String propertyName, String propertyValue)
	{
        if( !fUpdating )
        {
            fUpdating = true;
            execute( new UpdateServerPropertyOperation( server,
                    GenericServerUIMessages.UpdateOperationDescription, propertyName,
                    propertyValue ) );
            fUpdating = false;
        }
	}

    private void createPropertyControl(Composite parent, final Property property, FormToolkit toolkit){

    	if( Property.TYPE_DIRECTORY.equals(property.getType())) {
    		final Text path = SWTUtil.createLabeledPath(property.getLabel(),getPropertyValue(property),parent,toolkit);
            fControls.put( property.getId(), path );
    		path.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					executeUpdateOperation(property.getId(),path.getText());
				}
			});
     	} else if( Property.TYPE_FILE.equals(property.getType())) {

    	    final Text file = SWTUtil.createLabeledFile(property.getLabel(),getPropertyValue(property),parent,toolkit);
    		fControls.put( property.getId(), file );
            file.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					executeUpdateOperation(property.getId(),file.getText());
				}
			});
       	}else if( Property.TYPE_BOOLEAN.equals(property.getType())) {
    	    final Button bool = SWTUtil.createLabeledCheck(property.getLabel(),("true".equals( getPropertyValue(property))),parent,toolkit); //$NON-NLS-1$
    	    fControls.put( property.getId(), bool );
            bool.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					executeUpdateOperation(property.getId(),  Boolean.toString(bool.getSelection()));
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					// Do Nothing
				}
			});
       	}else if(Property.TYPE_SELECT.equals(property.getType())) {
    		StringTokenizer tokenizer = new StringTokenizer(property.getDefault(),","); //$NON-NLS-1$
    		int tokenCount = tokenizer.countTokens();
    		String[] values = new String[tokenCount];
    		int i =0;
    		while(tokenizer.hasMoreTokens() && i<tokenCount){
    			values[i]=tokenizer.nextToken();
    			i++;
    		}
       		final Combo combo = SWTUtil.createLabeledCombo(property.getLabel(), values, parent,toolkit);
       		fControls.put( property.getId(), combo );
            combo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					executeUpdateOperation(property.getId(),combo.getText());
				}
			});
       		combo.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					executeUpdateOperation(property.getId(),combo.getText());
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					// nothing to do
				}
			});
       	}else if(Property.TYPE_SELECT_EDIT.equals(property.getType())) {
    		StringTokenizer tokenizer = new StringTokenizer(property.getDefault(),","); //$NON-NLS-1$
    		int tokenCount = tokenizer.countTokens();
    		String[] values = new String[tokenCount];
    		int i =0;
    		while(tokenizer.hasMoreTokens() && i<tokenCount){
    			values[i]=tokenizer.nextToken();
    			i++;
    		}
       		final Combo combo = SWTUtil.createLabeledEditableCombo(property.getLabel(), values,getPropertyValue(property), parent,toolkit);
       		fControls.put( property.getId(), combo );
            combo.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					executeUpdateOperation(property.getId(),combo.getText());
				}
			});
       		combo.addSelectionListener(new SelectionListener() {
				public void widgetSelected(SelectionEvent e) {
					executeUpdateOperation(property.getId(),combo.getText());
				}
				public void widgetDefaultSelected(SelectionEvent e) {
					// nothing to do
				}
			});
       	}
       	else  {// Property.TYPE_TEXT
    	    final Text defaultText= SWTUtil.createLabeledText(property.getLabel(),getPropertyValue(property),parent,toolkit);
            fControls.put( property.getId(), defaultText );
    		defaultText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					executeUpdateOperation(property.getId(), defaultText.getText());
				}
			});
    	}
    }

	private String getPropertyValue(Property property) {
		 return(String) fServer.getServerInstanceProperties().get(property.getId());
	}

	public void dispose() {
	    super.dispose();
        if( server!= null )
            server.removePropertyChangeListener( fPropertyChangeListener );

        getLaunchManager().removeLaunchListener( fLaunchListener );
	}


	public ILaunch getExistingLaunch() {
		ILaunchManager launchManager = getLaunchManager();

		ILaunch[] launches = launchManager.getLaunches();
		int size = launches.length;
		for (int i = 0; i < size; i++) {
			ILaunchConfiguration launchConfig = launches[i].getLaunchConfiguration();
			try {
				if (launchConfig != null) {
					String serverId = launchConfig.getAttribute(GenericServerBehaviour.ATTR_SERVER_ID, (String) null);
					if (fServer.getServer().getId().equals(serverId)) {
						if (!launches[i].isTerminated())
							return launches[i];
					}
				}
			} catch (CoreException e) {
				// ignore
			}
		}
		return null;
	}

	private ILaunchManager getLaunchManager() {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager;
	}

	private IStatus validate(){
		if (getExistingLaunch() != null){
			return new Status(IStatus.WARNING,GenericUiPlugin.PLUGIN_ID, GenericServerUIMessages.serverRunningCanNotSave);
			}
		return null;

	}

	public IStatus[] getSaveStatus() {
		IStatus status = validate();
		if (status != null ){
			IStatus[] statusArray = {status};
			return statusArray;
		}
		return super.getSaveStatus();
	}
}
