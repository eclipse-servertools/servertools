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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jst.server.generic.ui.internal.SWTUtil;
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
	private GenericServerComposite fComposite;
	private String fLastMessage = null;
	protected IWizardHandle fWizard;
	private List fPropertyControls= new ArrayList();

	private final class PathModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			String path = ((Text) e.widget).getText();
			if(!pathExist(path)){
				fLastMessage = GenericServerUIMessages.bind(GenericServerUIMessages.invalidPath,path);
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

	
	public ServerTypeDefinitionDecorator(ServerRuntime definition, Map initialProperties, String context, IWizardHandle handle) {
		super();
		fDefinition = definition;
		fProperties = initialProperties;
		fContext = context;
		fWizard = handle;
	}

	public void decorate(GenericServerComposite composite) {
		fComposite=composite;
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
    		Text path = createLabeledPath(property.getLabel(),getPropertyValue(property),parent);
    		path.setData(property);
    		registerControl(path);
     	} else if( Property.TYPE_FILE.equals(property.getType())) {
    	    Text file = createLabeledFile(property.getLabel(),getPropertyValue(property),parent);
    		file.setData(property);
    		registerControl(file);
       	} else if( Property.TYPE_TEXT.equals(property.getType())) {
    	    Text str = createLabeledText(property.getLabel(),getPropertyValue(property),parent);
    		str.setData(property);
    		registerControl(str);
       	} else if( Property.TYPE_BOOLEAN.equals(property.getType())) {
    	    Button bool =createLabeledCheck(property.getLabel(),("true".equals( getPropertyValue(property))),	parent);
    		bool.setData(property);
    		registerControl(bool);
       	}else if(Property.TYPE_SELECT.equals(property.getType())) {
       		Combo combo = createLabeledCombo(parent, property);
       		combo.setData(property);
       		registerControl(combo);
       	}
       	else  {
    	    Text defaultText= createLabeledText(property.getLabel(),getPropertyValue(property),parent);
    		defaultText.setData(property);
    		registerControl(defaultText);
    	}
    }

	private Combo createLabeledCombo(Composite defPanel, Property property) {
		
	   	GridData gridData;
    	Label label = new Label(defPanel, SWT.WRAP);
    	gridData = new GridData();
    	label.setLayoutData(gridData);
    	label.setText(property.getLabel());

		Combo combo = new Combo(defPanel,SWT.READ_ONLY);
    	gridData = new GridData(GridData.FILL_HORIZONTAL
    			| GridData.GRAB_HORIZONTAL);
    	gridData.horizontalSpan = 2;
    	combo.setLayoutData(gridData);
    	
		StringTokenizer tokenizer = new StringTokenizer(property.getDefault(),",");
		while(tokenizer.hasMoreTokens()){
			combo.add(tokenizer.nextToken());
		}
		if(combo.getItemCount()>0)
			combo.select(0);
		return combo;
	}
    private void registerControl(Control control)
    {
    	fPropertyControls.add(control);
    }
    private Button createLabeledCheck(String title, boolean value, Composite defPanel) {
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
              
            }

            public void widgetDefaultSelected(SelectionEvent e) {
  
            }
        });
    	
    	return fButton;
    }
    private Text createLabeledFile(String title, String value,final Composite defPanel) {
    	GridData gridData;
    	Label label = new Label(defPanel, SWT.WRAP);
    	gridData = new GridData();
    	label.setLayoutData(gridData);
    	label.setText(title);
    
    	final Text text = new Text(defPanel, SWT.SHADOW_IN | SWT.BORDER);
    	gridData = new GridData(GridData.FILL_HORIZONTAL
    			| GridData.GRAB_HORIZONTAL);
    	gridData.horizontalSpan = 1;
    	text.setLayoutData(gridData);
    	text.setText(value);
    	text.addModifyListener(new PathModifyListener());
    	Button fButton = SWTUtil.createButton(defPanel,GenericServerUIMessages.serverTypeGroup_label_browse);
    	
    	fButton.addSelectionListener(new SelectionListener() {
    		public void widgetSelected(SelectionEvent e) {
    			FileDialog dlg = new FileDialog(defPanel.getShell());
    			dlg.setFileName(text.getText().replace('\\','/'));
    			String res = dlg.open();
    			if (res != null) {
    				text.setText(res.replace('\\','/'));

    			}
    		}
    
    		public void widgetDefaultSelected(SelectionEvent e) {
    			widgetSelected(e);
    		}
    
    	});
    
    	return text;
    }
	
    private Text createLabeledPath(String title, String value,
    		final Composite parent) {
    	GridData gridData;
    	Label label = new Label(parent, SWT.WRAP);
    	gridData = new GridData();
    	label.setLayoutData(gridData);
    	label.setText(title);
    
    	final Text text = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
    	gridData = new GridData(GridData.FILL_HORIZONTAL);
    	gridData.horizontalSpan = 1;
    	text.setLayoutData(gridData);
    	text.setText(value);
    	text.addModifyListener(new PathModifyListener());
    	Button fButton = SWTUtil.createButton(parent,GenericServerUIMessages.serverTypeGroup_label_browse);
    	fButton.addSelectionListener(new SelectionListener() {
    		public void widgetSelected(SelectionEvent e) {
    			DirectoryDialog dlg = new DirectoryDialog(parent.getShell());
    			dlg.setFilterPath(text.getText().replace('\\','/'));
    			String res = dlg.open();
    			if (res != null) {
    				text.setText(res.replace('\\','/'));

    			}
    		}
    
    		public void widgetDefaultSelected(SelectionEvent e) {
    			widgetSelected(e);
    		}
    
    	});
    	return text;
    }
    private Text createLabeledText(String title, String value,
    		Composite defPanel) {
    	GridData gridData;
    	Label label = new Label(defPanel, SWT.WRAP);
    	gridData = new GridData();
    	label.setLayoutData(gridData);
    	label.setText(title);
    
    	Text text = new Text(defPanel, SWT.SHADOW_IN | SWT.BORDER);
    	gridData = new GridData(GridData.FILL_HORIZONTAL
    			| GridData.GRAB_HORIZONTAL);
    	gridData.horizontalSpan = 2;
    	text.setLayoutData(gridData);
    	text.setText(value);

    	return text;
    }
	private String getPropertyValue(Property property)
	{
		String value = property.getDefault();
		if(fProperties!=null && fProperties.isEmpty()==false)
			value=(String)fProperties.get(property.getId()); 
		return value;
	}	


	
   /**
    * Returns the property name/value pairs.
    * @return
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
