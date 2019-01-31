/*******************************************************************************
 * Copyright (c) 2008, 2009 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery.internal.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.wst.server.discovery.internal.Messages;

public class ErrorWizardPage extends WizardPage {
	/**
	 * The nesting indent.
	 */
	private static final String NESTING_INDENT = "  "; //$NON-NLS-1$

	/**
	 * The SWT list control that displays the error details.
	 */
	private List list;

	/**
	 * Message (a localized string).
	 */
	protected String message;

	/**
	 * The main status object.
	 */
	private IStatus status;

	/**
	 * The current clipboard. To be disposed when closing the dialog.
	 */
	private Clipboard clipboard;

	public ErrorWizardPage() {
		super("error-page");
		setTitle(Messages.wizExtensionTitle);
		setDescription(Messages.wizExtensionDescription);
	}

	public void createControl(Composite parent) {
		Control control = createDropDownList(parent);
		setControl(control);
	}

	/**
	 * Set the status displayed by this error dialog to the given status. This
	 * only affects the status displayed by the Details list. The message, image
	 * and title should be updated by the subclass, if desired.
	 * 
	 * @param status
	 *            the status to be displayed in the details list
	 */
	public void setStatus(IStatus status) {
		if (this.status != status)
			this.status = status;
		
		setDescription(status.getMessage());
		if (list != null && !list.isDisposed()) {
			list.removeAll();
			populateList(list);
		}
	}

	/**
	 * Create this dialog's drop-down list component.
	 * 
	 * @param parent
	 *            the parent composite
	 * @return the drop-down list component
	 */
	protected List createDropDownList(Composite parent) {
		list = new List(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		populateList(list);
		GridData data = new GridData(SWT.FILL, SWT.FILL, false, false);
		data.widthHint = 225;
		list.setLayoutData(data);
		list.setFont(parent.getFont());
		Menu copyMenu = new Menu(list);
		MenuItem copyItem = new MenuItem(copyMenu, SWT.NONE);
		copyItem.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				copyToClipboard();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				copyToClipboard();
			}
		});
		copyItem.setText(JFaceResources.getString("copy")); //$NON-NLS-1$
		list.setMenu(copyMenu);
		//listCreated = true;
		return list;
	}

	/**
	 * Copy the contents of the statuses to the clipboard.
	 */
	protected void copyToClipboard() {
		if (clipboard != null)
			clipboard.dispose();
		
		StringBuffer statusBuffer = new StringBuffer();
		populateCopyBuffer(status, statusBuffer, 0);
		clipboard = new Clipboard(list.getDisplay());
		clipboard.setContents(new Object[] { statusBuffer.toString() },
				new Transfer[] { TextTransfer.getInstance() });
	}

	public void dispose() {
		if (clipboard != null)
			clipboard.dispose();
		
		super.dispose();
	}

	/**
	 * Put the details of the status of the error onto the stream.
	 * 
	 * @param buildingStatus
	 * @param buffer
	 * @param nesting
	 */
	private void populateCopyBuffer(IStatus buildingStatus, StringBuffer buffer, int nesting) {
		for (int i = 0; i < nesting; i++)
			buffer.append(NESTING_INDENT);
		
		buffer.append(buildingStatus.getMessage());
		buffer.append("\n"); //$NON-NLS-1$

		// Look for a nested core exception
		Throwable t = buildingStatus.getException();
		if (t instanceof CoreException) {
			CoreException ce = (CoreException) t;
			populateCopyBuffer(ce.getStatus(), buffer, nesting + 1);
		} else if (t != null) {
			// Include low-level exception message
			for (int i = 0; i < nesting; i++)
				buffer.append(NESTING_INDENT);
			
			String message = t.getLocalizedMessage();
			if (message == null)
				message = t.toString();
			
			buffer.append(message);
			buffer.append("\n"); //$NON-NLS-1$
		}

		IStatus[] children = buildingStatus.getChildren();
		for (int i = 0; i < children.length; i++)
			populateCopyBuffer(children[i], buffer, nesting + 1);
	}

	/**
	 * Populates the list using this error dialog's status object. This walks
	 * the child static of the status object and displays them in a list. The
	 * format for each entry is status_path : status_message If the status's
	 * path was null then it (and the colon) are omitted.
	 * 
	 * @param listToPopulate
	 *            The list to fill.
	 */
	private void populateList(List listToPopulate) {
		populateList(listToPopulate, status, 0, true);
	}

	/**
	 * Populate the list with the messages from the given status. Traverse the
	 * children of the status deeply and also traverse CoreExceptions that
	 * appear in the status.
	 * 
	 * @param listToPopulate
	 *            the list to populate
	 * @param buildingStatus
	 *            the status being displayed
	 * @param nesting
	 *            the nesting level (increases one level for each level of
	 *            children)
	 * @param includeStatus
	 *            whether to include the buildingStatus in the display or just
	 *            its children
	 */
	private void populateList(List listToPopulate, IStatus buildingStatus,
			int nesting, boolean includeStatus) {
		
		Throwable t = buildingStatus.getException();
		boolean isCoreException = t instanceof CoreException;
		boolean incrementNesting = false;
		
		if (includeStatus) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < nesting; i++)
				sb.append(NESTING_INDENT);
			
			String message = buildingStatus.getMessage();
			sb.append(message);
			listToPopulate.add(sb.toString());
			incrementNesting = true;
		}

		if (!isCoreException && t != null) {
			// Include low-level exception message
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < nesting; i++)
				sb.append(NESTING_INDENT);
			
			String message = t.getLocalizedMessage();
			if (message == null)
				message = t.toString();

			sb.append(message);
			listToPopulate.add(sb.toString());
			incrementNesting = true;
		}

		if (incrementNesting)
			nesting++;

		// Look for a nested core exception
		if (isCoreException) {
			CoreException ce = (CoreException) t;
			IStatus eStatus = ce.getStatus();
			// Only print the exception message if it is not contained in the
			// parent message
			if (message == null || message.indexOf(eStatus.getMessage()) == -1) {
				populateList(listToPopulate, eStatus, nesting, true);
			}
		}

		// Look for child status
		IStatus[] children = buildingStatus.getChildren();
		for (int i = 0; i < children.length; i++)
			populateList(listToPopulate, children[i], nesting, true);
	}
}