package org.eclipse.wst.server.ui.internal.wizard.page;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.*;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * A wizard page used to select a server client.
 */
public class NewDetectServerComposite extends Composite {
	protected String host;
	
	protected IServerWorkingCopy server;
	protected IServerSelectionListener listener;
	
	protected List servers = new ArrayList();
	
	protected Button detect;
	protected Table table;
	protected TableViewer tableViewer;
	protected Label hostLabel;

	public interface IServerSelectionListener {
		public void serverSelected(IServer server);
	}
	
	public class ServerContentProvider implements IStructuredContentProvider {
		public void dispose() { }

		public Object[] getElements(Object inputElement) {
			return servers.toArray();
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	}
	
	public class ServerLabelProvider implements ITableLabelProvider {
		public void addListener(ILabelProviderListener listener2) { }

		public void dispose() { }

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			IServer server2 = (IServer) element;
			if (columnIndex == 0)
				return server2.getName();
			else if (columnIndex == 0)
				return "n/a";
			return null;
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener2) { }
	}

	/**
	 * Create a new NewDetectServerComposite.
	 */
	public NewDetectServerComposite(Composite parent, IServerSelectionListener listener2) {
		super(parent, SWT.NONE);
		this.listener = listener2;

		createControl();
	}
	
	protected Label createHeadingLabel(Composite parent, String text, int span) {
		Label label = createLabel(parent, text, span, true, false);
		label.setFont(JFaceResources.getBannerFont());
		return label;
	}
	
	protected Label createLabel(Composite parent, String text, int span, boolean alignTop, boolean indent) {
		Label label = new Label(parent, SWT.WRAP);
		label.setText(text);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
		if (alignTop)
			data.verticalAlignment = GridData.BEGINNING;
		data.horizontalSpan = span;
		if (indent)
			data.horizontalIndent = 10;
		label.setLayoutData(data);
		return label;
	}

	/**
	 * Creates the UI of the page.
	 *
	 * @param org.eclipse.swt.widgets.Composite parent
	 */
	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = SWTUtil.convertHorizontalDLUsToPixels(this, 4);
		layout.verticalSpacing = SWTUtil.convertVerticalDLUsToPixels(this, 4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		setLayout(layout);
		//WorkbenchHelp.setHelp(this, ContextIds.SELECT_CLIENT_WIZARD);
	
		createHeadingLabel(this, "Select the Server", 2);
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.SINGLE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		data.heightHint = 60;
		data.widthHint = 50;
		data.horizontalSpan = 2;
		data.horizontalIndent = 10;
		table.setLayoutData(data);
		
		TableLayout tableLayout = new TableLayout();
		table.setHeaderVisible(true);

		tableLayout.addColumnData(new ColumnWeightData(50, 100, true));
		TableColumn col = new TableColumn(table, SWT.NONE);
		col.setText("Server");
		
		tableLayout.addColumnData(new ColumnWeightData(40, 80, true));
		TableColumn col2 = new TableColumn(table, SWT.NONE);
		col2.setText("Status");
		
		table.setLayout(tableLayout);
		
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new ServerContentProvider());
		tableViewer.setLabelProvider(new ServerLabelProvider());
		tableViewer.setColumnProperties(new String[] {"name", "status"});
		tableViewer.setInput("root");
		
		String date = "<now>";
		hostLabel = createLabel(this, "Last detected servers on " + host + " at " + date + ":", 1, false, true);
		
		detect = SWTUtil.createButton(this, "Refresh");
		detect.setEnabled(false);
		data = (GridData) detect.getLayoutData();
		data.horizontalAlignment = GridData.HORIZONTAL_ALIGN_END;

		// listeners
		detect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection sel = (IStructuredSelection) event.getSelection();
				Object obj = sel.getFirstElement();
				IServerWorkingCopy newServer = null;
				if (obj instanceof IServerWorkingCopy)
					newServer = (IServerWorkingCopy) obj;
				
				if ((newServer == null && server != null) || (newServer != null && !newServer.equals(server))) {
					server = newServer;
					listener.serverSelected(server);
				}
			}
		});
		
		setHost(null);
	
		Dialog.applyDialogFont(this);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
		servers = new ArrayList();
		tableViewer.refresh();
		if (host != null) {
			hostLabel.setText("Detected servers on " + host + ":");
			detect.setEnabled(true);
			table.setEnabled(true);
		} else {
			hostLabel.setText("No host selected");
			detect.setEnabled(false);
			table.setEnabled(false);
		}
	}

	public IServerWorkingCopy getServer() {
		return server;
	}
}
