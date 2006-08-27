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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * Provides the UI objects for gathering user information
 * for server properties.
 * 
 * @author Gorkem Ercan
 */
public abstract class ServerTypeDefinitionDecorator implements GenericServerCompositeDecorator {

	protected static final String CONTEXT_RUNTIME = Property.CONTEXT_RUNTIME;
	protected static final String CONTEXT_SERVER = Property.CONTEXT_SERVER;
	private ServerRuntime fDefinition;
    private Map fProperties;
	private String fContext;
	private String fLastMessage = null;
	protected IWizardHandle fWizard;
	private List fPropertyControls= new ArrayList();

	private final class PathModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			String path = ((Text) e.widget).getText();
			
			if(path.length()<1)
			{
				fLastMessage = GenericServerUIMessages.emptyPath;
				fWizard.setMessage(fLastMessage,IMessageProvider.ERROR);
			}
			else if(!pathExist(path)){
				fLastMessage = NLS.bind(GenericServerUIMessages.invalidPath,path);
				fWizard.setMessage(fLastMessage,IMessageProvider.ERROR);
			}else{
				if(fLastMessage!=null && fLastMessage.equals(fWizard.getMessage())){
					fLastMessage=null;
					fWizard.setMessage(null,IMessageProvider.NONE);
				}
				validate();
			}
		}
		private boolean pathExist(String path){
			File f = new File(path);
			return f.exists();
		}
	}

	/**
	 * Constructor
	 * 
	 * @param definition
	 * @param initialProperties
	 * @param context
	 * @param handle
	 */
	public ServerTypeDefinitionDecorator(ServerRuntime definition, Map initialProperties, String context, IWizardHandle handle) {
		super();
		fDefinition = definition;
		fProperties = initialProperties;
		fContext = context;
		fWizard = handle;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.generic.ui.internal.GenericServerCompositeDecorator#decorate(org.eclipse.jst.server.generic.ui.internal.GenericServerComposite)
	 */
	public void decorate(GenericServerComposite composite) {

		List properties =null; 
		if(fDefinition==null){
			properties= new ArrayList(0);
		}
		else{
			properties= fDefinition.getProperty();
		}
		for (int i = 0; i < properties.size(); i++) {
			Property property = (Property) properties.get(i);
			if (this.fContext.equals(property.getContext()))
				createPropertyControl(composite, property);
		}
		Dialog.applyDialogFont(composite);
	}

	
    private void createPropertyControl(Composite parent, Property property){
    	if( Property.TYPE_DIRECTORY.equals(property.getType())) {
    		Text path = SWTUtil.createLabeledPath(property.getLabel(),getPropertyValue(property),parent);
    		path.setData(property);
    		path.addModifyListener(new PathModifyListener());
    		registerControl(path);
     	} else if( Property.TYPE_FILE.equals(property.getType())) {
    	    Text file = SWTUtil.createLabeledFile(property.getLabel(),getPropertyValue(property),parent);
    		file.setData(property);
    		file.addModifyListener(new PathModifyListener());
    		registerControl(file);
       	} else if( Property.TYPE_BOOLEAN.equals(property.getType())) {
    	    Button bool =SWTUtil.createLabeledCheck(property.getLabel(),("true".equals( getPropertyValue(property))),	parent); //$NON-NLS-1$
    		bool.setData(property);
    		registerControl(bool);
       	}else if(Property.TYPE_SELECT.equals(property.getType())) {
    		StringTokenizer tokenizer = new StringTokenizer(property.getDefault(),","); //$NON-NLS-1$
    		int tokenCount = tokenizer.countTokens();
    		String[] values = new String[tokenCount];
    		int i =0;
    		while(tokenizer.hasMoreTokens() && i<tokenCount){
    			values[i]=tokenizer.nextToken();
    			i++;
    		}
       		Combo combo = SWTUtil.createLabeledCombo(property.getLabel(),values, parent);
       		combo.setData(property);
       		registerControl(combo);
       	}
       	else {//default is TEXT
    	    Text defaultText= SWTUtil.createLabeledText(property.getLabel(),getPropertyValue(property),parent);
    		defaultText.setData(property);
    		registerControl(defaultText);
    	}
    }

	private void registerControl(Control control)
    {
    	fPropertyControls.add(control);
    }
	
    private String getPropertyValue(Property property){	
		if(fProperties!=null && fProperties.isEmpty()==false){
		//user properties exist use those
			return(String)fProperties.get(property.getId()); 
		}	
		if(Property.CONTEXT_SERVER.equals(property.getContext()))
			return fDefinition.getResolver().resolveProperties(property.getDefault());
		return property.getDefault();
	}	

   /**
    * Returns the property name/value pairs.
    * @return Map containing the values collected from the user
    */
	public Map getValues(){
		Map propertyMap = new HashMap();
    	for(int i=0; i<fPropertyControls.size();i++){
    		Property prop = (Property)((Control)fPropertyControls.get(i)).getData();
    		if(fPropertyControls.get(i)instanceof Button){
    			Button button = (Button)fPropertyControls.get(i);
    			propertyMap.put(prop.getId(),Boolean.toString(button.getSelection()));
    		}
    		else if(fPropertyControls.get(i) instanceof Combo){
    			Combo combo = (Combo)fPropertyControls.get(i);
     			int index = combo.getSelectionIndex();
    			propertyMap.put(prop.getId(),combo.getItem(index));
    		}else{
    			Text text = (Text)fPropertyControls.get(i);
    			propertyMap.put(prop.getId(),text.getText());
    		}
    	}
    	return propertyMap;
    }
}
