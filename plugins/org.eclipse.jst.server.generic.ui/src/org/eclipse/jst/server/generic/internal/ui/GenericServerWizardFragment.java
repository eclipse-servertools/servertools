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
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
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


import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.jst.server.generic.internal.core.GenericServerRuntime;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * A serverDefinitionType aware wizard for defining runtimes.
 * 
 * @author Gorkem Ercan
 */
public class GenericServerWizardFragment extends WizardFragment {
	
	private Group selectionBar;
	private Combo fServerCombo;
	private Composite propertyBody;
	private Composite fContent;
	private ServerRuntimePropertyComposite fServerPanel;
	private IRuntimeWorkingCopy fRuntimeWC;
	
	/**
	 * 
	 */
	public GenericServerWizardFragment() {
		super();
	}
	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.ui.wizard.IWizardFragment#isComplete()
	 */
	public boolean isComplete() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(ITaskModel.TASK_RUNTIME);
		if (runtime == null)
			return false;
		IStatus status = runtime.validate();
		return (status != null && status.isOK());
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.wizard.IWizardFragment#createComposite(org.eclipse.swt.widgets.Composite, org.eclipse.wst.server.ui.wizard.IWizardHandle)
	 */
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		
		fRuntimeWC=null;
		fContent = createContainer(parent);
		createSelectionBar(fContent);
		createBody(fContent);
		return fContent;
	}

	
	private void swapBody(Composite content) {
		content.layout(true);
		if(propertyBody!=null)
		{
			propertyBody.dispose();
			propertyBody=null;
		}
		propertyBody = new Composite(content, SWT.NONE);
		propertyBody.setLayout(new GridLayout(1,true));
		propertyBody.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_VERTICAL));
		String selected = fServerCombo.getItem(fServerCombo.getSelectionIndex());
		if(getServerDefinitionId()!=null&& getServerDefinitionId().equals(selected))
		{
			fServerPanel = new ServerRuntimePropertyComposite(getRuntimeWorkingCopy(),propertyBody,SWT.NONE); 
		}
		else
		{	
			fServerPanel = new ServerRuntimePropertyComposite(selected,propertyBody,SWT.NONE);
		}
		propertyBody.layout(true);
		propertyBody.setRedraw(true);
		content.redraw();
		content.pack(true);

	}

	
	private void createBody(Composite content) {
//		ScrolledComposite scrolls = new ScrolledComposite(content, SWT.H_SCROLL | SWT.V_SCROLL | SWT.SHADOW_ETCHED_IN);
//		scrolls.setAlwaysShowScrollBars(true);
//		scrolls.setLayoutData(new GridData(GridData.FILL_BOTH));
//		scrolls.setLayout(new GridLayout(1,false));
//		Composite c= new Composite(scrolls,SWT.NONE);
//		scrolls.setContent(c);
//		c.setLayoutData(new GridData(GridData.FILL_BOTH));
//		c.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED));
//	
//		Button b = new Button(c,SWT.CHECK);
//		b.setText("My marvelous button");
//		scrolls.setMinSize(c.computeSize(SWT.DEFAULT,SWT.DEFAULT));
		
		propertyBody = new Composite(content, SWT.NONE);
		propertyBody.setLayout(new GridLayout(1,true));
		propertyBody.setLayoutData(new GridData(GridData.FILL_BOTH));
		if(getServerDefinitionId()==null)
		{
			String selected = fServerCombo.getItem(fServerCombo.getSelectionIndex());
			fServerPanel = new ServerRuntimePropertyComposite(selected,propertyBody,SWT.NONE);
		}
		else
		{	
			fServerPanel = new ServerRuntimePropertyComposite(getRuntimeWorkingCopy(),propertyBody,SWT.NONE);
		}
		propertyBody.layout(true);
		propertyBody.setRedraw(true);
		content.redraw();
		content.layout(true);
	}
	
	private void createSelectionBar(Composite content) {
		selectionBar = new Group(content, SWT.NONE);
		selectionBar.setLayout(new GridLayout(2,false));
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		selectionBar.setLayoutData(gridData);
		Label label = new Label(selectionBar, SWT.NONE);
		label.setText("Server types:");
		label.setLayoutData(new GridData());
		fServerCombo = new Combo(selectionBar, SWT.BORDER |SWT.READ_ONLY);
		ServerTypeDefinition[] servers = CorePlugin.getDefault().getServerTypeDefinitionManager().getServerTypeDefinitions();
		for(int i=0; i<servers.length; i++){
			fServerCombo.add(servers[i].getName());
		}
		fServerCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		if(fServerCombo.getItemCount()>0)
			fServerCombo.select(0);
		
		fServerCombo.addSelectionListener(
				new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						
						swapBody(fContent);

					}

					public void widgetDefaultSelected(SelectionEvent e) {}
				}
			);
	}

	
	private String getServerDefinitionId()
	{
		String currentDefinition= null;
		if(getRuntimeWorkingCopy()!=null)
			currentDefinition =  getRuntimeWorkingCopy().getAttribute(GenericServerRuntime.SERVER_DEFINITION_ID,"");
		if(currentDefinition!= null && currentDefinition.length()>0)
		{	
			return currentDefinition;
		}
		return null;
	}
	
	private void selectServerDefinition()
	{
		String currentDefinition = getServerDefinitionId();
		int selectIndex = 0;
		if(currentDefinition != null)
		{	
			selectIndex =  fServerCombo.indexOf(currentDefinition);
			if(selectIndex<0)
				selectIndex =0;
		}
		fServerCombo.select(selectIndex);
	}
	
	
	private Composite createContainer(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();

		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;
		layout.verticalSpacing=0;
		container.setLayout(layout);
		GridData gridData =
			new GridData(
				GridData.FILL_BOTH
					| GridData.GRAB_VERTICAL
					| GridData.GRAB_HORIZONTAL);
		container.setLayoutData(gridData);
		return container;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.wizard.IWizardFragment#enter()
	 */
	public void enter() {
		String serverDefinition = getServerDefinitionId();
		if(serverDefinition!=null && serverDefinition.length()>0)
		{
			selectServerDefinition();
			swapBody(fContent);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.ui.wizard.IWizardFragment#exit()
	 */
	public void exit() {
		String selected = fServerCombo.getItem(fServerCombo.getSelectionIndex());
		Map properties = fServerPanel.getProperties();
		IRuntimeWorkingCopy wc = getRuntimeWorkingCopy();
		wc.setAttribute(GenericServerRuntime.SERVER_DEFINITION_ID, selected);
		wc.setAttribute(GenericServerRuntime.SERVER_INSTANCE_PROPERTIES,properties);
		fRuntimeWC=null;
	}
	
	
	private IRuntimeWorkingCopy getRuntimeWorkingCopy()
	{
		if(fRuntimeWC == null)
			fRuntimeWC = (IRuntimeWorkingCopy)getTaskModel().getObject(ITaskModel.TASK_RUNTIME); 
		return fRuntimeWC;
	}
	
	public boolean hasComposite() {
		return true;
	}
}
