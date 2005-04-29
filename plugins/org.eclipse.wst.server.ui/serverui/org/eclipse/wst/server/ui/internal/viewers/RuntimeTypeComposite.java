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

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.ui.internal.Messages;
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
	
	public RuntimeTypeComposite(Composite parent, int style, boolean creation, RuntimeTypeSelectionListener listener2, String type, String version, String runtimeTypeId) {
		super(parent, style);
		this.listener = listener2;
		this.creation = creation;
		this.type = type;
		this.version = version;
		this.runtimeTypeId = runtimeTypeId;
	
		contentProvider = new RuntimeTypeTreeContentProvider(RuntimeTypeTreeContentProvider.STYLE_VENDOR, creation, type, version, runtimeTypeId);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new RuntimeTypeTreeLabelProvider());
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
		
		treeViewer.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof IRuntimeType && !(e2 instanceof IRuntimeType))
					return 1;
				if (!(e1 instanceof IRuntimeType) && e2 instanceof IRuntimeType)
					return -1;
				if (!(e1 instanceof IRuntimeType && e2 instanceof IRuntimeType))
					return super.compare(viewer, e1, e2);
				IRuntimeType r1 = (IRuntimeType) e1;
				IRuntimeType r2 = (IRuntimeType) e2;
				return r1.getName().compareToIgnoreCase(r2.getName());
			}
		});
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
		return Messages.runtimeTypeCompDescription;
	}

	protected String[] getComboOptions() {
		return new String[] { Messages.name,
			Messages.vendor, Messages.version,
			Messages.moduleSupport };
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		treeViewer.setContentProvider(new RuntimeTypeTreeContentProvider(option, creation, type, version, runtimeTypeId));
		treeViewer.setSelection(sel);
	}

	public IRuntimeType getSelectedRuntimeType() {
		return selection;
	}
}