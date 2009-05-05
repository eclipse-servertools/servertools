/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.wst.server.ui.internal.*;
/**
 * A multi-page server resource editor.
 */
public class ServerEditor extends MultiPageEditorPart {
	/**
	 * Internal part and shell activation listener
	 */
	class ActivationListener extends ShellAdapter implements IPartListener {
		private IWorkbenchPart fActivePart;
		private boolean fIsHandlingActivation = false;
		
		public void partActivated(IWorkbenchPart part) {
			fActivePart = part;
			handleActivation();
		}

		public void partBroughtToTop(IWorkbenchPart part) {
			// do nothing
		}

		public void partClosed(IWorkbenchPart part) {
			// do nothing
		}

		public void partDeactivated(IWorkbenchPart part) {
			fActivePart = null;
		}

		public void partOpened(IWorkbenchPart part) {
			// do nothing
		}

		public void shellActivated(ShellEvent e) {
			handleActivation();
		}

		private void handleActivation() {
			if (fIsHandlingActivation)
				return;
			
			if (fActivePart == ServerEditor.this) {
				fIsHandlingActivation = true;
				try {
					checkResourceState();
				} finally {
					fIsHandlingActivation = false;
				}
			}
		}
	}

	class LifecycleListener implements IServerLifecycleListener {
		public void serverAdded(IServer oldServer) {
			// do nothing
		}
		public void serverChanged(IServer oldServer) {
			// do nothing
		}
		public void serverRemoved(IServer oldServer) {
			if (oldServer.equals(server) && !isDirty())
				closeEditor();
		}
	}

	protected IServerWorkingCopy server;
	protected String serverId;
	protected String serverName;

	protected GlobalCommandManager commandManager;

	protected PropertyChangeListener listener;

	protected IAction undoAction;
	protected IAction redoAction;

	protected TextAction cutAction;
	protected TextAction copyAction;
	protected TextAction pasteAction;
	protected boolean updatingActions;

	protected IAction[] editorActions;

	protected java.util.List<IEditorPart> serverPages;

	// on focus change flag
	protected boolean resourceDeleted;

	// input given to the contained pages
	protected IServerEditorPartInput editorPartInput;

	// status line and status
	protected IStatusLineManager status;
	protected StatusLineContributionItem statusItem;

	private ActivationListener activationListener = new ActivationListener();
	protected LifecycleListener resourceListener;

	// used for disabling resource change check when saving through editor
	protected boolean isSaving = false;

	protected Map<IEditorPart, IServerEditorPartFactory> pageToFactory = new HashMap<IEditorPart, IServerEditorPartFactory>();

