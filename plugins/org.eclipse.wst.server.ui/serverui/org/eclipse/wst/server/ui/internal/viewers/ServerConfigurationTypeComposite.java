/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServerConfigurationType;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * 
 */
public class ServerConfigurationTypeComposite extends AbstractTreeComposite {
	protected IServerConfigurationType selection;
	protected ServerConfigurationTypeSelectionListener listener;
	
	public interface ServerConfigurationTypeSelectionListener {
		public void configurationTypeSelected(IServerConfigurationType type);
	}
	
	public ServerConfigurationTypeComposite(Composite parent, int style, ServerConfigurationTypeSelectionListener listener2) {
		super(parent, style);
		this.listener = listener2;
	
		treeViewer.setContentProvider(new ServerConfigurationTypeTreeContentProvider(AbstractTreeContentProvider.STYLE_FLAT));
		treeViewer.setLabelProvider(new ServerConfigurationTypeTreeLabelProvider());
		treeViewer.setInput(AbstractTreeContentProvider.ROOT);

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IServerConfigurationType) {
					selection = (IServerConfigurationType) obj;
					setDescription(selection.getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.configurationTypeSelected(selection);
			}
		});
	}
	
	protected String getDescriptionLabel() {
		return null;
	}

	protected boolean hasDescription() {
		return false;
	}

	protected String getTitleLabel() {
		return ServerUIPlugin.getResource("%wizImportConfigurationType");
	}

	protected String[] getComboOptions() {
		return new String[] { ServerUIPlugin.getResource("%name") };
			//, ServerUIPlugin.getResource("%vendor"), ServerUIPlugin.getResource("%version") }; 
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		treeViewer.setContentProvider(new ServerConfigurationTypeTreeContentProvider(option));
		treeViewer.setSelection(sel);
	}

	public IServerConfigurationType getSelectedServerConfigurationType() {
		return selection;
	}
}
