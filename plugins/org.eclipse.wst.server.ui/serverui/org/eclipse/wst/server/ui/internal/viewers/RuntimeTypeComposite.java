/*******************************************************************************
 * Copyright (c) 2003, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.discovery.Discovery;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * 
 */
public class RuntimeTypeComposite extends AbstractTreeComposite {
	protected IRuntimeType selection;
	protected RuntimeTypeSelectionListener listener;
	protected boolean creation;
	protected String type;
	protected String version;
	protected String runtimeTypeId;

	protected RuntimeTypeTreeContentProvider contentProvider;
	protected boolean initialSelection = true;

	public interface RuntimeTypeSelectionListener {
		public void runtimeTypeSelected(IRuntimeType runtimeType);
	}

	public RuntimeTypeComposite(Composite parent, boolean creation, RuntimeTypeSelectionListener listener2, String type, String version, String runtimeTypeId) {
		super(parent);
		this.listener = listener2;
		this.creation = creation;
		this.type = type;
		this.version = version;
		this.runtimeTypeId = runtimeTypeId;
		
		contentProvider = new RuntimeTypeTreeContentProvider(creation, type, version, runtimeTypeId);
		treeViewer.setContentProvider(contentProvider);
		
		ILabelProvider labelProvider = new RuntimeTypeTreeLabelProvider();
		labelProvider.addListener(new ILabelProviderListener() {
			public void labelProviderChanged(LabelProviderChangedEvent event) {
				Object[] obj = event.getElements();
				if (obj == null)
					treeViewer.refresh(true);
				else {
					obj = ServerUIPlugin.adaptLabelChangeObjects(obj);
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
				if (obj instanceof IRuntimeType) {
					selection = (IRuntimeType) obj;
					setDescription(selection.getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.runtimeTypeSelected(selection);
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

	protected String getTitleLabel() {
		return Messages.runtimeTypeCompTree;
	}

	protected String getDescriptionLabel() {
		return null;
	}

	public void refresh() {
		ISelection sel = treeViewer.getSelection();
		treeViewer.setContentProvider(new RuntimeTypeTreeContentProvider(creation, type, version, runtimeTypeId));
		treeViewer.setSelection(sel);
	}

	public IRuntimeType getSelectedRuntimeType() {
		return selection;
	}

	protected String getDetailsLabel() {
		return Messages.installableServerLink;
	}

	private void closeWizard(Composite comp) {
		if (comp == null || comp.isDisposed())
			return;
		
		Composite c = comp.getParent();
		if (c instanceof Shell) {
			Shell s = (Shell) c;
			s.close();
		}
		closeWizard(c);
	}

	protected void detailsSelected() {
		if (Discovery.launchExtensionWizard(getShell(), Messages.wizNewInstallableServerTitle,
				Messages.wizNewInstallableServerDescription)) {
			//refresh();
			closeWizard(this);
		}
	}
}