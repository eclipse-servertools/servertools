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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.custom.TableTreeItem;

import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.resources.IModuleFolder;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.core.resources.IRemoteFolder;
import org.eclipse.wst.server.core.resources.IRemoteResource;
/**
 * Publish tree viewer.
 */
public class PublishTableTreeViewer extends TableTreeViewer {
	protected VisualPublisher visualPublisher;
	protected PublishViewerFilter filter;

	/**
	 * PublishTableTreeViewer constructor comment.
	 * @param tree org.eclipse.swt.custom.TableTree
	 */
	public PublishTableTreeViewer(TableTree tree, VisualPublisher visualPublisher2) {
		super(tree);
		this.visualPublisher = visualPublisher2;
	
		setContentProvider(new PublishTreeContentProvider(visualPublisher));
		setLabelProvider(new PublishTreeLabelProvider(visualPublisher));
		setInput(visualPublisher.getModules());
		setSorter(new ViewerSorter() {
			public int category(Object element) {
				if (element instanceof IModuleFolder)
					return 0;
				else if (element instanceof IModuleResource)
					return 1;
				else if (element instanceof IRemoteFolder)
					return 0;
				else if (element instanceof IRemoteResource)
					return 1;
				else
					return 2;
			}
		});
	
		filter = new PublishViewerFilter(visualPublisher);
		addFilter(filter);
	
		setColumnProperties(new String[] {"local", "remote", "status", "action"});
		setCellEditors(new CellEditor[] {null, null, null, new CheckboxCellEditor(tree)});
	
		ICellModifier cellModifier = new ICellModifier() {
			public Object getValue(Object element, String property) {
				//Trace.trace("getValue " + element + " " + property);
	
				if (element instanceof IModuleResource) {
					IModuleResource resource = (IModuleResource) element;
					IPath path = visualPublisher.getPublishControl(resource.getModule()).getMappedLocation(resource);
					if (path == null || path.toString() == null)
						return new Boolean(false);
					else {
						if (visualPublisher.getResourcesToPublish(resource.getModule()).contains(resource))
							return new Boolean(true);
						else
							return new Boolean(false);
					}
				} else if (element instanceof ModuleRemoteResource) {
					ModuleRemoteResource prr = (ModuleRemoteResource) element;
					IRemoteResource remote = prr.getRemote();
					IPath path = remote.getPath();
					if (path == null || path.toString() == null)
						return new Boolean(false);
					else {
						IModule module = prr.getModule();
						if (visualPublisher.getResourcesToDelete(module).contains(remote))
							return new Boolean(true);
						else
							return new Boolean(false);
					}
				}
				return new Boolean(false);
			}
	
			public boolean canModify(Object element, String property) {
				//Trace.trace("canModify " + element + " " + property);
				if ("action".equals(property)) {
					// check that it is mapped!!
					if (element instanceof IModuleResource) {
						IModuleResource resource = (IModuleResource) element;
						IPath path = visualPublisher.getPublishControl(resource.getModule()).getMappedLocation(resource);
						if (path == null)
							return false;
						if (resource instanceof IModuleFolder) {
							byte status = visualPublisher.getResourceStatus(resource, path);
							if (status != VisualPublisher.STATUS_NEW)
								return false;
						}
						return true;
					} else
						return true;
				}
				else
					return false;
			}
	
			public void modify(Object element, String property, Object value) {
				if (!"action".equals(property))
					return;
	
				TableTreeItem tti = (TableTreeItem) element;
				Object obj = tti.getData();
				//Trace.trace("modify " + element + " " + property + " " + value + " " + obj);
				boolean val = ((Boolean) value).booleanValue();
	
				if (obj instanceof IModuleResource) {
					IModuleResource resource = (IModuleResource) obj;
					IModule module = resource.getModule();
					List publishList = visualPublisher.getResourcesToPublish(module);
					if (val) {
						publishList.add(resource);
						handlePublishResource(module, publishList, resource);
					} else {
						publishList.remove(resource);
						if (resource instanceof IModuleFolder)
							handleUnpublishContainer(module, publishList, (IModuleFolder) resource);
					}
				} else if (obj instanceof ModuleRemoteResource) {
					ModuleRemoteResource prr = (ModuleRemoteResource) obj;
					IRemoteResource remote = prr.getRemote();
					IModule module = prr.getModule();
					List deleteList = visualPublisher.getResourcesToDelete(module);
					if (val) {
						deleteList.add(remote);
						if (remote instanceof IRemoteFolder)
							handleDeleteRemoteFolder(module, deleteList, prr.getFolder(), (IRemoteFolder) remote);
					} else {
						deleteList.remove(remote);
						handleUndeleteRemoteResource(module, deleteList, prr.getFolder(), remote);
					}
				}
				refresh(obj);
			}
		};
		setCellModifier(cellModifier);
		expandToLevel(3);
	}

