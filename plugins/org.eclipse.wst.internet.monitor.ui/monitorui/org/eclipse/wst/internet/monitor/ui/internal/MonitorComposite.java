/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.help.WorkbenchHelp;
import org.eclipse.wst.internet.monitor.core.IMonitor;
import org.eclipse.wst.internet.monitor.core.IMonitorWorkingCopy;
import org.eclipse.wst.internet.monitor.core.MonitorCore;
/**
 * 
 */
public class MonitorComposite extends Composite {
	protected Table table;
	protected TableViewer tableViewer;
	
	protected Button edit;
	protected Button remove;
	protected Button start;
	protected Button stop;
	
	protected List selection2;
	
	public MonitorComposite(Composite parent, int style) {
		super(parent, style);
		
		createWidgets();
	}
	
	protected void createWidgets() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 6;
		layout.verticalSpacing = 6;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		setLayoutData(data);
		
		Label label = new Label(this, SWT.WRAP);
		label.setText(MonitorUIPlugin.getResource("%monitorList"));
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER));
		
		label = new Label(this, SWT.NONE);

		table = new Table(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.widthHint = 300;
		WorkbenchHelp.setHelp(table, ContextIds.PREF_MONITORS);
		
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableLayout tableLayout = new TableLayout();
		
		TableColumn statusColumn = new TableColumn(table, SWT.NONE);
		statusColumn.setText(MonitorUIPlugin.getResource("%columnStatus"));
		ColumnWeightData colData = new ColumnWeightData(6, 60, true);
		tableLayout.addColumnData(colData);
		
		TableColumn remoteColumn = new TableColumn(table, SWT.NONE);
		remoteColumn.setText(MonitorUIPlugin.getResource("%columnRemote"));
		colData = new ColumnWeightData(12, 120, true);
		tableLayout.addColumnData(colData);
		
		TableColumn httpColumn = new TableColumn(table, SWT.NONE);
		httpColumn.setText(MonitorUIPlugin.getResource("%columnType"));
		colData = new ColumnWeightData(5, 55, true);
		tableLayout.addColumnData(colData);
		
		TableColumn localColumn = new TableColumn(table, SWT.NONE);
		localColumn.setText(MonitorUIPlugin.getResource("%columnLocal"));
		colData = new ColumnWeightData(5, 50, true);
		tableLayout.addColumnData(colData);
		
		table.setLayout(tableLayout);
		
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new MonitorContentProvider());
		tableViewer.setLabelProvider(new MonitorTableLabelProvider());
		tableViewer.setInput("root");
		
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setSelection(event.getSelection());
			}
		});
		
		Composite buttonComp = new Composite(this, SWT.NONE);
		layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 8;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		buttonComp.setLayout(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL);
		buttonComp.setLayoutData(data);
		
		Button add = SWTUtil.createButton(buttonComp, MonitorUIPlugin.getResource("%add"));
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MonitorDialog dialog = new MonitorDialog(getShell());
				if (dialog.open() == Window.CANCEL)
					return;
				tableViewer.refresh();
				
				IMonitor[] monitors = MonitorCore.getMonitors();
				Object monitor = monitors[monitors.length - 1];
				tableViewer.setSelection(new StructuredSelection(monitor));
			}
		});		
		
		edit = SWTUtil.createButton(buttonComp, MonitorUIPlugin.getResource("%edit"));
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IMonitor monitor = (IMonitor) getSelection().get(0);
				IMonitorWorkingCopy wc = monitor.getWorkingCopy();
				
				MonitorDialog dialog = new MonitorDialog(getShell(), wc);
				if (dialog.open() != Window.CANCEL) {
					try {
						tableViewer.refresh(wc.save());
					} catch (Exception ex) {
						// ignore
					}
				}
			}
		});
		edit.setEnabled(false);
		
		remove = SWTUtil.createButton(buttonComp, MonitorUIPlugin.getResource("%remove"));
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Iterator iterator = getSelection().iterator();
				while (iterator.hasNext()) {
					IMonitor monitor = (IMonitor) iterator.next();
					try {
						monitor.delete();
					} catch (Exception ex) {
						// ignore
					}
					tableViewer.remove(monitor);
					
					IMonitor[] monitors = MonitorCore.getMonitors();
					Object monitor2 = monitors[monitors.length - 1];
					tableViewer.setSelection(new StructuredSelection(monitor2));
				}
			}
		});
		remove.setEnabled(false);
		
		start = SWTUtil.createButton(buttonComp, MonitorUIPlugin.getResource("%start"));
		start.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Iterator iterator = getSelection().iterator();
				while (iterator.hasNext()) {
					IMonitor monitor = (IMonitor) iterator.next();
					try {
						MonitorCore.startMonitor(monitor);
					} catch (CoreException ce) {
						MessageDialog.openError(getShell(), MonitorUIPlugin.getResource("%errorDialogTitle"), ce.getStatus().getMessage());
					} catch (Exception ce) {
						MessageDialog.openError(getShell(), MonitorUIPlugin.getResource("%errorDialogTitle"), ce.getMessage());
					}
					tableViewer.refresh(monitor, true);
				}
				tableViewer.setSelection(tableViewer.getSelection());
			}
		});
		start.setEnabled(false);
		
		stop = SWTUtil.createButton(buttonComp, MonitorUIPlugin.getResource("%stop"));
		stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Iterator iterator = getSelection().iterator();
				while (iterator.hasNext()) {
					IMonitor monitor = (IMonitor) iterator.next();
					try {
						MonitorCore.stopMonitor(monitor);
					} catch (Exception ex) {
						// ignore
					}
					tableViewer.refresh(monitor, true);
				}
				tableViewer.setSelection(tableViewer.getSelection());
			}
		});
		stop.setEnabled(false);
	}

	protected List getSelection() {
		return selection2;
	}

	protected void setSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		Iterator iterator = sel.iterator();
		selection2 = new ArrayList();
		
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof IMonitor)
				selection2.add(obj);
		}
		
		if (!selection2.isEmpty()) {
			remove.setEnabled(true);
			
			boolean allStopped = true;
			boolean allStarted = true;
			
			iterator = selection2.iterator();
			while (iterator.hasNext()) {
				IMonitor monitor = (IMonitor) iterator.next();
				if (monitor.isRunning())
					allStopped = false;
				else
					allStarted = false;
			}
			start.setEnabled(allStopped);
			stop.setEnabled(allStarted);
			edit.setEnabled(selection2.size() == 1 && allStopped);
		} else {
			edit.setEnabled(false);
			remove.setEnabled(false);
			start.setEnabled(false);
			stop.setEnabled(false);
		}
	}
}