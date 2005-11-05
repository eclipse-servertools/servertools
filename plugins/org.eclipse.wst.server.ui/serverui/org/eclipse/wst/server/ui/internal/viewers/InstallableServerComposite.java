/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
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

import org.eclipse.wst.server.core.internal.IInstallableServer;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * 
 */
public class InstallableServerComposite extends AbstractTreeComposite {
	protected IInstallableServer selection;
	protected InstallableServerSelectionListener listener;
	
	protected InstallableServerContentProvider contentProvider;
	protected boolean initialSelection = true;
	
	public interface InstallableServerSelectionListener {
		public void installableServerSelected(IInstallableServer server);
	}

	public InstallableServerComposite(Composite parent, int style, InstallableServerSelectionListener listener2) {
		super(parent, style);
		this.listener = listener2;
	
		contentProvider = new InstallableServerContentProvider(InstallableServerContentProvider.STYLE_VENDOR);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setLabelProvider(new InstallableServerLabelProvider());
		treeViewer.setInput(AbstractTreeContentProvider.ROOT);

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				Object obj = getSelection(event.getSelection());
				if (obj instanceof IInstallableServer) {
					selection = (IInstallableServer) obj;
					setDescription(selection.getDescription());
				} else {
					selection = null;
					setDescription("");
				}
				listener.installableServerSelected(selection);
			}
		});
		
		treeViewer.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				if (e1 instanceof IInstallableServer && !(e2 instanceof IInstallableServer))
					return 1;
				if (!(e1 instanceof IInstallableServer) && e2 instanceof IInstallableServer)
					return -1;
				if (!(e1 instanceof IInstallableServer && e2 instanceof IInstallableServer))
					return super.compare(viewer, e1, e2);
				IInstallableServer r1 = (IInstallableServer) e1;
				IInstallableServer r2 = (IInstallableServer) e2;
				return r1.getName().compareToIgnoreCase(r2.getName());
			}
		});
		treeViewer.expandToLevel(2);
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
		return Messages.installableServerCompTree;
	}

	protected String getDescriptionLabel() {
		return null;
	}

	protected String[] getComboOptions() {
		return new String[] { Messages.name,
			Messages.vendor, Messages.version };
	}

	protected void viewOptionSelected(byte option) {
		ISelection sel = treeViewer.getSelection();
		treeViewer.setContentProvider(new InstallableServerContentProvider(option));
		treeViewer.setSelection(sel);
	}

	public IInstallableServer getSelectedInstallableServer() {
		return selection;
	}
}