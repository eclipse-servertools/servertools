/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.ui.editor.IServerEditorPartInput;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
/**
 * 
 */
public class GlobalCommandManager {
	// maximum number of commands in the history
	private static final int MAX_HISTORY = 200;

	class ServerResourceCommand {
		IUndoableOperation command;
		String id;
	}

	// commands in the undo history
	protected List<ServerResourceCommand> undoList = new ArrayList<ServerResourceCommand>();

	// size of the undo stack on last save
	protected int undoSaveIndex = 0;

	// commands in the redo history
	protected List<ServerResourceCommand> redoList = new ArrayList<ServerResourceCommand>();

	class CommandManagerInfo {
		// number of open editors on this resource
		int count;
		
		// true if the resource has not been saved since
		// the last change
		boolean isDirty;
		
		// true if the resource is read-only
		boolean isReadOnly;
		
		// the element id
		String id;
		
		// the working copy
		IServerWorkingCopy wc;
		
		// files and timestamps
		Map<IFile, Long> fileMap;
		
		int timestamp;
	}

	protected Map<String, CommandManagerInfo> commandManagers = new HashMap<String, CommandManagerInfo>();

	// property change listeners
	protected List<PropertyChangeListener> propertyListeners;
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
			propertyListeners = new ArrayList<PropertyChangeListener>();
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
				} catch (Exception e) {
					// ignore
				}
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Get the command manager for a given id.
	 * 
	 * @param id an id
	 */
	public void getCommandManager(String id) {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Getting command manager for " + id);
		}
		try {
			CommandManagerInfo info = commandManagers.get(id);
			if (info != null) {
				info.count ++;
				return;
			}
		} catch (Exception e) {
			if (Trace.WARNING) {
				Trace.trace(Trace.STRING_WARNING, "Could not find existing command manager", e);
			}
		}
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Creating new command manager for " + id);
		}
		try {
			CommandManagerInfo info = new CommandManagerInfo();
			info.count = 1;
			info.id = id;
			IServer server = null;
			if (id != null)
				server = ServerCore.findServer(id);
			if (server != null)
				info.wc = server.createWorkingCopy();
			info.isDirty = false;
			info.isReadOnly = false;
			commandManagers.put(id, info);
			updateTimestamps(id);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not obtain command manager", e);
			}
		}
		return;
	}

	/**
	 * Release the command manager for a given id.
	 * 
	 * @param id an id
	 */
	public void releaseCommandManager(String id) {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Releasing command manager for " + id);
		}
		try {
			CommandManagerInfo info = commandManagers.get(id);
			if (info != null) {
				info.count --;
				if (info.count == 0) {
					commandManagers.remove(id);
					clearUndoList(id);
					clearRedoList(id);
				}
			}
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not release command manager", e);
			}
		}
	}

	/**
	 * Reload the command manager for a given id.
	 * 
	 * @param id an id
	 */
	public void reload(String id) {
		try {
			CommandManagerInfo info = getExistingCommandManagerInfo(id);
			if (info != null) {
				IServer server = null;
				if (id != null)
					server = ServerCore.findServer(id);
				if (server != null)
					info.wc = server.createWorkingCopy();
				firePropertyChangeEvent(PROP_RELOAD, id, null);
			}
			clearUndoList(id);
			clearRedoList(id);
			undoSaveIndex = undoList.size();
			setDirtyState(id, false);
			updateTimestamps(id);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not release command manager", e);
			}
		}
	}

	/**
	 * 
	 */
	protected CommandManagerInfo getExistingCommandManagerInfo(String id) {
		try {
			return commandManagers.get(id);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not find existing command manager info");
			}
		}
		return null;
	}

	/**
	 * Returns true if there is only one command manager.
	 * 
	 * @param id an id
	 * @return <code>true</code> if the only command manager
	 */
	public boolean isOnlyCommandManager(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		return (info != null && info.count == 1);
	}
	
	protected IServerEditorPartInput getPartInput(String serverId, ServerResourceCommandManager serverCommandManager) {
		CommandManagerInfo serverInfo = null;
		IServerWorkingCopy server = null;
		boolean serverReadOnly = false;
		if (serverId != null) {
			serverInfo = getExistingCommandManagerInfo(serverId);
			if (serverInfo == null)
				return null;
			
			server = serverInfo.wc;
			serverReadOnly = serverInfo.isReadOnly;
		}

		return new ServerEditorPartInput(serverCommandManager, server, serverReadOnly);
	}
	
	/**
	 * 
	 */
	protected IServerWorkingCopy getServerResource(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return null;
			
		return info.wc;
	}

	/**
	 * Return the currently active shell.
	 * 
	 * @return a shell
	 */
	private Shell getShell() {
		try {
			Display d = Display.getCurrent();
			if (d == null)
				d = Display.getDefault();
			
			return d.getActiveShell();
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not get shell", e);
			}
			return null;
		}
	}

	/**
	 * Execute the given command and place it in the undo stack.
	 * If the command cannot be undone, the user will be notified
	 * before it is executed.
	 *
	 * @param id an id
	 * @param command a task
	 */
	public void executeCommand(String id, IUndoableOperation command) {
		final Shell shell = getShell();
		
		if (!command.canUndo() && !undoList.isEmpty()) {
			try {
				if (!MessageDialog.openConfirm(shell, Messages.editorServerEditor, Messages.editorPromptIrreversible))
					return;
			} catch (Exception e) {
				// ignore
			}
		}
		
		ServerResourceCommand src = new ServerResourceCommand();
		src.id = id;
		src.command = command;
		
		try {
			IAdaptable adaptable = new IAdaptable() {
				public Object getAdapter(Class adapter) {
					if (Shell.class.equals(adapter))
						return shell;
					return null;
				}
			};
			IStatus status = command.execute(new NullProgressMonitor(), adaptable);
			if (status != null && !status.isOK())
				MessageDialog.openError(shell, Messages.editorServerEditor, status.getMessage());
		} catch (ExecutionException ce) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error executing command", ce);
			}
			return;
		}
		
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return;
		
		if (command.canUndo())
			addToUndoList(src);
		else {
			undoSaveIndex = -1;
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
			ServerResourceCommand src = undoList.get(i);
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
			ServerResourceCommand src = redoList.get(i);
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
	 * @param a an id
	 * @return a task
	 */
	public IUndoableOperation getUndoCommand(String a) {
		int size = undoList.size();
		for (int i = size - 1; i >= 0; i--) {
			ServerResourceCommand src = undoList.get(i);
			if (src.id == a)
				return src.command;
		}
		return null;
	}

	/**
	 * Returns the command that would be redone next.
	 * 
	 * @param a an id
	 * @return a task
	 */
	public IUndoableOperation getRedoCommand(String a) {
		int size = redoList.size();
		for (int i = size - 1; i >= 0; i--) {
			ServerResourceCommand src = redoList.get(i);
			if (src.id == a)
				return src.command;
		}
		return null;
	}

	/**
	 * Returns true if the server resource is "dirty".
	 *
	 * @param id an id
	 * @return a task
	 */
	public boolean isDirty(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return false;
		
		return info.isDirty;
	}
	
	/**
	 * Returns true if the server resource is read-only.
	 *
	 * @param id an id
	 * @return boolean
	 */
	public boolean isReadOnly(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return false;
		return info.isReadOnly;
	}
	
	/**
	 * Sets the server resource read-only flag.
	 * 
	 * @param id an id
	 * @param readOnly <code>true</code> to set read-only, <code>false</code> otherwise
	 */
	public void setReadOnly(String id, boolean readOnly) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return;
		
		if (info.isReadOnly == readOnly)
			return;
		info.isReadOnly = readOnly;
		firePropertyChangeEvent(PROP_RELOAD, id, null);
	}

	/**
	 * Returns true if the server resource files are read-only.
	 *
	 * @param id an id
	 * @return <code>true</code> if the files are read-only
	 */
	public boolean areFilesReadOnly(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return false;
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
	protected void undo(String a) {
		ServerResourceCommand src = null;
		Iterator iterator = undoList.iterator();
		while (iterator.hasNext()) {
			ServerResourceCommand src2 = (ServerResourceCommand) iterator.next();
			if (src2.id == a)
				src = src2;
		}
		if (src == null)
			return;
		
		try {
			src.command.undo(null, null);
		} catch (ExecutionException ee) {
			// do something
		}
		
		undoList.remove(src);
		firePropertyChangeEvent(PROP_UNDO, src.id, null);
		redoList.add(src);
		firePropertyChangeEvent(PROP_REDO, src.id, null);
		
		if (undoSaveIndex == undoList.size())
			setDirtyState(src.id, false);
		else
			setDirtyState(src.id, true);
	}

	/**
	 * Redo the last command.
	 */
	protected void redo(String a) {
		ServerResourceCommand src = null;
		Iterator iterator = redoList.iterator();
		while (iterator.hasNext()) {
			ServerResourceCommand src2 = (ServerResourceCommand) iterator.next();
			if (src2.id == a)
				src = src2;
		}
		if (src == null)
			return;

		try {
			final Shell shell = getShell();
			IAdaptable adaptable = new IAdaptable() {
				public Object getAdapter(Class adapter) {
					if (Shell.class.equals(adapter))
						return shell;
					return null;
				}
			};
			IStatus status = src.command.execute(new NullProgressMonitor(), adaptable);
			if (status != null && !status.isOK())
				MessageDialog.openError(shell, Messages.editorServerEditor, status.getMessage());
		} catch (ExecutionException ce) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error executing command", ce);
			}
			return;
		}
		redoList.remove(src);
		firePropertyChangeEvent(PROP_REDO, src.id, null);
		undoList.add(src);
		firePropertyChangeEvent(PROP_UNDO, src.id, null);
		
		if (undoSaveIndex == undoList.size())
			setDirtyState(src.id, false);
		else
			setDirtyState(src.id, true);
	}

	/**
	 * Clears the history list.
	 * 
	 * @param id an id
	 */
	public void resourceSaved(String id) {
		undoSaveIndex = undoList.size();
		setDirtyState(id, false);
	}

	/**
	 * Return an array of read-only files.
	 * 
	 * @param server a server
	 * @return a possibly empty array of files
	 */
	public static IFile[] getReadOnlyFiles(IServerAttributes server) {
		try {
			List<IFile> list = new ArrayList<IFile>();
			IFile file = ((Server)server).getFile();
			
			if (file != null)
				list.add(file);
			
			IFile[] files = new IFile[list.size()];
			list.toArray(files);
			return files;
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "getReadOnlyFiles", e);
			}
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
		List<IFile> list = new ArrayList<IFile>();
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
	 * Update the timestamps.
	 * 
	 * @param id an id
	 */
	public void updateTimestamps(String id) {
		CommandManagerInfo info = getExistingCommandManagerInfo(id);
		if (info == null)
			return;
		
		info.fileMap = new HashMap<IFile, Long>();
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
		IServer server = info.wc.getOriginal();
		
		if (server != null)
			return ((Server)server).getTimestamp();
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
		if (files == null)
			return false;
		int size = files.length;
		
		int count = 0;
		for (int i = 0; i < size; i++) {
			count++;
			File f = files[i].getLocation().toFile();
			try {
				Long time = info.fileMap.get(files[i]);
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
		return false;
	}
}