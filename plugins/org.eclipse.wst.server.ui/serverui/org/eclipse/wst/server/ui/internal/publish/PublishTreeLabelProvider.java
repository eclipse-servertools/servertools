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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.resources.IModuleFolder;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.core.resources.IRemoteResource;
import org.eclipse.wst.server.ui.ServerUICore;
import org.eclipse.wst.server.ui.internal.ImageResource;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.swt.graphics.Image;
/**
 * Label provider for the publisher wizard tree.
 */
public class PublishTreeLabelProvider implements ITableLabelProvider {
	protected VisualPublisher visualPublisher;
	protected static final ILabelProvider labelProvider = ServerUICore.getLabelProvider();

	protected static final String[] statusStrings = new String[] {
		ServerUIPlugin.getResource("%wizPublishStateNew"),
		ServerUIPlugin.getResource("%wizPublishStateUnmodified"),
		ServerUIPlugin.getResource("%wizPublishStateModifiedLocal"),
		ServerUIPlugin.getResource("%wizPublishStateModifiedRemote"),
		ServerUIPlugin.getResource("%wizPublishStateModifiedBoth")};

	/**
	 * PublishTreeLabelProvider constructor comment.
	 */
	public PublishTreeLabelProvider(VisualPublisher visualPublisher) {
		super();
		this.visualPublisher = visualPublisher;
	}

	/**
	 * Adds a listener to this label provider. 
	 * Has no effect if an identical listener is already registered.
	 * <p>
	 * Label provider listeners are informed about state changes 
	 * that affect the rendering of the viewer that uses this label provider.
	 * </p>
	 *
	 * @param listener a label provider listener
	 */
	public void addListener(ILabelProviderListener listener) {}

	/**
	 * Disposes of this label provider.  When a label provider is
	 * attached to a viewer, the viewer will automatically call
	 * this method when the viewer is being closed.  When label providers
	 * are used outside of the context of a viewer, it is the client's
	 * responsibility to ensure that this method is called when the
	 * provider is no longer needed.
	 */
	public void dispose() { }

	/**
	 * Returns the label image for the given column of the given element.
	 *
	 * @param element the object representing the entire row, or 
	 *    <code>null</code> indicating that no input object is set
	 *    in the viewer
	 * @param columnIndex the zero-based index of the column in which
	 *   the label appears
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (element instanceof IModule) {
			if (columnIndex == 0)
				return labelProvider.getImage(element);
			return null;
		} else if (element instanceof IModuleResource) {
			IModuleResource resource = (IModuleResource) element;
			IPath path = visualPublisher.getPublishControl(resource.getModule()).getMappedLocation(resource);
			if (columnIndex == 3) {
				if (resource instanceof IModuleFolder) {
					byte status = visualPublisher.getResourceStatus(resource, path);
					if (status != VisualPublisher.STATUS_NEW)
						return null;
				}
				if (path == null || path.toString() == null)
					return null;
				
				if (visualPublisher.getResourcesToPublish(resource.getModule()).contains(resource))
					return ImageResource.getImage(ImageResource.IMG_PUBLISH_ENABLED);
				return ImageResource.getImage(ImageResource.IMG_PUBLISH_DISABLED);
			}
		} else if (element instanceof ModuleRemoteResource) {
			ModuleRemoteResource prr = (ModuleRemoteResource) element;
			IRemoteResource remote = prr.getRemote();
			IPath path = remote.getPath();
			if (columnIndex == 3) {
				if (path == null || path.toString() == null)
					return null;
				
				IModule module = prr.getModule();
				if (visualPublisher.getResourcesToDelete(module).contains(remote))
					return ImageResource.getImage(ImageResource.IMG_PUBLISH_ENABLED);
				return ImageResource.getImage(ImageResource.IMG_PUBLISH_DISABLED);
			}
		}
	
		return null;
	}

	/**
	 * Returns the label text for the given column of the given element.
	 *
	 * @param element the object representing the entire row, or
	 *   <code>null</code> indicating that no input object is set
	 *   in the viewer
	 * @param columnIndex the zero-based index of the column in which the label appears
	 */
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof IModule) {
			if (columnIndex == 0)
				return labelProvider.getText(element);
			return "";
		} else if (element instanceof IModuleResource) {
			IModuleResource resource = (IModuleResource) element;
			IPath path = visualPublisher.getPublishControl(resource.getModule()).getMappedLocation(resource);
			byte status = visualPublisher.getResourceStatus(resource, path);
			if (columnIndex == 0)
				return resource.getName();
			else if (columnIndex == 1) {
				if (path == null || path.toString() == null)
					return "";
				return path.toString();
			} else if (columnIndex == 2) {
				if (path == null || path.toString() == null)
					return "";
				return statusStrings[status];
			} else if (columnIndex == 3) {
				if (path == null || path.toString() == null)
					return "";
				if (resource instanceof IModuleFolder && status != VisualPublisher.STATUS_NEW)
					return "";
				return ServerUIPlugin.getResource("%wizPublishActionPublish");
			}
		} else if (element instanceof ModuleRemoteResource) {
			if (columnIndex == 0)
				return ServerUIPlugin.getResource("%wizPublishDeletedResource");
			else if (columnIndex == 1) {
				ModuleRemoteResource prr = (ModuleRemoteResource) element;
				IPath path = prr.getRemote().getPath();
				if (path == null || path.toString() == null)
					return "";
				return path.toString();
			} else if (columnIndex == 2) {
				return ServerUIPlugin.getResource("%wizPublishStateDeleted");
			} else if (columnIndex == 3)
				return ServerUIPlugin.getResource("%wizPublishActionDelete");
		} else if (element instanceof ModuleDeletedResourceFolder) {
			if (columnIndex == 0)
				return ServerUIPlugin.getResource("%wizPublishDeletedFolder");
			return "";
		}
		return "";
	}

	/**
	 * Returns whether the label would be affected 
	 * by a change to the given property of the given element.
	 * This can be used to optimize a non-structural viewer update.
	 * If the property mentioned in the update does not affect the label,
	 * then the viewer need not update the label.
	 *
	 * @param element the element
	 * @param property the property
	 * @return <code>true</code> if the label would be affected,
	 *    and <code>false</code> if it would be unaffected
	 */
	public boolean isLabelProperty(Object element, String property) {
		return "action".equals(property);
	}

	/**
	 * Removes a listener to this label provider.
	 * Has no affect if an identical listener is not registered.
	 *
	 * @param listener a label provider listener
	 */
	public void removeListener(ILabelProviderListener listener) { }
}
