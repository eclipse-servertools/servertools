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
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.jst.server.generic.internal.core.GenericServerRuntime;
import org.eclipse.jst.server.generic.internal.core.ServerTypeDefinitionManager;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinition;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinitionProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.ui.internal.SWTUtil;
/**
 * A composite that renders ServerTypeDefinition. 
 * 
 * @author Gorkem Ercan
 */
public class ServerRuntimePropertyComposite extends Composite
{
	private ServerTypeDefinition fServerDefinition;
	private IRuntimeWorkingCopy fRuntime;
	private Map fPropertyMap =new HashMap(); 
	private List propertyControls = new ArrayList();
	
	public ServerRuntimePropertyComposite(String serverDefinitionId, Composite parent, int style)
	{
		super(parent,style);
		this.fServerDefinition = getServerTypeDefinitionManager().getServerRuntimeDefinition(serverDefinitionId);
		createControl(parent);
	}

	public ServerRuntimePropertyComposite(IRuntimeWorkingCopy runtime, Composite parent, int style)
	{
		super(parent,style);
		this.fRuntime = runtime;
		this.fServerDefinition = getServerTypeDefinitionManager().getServerRuntimeDefinition(runtime.getAttribute(GenericServerRuntime.SERVER_DEFINITION_ID,""));
		fPropertyMap = runtime.getAttribute(GenericServerRuntime.SERVER_INSTANCE_PROPERTIES,new HashMap());
		createControl(parent);
	}
	
	private ServerTypeDefinitionManager getServerTypeDefinitionManager()
	{
		return CorePlugin.getDefault().getServerTypeDefinitionManager();
	}
	

	

	/**
	 * 
	 */
	private void createControl(Composite parent) {
	
		Group defPanel = new Group(parent, SWT.NONE);
		defPanel.setText(fServerDefinition.getName());
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		defPanel.setLayout(layout);
//		gridData.widthHint = convertWidthInCharsToPixels(100);
		defPanel.setLayoutData(new GridData(GridData.FILL_BOTH	| GridData.GRAB_VERTICAL| GridData.GRAB_HORIZONTAL));
		createPropertyControls(defPanel);
		defPanel.layout(true);
		defPanel.redraw();
		defPanel.pack(true);
		
//		parent.getShell().setSize(defPanel.getSize());
//		parent.setSize(parent.computeSize(SWT.DEFAULT, SWT.DEFAULT));
//		parent.layout(true);
		
		
	}

	public Map getProperties()
	{
		for(int i=0; i<propertyControls.size();i++)
		{
			if(propertyControls.get(i)instanceof Button)
			{
				Button button = (Button)propertyControls.get(i);
				ServerTypeDefinitionProperty prop = (ServerTypeDefinitionProperty)button.getData();
				fPropertyMap.put(prop.getId(),Boolean.toString(button.getSelection()));
			}
			else
			{
				Text text = (Text)propertyControls.get(i);
				ServerTypeDefinitionProperty prop = (ServerTypeDefinitionProperty)text.getData();
				fPropertyMap.put(prop.getId(),text.getText());
			}
		}
		return fPropertyMap;
	}
	
	private void createPropertyControls(Composite parent)
	{
		List properties = fServerDefinition.getProperties();
		for(int i = 0; i<properties.size(); i++)
		{
			createPropertyControl(parent,(ServerTypeDefinitionProperty)properties.get(i));
		}
	}
	
	private void createPropertyControl(Composite parent, ServerTypeDefinitionProperty property)
	{
		switch (property.getType()) {
		case ServerTypeDefinitionProperty.TYPE_DIRECTORY :
			Text path = createLabeledPath(property.getLabel(),getPropertyValue(property),parent);
			path.setData(property);
			propertyControls.add(path);
			break;
		case ServerTypeDefinitionProperty.TYPE_FILE :
			Text file = createLabeledFile(property.getLabel(),getPropertyValue(property),parent);
			file.setData(property);
			propertyControls.add(file);
			break;
		case ServerTypeDefinitionProperty.TYPE_STRING :
			Text str = createLabeledText(property.getLabel(),getPropertyValue(property),parent);
			str.setData(property);
			propertyControls.add(str);
			break;
		case ServerTypeDefinitionProperty.TYPE_BOOLEAN :
			Button bool =createLabeledCheck(property.getLabel(),("true".equals( getPropertyValue(property))),	parent);
			bool.setData(property);
			propertyControls.add(bool);
			break;
		default :
			Text defaultText= createLabeledText(property.getLabel(),getPropertyValue(property),parent);
			defaultText.setData(property);
			propertyControls.add(defaultText);
			break;
		}
	}
	private String getPropertyValue(ServerTypeDefinitionProperty property)
	{
		String value = (String)fPropertyMap.get(property.getId()); 
		if(value==null)
			value=property.getDefaultValue();
		return value;
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

	protected Text createLabeledPath(String title, String value,
			Composite parent) {
		GridData gridData;
		Label label = new Label(parent, SWT.WRAP);
		gridData = new GridData();
		//gridData.horizontalSpan = 1;
		label.setLayoutData(gridData);
		label.setText(title);

		final Text fText = new Text(parent, SWT.SHADOW_IN | SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		gridData.horizontalSpan = 1;
		fText.setLayoutData(gridData);
		fText.setText(value);
		final String fpath = value;
		Button fButton = new Button(parent, SWT.PUSH);
		fButton.setText("...");
		fButton.setLayoutData(new GridData());
		fButton.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(getShell());
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

	
	protected Text createLabeledFile(String title, String value,
			Composite defPanel) {
		GridData gridData;
		Label label = new Label(defPanel, SWT.WRAP);
		gridData = new GridData();
		//gridData.horizontalSpan = 1;
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
				FileDialog dlg = new FileDialog(getShell());
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




}
