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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.resources.IModuleFolder;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.core.resources.IRemoteResource;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Content provider for the publisher wizard.
 */
public class PublishTreeContentProvider implements ITreeContentProvider {
	protected VisualPublisher visualPublisher;

	protected Map remoteToProject = new HashMap();
	
	/**
	 * PublishTreeContentProvider constructor comment.
	 */
	public PublishTreeContentProvider(VisualPublisher visualPublisher) {
		super();
		this.visualPublisher = visualPublisher;
	}

	/**
	 * Disposes of this content provider.  
	 * This is called by the viewer when it is disposed.
	 */
	public void dispose() {}

	/**
	 * Returns the child elements of the given parent element.
	 * <p>
	 * The difference between this method and <code>IStructuredContentProvider.getElements</code> 
	 * is that <code>getElements</code> is called to obtain the 
	 * tree viewer's root elements, whereas <code>getChildren</code> is used
	 * to obtain the children of a given parent element in the tree (including a root).
	 * </p>
	 * The result is not modified by the viewer.
	 *
	 * @param parentElement the parent element
	 * @return an array of child elements
	 */
	public Object[] getChildren(Object element) {
		if (element instanceof IModule) {
			try {
				IModule module = (IModule) element;
				IModuleResource[] children = module.members();
				
				// add deleted remote elements
				List del = visualPublisher.getDeletedResources((IModule) element);
				if (del == null || del.size() == 0)
					return children;
	
				Object[] res = new Object[children.length + 1];
				System.arraycopy(children, 0, res, 0, children.length);
				res[children.length] = new ModuleDeletedResourceFolder((IModule) element);
				return res;
			} catch (Exception e) {
				Trace.trace("Error getting module children", e);
			}
		} else if (element instanceof IModuleFolder) {
			IModuleFolder folder = (IModuleFolder) element;
			try {
				return folder.members();
			} catch (Exception e) {
				Trace.trace("Error getting ModuleFolder members", e);
			}
		} else if (element instanceof ModuleDeletedResourceFolder) {
			ModuleDeletedResourceFolder folder = (ModuleDeletedResourceFolder) element;
			IModule module = folder.getModule();
			List del = visualPublisher.getDeletedResources(module);
			if (del == null || del.size() == 0)
				return new Object[0];
	
			int size = del.size();
			ModuleRemoteResource[] remote = new ModuleRemoteResource[size];
			for (int i = 0; i < size; i++)
				remote[i] = new ModuleRemoteResource(folder, module, (IRemoteResource) del.get(i));
	
			return remote;
		}
		return new Object[0];
	}

	/**
	 * Returns the elements to display in the viewer 
	 * when its input is set to the given element. 
	 * These elements can be presented as rows in a table, items in a list, etc.
	 * The result is not modified by the viewer.
	 *
	 * @param inputElement the input element
	 * @return the array of elements to display in the viewer
	 */
	public Object[] getElements(Object element) {
		if (element instanceof List) {
			List l = (List) element;
			return l.toArray();
		}
		return null;
	}

	/**
	 * Returns the parent for the given element, or <code>null</code> 
	 * indicating that the parent can't be computed. 
	 * In this case the tree-structured viewer can't expand
	 * a given node correctly if requested.
	 *
	 * @param element the element
	 * @return the parent element, or <code>null</code> if it
	 *   has none or if the parent cannot be computed
	 */
	public Object getParent(Object element) {
		if (element instanceof IModuleResource) {
			IModuleResource resource = (IModuleResource) element;
			if (resource.getParent() != null)
				return resource.getParent();
			return resource.getModule();
		} else if (element instanceof ModuleDeletedResourceFolder) {
			ModuleDeletedResourceFolder folder = (ModuleDeletedResourceFolder) element;
			return folder.getModule();
		} else if (element instanceof ModuleRemoteResource) {
			ModuleRemoteResource remote = (ModuleRemoteResource) element;
			if (remote.getFolder() != null)
				return remote.getFolder();
			return remote.getModule();
		}
		return null;
	}

	/**
	 * Returns whether the given element has children.
	 * <p>
	 * Intended as an optimization for when the viewer does not
	 * need the actual children.  Clients may be able to implement
	 * this more efficiently than <code>getChildren</code>.
	 * </p>
	 *
	 * @param element the element
	 * @return <code>true</code> if the given element has children,
	 *  and <code>false</code> if it has no children
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof IModuleFolder) {
			IModuleFolder container = (IModuleFolder) element;
			try {
				IModuleResource[] children = container.members();
				if (children != null && children.length > 0)
					return true;
				else if (container instanceof IModule) {
					return (!visualPublisher.getDeletedResources((IModule) element).isEmpty());
				}
				return false;
			} catch (Exception e) {
				Trace.trace("Project members", e);
			}
		} else if (element instanceof ModuleDeletedResourceFolder) {
			return true;
		}
		return false;
	}

	/**
	 * Notifies this content provider that the given viewer's input
	 * has been switched to a different element.
	 * <p>
	 * A typical use for this method is registering the content provider as a listener
	 * to changes on the new input (using model-specific means), and deregistering the viewer 
	 * from the old input. In response to these change notifications, the content provider
	 * propagates the changes to the viewer.
	 * </p>
	 *
	 * @param viewer the viewer
	 * @param oldInput the old input element, or <code>null</code> if the viewer
	 *   did not previously have an input
	 * @param newInput the new input element, or <code>null</code> if the viewer
	 *   does not have an input
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}