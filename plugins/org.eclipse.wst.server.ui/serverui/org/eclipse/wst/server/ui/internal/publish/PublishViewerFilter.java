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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.eclipse.wst.server.core.resources.IModuleFolder;
import org.eclipse.wst.server.core.resources.IModuleResource;
/**
 * A filter for the visual publisher's dialog.
 */
public class PublishViewerFilter extends ViewerFilter {
	protected VisualPublisher visualPublisher;

	protected boolean filterUnpublishable = true;
	protected boolean filterDeleted = false;
	protected boolean filterNew = false;
	protected boolean filterModified = false;
	protected boolean filterUnmodified = false;

	/**
	 * PublishViewerFilter constructor comment.
	 */
	protected PublishViewerFilter(VisualPublisher visualPublisher) {
		super();
		this.visualPublisher = visualPublisher;
	}

	/**
	 * Returns true if deleted files are filtered.
	 *
	 * @return boolean
	 */
	public boolean isFilteringDeleted() {
		return filterDeleted;
	}

	/**
	 * Returns true if modified files are filtered.
	 *
	 * @return boolean
	 */
	public boolean isFilteringModified() {
		return filterModified;
	}

	/**
	 * Returns true if new files are filtered.
	 *
	 * @return boolean
	 */
	public boolean isFilteringNew() {
		return filterNew;
	}

	/**
	 * Returns true if unmodified files are filtered.
	 *
	 * @return boolean
	 */
	public boolean isFilteringUnmodified() {
		return filterUnmodified;
	}

	/**
	 * Returns true if files that are not publishable are filtered.
	 *
	 * @return boolean
	 */
	public boolean isFilteringUnpublishable() {
		return filterUnpublishable;
	}

	/**
	 * Returns whether the given element makes it through this filter.
	 *
	 * @param viewer the viewer
	 * @param parentElement the parent element
	 * @param element the element
	 * @return <code>true</code> if element is included in the
	 *   filtered set, and <code>false</code> if excluded
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IModuleResource) {
			IModuleResource resource = (IModuleResource) element;
			IPath path = visualPublisher.getPublishControl(resource.getModule()).getMappedLocation(resource);
			if (filterUnpublishable && path == null) {
				if (resource instanceof IModuleFolder) {
					if (visualPublisher.getPublishControl(resource.getModule()).shouldMapMembers((IModuleFolder) resource))
						return true;
					return false;
				}
				return false;
			}
			byte status = visualPublisher.getResourceStatus(resource, path);
			if (status == VisualPublisher.STATUS_NEW && filterNew)
				return false;
			
			if (status == VisualPublisher.STATUS_UNCHANGED && filterUnmodified)
				return false;
	
			if ((status == VisualPublisher.STATUS_NEWER_LOCALLY || status == VisualPublisher.STATUS_NEWER_REMOTELY) &&
				filterModified)
				return false;
		} else if (element instanceof ModuleRemoteResource) {
			if (filterDeleted)
				return false;
			return true;
		} else if (element instanceof ModuleDeletedResourceFolder) {
			if (filterDeleted)
				return false;
			return true;
		}
		return true;
	}

	/**
	 * Set whether deleted files are filtered.
	 *
	 * @param boolean
	 */
	public void setFilteringDeleted(boolean b) {
		filterDeleted = b;
	}

	/**
	 * Set whether modified files are filtered.
	 *
	 * @param boolean
	 */
	public void setFilteringModified(boolean b) {
		filterModified = b;
	}

	/**
	 * Set whether new files are filtered.
	 *
	 * @param boolean
	 */
	public void setFilteringNew(boolean b) {
		filterNew = b;
	}

	/**
	 * Set whether unmodified files are filtered.
	 *
	 * @param boolean
	 */
	public void setFilteringUnmodified(boolean b) {
		filterUnmodified = b;
	}

	/**
	 * Set whether unpublishable files are filtered.
	 *
	 * @param boolean
	 */
	public void setFilteringUnpublishable(boolean b) {
		filterUnpublishable = b;
	}
}