	/**
	 * Returns the filter.
	 *
	 * @return org.eclipse.wst.server.ui.internal.publish.PublishViewerFilter
	 */
	public PublishViewerFilter getFilter() {
		return filter;
	}

	/**
	 * Handles a deletion by making sure that the child resources
	 * are also deleted.
	 *
	 * @param remote IRemoteResource
	 */
	protected void handleDeleteRemoteFolder(IModule module, List deleteList, ModuleDeletedResourceFolder folder, IRemoteFolder remoteFolder) {
		Iterator iterator = remoteFolder.getContents().iterator();
		while (iterator.hasNext()) {
			IRemoteResource remote = (IRemoteResource) iterator.next();
	
			if (!deleteList.contains(remote)) {
				deleteList.add(remote);
				refresh(new ModuleRemoteResource(folder, module, remote));
			}
			if (remote instanceof IRemoteFolder)
				handleDeleteRemoteFolder(module, deleteList, folder, (IRemoteFolder) remote);
		}
	}

	/**
	 * Handles a file publishing by making sure that the
	 * parent directories are published.
	 * !Should really check remote parent instead of local parent!
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 */
	protected void handlePublishResource(IModule module, List publishList, IModuleResource resource) {
		IModuleFolder container = resource.getParent();
		while (container != null) {
			IPath path = visualPublisher.getPublishControl(module).getMappedLocation(container);
			if (path != null) {
				byte status = visualPublisher.getResourceStatus(container, path);
				if (status == VisualPublisher.STATUS_NEW && !publishList.contains(container)) {
					publishList.add(container);
					refresh(container);
				}
			}
			container = container.getParent();
		}
	}
	
	/**
	 * Handles a deletion by making sure that the parent resources
	 * are also deleted.
	 *
	 * @param remote IRemoteResource
	 */
	protected void handleUndeleteRemoteResource(IModule module, List deleteList, ModuleDeletedResourceFolder folder, IRemoteResource remote) {
		IRemoteFolder remoteFolder = remote.getParent();
		while (remoteFolder != null) {
			if (deleteList.contains(remoteFolder)) {
				deleteList.remove(remoteFolder);
				refresh(new ModuleRemoteResource(folder, module, remoteFolder));
			}
	
			remoteFolder = remoteFolder.getParent();
		}
	}

	/**
	 * Handles a container not being published by deselecting anything
	 * in the container,
	 * !Should really check remote children instead of local children!
	 *
	 * @param resource org.eclipse.core.resources.IResource
	 */
	protected void handleUnpublishContainer(IModule module, List publishList, IModuleFolder container) {
		IModuleResource[] children = null;
		try {
			children = container.members();
		} catch (Exception e) {
		}
		if (children == null)
			return;
	
		int size = children.length;
		for (int i = 0; i < size; i++) {
			IPath path = visualPublisher.getPublishControl(module).getMappedLocation(children[i]);
			if (path != null && publishList.contains(children[i])) {
				publishList.remove(children[i]);
				refresh(children[i]);
			}
	
			if (children[i] instanceof IModuleFolder)
				handleUnpublishContainer(module, publishList, (IModuleFolder) children[i]);
		}
	}

	/**
	 * Updates the filter.
	 *
	 * @param filter org.eclipse.wst.server.ui.internal.publish.PublishViewerFilter
	 */
	public void updateFilter() {
		removeFilter(filter);
		addFilter(filter);
	}
}
