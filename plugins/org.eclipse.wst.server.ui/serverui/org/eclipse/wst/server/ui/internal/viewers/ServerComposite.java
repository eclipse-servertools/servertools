/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * 
 */
public class ServerComposite extends AbstractTreeComposite {
	protected IServer selection;
	protected ServerSelectionListener listener;
	protected ServerTreeContentProvider contentProvider;
	protected boolean initialSelection = true;
	protected byte viewOption;
	
	protected IModule module;
	protected String launchMode;
	protected boolean includeIncompatibleVersions;
	
	public interface ServerSelectionListener {
		public void serverSelected(IServer server);
	}
	
	public ServerComposite(Composite parent, int style, ServerSelectionListener listener2, IModule module, String launchMode) {
		super(parent, style);
		this.module = module;
		this.launchMode = launchMode;
		
		this.listener = listener2;
		
		contentProvider = new ServerTreeContentProvider(ServerTreeContentProvider.STYLE_HOST, module, launchMode);
		viewOption = ServerTreeContentProvider.STYLE_HOST;
		treeViewer.setContentProvider(contentProvider);
		
		ILabelProvider labelProvider = new ServerTreeLabelProvider();
		labelProvider.addListener(new ILabelProviderListener() {
			public void labelProviderChanged(LabelProviderChangedEvent event) {
				Object[] obj = event.getElements();
				if (obj == null)
					treeViewer.refresh(true);
				else {
					int size = obj.length;
					for (int i = 0; i < size; i++)
						treeViewer.refresh(obj[i], true);
				}
			}
		});
		treeViewer.setLabelProvider(labelProvider);
		treeViewer.setInput(AbstractTreeContentProvider.ROOT);
		treeViewer.expandToLevel(1);

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IServer) {
					selection = (IServer) obj;
					setDescription(selection.getServerType().getRuntimeType().getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.serverSelected(selection);
			}
		});
	}

	public ServerComposite(Composite parent, int style, ServerSelectionListener listener2) {
		this(parent, style, listener2, null, null);
	}
	
	public void setIncludeIncompatibleVersions(boolean b) {
		includeIncompatibleVersions = b;
		ISelection sel = treeViewer.getSelection();
		contentProvider.setIncludeIncompatibleVersions(b);
		treeViewer.refresh();
		treeViewer.setSelection(sel, true);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && initialSelection) {
			initialSelection = false;
			if (contentProvider.getInitialSelection() != null)
				treeViewer.setSelection(new StructuredSelection(contentProvider.getInitialSelection()), true);
		}
	}

	public void refreshAll() {
		ISelection sel = treeViewer.getSelection();
		contentProvider = new ServerTreeContentProvider(viewOption, module, launchMode);
		contentProvider.setIncludeIncompatibleVersions(includeIncompatibleVersions);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSelection(sel);
	}

	protected String getDescriptionLabel() {
		return null; //Messages.serverTypeCompDescription");
	}
	
	protected String getTitleLabel() {
		return Messages.wizNewServerSelectExisting;
	}

	protected String[] getComboOptions() {
		return new String[] { Messages.name, Messages.host, 
			Messages.vendor, Messages.version };
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		viewOption = option;
		contentProvider = new ServerTreeContentProvider(option, module, launchMode);
		contentProvider.setIncludeIncompatibleVersions(includeIncompatibleVersions);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSelection(sel);
	}
	
	public IServer getSelectedServer() {
		return selection;
	}
	
	public void setSelection(IServer server) {
		treeViewer.setSelection(new StructuredSelection(server), true);
	}
}