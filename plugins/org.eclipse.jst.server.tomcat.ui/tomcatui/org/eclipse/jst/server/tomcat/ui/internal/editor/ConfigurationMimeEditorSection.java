/**********************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.ui.internal.editor;

import java.beans.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jst.server.tomcat.core.internal.MimeMapping;
import org.eclipse.jst.server.tomcat.core.internal.TomcatConfiguration;
import org.eclipse.jst.server.tomcat.core.internal.TomcatServer;
import org.eclipse.jst.server.tomcat.core.internal.command.*;
import org.eclipse.jst.server.tomcat.ui.internal.ContextIds;
import org.eclipse.jst.server.tomcat.ui.internal.TomcatUIPlugin;
import org.eclipse.ui.*;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.WorkbenchHelp;
/**
 * Tomcat configuration mime editor section.
 */
public class ConfigurationMimeEditorSection extends ServerEditorSection {
	protected TomcatConfiguration tomcatConfiguration;

	protected boolean updating;

	protected PropertyChangeListener listener;
	
	protected Tree mimeTypes;
	protected int index = -1;
	protected List mappings;
	protected Button add;
	protected Button remove;
	protected Button edit;

	/**
	 * ConfigurationMimeEditorSection constructor comment.
	 */
	protected ConfigurationMimeEditorSection() {
		super();
	}

