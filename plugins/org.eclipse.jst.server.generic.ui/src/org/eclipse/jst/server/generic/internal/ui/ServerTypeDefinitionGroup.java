package org.eclipse.jst.server.generic.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinition;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinitionProperty;
import org.eclipse.swt.SWT;
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

/**
 *  Renders a ServerTypeDefinion inside a Group. 
 *
 * @author Gorkem Ercan
 */
public class ServerTypeDefinitionGroup 
{
    public static final String CONTEXT_SERVER = ServerTypeDefinitionProperty.CONTEXT_SERVER;
    public static final String CONTEXT_RUNTIME = ServerTypeDefinitionProperty.CONTEXT_RUNTIME;

    private ServerTypeDefinition fServerTypeDefinition;
    private List fPropertyControls = new ArrayList();
    private Map fPropertyMap =new HashMap();
    private String fContext="undefined";
    private Group fDefinitionGroup;
    
    /**
     * Construct a composite for the given ServerTypeDefinition
     * 
     * @param definition
     * @param initialProperties initial values null means use default
     * @param parent
     * @param style
     */
    public ServerTypeDefinitionGroup(ServerTypeDefinition definition, String context, Map initialProperties, Composite parent, int style) 
    {
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
    public void reset(ServerTypeDefinition definition, String context, Map initialProperties)
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
    private void initServerTypeDefinition(ServerTypeDefinition definition, String context, Map initialProperties) {
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
		List properties = fServerTypeDefinition.getProperties();
		for(int i = 0; i<properties.size(); i++)
		{
		    ServerTypeDefinitionProperty property = (ServerTypeDefinitionProperty)properties.get(i);		    
//		    if(this.fContext.equals(property.getContext()))
		        createPropertyControl(definitionComposite,property);
		}
        
    }
     
    private void createPropertyControl(Composite parent, ServerTypeDefinitionProperty property)
    {
    	switch (property.getType()) {
    	case ServerTypeDefinitionProperty.TYPE_DIRECTORY :
    		Text path = createLabeledPath(property.getLabel(),getPropertyValue(property),parent);
    		path.setData(property);
    		fPropertyControls.add(path);
    		break;
    	case ServerTypeDefinitionProperty.TYPE_FILE :
    		Text file = createLabeledFile(property.getLabel(),getPropertyValue(property),parent);
    		file.setData(property);
    		fPropertyControls.add(file);
    		break;
    	case ServerTypeDefinitionProperty.TYPE_STRING :
    		Text str = createLabeledText(property.getLabel(),getPropertyValue(property),parent);
    		str.setData(property);
    		fPropertyControls.add(str);
    		break;
    	case ServerTypeDefinitionProperty.TYPE_BOOLEAN :
    		Button bool =createLabeledCheck(property.getLabel(),("true".equals( getPropertyValue(property))),	parent);
    		bool.setData(property);
    		fPropertyControls.add(bool);
    		break;
    	default :
    		Text defaultText= createLabeledText(property.getLabel(),getPropertyValue(property),parent);
    		defaultText.setData(property);
    		fPropertyControls.add(defaultText);
    		break;
    	}
    }
	private String getPropertyValue(ServerTypeDefinitionProperty property)
	{
		String value = property.getDefaultValue();
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
    	gridData = new GridData(GridData.FILL_HORIZONTAL
    			| GridData.GRAB_HORIZONTAL);
    	gridData.horizontalSpan = 1;
    	fText.setLayoutData(gridData);
    	fText.setText(value);
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
    	return fText;
    }
    public Map getProperties()
    {
    	for(int i=0; i<fPropertyControls.size();i++)
    	{
    		if(fPropertyControls.get(i)instanceof Button)
    		{
    			Button button = (Button)fPropertyControls.get(i);
    			ServerTypeDefinitionProperty prop = (ServerTypeDefinitionProperty)button.getData();
    			fPropertyMap.put(prop.getId(),Boolean.toString(button.getSelection()));
    		}
    		else
    		{
    			Text text = (Text)fPropertyControls.get(i);
    			ServerTypeDefinitionProperty prop = (ServerTypeDefinitionProperty)text.getData();
    			fPropertyMap.put(prop.getId(),text.getText());
    		}
    	}
    	return fPropertyMap;
    }
}
