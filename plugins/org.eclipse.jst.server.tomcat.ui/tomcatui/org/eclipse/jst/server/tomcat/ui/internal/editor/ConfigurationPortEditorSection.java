package org.eclipse.jst.server.tomcat.ui.internal.editor;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import java.beans.*;
import java.util.Iterator;

import org.eclipse.wst.server.core.model.IServerPort;
import org.eclipse.wst.server.core.util.ServerPort;
import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.jface.viewers.*;
import org.eclipse.jst.server.tomcat.core.ITomcatConfigurationWorkingCopy;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.command.*;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatUIPlugin;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.WorkbenchHelp;
/**
 * Tomcat configuration port editor page.
 */
public class ConfigurationPortEditorSection extends ServerResourceEditorSection {
	protected ITomcatConfigurationWorkingCopy tomcatConfiguration;

	protected boolean updating;

	protected Table ports;
	protected TableViewer viewer;

	protected PropertyChangeListener listener;

	/**
	 * ConfigurationPortEditorSection constructor comment.
	 */
	protected ConfigurationPortEditorSection() {
		super();
	}

	/**
	 * 
	 */
	protected void addChangeListener() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (TomcatConfiguration.MODIFY_PORT_PROPERTY.equals(event.getPropertyName())) {
					String id = (String) event.getOldValue();
					Integer i = (Integer) event.getNewValue();
					changePortNumber(id, i.intValue());
				}
			}
		};
		serverConfiguration.addPropertyChangeListener(listener);
	}
	
	/**
	 * 
	 * @param id java.lang.String
	 * @param port int
	 */
	protected void changePortNumber(String id, int port) {
		TableItem[] items = ports.getItems();
		int size = items.length;
		for (int i = 0; i < size; i++) {
			IServerPort sp = (IServerPort) items[i].getData();
			if (sp.getId().equals(id)) {
				items[i].setData(new ServerPort(id, sp.getName(), port, sp.getProtocol()));
				items[i].setText(1, port + "");
				/*if (i == selection) {
					selectPort();
				}*/
				return;
			}
		}
	}
	
	/**
	 * Creates the SWT controls for this workbench part.
	 *
	 * @param parent the parent control
	 */
	public void createSection(Composite parent) {
		super.createSection(parent);
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		
		Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED|ExpandableComposite.TITLE_BAR|Section.DESCRIPTION|ExpandableComposite.FOCUS_TITLE);
		section.setText(TomcatUIPlugin.getResource("%configurationEditorPortsSection"));
		section.setDescription(TomcatUIPlugin.getResource("%configurationEditorPortsDescription"));
		section.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
		
		// ports
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 8;
		layout.marginWidth = 8;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.FILL_HORIZONTAL));
		WorkbenchHelp.setHelp(composite, ContextIds.CONFIGURATION_EDITOR_PORTS);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		ports = toolkit.createTable(composite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		ports.setHeaderVisible(true);
		ports.setLinesVisible(true);
		WorkbenchHelp.setHelp(ports, ContextIds.CONFIGURATION_EDITOR_PORTS_LIST);
		
		TableLayout tableLayout = new TableLayout();
	
		TableColumn col = new TableColumn(ports, SWT.NONE);
		col.setText(TomcatUIPlugin.getResource("%configurationEditorPortNameColumn"));
		ColumnWeightData colData = new ColumnWeightData(15, 150, true);
		tableLayout.addColumnData(colData);

		col = new TableColumn(ports, SWT.NONE);
		col.setText(TomcatUIPlugin.getResource("%configurationEditorPortValueColumn"));
		colData = new ColumnWeightData(8, 80, true);
		tableLayout.addColumnData(colData);

		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		data.widthHint = 230;
		data.heightHint = 100;
		ports.setLayoutData(data);
		ports.setLayout(tableLayout);
	
		viewer = new TableViewer(ports);
		viewer.setColumnProperties(new String[] {"name", "port"});

		initialize();
	}

	protected void setupPortEditors() {
		viewer.setCellEditors(new CellEditor[] {null, new TextCellEditor(ports)});
	
		ICellModifier cellModifier = new ICellModifier() {
			public Object getValue(Object element, String property) {
				IServerPort sp = (IServerPort) element;
				return sp.getPort() + "";
			}
	
			public boolean canModify(Object element, String property) {
				if ("port".equals(property))
					return true;
				
				return false;
			}
	
			public void modify(Object element, String property, Object value) {
				try {
					Item item = (Item) element;
					IServerPort sp = (IServerPort) item.getData();
					int port = Integer.parseInt((String) value);
					commandManager.executeCommand(new ModifyPortCommand(tomcatConfiguration, sp.getId(), port));
				} catch (Exception ex) { }
			}
		};
		viewer.setCellModifier(cellModifier);
		
		// preselect second column (Windows-only)
		String os = System.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("win") >= 0) {
			ports.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					try {
						int n = ports.getSelectionIndex();
						viewer.editElement(ports.getItem(n).getData(), 1);
					} catch (Exception e) { }
				}
			});
		}
	}
	
	public void dispose() {
		if (serverConfiguration != null)
			serverConfiguration.removePropertyChangeListener(listener);
	}
	
	/* (non-Javadoc)
	 * Initializes the editor part with a site and input.
	 * <p>
	 * Subclasses of <code>EditorPart</code> must implement this method.  Within
	 * the implementation subclasses should verify that the input type is acceptable
	 * and then save the site and input.  Here is sample code:
	 * </p>
	 * <pre>
	 *		if (!(input instanceof IFileEditorInput))
	 *			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
	 *		setSite(site);
	 *		setInput(editorInput);
	 * </pre>
	 */
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		if (serverConfiguration != null) {
			tomcatConfiguration = (ITomcatConfigurationWorkingCopy) serverConfiguration.getWorkingCopyDelegate();
			addChangeListener();
		}
		initialize();
	}

	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
		if (ports == null)
			return;

		ports.removeAll();

		Iterator iterator = tomcatConfiguration.getServerPorts().iterator();
		while (iterator.hasNext()) {
			IServerPort port = (IServerPort) iterator.next();
			TableItem item = new TableItem(ports, SWT.NONE);
			String[] s = new String[] {port.getName(), port.getPort() + ""};
			item.setText(s);
			item.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_PORT));
			item.setData(port);
		}
		
		if (readOnly) {
			viewer.setCellEditors(new CellEditor[] {null, null});
			viewer.setCellModifier(null);
		} else {
			setupPortEditors();
		}
	}
	
	protected void validate() { }
}