	/**
	 * 
	 */
	protected void addChangeListener() {
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (TomcatConfiguration.ADD_MAPPING_PROPERTY.equals(event.getPropertyName())) {
					Integer in = (Integer) event.getOldValue();
					MimeMapping mapping = (MimeMapping) event.getNewValue();
					addMimeMapping(in.intValue(), mapping);
				} else if (TomcatConfiguration.REMOVE_MAPPING_PROPERTY.equals(event.getPropertyName())) {
					Integer in = (Integer) event.getNewValue();
					removeMimeMapping(in.intValue());
				} else if (TomcatConfiguration.MODIFY_MAPPING_PROPERTY.equals(event.getPropertyName())) {
					Integer in = (Integer) event.getOldValue();
					MimeMapping mapping = (MimeMapping) event.getNewValue();
					modifyMimeMapping(in.intValue(), mapping);
				}
			}
		};
		tomcatConfiguration.addPropertyChangeListener(listener);
	}

	public void createSection(Composite parent) {
		super.createSection(parent);
		
		FormToolkit toolkit = getFormToolkit(parent.getDisplay());
		
		Section section = toolkit.createSection(parent, ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR
			| Section.DESCRIPTION | ExpandableComposite.FOCUS_TITLE);
		section.setText(TomcatUIPlugin.getResource("%configurationEditorMimeMappingsSection"));
		section.setDescription(TomcatUIPlugin.getResource("%configurationEditorMimeMappingsDescription"));
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL));
		
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 15;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(data);
		WorkbenchHelp.setHelp(composite, ContextIds.CONFIGURATION_EDITOR_MAPPINGS);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		
		mimeTypes = toolkit.createTree(composite, SWT.V_SCROLL | SWT.SINGLE | SWT.H_SCROLL);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.widthHint = 220;
		data.heightHint = 200;
		mimeTypes.setLayoutData(data);
		WorkbenchHelp.setHelp(mimeTypes, ContextIds.CONFIGURATION_EDITOR_MAPPINGS_LIST);
		
		// add listener to the table
		mimeTypes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectMimeMapping();
			}
		});
		
		Composite buttonComp = toolkit.createComposite(composite);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttonComp.setLayout(layout);
		buttonComp.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		add = toolkit.createButton(buttonComp, TomcatUIPlugin.getResource("%editorAdd"), SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = 75;
		add.setLayoutData(data);
		
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MimeMappingDialog dialog = new MimeMappingDialog(getShell());
				dialog.open();
				if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
					commandManager.executeCommand(new AddMimeMappingCommand(tomcatConfiguration, dialog.getMimeMapping()));
				}
			}
		});
		WorkbenchHelp.setHelp(add, ContextIds.CONFIGURATION_EDITOR_MAPPINGS_ADD);
		
		edit = toolkit.createButton(buttonComp, TomcatUIPlugin.getResource("%editorEdit"), SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = 75;
		edit.setLayoutData(data);
		edit.setEnabled(false);
		
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (index < 0)
					return;
				MimeMappingDialog dialog = new MimeMappingDialog(getShell(), (MimeMapping) tomcatConfiguration.getMimeMappings().get(index));
				dialog.open();
				if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
					commandManager.executeCommand(new ModifyMimeMappingCommand(tomcatConfiguration, index, dialog.getMimeMapping()));
				}
			}
		});
		WorkbenchHelp.setHelp(edit, ContextIds.CONFIGURATION_EDITOR_MAPPINGS_EDIT);
		
		remove = toolkit.createButton(buttonComp, TomcatUIPlugin.getResource("%editorRemove"), SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = 75;
		remove.setLayoutData(data);
		remove.setEnabled(false);
		
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (index < 0)
					return;
				commandManager.executeCommand(new RemoveMimeMappingCommand(tomcatConfiguration, index));
				index = -1;
				edit.setEnabled(false);
				remove.setEnabled(false);
			}
		});
		WorkbenchHelp.setHelp(remove, ContextIds.CONFIGURATION_EDITOR_MAPPINGS_REMOVE);
		
		initialize();
	}
	
	public void dispose() {
		if (tomcatConfiguration != null)
			tomcatConfiguration.removePropertyChangeListener(listener);
	}

	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		
		TomcatServer ts = (TomcatServer) server.getAdapter(TomcatServer.class);
		tomcatConfiguration = ts.getTomcatConfiguration();
		addChangeListener();
		initialize();
	}

	/**
	 * Initialize the fields in this editor.
	 */
	protected void initialize() {
		if (mimeTypes == null)
			return;

		mimeTypes.removeAll();
		
		mappings = tomcatConfiguration.getMimeMappings();
		
		// sort mappings
		int size = mappings.size();
		int[] map = new int[size];
		for (int i = 0; i < size; i++)
			map[i] = i;
		
		for (int i = 0; i < size - 1; i++) {
			for (int j = i + 1; j < size; j++) {
				MimeMapping mappingA = (MimeMapping) mappings.get(map[i]);
				MimeMapping mappingB = (MimeMapping) mappings.get(map[j]);
				if (mappingA.getMimeType().compareTo(mappingB.getMimeType()) > 0 ||
						((mappingA.getMimeType().equals(mappingB.getMimeType())) &&
								mappingA.getExtension().compareTo(mappingB.getExtension()) > 0)) {
					int temp = map[i];
					map[i] = map[j];
					map[j] = temp;
				}
			}
		}
		
		// display them
		Map hash = new HashMap();
		
		for (int i = 0; i < size; i++) {
			MimeMapping mapping = (MimeMapping) mappings.get(map[i]);
			// get parent node
			TreeItem parent = (TreeItem) hash.get(mapping.getMimeType());
			if (parent == null) {
				parent = new TreeItem(mimeTypes, SWT.NONE);
				parent.setText(mapping.getMimeType());
				parent.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_MIME_MAPPING));
				hash.put(mapping.getMimeType(), parent);
			}
			
			// add node
			TreeItem item = new TreeItem(parent, SWT.NONE);
			item.setText(mapping.getExtension());
			item.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_MIME_EXTENSION));
			item.setData(new Integer(map[i]));
		}
		
		if (readOnly) {
			add.setEnabled(false);
			edit.setEnabled(false);
			remove.setEnabled(false);
		} else {
			add.setEnabled(true);
			selectMimeMapping();
		}
	}
	
	/**
	 * Add a mime mapping.
	 * 
	 * @param index int
	 * @param map org.eclipse.jst.server.tomcat.MimeMapping
	 */
	protected void addMimeMapping(int index2, MimeMapping map) {
		mappings.add(index2, map);
		
		// correct all index numbers
		int size = mimeTypes.getItemCount();
		TreeItem[] parents = mimeTypes.getItems();
		for (int i = 0; i < size; i++) {
			TreeItem parent = parents[i];
			
			int size2 = parent.getItemCount();
			TreeItem[] children = parent.getItems();
			for (int j = 0; j < size2; j++) {
				Integer in = (Integer) children[j].getData();
				if (in.intValue() >= index2)
					children[j].setData(new Integer(in.intValue() + 1));
			}
		}
		
		// check if there is a parent. If so, just add a new child
		for (int i = 0; i < size; i++) {
			TreeItem parent = parents[i];
			if (parent.getText().equals(map.getMimeType())) {
				TreeItem item = new TreeItem(parent, SWT.NONE);
				item.setText(map.getExtension());
				item.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_MIME_EXTENSION));
				item.setData(new Integer(index2));
				mimeTypes.showItem(item);
				return;
			}
		}
		
		// if not, add a new parent and child to the end
		TreeItem parent = new TreeItem(mimeTypes, SWT.NONE);
		parent.setText(map.getMimeType());
		parent.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_MIME_MAPPING));
		
		TreeItem item = new TreeItem(parent, SWT.NONE);
		item.setText(map.getExtension());
		item.setImage(TomcatUIPlugin.getImage(TomcatUIPlugin.IMG_MIME_EXTENSION));
		item.setData(new Integer(index2));
		mimeTypes.showItem(item);
	}
	
	/**
	 * 
	 * @param index int
	 * @param map org.eclipse.jst.server.tomcat.MimeMapping
	 */
	protected void modifyMimeMapping(int index2, MimeMapping map) {
		MimeMapping oldMap = (MimeMapping) mappings.get(index2);
		mappings.set(index2, map);
		
		int size = mimeTypes.getItemCount();
		TreeItem[] parents = mimeTypes.getItems();
		
		if (oldMap.getMimeType().equals(map.getMimeType())) {
			for (int i = 0; i < size; i++) {
				TreeItem parent = parents[i];
				
				if (parent.getText().equals(map.getMimeType())) {
					int size2 = parent.getItemCount();
					TreeItem[] children = parent.getItems();
					for (int j = 0; j < size2; j++) {
						Integer in = (Integer) children[j].getData();
						if (in.intValue() == index2) {
							children[j].setText(map.getExtension());
							children[j].setData(new Integer(index2));
							mimeTypes.showItem(children[j]);
							return;
						}
					}
				}
			}
			return;
		}
		
		// otherwise, let's try a remove and an add
		removeMimeMapping(index2);
		addMimeMapping(index2, map);
	}
	
	/**
	 * Remove the mime mapping at the given index.
	 * @param i int
	 */
	protected void removeMimeMapping(int index2) {
		mappings.remove(index2);
		
		// remove item
		int size = mimeTypes.getItemCount();
		TreeItem[] parents = mimeTypes.getItems();
		for (int i = 0; i < size; i++) {
			TreeItem parent = parents[i];
			
			int size2 = parent.getItemCount();
			TreeItem[] children = parent.getItems();
			for (int j = 0; j < size2; j++) {
				Integer in = (Integer) children[j].getData();
				if (in.intValue() == index2) {
					children[j].dispose();
					if (size2 == 1)
						parent.dispose();
					i += size;
					j += size2;
				}
			}
		}
		
		// correct all index numbers
		size = mimeTypes.getItemCount();
		parents = mimeTypes.getItems();
		for (int i = 0; i < size; i++) {
			TreeItem parent = parents[i];
			
			int size2 = parent.getItemCount();
			TreeItem[] children = parent.getItems();
			for (int j = 0; j < size2; j++) {
				Integer in = (Integer) children[j].getData();
				if (in.intValue() > index2)
					children[j].setData(new Integer(in.intValue() - 1));
			}
		}
	}

	/**
	 * 
	 */
	protected void selectMimeMapping() {
		if (readOnly)
			return;

		try {
			TreeItem item = mimeTypes.getSelection()[0];
			Integer in = (Integer) item.getData();
			index = in.intValue();
			
			remove.setEnabled(true);
			edit.setEnabled(true);
		} catch (Exception e) {
			index = -1;
			remove.setEnabled(false);
			edit.setEnabled(false);
		}
	}
}