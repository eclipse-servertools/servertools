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

import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.extension.ExtensionUtility;
/**
 * 
 */
public class ServerTypeComposite extends AbstractTreeComposite {
	protected IServerType selection;
	protected ServerTypeSelectionListener listener;
	protected ServerTypeTreeContentProvider contentProvider;
	protected boolean initialSelection = true;
	
	protected IModuleType moduleType;
	
	protected boolean isLocalhost;
	protected boolean includeIncompatibleVersions;
	
	public interface ServerTypeSelectionListener {
		public void serverTypeSelected(IServerType type);
	}
	
	public ServerTypeComposite(Composite parent, int style, IModuleType moduleType, ServerTypeSelectionListener listener2) {
		super(parent, style);
		this.listener = listener2;
		
		this.moduleType = moduleType;
		
		contentProvider = new ServerTypeTreeContentProvider(ServerTypeTreeContentProvider.STYLE_VENDOR, moduleType);
		treeViewer.setContentProvider(contentProvider);
		
		ILabelProvider labelProvider = new ServerTypeTreeLabelProvider();
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

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IServerType) {
					selection = (IServerType) obj;
					setDescription(selection.getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.serverTypeSelected(selection);
			}
		});
		
		treeViewer.setSorter(new DefaultViewerSorter());
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && initialSelection) {
			initialSelection = false;
			if (contentProvider.getInitialSelection() != null)
				treeViewer.setSelection(new StructuredSelection(contentProvider.getInitialSelection()), true);
		}
	}

	public boolean setHost(boolean newHost) {
		if (isLocalhost == newHost)
			return false;
		
		isLocalhost = newHost;
		ISelection sel = treeViewer.getSelection();
		contentProvider.setLocalhost(isLocalhost);
		treeViewer.refresh();
		//treeViewer.expandToLevel(2);
		treeViewer.setSelection(sel, true);
		return true;
	}

	public void setIncludeIncompatibleVersions(boolean b) {
		includeIncompatibleVersions = b;
		ISelection sel = treeViewer.getSelection();
		contentProvider.setIncludeIncompatibleVersions(b);
		treeViewer.refresh();
		treeViewer.setSelection(sel, true);
	}

	protected String getDescriptionLabel() {
		return null;
	}

	protected String getTitleLabel() {
		return Messages.serverTypeCompLabel;
	}

	protected String[] getComboOptions() {
		return new String[] { Messages.name,
			Messages.vendor, Messages.version,
			Messages.moduleSupport };
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		contentProvider = new ServerTypeTreeContentProvider(option, moduleType);
		contentProvider.setLocalhost(isLocalhost);
		contentProvider.setIncludeIncompatibleVersions(includeIncompatibleVersions);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSelection(sel);
	}

	public IServerType getSelectedServerType() {
		return selection;
	}

	public void refresh() {
		ISelection sel = treeViewer.getSelection();
		ServerTypeTreeContentProvider cp = (ServerTypeTreeContentProvider) treeViewer.getContentProvider();
		treeViewer.setContentProvider(new ServerTypeTreeContentProvider(cp.style, moduleType));
		treeViewer.setSelection(sel);
	}

	protected String getDetailsLabel() {
		if (ServerPlugin.getInstallableServers().length > 0)
			return Messages.installableServerLink;
		return null;
	}

	protected void detailsSelected() {
		if (ExtensionUtility.launchExtensionWizard(getShell(), Messages.wizNewInstallableServerTitle,
				Messages.wizNewInstallableServerDescription))
			refresh();
	}
}