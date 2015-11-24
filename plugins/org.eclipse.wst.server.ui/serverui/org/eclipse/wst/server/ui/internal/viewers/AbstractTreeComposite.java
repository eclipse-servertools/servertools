/*******************************************************************************
 * Copyright (c) 2003, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * 
 */
public abstract class AbstractTreeComposite extends Composite {
	protected FilteredTree tree;
	protected TreeViewer treeViewer;
	protected Label description;
	protected Button showAdapters;
	protected Link prefLink;
	protected Button refreshButton;
	protected AbstractTreeContentProvider contentProvider;
	

	public AbstractTreeComposite(Composite parent) {
		super(parent, SWT.NONE);
		
		createWidgets();
	}

	protected void createWidgets() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 3;
		layout.verticalSpacing = 3;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		setLayout(layout);
		
		String descriptionText = getDescriptionLabel();
		if (descriptionText != null) {
			Label label = new Label(this, SWT.WRAP);
			label.setText(descriptionText);
			GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
			data.horizontalSpan = 2;
			label.setLayoutData(data);
		}
		
		String details = getDetailsLabel();
		if (details != null) {
			Composite comp = new Composite(this,  SWT.NONE);
			layout = new GridLayout();
			layout.numColumns = 3;
			comp.setLayout(layout);
			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
			data.horizontalSpan = 2;
			comp.setLayoutData(data);
			Dialog.applyDialogFont(comp);
			if (getDetailsLink()){
				showAdapters = new Button(comp,  SWT.CHECK);
				
				showAdapters.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						handleShowAdapterSelection(showAdapters.getSelection());
					}
				});
				showAdapters.setSelection(ServerUIPlugin.getPreferences().getExtAdapter());
			}
			prefLink = new Link(comp, SWT.NONE);
			prefLink.setText("<a>" + details + "</a>");
			prefLink.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					detailsSelected();
				}
			});
			if (getDetailsLink()){
				prefLink.setEnabled(!ServerUIPlugin.getPreferences().getExtAdapter());
				refreshButton = new Button(comp, SWT.PUSH);
				refreshButton.setText(Messages.refreshButton);
				refreshButton.setEnabled(ServerUIPlugin.getPreferences().getExtAdapter());
				refreshButton.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						refreshButton.setEnabled(false);
						refreshServerNode();
					}
				});
			}
		}
		
		Label label = new Label(this, SWT.WRAP);
		label.setText(getTitleLabel());
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		if (descriptionText != null && details == null)
			data.verticalIndent = 7;
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		
		createTree();
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		tree.setLayoutData(data);
		
		treeViewer = tree.getViewer();
		treeViewer.setSorter(new ViewerSorter());
		treeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection s = (IStructuredSelection) event.getSelection();
				Object element = s.getFirstElement();
				if (treeViewer.isExpandable(element))
					treeViewer.setExpandedState(element, !treeViewer.getExpandedState(element));
		    }
		});
		
		if (hasDescription()) {
			description = new Label(this, SWT.WRAP);
			description.setText("Multi\nLine\nMessage");
			Dialog.applyDialogFont(this);
			Point p = description.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			description.setText("");
			data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
			data.horizontalSpan = 1;
			if (p.y > 10)
				data.heightHint = p.y;
			else
				data.heightHint = 42;
			description.setLayoutData(data);
		}
		
		tree.forceFocus();
	}

	protected void createTree() {
		tree = new FilteredTree(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE, new ServerPatternFilter());
	}

	protected abstract String getDescriptionLabel();

	protected abstract String getTitleLabel();

	protected boolean hasDescription() {
		return true;
	}

	protected void setDescription(String text) {
		if (description != null && text != null)
			description.setText(text);
	}

	protected TreeViewer getTreeViewer() {
		return treeViewer;
	}

	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}

	public void refresh() {
		treeViewer.refresh();
	}

	public void refresh(Object obj) {
		treeViewer.refresh(obj);
	}

	public void remove(Object obj) {
		treeViewer.remove(obj);
	}

	protected String getDetailsLabel() {
		return null;
	}
	
	protected boolean getDetailsLink() {
		return false;
	}

	protected void detailsSelected() {
		// do nothing
	}

	protected void refreshServerNode(){
		// class implementing will provide the details if required
	}
	protected void downloadAdaptersSelectionChanged(boolean action) {
		ServerUIPlugin.getPreferences().setExtAdapter(action);
		Job job = new Job(Messages.jobInitializingServersView) {
			public IStatus run(final IProgressMonitor monitor) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							if (ServerUIPlugin.getPreferences().getExtAdapter()){
								handleShowAdapters(monitor);
							}
							else {
								contentProvider.fillTree();
								refresh("root");
								if (contentProvider.getInitialSelection() != null){
									treeViewer.setSelection(new StructuredSelection(contentProvider.getInitialSelection()), true);
								}
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
	
	protected void handleShowAdapterSelection(boolean selection){
		showAdapters.setSelection(selection);
		prefLink.setEnabled(!selection);
		refreshButton.setEnabled(selection);
		downloadAdaptersSelectionChanged(selection);
	}

	protected void deferInitialization() {
		Job job = new Job(Messages.jobInitializingServersView) {
			public IStatus run(final IProgressMonitor monitor) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						try {
							if (ServerUIPlugin.getPreferences().getExtAdapter()){
								handleShowAdapters(monitor);
							}
							else if (contentProvider.getInitialSelection() != null){
								treeViewer.setSelection(new StructuredSelection(contentProvider.getInitialSelection()), true);
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

	protected void handleShowAdapters(IProgressMonitor monitor){
		contentProvider.fillAdapterTree(treeViewer, monitor);
		if (contentProvider.getInitialSelection() != null && !treeViewer.getTree().isDisposed()){
			treeViewer.setSelection(new StructuredSelection(contentProvider.getInitialSelection()), true);
		}
		
	}
	
	protected void enableRefresh(){
		if ( !showAdapters.isDisposed() && showAdapters.getSelection() )
			refreshButton.setEnabled(true);
	}
	
	protected void disableRefresh(){
		refreshButton.setEnabled(false);
	}
}