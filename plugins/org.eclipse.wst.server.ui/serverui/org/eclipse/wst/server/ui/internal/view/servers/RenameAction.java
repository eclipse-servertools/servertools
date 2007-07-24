/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.view.servers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.actions.TextActionHandler;

import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.Messages;
/**
 * Action to rename a server.
 */
public class RenameAction extends AbstractServerAction {
	/*
	 * The tree editing widgets. If treeEditor is null then edit using the
	 * dialog. We keep the editorText around so that we can close it if a new
	 * selection is made.
	 */
	protected TreeEditor treeEditor;

	protected Tree tree;

	protected Text textEditor;

	protected Composite textEditorParent;

	private TextActionHandler textActionHandler;

	// The server being edited if this is being done inline
	protected IServer editedServer;

	protected boolean saving = false;

	public RenameAction(Shell shell, TreeViewer viewer, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, Messages.actionRename);
		this.tree = viewer.getTree();
		this.treeEditor = new TreeEditor(tree);
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
			// ignore
		}
	}

	public void perform(IServer server) {
		runWithInlineEditor(server);
	}

	/*
	 * Run the receiver using an inline editor from the supplied navigator. The
	 * navigator will tell the action when the path is ready to run.
	 */
	private void runWithInlineEditor(IServer server) {
		queryNewServerNameInline(server);
	}

	/**
	 * On Mac the text widget already provides a border when it has focus, so
	 * there is no need to draw another one. The value of returned by this
	 * method is usd to control the inset we apply to the text field bound's in
	 * order to get space for drawing a border. A value of 1 means a one-pixel
	 * wide border around the text field. A negative value supresses the border.
	 * However, in M9 the system property
	 * "org.eclipse.swt.internal.carbon.noFocusRing" has been introduced as a
	 * temporary workaround for bug #28842. The existence of the property turns
	 * the native focus ring off if the widget is contained in a main window
	 * (not dialog). The check for the property should be removed after a final
	 * fix for #28842 has been provided.
	 */
	private static int getCellEditorInset(Control c) {
		// special case for MacOS X
		if ("carbon".equals(SWT.getPlatform())) { //$NON-NLS-1$
			if (System
					.getProperty("org.eclipse.swt.internal.carbon.noFocusRing") == null || c.getShell().getParent() != null) { //$NON-NLS-1$
				return -2; // native border
			}
		}
		return 1; // one pixel wide black border
	}

	/**
	 * Get the Tree being edited.
	 * 
	 * @returnTree
	 */
	private Tree getTree() {
		return tree;
	}

	private Composite createParent() {
		Tree tree2 = getTree();
		Composite result = new Composite(tree2, SWT.NONE);
		TreeItem[] selectedItems = tree2.getSelection();
		treeEditor.horizontalAlignment = SWT.LEFT;
		treeEditor.grabHorizontal = true;
		treeEditor.setEditor(result, selectedItems[0]);
		return result;
	}

	/**
	 * Return the new name to be given to the target resource or
	 * <code>null<code>
	 * if the query was canceled. Rename the currently selected server using the table editor. 
	 * Continue the action when the user is done.
	 *
	 * @param server the server to rename
	 */
	private void queryNewServerNameInline(final IServer server) {
		// Make sure text editor is created only once. Simply reset text
		// editor when action is executed more than once. Fixes bug 22269
		if (textEditorParent == null) {
			createTextEditor(server);
		}
		textEditor.setText(server.getName());

		// Open text editor with initial size
		textEditorParent.setVisible(true);
		Point textSize = textEditor.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		textSize.x += textSize.y; // Add extra space for new characters
		Point parentSize = textEditorParent.getSize();
		int inset = getCellEditorInset(textEditorParent);
		textEditor.setBounds(2, inset, Math.min(textSize.x, parentSize.x - 4),
				parentSize.y - 2 * inset);
		textEditorParent.redraw();
		textEditor.selectAll();
		textEditor.setFocus();
	}

	/**
	 * Create the text editor widget.
	 * 
	 * @param server the server to rename
	 */
	private void createTextEditor(final IServer server) {
		// Create text editor parent. This draws a nice bounding rect
		textEditorParent = createParent();
		textEditorParent.setVisible(false);
		final int inset = getCellEditorInset(textEditorParent);
		if (inset > 0) {
			textEditorParent.addListener(SWT.Paint, new Listener() {
				public void handleEvent(Event e) {
					Point textSize = textEditor.getSize();
					Point parentSize = textEditorParent.getSize();
					e.gc.drawRectangle(0, 0, Math.min(textSize.x + 4,
							parentSize.x - 1), parentSize.y - 1);
				}
			});
		}
		// Create inner text editor
		textEditor = new Text(textEditorParent, SWT.NONE);
		textEditor.setFont(tree.getFont());
		textEditorParent.setBackground(textEditor.getBackground());
		textEditor.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				Point textSize = textEditor.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				textSize.x += textSize.y; // Add extra space for new
				// characters.
				Point parentSize = textEditorParent.getSize();
				textEditor.setBounds(2, inset, Math.min(textSize.x,
						parentSize.x - 4), parentSize.y - 2 * inset);
				textEditorParent.redraw();
			}
		});
		textEditor.addListener(SWT.Traverse, new Listener() {
			public void handleEvent(Event event) {

				// Workaround for Bug 20214 due to extra
				// traverse events
				switch (event.detail) {
				case SWT.TRAVERSE_ESCAPE:
					// Do nothing in this case
					disposeTextWidget();
					event.doit = true;
					event.detail = SWT.TRAVERSE_NONE;
					break;
				case SWT.TRAVERSE_RETURN:
					saveChangesAndDispose(server);
					event.doit = true;
					event.detail = SWT.TRAVERSE_NONE;
					break;
				}
			}
		});
		textEditor.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent fe) {
				saveChangesAndDispose(server);
			}
		});

		if (textActionHandler != null) {
			textActionHandler.addText(textEditor);
		}
	}

	/**
	 * Close the text widget and reset the editorText field.
	 */
	protected void disposeTextWidget() {
		if (textActionHandler != null)
			textActionHandler.removeText(textEditor);

		if (textEditorParent != null) {
			textEditorParent.dispose();
			textEditorParent = null;
			textEditor = null;
			treeEditor.setEditor(null, null);
		}
	}

	/**
	 * Save the changes and dispose of the text widget.
	 * 
	 * @param server the server to rename
	 */
	protected void saveChangesAndDispose(IServer server) {
		if (saving == true)
			return;
		
		saving = true;
		// Cache the resource to avoid selection loss since a selection of
		// another item can trigger this method
		editedServer = server;
		final String newName = textEditor.getText();
		// Run this in an async to make sure that the operation that triggered
		// this action is completed. Otherwise this leads to problems when the
		// icon of the item being renamed is clicked (i.e., which causes the
		// rename text widget to lose focus and trigger this method)
		getTree().getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					if (!newName.equals(editedServer.getName())) {
						if (ServerPlugin.isNameInUse(editedServer, newName)) {
							MessageDialog.openError(shell, Messages.defaultDialogTitle, Messages.errorDuplicateName);
						} else {
							try {
								IServerWorkingCopy wc = editedServer.createWorkingCopy();
								wc.setName(newName);
								wc.save(false, null);
							} catch (CoreException ce) {
								// ignore for now
							}
						}
					}
					editedServer = null;
					// Dispose the text widget regardless
					disposeTextWidget();
					// Ensure the Navigator tree has focus, which it may not if
					// the text widget previously had focus
					if (tree != null && !tree.isDisposed()) {
						tree.setFocus();
					}
				} finally {
					saving = false;
				}
			}
		});
	}
}