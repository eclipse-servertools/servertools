/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Gorkem Ercan - initial API and implementation
 *     Naci M. Dai
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ETERATION A.S. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Eteration Bilisim A.S.  For more
 * information on eteration, please see
 * <http://www.eteration.com/>.
 ***************************************************************************/
package org.eclipse.jst.server.generic.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class ServerTypeDefinitionGroup 
{
    public static final String CONTEXT_SERVER = Property.CONTEXT_SERVER;
    public static final String CONTEXT_RUNTIME = Property.CONTEXT_RUNTIME;

    private ServerRuntime fServerTypeDefinition;
    private List fPropertyControls = new ArrayList();
    private Map fPropertyMap =new HashMap();
    private String fContext="undefined";
    private Group fDefinitionGroup;
    private ServerDefinitionTypeAwareWizardFragment fAwareWizardFragment;
    private class PropertyModifyListener implements ModifyListener
    {
        /* (non-Javadoc)
         * @see org.eclipse.swt.events.ModifyListener#modifyText(org.eclipse.swt.events.ModifyEvent)
         */
        public void modifyText(ModifyEvent e) {
            fAwareWizardFragment.serverDefinitionTypePropertiesChanged();
            
        }
        
    }
    
    /**
     * Construct a composite for the given ServerTypeDefinition
     * 
     * @param definition
     * @param initialProperties initial values null means use default
     * @param parent
     * @param style
     */
    public ServerTypeDefinitionGroup(ServerDefinitionTypeAwareWizardFragment fragment, ServerRuntime definition, String context, Map initialProperties, Composite parent) 
    {
        fAwareWizardFragment = fragment;
        initServerTypeDefinition(definition,context,initialProperties);
        createControl(parent);
    }
    private void initProperties(Map initialProperties)
    {
        if(initialProperties!= null)
            this.fPropertyMap=initialProperties;
        else
            fPropertyMap=new HashMap();
    }
    /**
     * Changes the values with the given ones. Renders the UI 
     * with the given new values.
     *  
     * @param definition
     * @param context
     * @param initialProperties
     */
    public void reset(ServerRuntime definition, String context, Map initialProperties)
    {
        initServerTypeDefinition(definition, context, initialProperties);
        fDefinitionGroup.setText(definition.getName());
        Control[] allControls = fDefinitionGroup.getChildren();
        for(int i= 0; i<allControls.length;i++)
        {
            Control c = allControls[i];
            c.dispose();
        }
        fPropertyControls.clear();
        createPropertyControls(fDefinitionGroup);
        fDefinitionGroup.layout(true);
    }
    
    /**
     * @param definition
     * @param context
     * @param initialProperties
     */
    private void initServerTypeDefinition(ServerRuntime definition, String context, Map initialProperties) {
        fServerTypeDefinition = definition;
        initProperties(initialProperties);
        this.fContext = context;
    }
    /**
     * @param parent
     */
    private void createControl(Composite parent) {

        fDefinitionGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
        fDefinitionGroup.setText(fServerTypeDefinition.getName());
		fDefinitionGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		fDefinitionGroup.setLayout(new GridLayout(3,false));
		createPropertyControls(fDefinitionGroup);
    }
    /**
     * @param defPanel
     */
    private void createPropertyControls(Composite definitionComposite) {
		List properties = fServerTypeDefinition.getProperty();
		for(int i = 0; i<properties.size(); i++)
		{
		    Property property = (Property)properties.get(i);		    
		    if(this.fContext.equals(property.getContext()))
		        createPropertyControl(definitionComposite,property);
		}
        
    }
     
    private void createPropertyControl(Composite parent, Property property)
    {
    	if( "directory".equals(property.getType())) {
    		Text path = createLabeledPath(property.getLabel(),getPropertyValue(property),parent);
    		path.setData(property);
    		fPropertyControls.add(path);
    	} else if( "file".equals(property.getType())) {
    	    Text file = createLabeledFile(property.getLabel(),getPropertyValue(property),parent);
    		file.setData(property);
    		fPropertyControls.add(file);
    	} else if( "string".equals(property.getType())) {
    	    Text str = createLabeledText(property.getLabel(),getPropertyValue(property),parent);
    		str.setData(property);
    		fPropertyControls.add(str);
    	} else if( "boolean".equals(property.getType())) {
    	    Button bool =createLabeledCheck(property.getLabel(),("true".equals( getPropertyValue(property))),	parent);
    		bool.setData(property);
    		fPropertyControls.add(bool);
    	} else  {
    	    Text defaultText= createLabeledText(property.getLabel(),getPropertyValue(property),parent);
    		defaultText.setData(property);
    		fPropertyControls.add(defaultText);
     	}
    }
	private String getPropertyValue(Property property)
	{
		String value = property.getDefault();
		if(fPropertyMap!=null && fPropertyMap.isEmpty()==false)
			value=(String)fPropertyMap.get(property.getId()); 
		return value;
	}
    protected Button createLabeledCheck(String title, boolean value,
    		Composite defPanel) {
    	GridData gridData;
    	Label label = new Label(defPanel, SWT.WRAP);
    	gridData = new GridData();
    	label.setLayoutData(gridData);
    	label.setText(title);

    	Button fButton = new Button(defPanel, SWT.CHECK);
    	
    	gridData = new GridData(GridData.FILL_HORIZONTAL
    			| GridData.GRAB_HORIZONTAL);
    	gridData.horizontalSpan = 2;
    	fButton.setLayoutData(gridData);
    	fButton.setSelection(value);
    	fButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
               fAwareWizardFragment.serverDefinitionTypePropertiesChanged();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }
        });
    	
    	return fButton;
    }
    protected Text createLabeledFile(String title, String value,
    		Composite defPanel) {
    	GridData gridData;
    	Label label = new Label(defPanel, SWT.WRAP);
    	gridData = new GridData();
    	label.setLayoutData(gridData);
    	label.setText(title);
    
    	final Text fText = new Text(defPanel, SWT.SHADOW_IN | SWT.BORDER);
    	gridData = new GridData(GridData.FILL_HORIZONTAL
    			| GridData.GRAB_HORIZONTAL);
    	gridData.horizontalSpan = 1;
    	fText.setLayoutData(gridData);
    	fText.setText(value);
    	fText.addModifyListener(new PropertyModifyListener());
    	Button fButton = new Button(defPanel, SWT.PUSH);
    	fButton.setText("...");
    	fButton.setLayoutData(new GridData());
    	fButton.addSelectionListener(new SelectionListener() {
    		public void widgetSelected(SelectionEvent e) {
    			FileDialog dlg = new FileDialog(fDefinitionGroup.getShell());
    			dlg.setFileName(fText.getText());
    			String res = dlg.open();
    			if (res != null) {
    				fText.setText(res);
    			}
    		}
    
    		public void widgetDefaultSelected(SelectionEvent e) {
    			widgetSelected(e);
    		}
    
    	});
    
    	return fText;
    }
    protected Text createLabeledPath(String title, String value,
    		Composite parent) {
    	GridData gridData;
    	Label label = new Label(parent, SWT.WRAP);
    	gridData = new GridData();
    	label.setLayoutData(gridData);
    	label.setText(title);
    
    	final Text fText = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
    	gridData = new GridData(GridData.FILL_HORIZONTAL);
    	gridData.horizontalSpan = 1;
    	fText.setLayoutData(gridData);
    	fText.setText(value);
    	fText.addModifyListener(new PropertyModifyListener());
    	Button fButton = new Button(parent, SWT.PUSH);
    	fButton.setText("...");
    	fButton.setLayoutData(new GridData());
    	fButton.addSelectionListener(new SelectionListener() {
    		public void widgetSelected(SelectionEvent e) {
    			DirectoryDialog dlg = new DirectoryDialog(fDefinitionGroup.getShell());
    			dlg.setFilterPath(fText.getText());
    			String res = dlg.open();
    			if (res != null) {
    				fText.setText(res);
    			}
    		}
    
    		public void widgetDefaultSelected(SelectionEvent e) {
    			widgetSelected(e);
    		}
    
    	});
    
    	return fText;
    }
    protected Text createLabeledText(String title, String value,
    		Composite defPanel) {
    	GridData gridData;
    	Label label = new Label(defPanel, SWT.WRAP);
    	gridData = new GridData();
    	label.setLayoutData(gridData);
    	label.setText(title);
    
    	Text fText = new Text(defPanel, SWT.SHADOW_IN | SWT.BORDER);
    	gridData = new GridData(GridData.FILL_HORIZONTAL
    			| GridData.GRAB_HORIZONTAL);
    	gridData.horizontalSpan = 2;
    	fText.setLayoutData(gridData);
    	fText.setText(value);
    	fText.addModifyListener(new PropertyModifyListener());
    	return fText;
    }
    public Map getProperties()
    {
    	for(int i=0; i<fPropertyControls.size();i++)
    	{
    		if(fPropertyControls.get(i)instanceof Button)
    		{
    			Button button = (Button)fPropertyControls.get(i);
    			Property prop = (Property)button.getData();
    			fPropertyMap.put(prop.getId(),Boolean.toString(button.getSelection()));
    		}
    		else
    		{
    			Text text = (Text)fPropertyControls.get(i);
    			Property prop = (Property)text.getData();
    			fPropertyMap.put(prop.getId(),text.getText());
    		}
    	}
    	return fPropertyMap;
    }
}
