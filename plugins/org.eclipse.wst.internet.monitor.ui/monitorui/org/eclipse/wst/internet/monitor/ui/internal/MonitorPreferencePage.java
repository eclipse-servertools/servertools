/*******************************************************************************
 * Copyright (c) 2003, 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *     Tianchao Li (Tianchao.Li@gmail.com) - Start monitors by default 
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.ConfigureColumns;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitor;
import org.eclipse.wst.internet.monitor.core.internal.provisional.IMonitorWorkingCopy;
import org.eclipse.wst.internet.monitor.core.internal.provisional.MonitorCore;
/**
 * The preference page that holds monitor properties.
 */
public class MonitorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IShellProvider {
	protected Button displayButton;
	
	protected Table table;
	protected TableViewer tableViewer;
	
	protected Button edit;
	protected Button remove;
	protected Button start;
	protected Button stop;
	protected Button columns;
	
	protected List<Object> selection2;

	/**
	 * MonitorPreferencePage constructor comment.
	 */
	public MonitorPreferencePage() {
		super();
		noDefaultAndApplyButton();
	}

	/**
	 * Create the preference options.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(3);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.PREF);
				
		Text description = new Text(composite, SWT.READ_ONLY);
		description.setText(Messages.preferenceDescription);
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		description.setLayoutData(data);
	
		displayButton = new Button(composite, SWT.CHECK);
		displayButton.setText(Messages.prefShowView);
		displayButton.setSelection(MonitorUIPlugin.getShowOnActivityPreference());
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		data.verticalIndent = 8;
		displayButton.setLayoutData(data);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(displayButton, ContextIds.PREF_SHOW);
		
		Label label = new Label(composite, SWT.WRAP);
		label.setText(Messages.monitorList);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.verticalIndent = 8;
		label.setLayoutData(data);
		
		table = new Table(composite, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 350;
		table.setLayoutData(data);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(table, ContextIds.PREF_MONITORS);
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableLayout tableLayout = new TableLayout();
		
		TableColumn statusColumn = new TableColumn(table, SWT.NONE);
		statusColumn.setText(Messages.columnStatus);
		ColumnWeightData colData = new ColumnWeightData(6, 60, true);
		tableLayout.addColumnData(colData);
		
		TableColumn remoteColumn = new TableColumn(table, SWT.NONE);
		remoteColumn.setText(Messages.columnRemote);
		colData = new ColumnWeightData(12, 120, true);
		tableLayout.addColumnData(colData);
		
		TableColumn httpColumn = new TableColumn(table, SWT.NONE);
		httpColumn.setText(Messages.columnType);
		colData = new ColumnWeightData(5, 50, true);
		tableLayout.addColumnData(colData);
		
		TableColumn localColumn = new TableColumn(table, SWT.NONE);
		localColumn.setText(Messages.columnLocal);
		colData = new ColumnWeightData(6, 60, true);
		tableLayout.addColumnData(colData);
		
		TableColumn startOnStartupColumn = new TableColumn(table, SWT.NONE);
		startOnStartupColumn.setText(Messages.columnAutoStart);
		colData = new ColumnWeightData(7, 70, true);
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
		
		Composite buttonComp = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = convertVerticalDLUsToPixels(3);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		buttonComp.setLayout(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL);
		buttonComp.setLayoutData(data);
		
		Button add = SWTUtil.createButton(buttonComp, Messages.add);
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
		
		edit = SWTUtil.createButton(buttonComp, Messages.edit);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IMonitor monitor = (IMonitor) getSelection().get(0);
				IMonitorWorkingCopy wc = monitor.createWorkingCopy();
				
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
		
		remove = SWTUtil.createButton(buttonComp, Messages.remove);
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
					if (monitors.length > 0) {
						Object monitor2 = monitors[monitors.length - 1];
						tableViewer.setSelection(new StructuredSelection(monitor2));
					}
				}
			}
		});
		remove.setEnabled(false);
		
		start = SWTUtil.createButton(buttonComp, Messages.start);
		data = (GridData) start.getLayoutData();
		data.verticalIndent = 9;
		start.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Iterator iterator = getSelection().iterator();
				while (iterator.hasNext()) {
					IMonitor monitor = (IMonitor) iterator.next();
					try {
						monitor.start();
					} catch (CoreException ce) {
						MessageDialog.openError(getShell(), Messages.errorDialogTitle, ce.getStatus().getMessage());
					} catch (Exception ce) {
						MessageDialog.openError(getShell(), Messages.errorDialogTitle, ce.getMessage());
					}
					tableViewer.refresh(monitor, true);
				}
				tableViewer.setSelection(tableViewer.getSelection());
			}
		});
		start.setEnabled(false);
		
		stop = SWTUtil.createButton(buttonComp, Messages.stop);
		stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Iterator iterator = getSelection().iterator();
				while (iterator.hasNext()) {
					IMonitor monitor = (IMonitor) iterator.next();
					try {
						monitor.stop();
					} catch (Exception ex) {
						// ignore
					}
					tableViewer.refresh(monitor, true);
				}
				tableViewer.setSelection(tableViewer.getSelection());
			}
		});
		stop.setEnabled(false);
		
		columns = SWTUtil.createButton(buttonComp, Messages.columns);
		data = (GridData) columns.getLayoutData();
		data.verticalIndent = 9;
		
		final MonitorPreferencePage thisClass = this;
		columns.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ConfigureColumns.forTable(tableViewer.getTable(), thisClass);
			}
		});
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}

	/**
	 * Initializes this preference page using the passed workbench.
	 *
	 * @param workbench the current workbench
	 */
	public void init(IWorkbench workbench) {
		// do nothing
	}

	/**
	 * Performs special processing when this page's Defaults button has been pressed.
	 * <p>
	 * This is a framework hook method for subclasses to do special things when
	 * the Defaults button has been pressed.
	 * Subclasses may override, but should call <code>super.performDefaults</code>.
	 * </p>
	 */
	protected void performDefaults() {
		displayButton.setSelection(MonitorUIPlugin.getDefaultShowOnActivityPreference());
		super.performDefaults();
	}

	/** 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		MonitorUIPlugin.setShowOnActivityPreference(displayButton.getSelection());
		MonitorUIPlugin.getInstance().savePluginPreferences();
		return true;
	}
	
	protected List getSelection() {
		return selection2;
	}

	protected void setSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		Iterator iterator = sel.iterator();
		selection2 = new ArrayList<Object>();
		
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
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), ContextIds.PREF);
	}
}