package org.eclipse.wst.server.ui.internal.editor;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.eclipse.ui.part.MultiPageEditorPart;

import org.eclipse.wst.server.core.*;
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
					fIsHandlingActivation= false;
				}
			}
		}
	}
	
	class LifecycleListener implements IServerLifecycleListener, IServerConfigurationLifecycleListener {
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

		public void serverConfigurationAdded(IServerConfiguration oldServerConfiguration) {
			// do nothing
		}
		public void serverConfigurationChanged(IServerConfiguration oldServerConfiguration) {
			// do nothing
		}
		public void serverConfigurationRemoved(IServerConfiguration oldServerConfiguration) {
			if (oldServerConfiguration.equals(serverConfiguration) && !isDirty())
				closeEditor();
		}
	}

	protected IServerWorkingCopy server;
	protected IServerConfigurationWorkingCopy serverConfiguration;
	protected String serverId;
	protected String serverConfigurationId;

	protected GlobalCommandManager commandManager;

	protected PropertyChangeListener listener;

	protected IAction undoAction;
	protected IAction redoAction;
	
	protected TextAction cutAction;
	protected TextAction copyAction;
	protected TextAction pasteAction;
	protected boolean updatingActions;
	
	protected IAction[] editorActions;

	protected java.util.List serverPages;
	
	// on focus change flag
	protected boolean resourceDeleted;
	
	// input given to the contained pages
	protected IServerEditorPartInput editorPartInput;
	
	// status line and status
	protected IStatusLineManager status;
	protected StatusLineContributionItem statusItem;
	
	private ActivationListener activationListener = new ActivationListener();
	protected LifecycleListener resourceListener;
	
	// Used for disabling resource change check when saving through editor.
	protected boolean isSaving = false;

	/**
	 * ServerEditor constructor comment.
	 */
	public ServerEditor() {
		super();
		
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

		undoAction = new Action() {
			public void run() {
				getCommandManager().undo(serverId, serverConfigurationId);
			}
		};
		undoAction.setEnabled(false);
		undoAction.setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO));
		//undoAction.setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_HOVER));
		//undoAction.setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_UNDO_DISABLED));
	
		redoAction = new Action() {
			public void run() {
				getCommandManager().redo(serverId, serverConfigurationId);
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
		List actionList = new ArrayList();
		
		// add server actions
		if (server != null && server.getServerType() != null) {
			Iterator iterator = ServerEditorCore.getServerEditorActionFactories().iterator();
			String id = server.getServerType().getId();
			while (iterator.hasNext()) {
				ServerEditorActionFactory factory = (ServerEditorActionFactory) iterator.next();
				if (factory.supportsServerElementType(id) && factory.shouldDisplay(server, serverConfiguration))
					actionList.add(factory.createAction(getEditorSite(), editorPartInput));
			}
		}
		
		// add server configuration actions
		if (serverConfiguration != null && serverConfiguration.getServerConfigurationType() != null) {
			Iterator iterator = ServerEditorCore.getServerEditorActionFactories().iterator();
			String id = serverConfiguration.getServerConfigurationType().getId();
			while (iterator.hasNext()) {
				ServerEditorActionFactory factory = (ServerEditorActionFactory) iterator.next();
				if (factory.supportsServerElementType(id) && factory.shouldDisplay(server, serverConfiguration))
					actionList.add(factory.createAction(getEditorSite(), editorPartInput));
			}
		}

		editorActions = new IAction[actionList.size()];
		actionList.toArray(editorActions);
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
			serverPages = new ArrayList();
			
			// add editor pages
			int pageCount = 0;
			
			String serverTypeId = null;
			String serverConfigurationTypeId = null;
			if (server != null && server.getServerType() != null)
				serverTypeId = server.getServerType().getId();
			if (serverConfiguration != null && serverConfiguration.getServerConfigurationType() != null)
				serverConfigurationTypeId = serverConfiguration.getServerConfigurationType().getId();
	
			Iterator iterator = ServerEditorCore.getServerEditorPageFactories().iterator();
			while (iterator.hasNext()) {
				IServerEditorPartFactory factory = (IServerEditorPartFactory) iterator.next();
				if (((serverTypeId != null && factory.supportsType(serverTypeId)) || 
						(serverConfigurationTypeId != null && factory.supportsType(serverConfigurationTypeId)))
						&& factory.shouldCreatePage(server, serverConfiguration)) {
					Trace.trace(Trace.FINEST, "Adding page: " + factory.getId() + " " + editorPartInput);
					try {
						IEditorPart page = factory.createPage();
						if (page instanceof ServerResourceEditorPart) {
							ServerResourceEditorPart srep = (ServerResourceEditorPart) page;
							srep.setPageFactory(factory);
						}
						index = addPage(page, editorPartInput);
						serverPages.add(page);
	
						setPageText(index, factory.getName());
				
						pageCount ++;
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE, "Could not display editor page " + factory.getId(), e);
					}
				}
			}
			
			setActivePage(0);
			
			// register for events that might change the cut/copy/paste actions
			int count = getPageCount();
			for (int i = 0; i < count; i++) {
				Control control = getControl(i);
				registerEvents(control);
			}
			updateActions();
		} catch (Exception e) {
			Trace.trace("Error creating server editor pages", e);
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
		
		if (resourceListener != null) {
			ServerCore.removeServerLifecycleListener(resourceListener);
			ServerCore.removeServerConfigurationLifecycleListener(resourceListener);
		}

		super.dispose();
		if (commandManager != null)
			commandManager.removePropertyChangeListener(listener);

		if (serverId != null)
			commandManager.releaseCommandManager(serverId);
			
		if (serverConfigurationId != null)
			commandManager.releaseCommandManager(serverConfigurationId);
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
		// Set the isSaving flag to true.
		isSaving = true;
		
		// check pages for errors first
		java.util.List errors = new ArrayList();
		Iterator iterator = serverPages.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			IEditorPart part = (IEditorPart) iterator.next();
			if (part instanceof ServerResourceEditorPart) {
				IStatus[] status2 = ((ServerResourceEditorPart) part).getSaveStatus();
				if (status2 != null) {
					int size = status2.length;
					for (int i = 0; i < size; i++)
						errors.add("[" + getPageText(count) + "] " + status2[i].getMessage());
				}
			}
			count ++;
		}
		if (!errors.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append(ServerUIPlugin.getResource("%errorEditorCantSave") + "\n");
			iterator = errors.iterator();
			while (iterator.hasNext())
				sb.append("\t" + ((String) iterator.next()) + "\n");

			EclipseUtil.openError(getEditorSite().getShell(), sb.toString());
			monitor.setCanceled(true);
			// Reset the isSaving flag.
			isSaving = false;
			return;
		}
		
		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			int ticks = 2000;
			String name = "";
			if (server != null)
				name = server.getName();
			else
				name = serverConfiguration.getName();
			monitor.beginTask(ServerUIPlugin.getResource("%savingTask", name), ticks);
			if (server != null && serverConfiguration != null)
				ticks /= 2;

			if (server != null)  {
				server.save(false, ProgressUtil.getSubMonitorFor(monitor, ticks));
				getCommandManager().resourceSaved(serverId);
				commandManager.updateTimestamps(serverId);
			}

			if (serverConfiguration != null) {
				serverConfiguration.save(false, ProgressUtil.getSubMonitorFor(monitor, ticks));
				getCommandManager().resourceSaved(serverConfigurationId);
				commandManager.updateTimestamps(serverConfigurationId);
			}
			
			ILabelProvider labelProvider = ServerUICore.getLabelProvider();
			if (server != null)
				setPartName(labelProvider.getText(server));
			else
				setPartName(labelProvider.getText(serverConfiguration));
		} catch (Exception e) {
			Trace.trace("Error saving from configuration editor", e);
	
			monitor.setCanceled(true);
	
			String title = ServerUIPlugin.getResource("%editorSaveErrorDialog");
			String message = ServerUIPlugin.getResource("%editorSaveErrorMessage", e.getLocalizedMessage());
			MessageDialog.openError(getEditorSite().getShell(), title,  message);
		} finally {
			monitor.done();
		}
		// Reset the isSaving flag.
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
	 * @param i int
	 */
	protected void firePropertyChange(int i) {
		if (i == ServerResourceEditorPart.PROP_ERROR)
			updateStatusError();
		super.firePropertyChange(i);
	}

	/**
	 * 
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
			else if (serverConfiguration != null && commandManager.areFilesReadOnly(serverConfigurationId))
				readOnly = true;
			
			if (readOnly)
				statusItem.setText(ServerUIPlugin.getResource("%editorReadOnly"));
			else
				statusItem.setText(ServerUIPlugin.getResource("%editorWritable"));
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
			if (serverConfiguration != null) {
				IFile[] files = commandManager.getReadOnlyFiles(serverConfigurationId);
				for (int i = 0; i < files.length; i++) {
					if (!first)
						sb.append(", ");
					sb.append(files[i].getName());
					first = false;
				}
			}
			if (sb.length() > 1)
				status.setMessage(ServerUIPlugin.getResource("%editorReadOnlyFiles", sb.toString()));
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
		if (part instanceof ServerResourceEditorPart)
			error = ((ServerResourceEditorPart) part).getErrorMessage();
		
		Iterator iterator = serverPages.iterator();
		int count = 0;
		while (error == null && iterator.hasNext()) {
			part = (IEditorPart) iterator.next();
			if (part instanceof ServerResourceEditorPart) {
				error = ((ServerResourceEditorPart) part).getErrorMessage();
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
	 * 
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
				IServer server2 = ServerUtil.getServer(file);
				if (server2 != null) {
					serverId = server2.getId();
					if (server2.getServerConfiguration() != null)
						serverConfigurationId = server2.getServerConfiguration().getId();
				} else {
					IServerConfiguration configuration = ServerUtil.getServerConfiguration(file);
					if (configuration != null)
						serverConfigurationId = configuration.getId();
				}
			}
			if (serverId == null && serverConfigurationId == null)
				throw new PartInitException(ServerUIPlugin.getResource("%errorEditor", file.getName()));
		} else if (input instanceof IServerEditorInput) {
			IServerEditorInput sei = (IServerEditorInput) input;
			serverId = sei.getServerId();
			serverConfigurationId = sei.getServerConfigurationId();
		}

		if (serverId != null) {
			commandManager.getCommandManager(serverId);
			server = (IServerWorkingCopy) commandManager.getServerResource(serverId);
		}

		if (serverConfigurationId != null) {
			commandManager.getCommandManager(serverConfigurationId);
			serverConfiguration = (IServerConfigurationWorkingCopy) commandManager.getServerResource(serverConfigurationId);
		}

		ILabelProvider labelProvider = ServerUICore.getLabelProvider();
		if (server != null) {
			setPartName(labelProvider.getText(server));
			setTitleImage(labelProvider.getImage(server));
			setTitleToolTip(serverId);
		} else if (serverConfiguration != null) {
			setPartName(labelProvider.getText(serverConfiguration));
			setTitleImage(labelProvider.getImage(serverConfiguration));
			setTitleToolTip(serverConfigurationId);
		} else {
			setPartName("-");	
		}

		cutAction = new TextAction(site.getShell().getDisplay(), TextAction.CUT_ACTION);
		copyAction = new TextAction(site.getShell().getDisplay(), TextAction.COPY_ACTION);
		pasteAction = new TextAction(site.getShell().getDisplay(), TextAction.PASTE_ACTION);

		listener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (GlobalCommandManager.PROP_DIRTY.equals(event.getPropertyName())) {
					Object obj = event.getOldValue();
					if (obj == serverId || obj == serverConfigurationId)
						firePropertyChange(PROP_DIRTY);
				} else if (GlobalCommandManager.PROP_UNDO.equals(event.getPropertyName())) {
					Object obj = event.getOldValue();
					if (obj == serverId || obj == serverConfigurationId)
						updateUndoAction();
				} else if (GlobalCommandManager.PROP_REDO.equals(event.getPropertyName())) {
					Object obj = event.getOldValue();
					if (obj == serverId || obj == serverConfigurationId)
						updateRedoAction();
				} else if (GlobalCommandManager.PROP_RELOAD.equals(event.getPropertyName())) {
					Object obj = event.getOldValue();
					if (obj == serverId) {
						server = (IServerWorkingCopy) commandManager.getServerResource(serverId);
						refresh();
					} else if (obj == serverConfigurationId) {
						serverConfiguration = (IServerConfigurationWorkingCopy) commandManager.getServerResource(serverConfigurationId);
						refresh();
					}
				}
			}
		};
		if ((server != null && commandManager.isDirty(serverId)) ||
			(serverConfiguration != null && commandManager.isDirty(serverConfigurationId)))
			firePropertyChange(PROP_DIRTY);

		commandManager.addPropertyChangeListener(listener);
		
		// create editor input
		ICommandManager serverCommandManager = null;
		if (server != null)
			serverCommandManager = new ServerResourceCommandManager(this, serverId, commandManager);
		ICommandManager configurationCommandManager = null;
		if (serverConfiguration != null)
			configurationCommandManager = new ServerResourceCommandManager(this, serverConfigurationId, commandManager);
		editorPartInput = commandManager.getPartInput(serverId, serverCommandManager, serverConfigurationId, configurationCommandManager);
		
		createActions();
		
		// add resource listener
		resourceListener = new LifecycleListener();
		ServerCore.addServerLifecycleListener(resourceListener);
		ServerCore.addServerConfigurationLifecycleListener(resourceListener);
		
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
			if (serverConfiguration != null && commandManager.isDirty(serverConfigurationId))
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
		ITask command = commandManager.getUndoCommand(serverId, serverConfigurationId);
		if (command == null) {
			undoAction.setText(ServerUIPlugin.getResource("%editorUndoDisabled"));
			undoAction.setToolTipText("");
			undoAction.setDescription("");
			undoAction.setEnabled(false);
		} else {
			String text = ServerUIPlugin.getResource("%editorUndoEnabled", new Object[] {command.getName()});
			undoAction.setText(text);
			undoAction.setToolTipText(command.getDescription());
			undoAction.setDescription(command.getDescription());
			undoAction.setEnabled(true);
		}
	}

	/**
	 * Update the redo action.
	 */
	protected void updateRedoAction() {
		ITask command = commandManager.getRedoCommand(serverId, serverConfigurationId);
		if (command == null) {
			redoAction.setText(ServerUIPlugin.getResource("%editorRedoDisabled"));
			redoAction.setToolTipText("");
			redoAction.setDescription("");
			redoAction.setEnabled(false);
		} else {
			String text = ServerUIPlugin.getResource("%editorRedoEnabled", new Object[] {command.getName()});
			redoAction.setText(text);
			redoAction.setToolTipText(command.getDescription());
			redoAction.setDescription(command.getDescription());
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
		ICommandManager serverCommandManager = null;
		if (server != null)
			serverCommandManager = new ServerResourceCommandManager(this, serverId, commandManager);
		ICommandManager configurationCommandManager = null;
		if (serverConfiguration != null)
			configurationCommandManager = new ServerResourceCommandManager(this, serverConfigurationId, commandManager);
		editorPartInput = commandManager.getPartInput(serverId, serverCommandManager, serverConfigurationId, configurationCommandManager);
		
		Iterator iterator = serverPages.iterator();
		while (iterator.hasNext()) {
			IEditorPart part = (IEditorPart) iterator.next();						
			try {
				part.init(part.getEditorSite(), editorPartInput);
			} catch (Exception e) {
				Trace.trace("Error refresh()ing editor part", e);
			}
		}	
	}
	
	/**
	 * 
	 */
	protected void promptReadOnlyServerFile(String id) {
		commandManager.setReadOnly(id, true);
		String title = ServerUIPlugin.getResource("%editorResourceModifiedTitle");
		String message = ServerUIPlugin.getResource("%editorReadOnlyMessage");
		MessageDialog.openInformation(getEditorSite().getShell(), title, message);
	}

	/**
	 * 
	 */
	protected void promptReloadServerFile(String id, IServer serverFile2) {
		String title = ServerUIPlugin.getResource("%editorResourceModifiedTitle");
		String message = ServerUIPlugin.getResource("%editorServerModifiedMessage");

		if (MessageDialog.openQuestion(getEditorSite().getShell(), title, message)) {
			try {
				//file.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
				//TODO
			} catch (Exception e) {
				Trace.trace("Error refreshing server", e);
			}
			commandManager.reload(id, new NullProgressMonitor());
		}
	}
	
	protected void promptReloadServerConfigurationFile(String id, IServerConfiguration serverFile2) {
		String title = ServerUIPlugin.getResource("%editorResourceModifiedTitle");
		String message = ServerUIPlugin.getResource("%editorServerConfigurationModifiedMessage");

		if (MessageDialog.openQuestion(getEditorSite().getShell(), title, message)) {
			try {
				//file.refreshLocal(IResource.DEPTH_ONE, new NullProgressMonitor());
				//TODO
			} catch (Exception e) {
				Trace.trace("Error refreshing server", e);
			}
			commandManager.reload(id, new NullProgressMonitor());
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
		// Do not check the resource state change if saving through the editor.
		if (isSaving) {
			// Do nothing.
			return;
		}
		
		// check for deleted files
		if (resourceDeleted) {
			String title = ServerUIPlugin.getResource("%editorResourceDeleteTitle");
			String message = null;
			if (server != null && serverConfiguration != null)
				message = ServerUIPlugin.getResource("%editorResourceDeleteBothMessage", new String[] {server.getName(), serverConfiguration.getName()});
			else if (server != null)
				message = ServerUIPlugin.getResource("%editorResourceDeleteServerMessage", server.getName());
			else
				message = ServerUIPlugin.getResource("%editorResourceDeleteServerConfigurationMessage", serverConfiguration.getName());
			String[] labels = new String[] {ServerUIPlugin.getResource("%editorResourceDeleteSave"), IDialogConstants.CLOSE_LABEL};
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
				if (commandManager.hasChanged(serverId))
					promptReloadServerFile(serverId, server);
			} else {
				if (commandManager.hasChanged(serverId) && !commandManager.areFilesReadOnly(serverId))
					promptReloadServerFile(serverId, server);
				else if (commandManager.areFilesReadOnly(serverId) && !commandManager.isReadOnly(serverId))
					promptReadOnlyServerFile(serverId);
			}
			if (commandManager.isReadOnly(serverId) && !commandManager.areFilesReadOnly(serverId))
				commandManager.setReadOnly(serverId, false);
			commandManager.updateTimestamps(serverId);
		}

		// check for server configuration changes
		if (serverConfigurationId != null) {
			if (!commandManager.isDirty(serverConfigurationId)) {
				if (commandManager.hasChanged(serverConfigurationId))
					promptReloadServerConfigurationFile(serverConfigurationId, serverConfiguration);
			} else {
				if (commandManager.hasChanged(serverConfigurationId) && !commandManager.areFilesReadOnly(serverConfigurationId))
					promptReloadServerConfigurationFile(serverConfigurationId, serverConfiguration);
				else if (commandManager.areFilesReadOnly(serverConfigurationId) && !commandManager.isReadOnly(serverConfigurationId))
					promptReadOnlyServerFile(serverConfigurationId);
			}		
			if (commandManager.isReadOnly(serverConfigurationId) && !commandManager.areFilesReadOnly(serverConfigurationId))
				commandManager.setReadOnly(serverConfigurationId, false);
			commandManager.updateTimestamps(serverConfigurationId);
		}

		updateStatusLine();
	}
	
	/**
	 * Set the tooltip.
	 */
	public String getTitleToolTip() {
		if (server != null && server.getFile() != null)
			return server.getFile().getFullPath().toString();
		else if (serverConfiguration != null && serverConfiguration.getFile() != null)
			return serverConfiguration.getFile().getFullPath().toString();
		else if (server != null)
			return server.getName();
		else if (serverConfiguration != null)
			return serverConfiguration.getName();
		else
			return "error";
	}
}