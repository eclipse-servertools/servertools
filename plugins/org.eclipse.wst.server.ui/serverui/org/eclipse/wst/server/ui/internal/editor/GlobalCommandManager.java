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
 *
 **********************************************************************/
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.server.core.IElement;
import org.eclipse.wst.server.core.IElementWorkingCopy;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.editor.ICommandManager;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
import org.eclipse.wst.server.ui.editor.ServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class GlobalCommandManager {
	// maximum number of commands in the history
	private static final int MAX_HISTORY = 200;

	class ServerResourceCommand {
		ITask command;
		String id;
	}
	
	// commands in the undo history
	protected List undoList = new ArrayList();

	// commands in the redo history
	protected List redoList = new ArrayList();

	class CommandManagerInfo {
		// number of open editors on this resource
		int count;
		
		// true if the resource has not been saved since
		// the last change
		boolean isDirty;
		
		// true if the resource is read-only
		boolean isReadOnly;
		
		// true if all changes can be undone, false if
		// a non-reversable change has been made
		boolean canCompletelyUndo = true;
		
		// the element id
		String id;
		
		// the working copy
		IElementWorkingCopy wc;
		
		// files and timestamps
		Map fileMap;
		
		int timestamp;
	}

	protected Map commandManagers = new HashMap();

	// property change listeners
	protected List propertyListeners;
	public static final String PROP_DIRTY = "dirtyState";
	public static final String PROP_UNDO = "undoAction";
	public static final String PROP_REDO = "redoAction";
	public static final String PROP_RELOAD = "reload";

	protected static GlobalCommandManager instance;

	public static GlobalCommandManager getInstance() {
		if (instance == null)
			instance = new GlobalCommandManager();
		return instance;
	}

	/**
	 * Add a property change listener to this instance.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners == null)
			propertyListeners = new ArrayList();
		propertyListeners.add(listener);
	}

	/**
	 * Remove a property change listener from this instance.
	 *
	 * @param listener java.beans.PropertyChangeListener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		if (propertyListeners != null)
			propertyListeners.remove(listener);
	}

	/**
	 * Fire a property change event.
	 */
	protected void firePropertyChangeEvent(String propertyName, Object oldValue, Object newValue) {
		if (propertyListeners == null)
			return;

		PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, oldValue, newValue);
		//Trace.trace("Firing: " + event + " " + oldValue);
		try {
			int size = propertyListeners.size();
			PropertyChangeListener[] pcl = new PropertyChangeListener[size];
			propertyListeners.toArray(pcl);
			
			for (int i = 0; i < size; i++)
				try {
					pcl[i].propertyChange(event);
				} catch (Exception e) { }
		} catch (Exception e) { }
	}

	/**
	 * 
	 */
	public void getCommandManager(String id) {
		Trace.trace("Getting command manager for " + id);
		try {
			CommandManagerInfo info = (CommandManagerInfo) commandManagers.get(id);
			if (info != null) {
				info.count ++;
				return;
			}
		} catch (Exception e) {
			Trace.trace("Could not find existing command manager", e);
		}
		Trace.trace("Creating new command manager for " + id);
		try {
			CommandManagerInfo info = new CommandManagerInfo();
			info.count = 1;
			info.id = id;
			IServer server = ServerCore.getResourceManager().getServer(id);
			if (server != null)
				info.wc = server.getWorkingCopy();
			else {
				IServerConfiguration config = ServerCore.getResourceManager().getServerConfiguration(id);
				if (config != null)
					info.wc = config.getWorkingCopy();
			}
			info.isDirty = false;
			info.isReadOnly = false;
			commandManagers.put(id, info);
			updateTimestamps(id);
		} catch (Exception e) {
			Trace.trace("Could not obtain command manager", e);
		}
		return;
	}

	/**
	 * 
	 */
	public void releaseCommandManager(String id) {
		Trace.trace("Releasing command manager for " + id);
		try {
			CommandManagerInfo info = (CommandManagerInfo) commandManagers.get(id);
			if (info != null) {
				info.count --;
				if (info.count == 0) {
					if (info.wc instanceof IServerWorkingCopy) {
						IServerWorkingCopy wc = (IServerWorkingCopy) info.wc;
						wc.release();
					} else {
						IServerConfigurationWorkingCopy wc = (IServerConfigurationWorkingCopy) info.wc;
						wc.release();
					}
					
					commandManagers.remove(id);
					clearUndoList(id);
					clearRedoList(id);
				}
			}
		} catch (Exception e) {
			Trace.trace("Could not release command manager", e);
		}
	}

	/**
	 * 
	 */
	public void reload(String id, IProgressMonitor monitor) {
		try {
			CommandManagerInfo info = getExistingCommandManagerInfo(id);
			if (info != null) {
				IServer server = ServerCore.getResourceManager().getServer(id);
				if (server != null)
					info.wc = server.getWorkingCopy();
				else {
					IServerConfiguration config = ServerCore.getResourceManager().getServerConfiguration(id);
					if (config != null)
						info.wc = config.getWorkingCopy();
				}
				//info.serverElement = ServerCore.getResourceManager().getServer()
				//info.serverElement = ServerCore.getEditManager().reloadEditModel(info.file, monitor);
				firePropertyChangeEvent(PROP_RELOAD, id, null);
			}
		} catch (Exception e) {
			Trace.trace("Could not release command manager", e);
		}
	}
	
	/**
	 * 
	 */
	protected CommandManagerInfo getExistingCommandManagerInfo(String id) {
		try {
			return (CommandManagerInfo) commandManagers.get(id);
		} catch (Exception e) {
			Trace.trace("Could not find existing command manager info");
		}
		return null;
	}

	/**
	 * Returns true if there is only one command manager.
	 */
	public boolean isOnlyCommandManager(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		return (info != null && info.count == 1);
	}
	
	protected IServerEditorPartInput getPartInput(String serverId, ICommandManager serverCommandManager, String configurationId, ICommandManager configurationCommandManager) {
		CommandManagerInfo serverInfo = null;
		IServerWorkingCopy server = null;
		boolean serverReadOnly = false;
		if (serverId != null) {
			serverInfo = getExistingCommandManagerInfo(serverId);
			if (serverInfo == null)
				return null;
			else {
				server = (IServerWorkingCopy) serverInfo.wc;
				serverReadOnly = serverInfo.isReadOnly;
			}
		}
		
		CommandManagerInfo configurationInfo = null;
		IServerConfigurationWorkingCopy configuration = null;
		boolean configurationReadOnly = false;
		if (configurationId != null) {
			configurationInfo = getExistingCommandManagerInfo(configurationId);
			if (configurationInfo == null)
				return null;
			else {
				configuration = (IServerConfigurationWorkingCopy) configurationInfo.wc;
				configurationReadOnly = configurationInfo.isReadOnly;
			}
		}

		return new ServerEditorPartInput(serverCommandManager, server, serverReadOnly,
			configurationCommandManager, configuration, configurationReadOnly);
	}
	
	/**
	 * 
	 */
	protected IElement getServerResource(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return null;
			
		return info.wc;
	}

	/**
	 * Execute the given command and place it in the undo stack.
	 * If the command cannot be undone, the user will be notifed
	 * before it is executed.
	 *
	 * @param command ICommand
	 */
	public void executeCommand(String id, ITask command) {
		if (!command.canUndo() && !undoList.isEmpty() && ServerUICore.getPreferences().getPromptBeforeIrreversibleChange()) {
			try {
				Display d = Display.getCurrent();
				if (d == null)
					d = Display.getDefault();
		
				Shell shell = d.getActiveShell();
				if (!MessageDialog.openConfirm(shell, ServerUIPlugin.getResource("%editorServerEditor"), ServerUIPlugin.getResource("%editorPromptIrreversible")))
					return;
			} catch (Exception e) { }
		}
		
		ServerResourceCommand src = new ServerResourceCommand();
		src.id = id;
		src.command = command;

		try {
			command.execute(new NullProgressMonitor());
		} catch (CoreException ce) {
			return;
		}

		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return;

		if (command.canUndo())
			addToUndoList(src);
		else {
			info.canCompletelyUndo = false;
			clearUndoList(id);
		}

		// clear redo list since a new command has been executed.
		clearRedoList(id);

		setDirtyState(id, true);
	}

	/**
	 * Add a command to the history.
	 */
	private void addToUndoList(ServerResourceCommand src) {
		undoList.add(src);

		// limit history growth
		if (undoList.size() > MAX_HISTORY)
			undoList.remove(0);

		firePropertyChangeEvent(PROP_UNDO, src.id, null);
	}

	/**
	 * Clears the undo of a particular resource.
	 */
	private void clearUndoList(String id) {
		int i = 0;
		boolean modified = false;
		while (i < undoList.size()) {
			ServerResourceCommand src = (ServerResourceCommand) undoList.get(i);
			if (src.id.equals(id)) {
				modified = true;
				undoList.remove(i);
			} else
				i++;
		}
		if (modified)
			firePropertyChangeEvent(PROP_UNDO, id, null);
	}

	/**
	 * Clears the redo of a particular resource.
	 */
	private void clearRedoList(String id) {
		int i = 0;
		boolean modified = false;
		while (i < redoList.size()) {
			ServerResourceCommand src = (ServerResourceCommand) redoList.get(i);
			if (src.id.equals(id)) {
				redoList.remove(i);
				modified = true;
			} else
				i++;
		}
		if (modified)
			firePropertyChangeEvent(PROP_REDO, id, null);
	}

	/**
	 * Returns true if there is a command that can be undone.
	 * @return boolean
	 */
	protected boolean canUndo(String a, String b) {
		Iterator iterator = undoList.iterator();
		while (iterator.hasNext()) {
			ServerResourceCommand src = (ServerResourceCommand) iterator.next();
			if (src.id == a || src.id == b)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns true if there is a command that can be redone.
	 * @return boolean
	 */
	protected boolean canRedo(String a, String b) {
		Iterator iterator = redoList.iterator();
		while (iterator.hasNext()) {
			ServerResourceCommand src = (ServerResourceCommand) iterator.next();
			if (src.id == a || src.id == b)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns the command that would be undone next.
	 * 
	 * @return org.eclipse.wst.server.ui.editor.ICommand
	 */
	public ITask getUndoCommand(String a, String b) {
		int size = undoList.size();
		for (int i = size - 1; i >= 0; i--) {
			ServerResourceCommand src = (ServerResourceCommand) undoList.get(i);
			if (src.id == a || src.id == b)
				return src.command;
		}
		return null;
	}

	/**
	 * Returns the command that would be redone next.
	 * 
	 * @return org.eclipse.wst.server.ui.editor.ICommand
	 */
	public ITask getRedoCommand(String a, String b) {
		int size = redoList.size();
		for (int i = size - 1; i >= 0; i--) {
			ServerResourceCommand src = (ServerResourceCommand) redoList.get(i);
			if (src.id == a || src.id == b)
				return src.command;
		}
		return null;
	}

	/**
	 * Returns true if the server resource is "dirty".
	 *
	 * @return boolean
	 */
	public boolean isDirty(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return false;
		else
			return info.isDirty;
	}
	
	/**
	 * Returns true if the server resource is read-only.
	 *
	 * @return boolean
	 */
	public boolean isReadOnly(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return false;
		else
			return info.isReadOnly;
	}
	
	/**
	 * Returns the server resource read-only flag.
	 */
	public void setReadOnly(String id, boolean readOnly) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return;
		else {
			if (info.isReadOnly == readOnly)
				return;
			info.isReadOnly = readOnly;
			firePropertyChangeEvent(PROP_RELOAD, id, null);
		}
	}

	/**
	 * Returns true if the server resource files are read-only.
	 *
	 * @return boolean
	 */
	public boolean areFilesReadOnly(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return false;
		else
			return (getReadOnlyFiles(id).length > 0);
	}
	
	/**
	 * Sets the dirty state and fires an event if needed.
	 * @param dirty boolean
	 */
	private void setDirtyState(String id, boolean dirty) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info.isDirty == dirty)
			return;

		info.isDirty = dirty;
		firePropertyChangeEvent(PROP_DIRTY, id, null);
	}

	/**
	 * Undo the last command.
	 */
	protected void undo(String a, String b) {
		ServerResourceCommand src = null;
		Iterator iterator = undoList.iterator();
		while (iterator.hasNext()) {
			ServerResourceCommand src2 = (ServerResourceCommand) iterator.next();
			if (src2.id == a || src2.id == b)
				src = src2;
		}
		if (src == null)
			return;

		src.command.undo();
		undoList.remove(src);
		firePropertyChangeEvent(PROP_UNDO, src.id, null);
		redoList.add(src);
		firePropertyChangeEvent(PROP_REDO, src.id, null);

		CommandManagerInfo info = getExistingCommandManagerInfo(src.id);
		if (info.canCompletelyUndo && getUndoCommand(src.id, null) == null)
			setDirtyState(src.id, false);
	}

	/**
	 * Redo the last command.
	 */
	protected void redo(String a, String b) {
		ServerResourceCommand src = null;
		Iterator iterator = redoList.iterator();
		while (iterator.hasNext()) {
			ServerResourceCommand src2 = (ServerResourceCommand) iterator.next();
			if (src2.id == a || src2.id == b)
				src = src2;
		}
		if (src == null)
			return;

		try {
			src.command.execute(new NullProgressMonitor());
		} catch (CoreException ce) {
			return;
		}
		redoList.remove(src);
		firePropertyChangeEvent(PROP_REDO, src.id, null);
		undoList.add(src);
		firePropertyChangeEvent(PROP_UNDO, src.id, null);

		setDirtyState(src.id, true);
	}

	/**
	 * Clears the history list.
	 */
	public void resourceSaved(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		//clearUndoList(resource);
		//clearRedoList(resource);
		info.canCompletelyUndo = true;
		setDirtyState(id, false);
	}
	
	/**
	 * 
	 */
	public static IFile[] getReadOnlyFiles(IElement element) {
		try {
			List list = new ArrayList();
			IFile file = null;
			if (element instanceof IServer)
				file = ((IServer) element).getFile();
			else if (element instanceof IServerConfiguration)
				file = ((IServerConfiguration) element).getFile();
			
			if (file != null)
				list.add(file);
			
			//if ()
			//IServerConfiguration config = (IServerConfiguration) element;
			// TODO
			IFile[] files = new IFile[list.size()];
			list.toArray(files);
			return files;
		} catch (Exception e) {
			Trace.trace("getReadOnlyFiles", e);
		}
		return null;
	}
	
	/**
	 * 
	 */
	protected IFile[] getServerResourceFiles(String id) {
		if (id == null)
			return new IFile[0];

		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return new IFile[0];

		return getReadOnlyFiles(info.wc);
	}
	
	protected IFile[] getReadOnlyFiles(String id) {
		List list = new ArrayList();
		IFile[] files = getServerResourceFiles(id);
		int size = files.length;
		for (int i = 0; i < size; i++) {
			if (files[i].isReadOnly())
				list.add(files[i]);
		}
		
		IFile[] fileList = new IFile[list.size()];
		list.toArray(fileList);
		return fileList;
	}

	/**
	 * 
	 */
	public void updateTimestamps(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return;
		
		info.fileMap = new HashMap();
		IFile[] files = getServerResourceFiles(id);
		if (files != null) {
			int size = files.length;
		
			for (int i = 0; i < size; i++) {
				if (files[i] != null) {
					File f = files[i].getLocation().toFile();
					if (f != null) {
						long time = f.lastModified();
						info.fileMap.put(files[i], new Long(time));
					}
				}
			}
		}
		info.timestamp = getTimestamp(info);
	}
	
	protected static int getTimestamp(CommandManagerInfo info) {
		IElement element = info.wc;
		IElement element2 = null;
		if (element instanceof IServer)
			element2 = ((IServerWorkingCopy) element).getOriginal();
		else if (element instanceof IServerConfiguration)
			element2 = ((IServerConfigurationWorkingCopy) element).getOriginal();

		if (element2 != null)
			return element2.getTimestamp();
		return -1;
	}

	/**
	 * 
	 */
	protected boolean hasChanged(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return false;
		IFile[] files = getServerResourceFiles(id);
		int size = files.length;

		int count = 0;
		for (int i = 0; i < size; i++) {
			count++;
			File f = files[i].getLocation().toFile();
			try {
				Long time = (Long) info.fileMap.get(files[i]);
				if (time.longValue() != f.lastModified())
					return true;
			} catch (Exception e) {
				return true;
			}
		}
		
		int timestamp = getTimestamp(info);
		if (info.timestamp != timestamp)
			return true;

		if (count != info.fileMap.size())
			return true;
		else
			return false;
	}
}
