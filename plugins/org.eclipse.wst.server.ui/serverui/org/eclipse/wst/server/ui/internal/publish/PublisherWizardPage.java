package org.eclipse.wst.server.ui.internal.publish;
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * Publish Wizard that allows that user to select which files
 * to publish.
 */
public class PublisherWizardPage extends WizardPage {
	protected TableTree tableTree;

	protected VisualPublisher visualPublisher;

	protected static final String[] descriptionStrings = new String[] {
		ServerUIPlugin.getResource("%wizPublishStateNewDescription"),
		ServerUIPlugin.getResource("%wizPublishStateUnmodifiedDescription"),
		ServerUIPlugin.getResource("%wizPublishStateModifiedLocalDescription"),
		ServerUIPlugin.getResource("%wizPublishStateModifiedRemoteDescription"),
		ServerUIPlugin.getResource("%wizPublishStateModifiedBothDescription"),
		ServerUIPlugin.getResource("%wizPublishStateDeletedDescription")};

	/**
	 * PublisherWizardPage constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public PublisherWizardPage(VisualPublisher visualPublisher) {
		super("publish wizard page");
		this.visualPublisher = visualPublisher;
	
		setTitle(ServerUIPlugin.getResource("%wizPublishTitle"));
		setDescription(ServerUIPlugin.getResource("%wizPublishDescription"));
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZBAN_VISUAL_PUBLISHER));
	}

	/**
	 * Creates the top level control for this dialog
	 * page under the given parent composite.
	 * <p>
	 * Implementors are responsible for ensuring that
	 * the created control can be accessed via <code>getControl</code>
	 * </p>
	 *
	 * @param parent the parent composite
	 */
	public void createControl(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		c.setLayout(layout);
	
		tableTree = new TableTree(c, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
	
		Table table = tableTree.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
	
		TableColumn column = new TableColumn(table, SWT.SINGLE);
		column.setText(ServerUIPlugin.getResource("%wizPublishColumnLocal"));
		column.setWidth(150);
	
		column = new TableColumn(table, SWT.SINGLE);
		column.setText(ServerUIPlugin.getResource("%wizPublishColumnRemote"));
		column.setWidth(140);
	
		column = new TableColumn(table, SWT.SINGLE);
		column.setText(ServerUIPlugin.getResource("%wizPublishColumnStatus"));
		column.setWidth(135);
	
		column = new TableColumn(table, SWT.SINGLE);
		column.setText(ServerUIPlugin.getResource("%wizPublishColumnAction"));
		column.setWidth(120);
	
		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 550;
		data.heightHint = 400;
		tableTree.setLayoutData(data);
	
		final PublishTableTreeViewer viewer = new PublishTableTreeViewer(tableTree, visualPublisher);
	
		final Label description = new Label(c, SWT.WRAP);
		data = new GridData(GridData.FILL_HORIZONTAL);
		description.setLayoutData(data);
	
		tableTree.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent event) {
				try {
					TableTreeItem item = tableTree.getSelection()[0];
					Object obj = item.getData();
					description.setText(getDescription(obj));
				} catch (Exception e) {
					description.setText("!");
				}
			}
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
	
		final Button filter = new Button(c, SWT.NONE);
		filter.setText(ServerUIPlugin.getResource("%wizPublishFilter"));
		filter.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PublishViewerFilterDialog dialog = new PublishViewerFilterDialog(getShell(), viewer.getFilter());
				if (dialog.open() == Window.OK)
					viewer.updateFilter();
			}
		});
		
		Dialog.applyDialogFont(c);
		
		setControl(c);
	}

	/**
	 * Returns a description for the element.
	 *
	 * @param element java.lang.Object
	 * @return java.lang.String
	 */
	protected String getDescription(Object element) {
		if (element instanceof IModuleResource) {
			IModuleResource resource = (IModuleResource) element;
			IPath path = visualPublisher.getPublishControl(resource.getModule()).getMappedLocation(resource);
			if (path == null || path.toString() == null)
				return "";
			else {
				byte status = visualPublisher.getResourceStatus(resource, path);
				return descriptionStrings[status];
			}
		} else if (element instanceof ModuleRemoteResource) {
			return descriptionStrings[5];
		}
		return "";
	}
}
