/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.*;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.wst.server.core.IMonitoredServerPort;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerMonitorManager;
import org.eclipse.wst.server.core.IServerPort;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.internal.EclipseUtil;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * 
 */
public class MonitorComposite extends Composite {
	//protected IServerPort selection;
	protected PortSelectionListener listener;
	protected IServer server;
	
	protected IServerMonitorManager smm;
	
	protected Table monitorTable;
	protected TableViewer monitorTableViewer;
	
	public interface PortSelectionListener {
		public void portSelected(IServerPort port);
	}
	
	public MonitorComposite(Composite parent, int style, PortSelectionListener listener2, IServer server) {
		super(parent, style);
		this.listener = listener2;
		this.server = server;
		
		smm = ServerCore.getServerMonitorManager();
		
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		setLayout(layout);

		GridData data = new GridData(GridData.FILL_BOTH);
		setLayoutData(data);
		
		monitorTable = new Table(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		//data.horizontalSpan = 2;
		data.heightHint = 150;
		//data.widthHint = 340;
		monitorTable.setLayoutData(data);
		monitorTable.setLinesVisible(true);
		monitorTableViewer = new TableViewer(monitorTable);
		
		TableLayout tableLayout = new TableLayout();
		monitorTable.setLayout(tableLayout);
		monitorTable.setHeaderVisible(true);
		
		tableLayout.addColumnData(new ColumnWeightData(8, 80, true));
		TableColumn col = new TableColumn(monitorTable, SWT.NONE);
		col.setText(ServerUIPlugin.getResource("%dialogMonitorColumnStatus"));
		
		tableLayout.addColumnData(new ColumnWeightData(12, 120, true));
		col = new TableColumn(monitorTable, SWT.NONE);
		col.setText(ServerUIPlugin.getResource("%dialogMonitorColumnType"));
		
		tableLayout.addColumnData(new ColumnWeightData(8, 80, true));
		col = new TableColumn(monitorTable, SWT.NONE);
		col.setText(ServerUIPlugin.getResource("%dialogMonitorColumnPort"));
		
		tableLayout.addColumnData(new ColumnWeightData(8, 80, true));
		col = new TableColumn(monitorTable, SWT.NONE);
		col.setText(ServerUIPlugin.getResource("%dialogMonitorColumnMonitorPort"));
		
		tableLayout.addColumnData(new ColumnWeightData(8, 80, true));
		col = new TableColumn(monitorTable, SWT.NONE);
		col.setText(ServerUIPlugin.getResource("%dialogMonitorColumnContentType"));
		
		monitorTableViewer.setContentProvider(new MonitorContentProvider(server));
		monitorTableViewer.setLabelProvider(new MonitorLabelProvider(server));
		monitorTableViewer.setInput(AbstractTreeContentProvider.ROOT);
		
		monitorTableViewer.setSorter(new ViewerSorter() {
			public int compare(Viewer viewer, Object e1, Object e2) {
				IMonitoredServerPort port1 = (IMonitoredServerPort) e1;
				IMonitoredServerPort port2 = (IMonitoredServerPort) e2;
				if (port1.getServerPort().getPort() == port2.getServerPort().getPort()) {
					if (port1.getMonitorPort() == port2.getMonitorPort()) {
						return 0;
					} else if (port1.getMonitorPort() > port2.getMonitorPort())
						return 1;
					else
						return -1;
				} else if (port1.getServerPort().getPort() > port2.getServerPort().getPort())
					return 1;
				else
					return -1;
			}
		});
		
		Composite buttonComp = new Composite(this, SWT.NONE);
		buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));
		layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		buttonComp.setLayout(layout);
		
		final IServer server2 = server;
		Button add = SWTUtil.createButton(buttonComp, ServerUIPlugin.getResource("%add"));
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MonitorDialog dialog = new MonitorDialog(getShell(), server2);
				if (dialog.open() != Window.CANCEL) {
					IServerPort port = dialog.getServerPort();
					IMonitoredServerPort sp = smm.createMonitor(server2, port, dialog.getMonitorPort(), dialog.getContentTypes());
					if (sp != null)
						monitorTableViewer.add(sp);
				}
			}
		});
		
		final Button edit = SWTUtil.createButton(buttonComp, ServerUIPlugin.getResource("%edit"));
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) monitorTableViewer.getSelection();
				IMonitoredServerPort port = (IMonitoredServerPort) sel.getFirstElement();
				MonitorDialog dialog = new MonitorDialog(getShell(), server2, port.getServerPort(), port.getMonitorPort(), port.getContentTypes());
				if (dialog.open() != Window.CANCEL) {
					smm.removeMonitor(port);
					monitorTableViewer.remove(port);
					port = smm.createMonitor(server2, dialog.getServerPort(), dialog.getMonitorPort(), dialog.getContentTypes());
					if (port != null) {
						monitorTableViewer.add(port);
						monitorTableViewer.setSelection(new StructuredSelection(port));
					}
				}
			}
		});
		edit.setEnabled(false);
		
		final Button remove = SWTUtil.createButton(buttonComp, ServerUIPlugin.getResource("%remove"));
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IMonitoredServerPort msp = (IMonitoredServerPort) getSelection(monitorTableViewer.getSelection());
				if (msp.isStarted())
					smm.stopMonitor(msp);
				smm.removeMonitor(msp);
				monitorTableViewer.remove(msp);
			}
		});
		remove.setEnabled(false);
		
		final Button start = SWTUtil.createButton(buttonComp, ServerUIPlugin.getResource("%start"));
		start.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) monitorTableViewer.getSelection();
				IMonitoredServerPort msp = (IMonitoredServerPort) sel.getFirstElement();
				try {
					smm.startMonitor(msp);
				} catch (CoreException ce) {
					EclipseUtil.openError(getShell(), ce.getLocalizedMessage());
				}
				monitorTableViewer.refresh(msp);
				monitorTableViewer.setSelection(new StructuredSelection(msp));
			}
		});
		start.setEnabled(false);
		
		final Button stop = SWTUtil.createButton(buttonComp, ServerUIPlugin.getResource("%stop"));
		stop.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) monitorTableViewer.getSelection();
				IMonitoredServerPort msp = (IMonitoredServerPort) sel.getFirstElement();
				smm.stopMonitor(msp);
				monitorTableViewer.refresh(msp);
				monitorTableViewer.setSelection(new StructuredSelection(msp));
			}
		});
		stop.setEnabled(false);
		
		monitorTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IMonitoredServerPort port = (IMonitoredServerPort) getSelection(monitorTableViewer.getSelection());
				if (port != null) {
					edit.setEnabled(!port.isStarted());
					remove.setEnabled(true);
					start.setEnabled(!port.isStarted());
					stop.setEnabled(port.isStarted());
				} else {
					edit.setEnabled(false);
					remove.setEnabled(false);
					start.setEnabled(false);
					stop.setEnabled(false);
				}
			}
		});
		
		IMonitoredServerPort[] msps = ServerCore.getServerMonitorManager().getMonitoredPorts(server); 
		if (msps != null && msps.length > 0)
			monitorTableViewer.setSelection(new StructuredSelection(msps[0]));
	}
	
	protected Object getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return sel.getFirstElement();
	}
}