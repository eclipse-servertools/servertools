/*******************************************************************************
 * Copyright (c) 2003, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.ConfigureColumns;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.view.servers.ServerTableLabelProvider;
/**
 * 
 */
public class ServerComposite extends AbstractTreeComposite implements IShellProvider {
	protected IServer selection;
	protected ServerSelectionListener listener;
	protected boolean initialSelection = true;

	protected IModule module;
	protected String launchMode;
	protected boolean includeIncompatibleVersions;

	public interface ServerSelectionListener {
		public void serverSelected(IServer server);
	}

	public ServerComposite(Composite parent, ServerSelectionListener listener2, IModule module, String launchMode) {
		super(parent);
		this.module = module;
		this.launchMode = launchMode;
		
		this.listener = listener2;
		
		Tree tree2 = treeViewer.getTree();
		tree2.setHeaderVisible(true);
		TreeColumn column = new TreeColumn(tree2, SWT.SINGLE);
		column.setText(Messages.viewServer);
		column.setWidth(400);
		
		TreeColumn column2 = new TreeColumn(tree2, SWT.SINGLE);
		column2.setText(Messages.viewState);
		column2.setWidth(100);
		
		contentProvider = new ServerTreeContentProvider(module, launchMode);
		treeViewer.setContentProvider(contentProvider);
		
		//ILabelProvider labelProvider = new ServerTreeLabelProvider();
		ILabelProvider labelProvider = new ServerTableLabelProvider();
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
		treeViewer.expandToLevel(2);
		
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

	protected void createTree() {
		tree = new FilteredTree(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION, new ServerPatternFilter(), true);
	}

	public void setIncludeIncompatibleVersions(boolean b) {
		includeIncompatibleVersions = b;
		ISelection sel = treeViewer.getSelection();
		((ServerTreeContentProvider)contentProvider).setIncludeIncompatibleVersions(b);
		treeViewer.refresh();
		treeViewer.setSelection(sel, true);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && initialSelection) {
			initialSelection = false;
			deferInitialization();
		}
	}

	public void refreshAll() {
		ISelection sel = treeViewer.getSelection();
		contentProvider = new ServerTreeContentProvider(module, launchMode);
		((ServerTreeContentProvider)contentProvider).setIncludeIncompatibleVersions(includeIncompatibleVersions);
		treeViewer.setContentProvider(contentProvider);
		treeViewer.setSelection(sel);
	}

	protected String getDescriptionLabel() {
		return null;
	}

	protected String getTitleLabel() {
		return Messages.wizNewServerSelectExisting;
	}

	public IServer getSelectedServer() {
		return selection;
	}

	public void setSelection(IServer server) {
		if (server != null)
			treeViewer.setSelection(new StructuredSelection(server), true);
		else
			treeViewer.setSelection(null);
	}

	protected void deferInitialization() {
		Job job = new Job(Messages.jobInitializingServersView) {
			public IStatus run(IProgressMonitor monitor) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							Object initial = contentProvider.getInitialSelection(module.getProject());
							if (initial != null) {
								treeViewer.setSelection(new StructuredSelection(initial), true);
							}
						} catch (Exception e) {
							// ignore - wizard has already been closed
						}
					}
				});
				return Status.OK_STATUS;
			}
		};
		
		job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule();
	}
	
	protected void createWidgets() {
		super.createWidgets();		
		
		Button columnsButton = new Button(this, SWT.PUSH);
		columnsButton.setText(Messages.actionColumns);
		final ServerComposite myClass = this;
		columnsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigureColumns.forTree(treeViewer.getTree(), myClass);
			}
		});	
		
		GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 1;
		columnsButton.setLayoutData(data);		
	}
	@Override
	public TreeViewer getTreeViewer() {
		return super.getTreeViewer();
	}
}
