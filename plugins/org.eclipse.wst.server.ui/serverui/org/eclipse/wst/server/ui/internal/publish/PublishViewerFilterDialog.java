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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.help.WorkbenchHelp;
/**
 * Dialog to modify the filter used in the publish viewer
 * of the visual publisher.
 */
public class PublishViewerFilterDialog extends Dialog {
	protected PublishViewerFilter filter;
	protected CheckboxTableViewer viewer;

	protected static final String[] filterStrings = new String[] {
		ServerUIPlugin.getResource("%wizPublishFilterUnpublished"),
		ServerUIPlugin.getResource("%wizPublishFilterNew"),
		ServerUIPlugin.getResource("%wizPublishFilterUnmodified"),
		ServerUIPlugin.getResource("%wizPublishFilterModified"),
		ServerUIPlugin.getResource("%wizPublishFilterDeleted")};

	protected static final String[] filterDescriptions = new String[] {
		ServerUIPlugin.getResource("%wizPublishFilterUnpublishedDescription"),
		ServerUIPlugin.getResource("%wizPublishFilterNewDescription"),
		ServerUIPlugin.getResource("%wizPublishFilterUnmodifiedDescription"),
		ServerUIPlugin.getResource("%wizPublishFilterModifiedDescription"),
		ServerUIPlugin.getResource("%wizPublishFilterDeletedDescription")};

	public class FilterLabelProvider implements ITableLabelProvider {
		public void addListener(ILabelProviderListener listener) { }
		public void dispose() { }
		
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}
		
		public String getColumnText(Object element, int columnIndex) {
			return filterStrings[((Integer) element).intValue()];
		}
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}
		public void removeListener(ILabelProviderListener listener) { }
	}
	
	public class FilterContentProvider implements IStructuredContentProvider {
		public void dispose() { }
	
		public Object[] getElements(Object element) {
			return new Integer[] {
				new Integer(0), new Integer(1), new Integer(2),
				new Integer(3), new Integer(4)};
		}
	
		public void inputChanged(Viewer newViewer, Object oldInput, Object newInput) { }
	}

	/**
	 * PublishViewerFilterDialog constructor comment.
	 * @param parentShell org.eclipse.swt.widgets.Shell
	 */
	public PublishViewerFilterDialog(Shell parentShell, PublishViewerFilter filter) {
		super(parentShell);
		this.filter = filter;
	}

	/**
	 *
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(ServerUIPlugin.getResource("%wizPublishFilterTitle"));
	}

	/**
	 * Creates and returns the contents of the upper part 
	 * of this dialog (above the button bar).
	 * <p>
	 * The <code>Dialog</code> implementation of this framework method
	 * creates and returns a new <code>Composite</code> with
	 * standard margins and spacing. Subclasses should override.
	 * </p>
	 *
	 * @param the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		WorkbenchHelp.setHelp(composite, ContextIds.PUBLISHER_DIALOG_FILTER_DIALOG);
	
		Label label = new Label(composite, SWT.NONE);
		label.setText(ServerUIPlugin.getResource("%wizPublishFilterMessage"));
	
		viewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
		viewer.setContentProvider(new FilterContentProvider());
		viewer.setLabelProvider(new FilterLabelProvider());
		viewer.setInput("root");
		WorkbenchHelp.setHelp(viewer.getTable(), ContextIds.PUBLISHER_DIALOG_FILTER_DIALOG);
	
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 200;
		data.heightHint = 90;
		viewer.getTable().setLayoutData(data);
	
		if (filter.isFilteringUnpublishable())
			viewer.setChecked(new Integer(0), true);
		if (filter.isFilteringNew())
			viewer.setChecked(new Integer(1), true);
		if (filter.isFilteringUnmodified())
			viewer.setChecked(new Integer(2), true);
		if (filter.isFilteringModified())
			viewer.setChecked(new Integer(3), true);
		if (filter.isFilteringDeleted())
			viewer.setChecked(new Integer(4), true);
	
		final Label description = new Label(composite, SWT.WRAP);
		description.setText(" \n ");
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		data.heightHint = 35;
		description.setLayoutData(data);
		
		viewer.getTable().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				try {
					TableItem item = viewer.getTable().getSelection()[0];
					Integer in = (Integer) item.getData();
					description.setText(filterDescriptions[in.intValue()]);
				} catch (Exception e) {
					description.setText("");
				}
			}
			public void widgetDefaultSelected(SelectionEvent event) {
			}
		});
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}

	/**
	 * Return the filter.
	 *
	 * @return org.eclipse.wst.server.ui.internal.publish.PublishViewerFilter
	 */
	public PublishViewerFilter getFilter() {
		return filter;
	}

	protected void okPressed() {
		filter.setFilteringUnpublishable(viewer.getChecked(new Integer(0)));
		filter.setFilteringNew(viewer.getChecked(new Integer(1)));
		filter.setFilteringUnmodified(viewer.getChecked(new Integer(2)));
		filter.setFilteringModified(viewer.getChecked(new Integer(3)));
		filter.setFilteringDeleted(viewer.getChecked(new Integer(4)));
	
		super.okPressed();
	}
}