	/**
	 * ServerEditor constructor comment.
	 */
	public ServerEditor() {
		super();
		
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		
		undoAction = new Action() {
			public void run() {
				getCommandManager().undo(serverId);
			}
		};
		undoAction.setEnabled(false);
		undoAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		//undoAction.setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_HOVER));
		//undoAction.setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
		
		redoAction = new Action() {
			public void run() {
				getCommandManager().redo(serverId);
			}
		};
		redoAction.setEnabled(false);
		redoAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO));
		//redoAction.setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO_HOVER));
		//redoAction.setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_REDO_DISABLED));
	}

	/**
	 * Close the editor correctly.
	 */
	protected void closeEditor() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getEditorSite().getPage().closeEditor(ServerEditor.this, false);
			}
		});
	}

	protected void createActions() {
		List<IAction> actionList = new ArrayList<IAction>();
		
		// add server actions
		if (server != null && server.getServerType() != null) {
			Iterator iterator = ServerEditorCore.getServerEditorActionFactories().iterator();
			String id = server.getServerType().getId();
			while (iterator.hasNext()) {
				ServerEditorActionFactory factory = (ServerEditorActionFactory) iterator.next();
				if (factory.supportsServerElementType(id) && factory.shouldDisplay(server))
					actionList.add(factory.createAction(getEditorSite(), editorPartInput));
			}
		}

		editorActions = new IAction[actionList.size()];
		actionList.toArray(editorActions);
	}

	public IServerEditorPartFactory getPageFactory(ServerEditorPart part) {
		try {
			return pageToFactory.get(part);
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/**
	 * Creates the pages of this multi-page editor.
	 * <p>
	 * Subclasses of <code>MultiPageEditor</code> must implement this method.
	 * </p>
	 */
	protected void createPages() {
		try {
			int index = 0;
			serverPages = new ArrayList<IEditorPart>();
			
			// add editor pages
			int pageCount = 0;
			
			String serverTypeId = null;
			if (server != null && server.getServerType() != null)
				serverTypeId = server.getServerType().getId();
			
			Iterator iterator = ServerEditorCore.getServerEditorPageFactories().iterator();
			while (iterator.hasNext()) {
				IServerEditorPartFactory factory = (IServerEditorPartFactory) iterator.next();
				if (factory.supportsType(serverTypeId) && factory.shouldCreatePage(server)) {
					Trace.trace(Trace.FINEST, "Adding page: " + factory.getId() + " " + editorPartInput);
					try {
						IEditorPart page = factory.createPage();
						if (page != null) {
							pageToFactory.put(page, factory);
							index = addPage(page, editorPartInput);
							setPageText(index, factory.getName());
							
							serverPages.add(page);
					
							pageCount ++;
						}
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Could not display editor page " + factory.getId(), e);
					}
				}
			}
			
			if (pageCount > 0)
				setActivePage(0);
			
			// register for events that might change the cut/copy/paste actions
			int count = getPageCount();
			for (int i = 0; i < count; i++) {
				Control control = getControl(i);
				registerEvents(control);
			}
			updateActions();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error creating server editor pages", e);
		}
	}

	public void dispose() {
		if (activationListener != null) {
			IWorkbenchWindow window = getSite().getWorkbenchWindow();
			window.getPartService().removePartListener(activationListener);
			Shell shell = window.getShell();
			if (shell != null && !shell.isDisposed())
				shell.removeShellListener(activationListener);
			activationListener = null;
		}
		
		if (resourceListener != null)
			ServerCore.removeServerLifecycleListener(resourceListener);
		
		if (serverName != null && !server.getName().equals(serverName)) {
			// only prompt if the server is in the workspace or there is a configuration
			if (server.getServerConfiguration() != null || ((Server)server).getFile() != null) {
				String title = Messages.editorServerEditor;
				String message = Messages.editorRenameFiles;
				if (MessageDialog.openQuestion(getEditorSite().getShell(), title,  message))
					try {
						((ServerWorkingCopy)server).renameFiles(null);
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Error renaming server", e);
					}
			}
		}
		
		super.dispose();
		if (commandManager != null) {
			commandManager.removePropertyChangeListener(listener);
			
			if (serverId != null)
				commandManager.releaseCommandManager(serverId);
			
			commandManager = null;
		}
	}

	/* (non-Javadoc)
	 * Saves the contents of this editor.
	 * <p>
	 * Subclasses must override this method to implement the open-save-close lifecycle
	 * for an editor.  For greater details, see <code>IEditorPart</code>
	 * </p>
	 *
	 * @see IEditorPart
	 */
	public void doSave(IProgressMonitor monitor) {
		// set the isSaving flag to true
		isSaving = true;
		
		// check pages for errors first
		java.util.List<String> errors = new ArrayList<String>();
		Iterator iterator = serverPages.iterator();
		int count = 0;
		int maxSeverity = -1;
		while (iterator.hasNext()) {
			IEditorPart part = (IEditorPart) iterator.next();
			if (part instanceof ServerEditorPart) {
				IStatus[] status2 = ((ServerEditorPart) part).getSaveStatus();
				if (status2 != null) {
					int size = status2.length;
					for (int i = 0; i < size; i++) {
						errors.add("[" + getPageText(count) + "] " + status2[i].getMessage());
						maxSeverity = Math.max(maxSeverity, status2[i].getSeverity());
					}
				}
			}
			count ++;
		}
		if (!errors.isEmpty() && maxSeverity > IStatus.OK) {
			StringBuffer sb = new StringBuffer();
			sb.append(Messages.errorEditorCantSave + "\n");
			iterator = errors.iterator();
			while (iterator.hasNext())
				sb.append("\t" + ((String) iterator.next()) + "\n");
			
			if (maxSeverity == IStatus.ERROR) {
				MessageDialog.openError(getEditorSite().getShell(), getPartName(), sb.toString());
				monitor.setCanceled(true);
				// reset the isSaving flag
				isSaving = false;
				return;
			} else if (maxSeverity == IStatus.WARNING)
				MessageDialog.openWarning(getEditorSite().getShell(), getPartName(), sb.toString());
			else // if (maxSeverity == IStatus.INFO)
				MessageDialog.openInformation(getEditorSite().getShell(), getPartName(), sb.toString());
		}
		
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			int ticks = 2000;
			String name = "";
			if (server != null)
				name = server.getName();
			monitor.beginTask(NLS.bind(Messages.savingTask, name), ticks);
			if (server != null)
				ticks /= 2;
			
			if (server != null)  {
				server.save(false, ProgressUtil.getSubMonitorFor(monitor, ticks));
				getCommandManager().resourceSaved(serverId);
				commandManager.updateTimestamps(serverId);
			}
			
			ILabelProvider labelProvider = ServerUICore.getLabelProvider();
			if (server != null)
				setPartName(labelProvider.getText(server));
			labelProvider.dispose();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error saving server editor", e);
			
			monitor.setCanceled(true);
			
			String title = Messages.editorSaveErrorDialog;
			String message = NLS.bind(Messages.editorSaveErrorMessage, e.getLocalizedMessage());
			MessageDialog.openError(getEditorSite().getShell(), title,  message);
		} finally {
			monitor.done();
		}
		// reset the isSaving flag
		isSaving = false;
	}

	/* (non-Javadoc)
	 * Saves the contents of this editor to another object.
	 * <p>
	 * Subclasses must override this method to implement the open-save-close lifecycle
	 * for an editor.  For greater details, see <code>IEditorPart</code>
	 * </p>
	 *
	 * @see IEditorPart
	 */
	public void doSaveAs() {
		// do nothing
	}

	/**
	 * Fire a property change event.
	 *
	 * @param i a property change value
	 */
	protected void firePropertyChange(int i) {
		if (i == ServerEditorPart.PROP_ERROR)
			updateStatusError();
		super.firePropertyChange(i);
	}

	/**
	 * Return the global command manager.
	 * 
	 * @return the global command manager
	 */
	public GlobalCommandManager getCommandManager() {
		return commandManager;
	}

	/**
	 * Return the redo action.
	 *
	 * @return org.eclipse.jface.action.Action
	 */
	public IAction getRedoAction() {
		return redoAction;
	}

	/**
	 * Return the undo action.
	 *
	 * @return org.eclipse.jface.action.Action
	 */
	public IAction getUndoAction() {
		return undoAction;
	}

	/* (non-Javadoc)
	 * Sets the cursor and selection state for this editor to the passage defined
	 * by the given marker.
	 * <p>
	 * Subclasses may override.  For greater details, see <code>IEditorPart</code>
	 * </p>
	 *
	 * @see IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		// do nothing
	}

	/**
	 * Update the cut, copy, and paste actions.
	 */
	public void updateActionsImpl() {
		if (updatingActions)
			return;
		
		updatingActions = true;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updatingActions = false;
				updateActions();
			}
		});
	}

	/**
	 * Update the cut, copy, and paste actions.
	 */
	public void updateActions() {
		cutAction.update();
		copyAction.update();
		pasteAction.update();
	}
	
	/**
	 * Update the cut, copy, and paste actions.
	 */
	protected void updateStatusLine() {
		if (statusItem != null) {
			boolean readOnly = false;
			if (server != null && commandManager.isReadOnly(serverId))
				readOnly = true;
			
			if (readOnly)
				statusItem.setText(Messages.editorReadOnly);
			else
				statusItem.setText(Messages.editorWritable);
		}

		if (status != null) {
			StringBuffer sb = new StringBuffer();
			boolean first = true;
			if (server != null) {
				IFile[] files = commandManager.getReadOnlyFiles(serverId);
				for (int i = 0; i < files.length; i++) {
					if (!first)
						sb.append(", ");
					sb.append(files[i].getName());
					first = false;
				}
			}
			/*if (serverConfiguration != null) {
				IFile[] files = commandManager.getReadOnlyFiles(serverConfigurationId);
				for (int i = 0; i < files.length; i++) {
					if (!first)
						sb.append(", ");
					sb.append(files[i].getName());
					first = false;
				}
			}*/
			if (sb.length() > 1)
				status.setMessage(NLS.bind(Messages.editorReadOnlyFiles, sb.toString()));
			else
				status.setMessage("");
		}
	}

	/**
	 * 
	 */
	public void updateStatusError() {
		if (status == null)
			return;
		
		String error = null;
		IEditorPart part = getActiveEditor();
		if (part instanceof ServerEditorPart)
			error = ((ServerEditorPart) part).getErrorMessage();
		
		Iterator iterator = serverPages.iterator();
		int count = 0;
		while (error == null && iterator.hasNext()) {
			part = (IEditorPart) iterator.next();
			if (part instanceof ServerEditorPart) {
				error = ((ServerEditorPart) part).getErrorMessage();
				if (error != null)
					error = "[" + getPageText(count) + "] " + error;
			}
			count ++;
		}
		status.setErrorMessage(error);
	}

	/**
	 * 
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		updateStatusError();
	}
	
	/**
	 * Set the status.
	 * 
	 * @param status a status line manager
	 * @param item a status contribution item
	 */
	public void setStatus(IStatusLineManager status, StatusLineContributionItem item) {
		this.status = status;
		this.statusItem = item;
		updateStatusError();
	}
	
	/**
	 * Register for key and traversal events to enable/disable the cut/copy/paste actions.
	 */
	protected void registerEvents(Control control) {
		if (control == null)
			return;
		
		if (control instanceof Text || control instanceof Combo) {
			// register the world... any of these actions could cause the state to change
			control.addTraverseListener(new TraverseListener() {
				public void keyTraversed(TraverseEvent event) {
					updateActionsImpl();
				}
			});
			control.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent event) {
					updateActionsImpl();
				}
				public void keyReleased(KeyEvent event) {
					updateActionsImpl();
				}
			});
			control.addMouseListener(new MouseListener() {
				public void mouseDown(MouseEvent event) {
					// do nothing
				}
				public void mouseUp(MouseEvent event) {
					updateActionsImpl();
				}
				public void mouseDoubleClick(MouseEvent event) {
					updateActionsImpl();
				}
			});
			control.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent event) {
					updateActionsImpl();
				}
				public void focusLost(FocusEvent event) {
					updateActionsImpl();
				}
			});
			if (control instanceof Text) {
				Text text = (Text) control;
				text.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent event) {
						updateActionsImpl();
					}
				});
				text.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent event) {
						updateActionsImpl();
					}
					public void widgetDefaultSelected(SelectionEvent event) {
						updateActionsImpl();
					}
				});
			} else {
				Combo combo = (Combo) control;
				combo.addModifyListener(new ModifyListener() {
					public void modifyText(ModifyEvent event) {
						updateActionsImpl();
					}
				});
				combo.addSelectionListener(new SelectionListener() {
					public void widgetSelected(SelectionEvent event) {
						updateActionsImpl();
					}
					public void widgetDefaultSelected(SelectionEvent event) {
						updateActionsImpl();
					}
				});
			}
		}
		
		if (control instanceof Composite) {
			Control[] children = ((Composite)control).getChildren();
			if (children != null) {
				int size = children.length;
				for (int i = 0; i < size; i++)
					registerEvents(children[i]);
			}
		}
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
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		commandManager = GlobalCommandManager.getInstance();
		super.init(site, input);

		if (input instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) input;
			IFile file = fei.getFile();
			if (file != null && file.exists()) {
				IServer server2 = ServerUIPlugin.findServer(file);
				if (server2 != null)
					serverId = server2.getId();
			}
			if (serverId == null) {
				if (file == null)
					throw new PartInitException(NLS.bind(Messages.errorEditor, Messages.elementUnknownName));
				throw new PartInitException(NLS.bind(Messages.errorEditor, file.getName()));
			}
		} else if (input instanceof IServerEditorInput) {
			IServerEditorInput sei = (IServerEditorInput) input;
			serverId = sei.getServerId();
		}

		if (serverId != null) {
			commandManager.getCommandManager(serverId);
			server = commandManager.getServerResource(serverId);
		}

		ILabelProvider labelProvider = ServerUICore.getLabelProvider();
		if (server != null) {
			setPartName(labelProvider.getText(server));
			setTitleImage(labelProvider.getImage(server));
			setTitleToolTip(serverId);
			serverName = server.getName();
		} else
			setPartName("-");
		labelProvider.dispose();
		labelProvider = null;
		
		cutAction = new TextAction(site.getShell().getDisplay(), TextAction.CUT_ACTION);
		copyAction = new TextAction(site.getShell().getDisplay(), TextAction.COPY_ACTION);
		pasteAction = new TextAction(site.getShell().getDisplay(), TextAction.PASTE_ACTION);
		
		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (GlobalCommandManager.PROP_DIRTY.equals(event.getPropertyName())) {
					Object obj = event.getOldValue();
					if (obj == serverId)
						firePropertyChange(PROP_DIRTY);
				} else if (GlobalCommandManager.PROP_UNDO.equals(event.getPropertyName())) {
					Object obj = event.getOldValue();
					if (obj == serverId)
						updateUndoAction();
				} else if (GlobalCommandManager.PROP_REDO.equals(event.getPropertyName())) {
					Object obj = event.getOldValue();
					if (obj == serverId)
						updateRedoAction();
				} else if (GlobalCommandManager.PROP_RELOAD.equals(event.getPropertyName())) {
					Object obj = event.getOldValue();
					if (obj == serverId) {
						server = commandManager.getServerResource(serverId);
						refresh();
					}
				}
			}
		};
		if (server != null && commandManager.isDirty(serverId))
			firePropertyChange(PROP_DIRTY);
		
		commandManager.addPropertyChangeListener(listener);
		
		// create editor input
		ServerResourceCommandManager serverCommandManager = null;
		if (server != null)
			serverCommandManager = new ServerResourceCommandManager(this, serverId, commandManager);
		editorPartInput = commandManager.getPartInput(serverId, serverCommandManager);
		
		createActions();
		
		// add resource listener
		resourceListener = new LifecycleListener();
		ServerCore.addServerLifecycleListener(resourceListener);
		
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		window.getPartService().addPartListener(activationListener);
		window.getShell().addShellListener(activationListener);
	}

	/* (non-Javadoc)
	 * Returns whether the contents of this editor have changed since the last save
	 * operation.
	 * <p>
	 * Subclasses must override this method to implement the open-save-close lifecycle
	 * for an editor.  For greater details, see <code>IEditorPart</code>
	 * </p>
	 *
	 * @see IEditorPart
	 */
	public boolean isDirty() {
		if (commandManager != null) {
			if (server != null && commandManager.isDirty(serverId))
				return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * Returns whether the "save as" operation is supported by this editor.
	 * <p>
	 * Subclasses must override this method to implement the open-save-close lifecycle
	 * for an editor.  For greater details, see <code>IEditorPart</code>
	 * </p>
	 *
	 * @see IEditorPart
	 */
	public boolean isSaveAsAllowed() {
		return false;
	}

	/**
	 * Update the undo action.
	 */
	protected void updateUndoAction() {
		IUndoableOperation command = commandManager.getUndoCommand(serverId);
		if (command == null) {
			undoAction.setText(Messages.editorUndoDisabled);
			undoAction.setToolTipText("");
			undoAction.setDescription("");
			undoAction.setEnabled(false);
		} else {
			String text = NLS.bind(Messages.editorUndoEnabled, new Object[] {command.getLabel()});
			undoAction.setText(text);
			undoAction.setToolTipText(command.getLabel());
			undoAction.setDescription(command.getLabel());
			undoAction.setEnabled(true);
		}
	}

	/**
	 * Update the redo action.
	 */
	protected void updateRedoAction() {
		IUndoableOperation command = commandManager.getRedoCommand(serverId);
		if (command == null) {
			redoAction.setText(Messages.editorRedoDisabled);
			redoAction.setToolTipText("");
			redoAction.setDescription("");
			redoAction.setEnabled(false);
		} else {
			String text = NLS.bind(Messages.editorRedoEnabled, new Object[] {command.getLabel()});
			redoAction.setText(text);
			redoAction.setToolTipText(command.getLabel());
			redoAction.setDescription(command.getLabel());
			redoAction.setEnabled(true);
		}
	}
	
	/**
	 * Return the cut action.
	 * 
	 * @return org.eclipse.jface.action.IAction
	 */
	public IAction getCutAction() {
		return cutAction;
	}
	
	/**
	 * Return the copy action.
	 * 
	 * @return org.eclipse.jface.action.IAction
	 */
	public IAction getCopyAction() {
		return copyAction;
	}
	
	/**
	 * Return the paste action.
	 * 
	 * @return org.eclipse.jface.action.IAction
	 */
	public IAction getPasteAction() {
		return pasteAction;
	}
	
	/**
	 * Returns the editor actions.
	 * 
	 * @return org.eclipse.jface.action.IAction
	 */
	public IAction[] getEditorActions() {
		return editorActions;
	}
	
	/**
	 * Update the server pages with new input.
	 */
	protected void refresh() {
		// create editor input
		ServerResourceCommandManager serverCommandManager = null;
		if (server != null)
			serverCommandManager = new ServerResourceCommandManager(this, serverId, commandManager);
		editorPartInput = commandManager.getPartInput(serverId, serverCommandManager);
		
		Iterator iterator = serverPages.iterator();
		while (iterator.hasNext()) {
			IEditorPart part = (IEditorPart) iterator.next();						
			try {
				part.init(part.getEditorSite(), editorPartInput);
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error refresh()ing editor part", e);
			}
		}	
	}
	
	/**
	 * 
	 */
	protected void promptReadOnlyServerFile(String id) {
		commandManager.setReadOnly(id, true);
		String title = Messages.editorResourceModifiedTitle;
		String message = Messages.editorReadOnlyMessage;
		MessageDialog.openInformation(getEditorSite().getShell(), title, message);
	}

	/**
	 * 
	 */
	protected void promptReloadServerFile(String id) {
		String title = Messages.editorResourceModifiedTitle;
		String message = Messages.editorServerModifiedMessage;
		
		if (MessageDialog.openQuestion(getEditorSite().getShell(), title, message)) {
			/*try {
				//wc.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
				//TODO: refresh local server
			} catch (Exception e) {
				Trace.trace(Trace.SEVERE, "Error refreshing server", e);
			}*/
			commandManager.reload(id);
		}
	}

	/**
	 * 
	 */
	public void setFocus() {
		super.setFocus();
	}

	/**
	 * 
	 */
	protected void checkResourceState() {
		// do not check the resource state change if saving through the editor
		if (isSaving) {
			// do nothing
			return;
		}
		
		// check for deleted files
		if (resourceDeleted) {
			String title = Messages.editorResourceDeleteTitle;
			String message = null;
			if (server != null)
				message = NLS.bind(Messages.editorResourceDeleteServerMessage, server.getName());
			String[] labels = new String[] {Messages.editorResourceDeleteSave, IDialogConstants.CLOSE_LABEL};
			MessageDialog dialog = new MessageDialog(getEditorSite().getShell(), title, null, message, MessageDialog.INFORMATION, labels, 0);
			
			if (dialog.open() == 0)
				doSave(new NullProgressMonitor());
			else
				closeEditor();
			return;
		}
		resourceDeleted = false;
		
		// check for server changes
		if (serverId != null) {
			if (!commandManager.isDirty(serverId)) {
				if (commandManager.hasChanged(serverId)) {
					IServer newServer = ServerCore.findServer(serverId);
					if (newServer != null && ((Server)newServer).getTimestamp() > ((Server)server).getTimestamp())
						commandManager.reload(serverId);
					else
						promptReloadServerFile(serverId);
				}
			} else {
				if (commandManager.hasChanged(serverId) && !commandManager.areFilesReadOnly(serverId))
					promptReloadServerFile(serverId);
				else if (commandManager.areFilesReadOnly(serverId) && !commandManager.isReadOnly(serverId))
					promptReadOnlyServerFile(serverId);
			}
			if (commandManager.isReadOnly(serverId) && !commandManager.areFilesReadOnly(serverId))
				commandManager.setReadOnly(serverId, false);
			commandManager.updateTimestamps(serverId);
		}
		
		updateStatusLine();
	}

	/**
	 * Set the title tooltip.
	 * 
	 * @return the title tooltip
	 */
	public String getTitleToolTip() {
		Server server2 = (Server) server;
		if (server != null && server2.getFile() != null)
			return server2.getFile().getFullPath().toString();
		else if (server != null)
			return server.getName();
		else
			return "error";
	}

	public int getOrientation() {
		return Window.getDefaultOrientation();
	}
}