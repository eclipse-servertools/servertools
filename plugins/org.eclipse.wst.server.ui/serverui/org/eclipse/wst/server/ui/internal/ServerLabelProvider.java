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
package org.eclipse.wst.server.ui.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

import org.eclipse.wst.server.core.*;
/**
 * A label provider for all server related objects.
 */
public class ServerLabelProvider implements ILabelProvider {
	public ServerLabelProvider() {
		// do nothing
	}

	protected Image getModuleImage(String typeId) {
		if (typeId == null)
			return null;

		Image image = ImageResource.getImage(typeId);
		int ind = typeId.indexOf(".");
		while (image == null && ind >= 0) {
			typeId = typeId.substring(0, ind);
			image = ImageResource.getImage(typeId);
		}
		return image;
	}
	
	protected ImageDescriptor getModuleImageDescriptor(String typeId) {
		if (typeId == null)
			return null;
		
		ImageDescriptor image = ImageResource.getImageDescriptor(typeId);
		int ind = typeId.indexOf(".");
		while (image == null && ind >= 0) {
			typeId = typeId.substring(0, ind);
			image = ImageResource.getImageDescriptor(typeId);
		}
		return image;
	}

	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public ImageDescriptor getImageDescriptor(Object element) {
		try {
			if (element instanceof IRuntimeType) {
				IRuntimeType runtimeType = (IRuntimeType) element;
				return ImageResource.getImageDescriptor(runtimeType.getId());
			} else if (element instanceof IRuntime) {
				IRuntime runtime = (IRuntime) element;
				return ImageResource.getImageDescriptor(runtime.getRuntimeType().getId());
			} else if (element instanceof IServerType) {
				IServerType serverType = (IServerType) element;
				return ImageResource.getImageDescriptor(serverType.getId());
			} else if (element instanceof IServer) {
				IServer server = (IServer) element;
				return ImageResource.getImageDescriptor(server.getServerType().getId());
			} else if (element instanceof IServerConfigurationType) {
				IServerConfigurationType configType = (IServerConfigurationType) element;
				return ImageResource.getImageDescriptor(configType.getId());
			} else if (element instanceof IServerConfiguration) {
				IServerConfiguration config = (IServerConfiguration) element;
				return ImageResource.getImageDescriptor(config.getServerConfigurationType().getId());
			} else if (element instanceof IModule) {
				IModule module = (IModule) element;
				IModuleType mt = module.getModuleType();
				return getModuleImageDescriptor(mt.getId());
			} else if (element instanceof IWorkbenchAdapter) {
				return ((IWorkbenchAdapter) element).getImageDescriptor(null);
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not get image descriptor", e);
		}
		return null;
	}

	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		try {
			if (element instanceof IRuntimeType) {
				IRuntimeType runtimeType = (IRuntimeType) element;
				return ImageResource.getImage(runtimeType.getId());
			} else if (element instanceof IRuntime) {
				IRuntime runtime = (IRuntime) element;
				return ImageResource.getImage(runtime.getRuntimeType().getId());
			} else if (element instanceof IServerType) {
				IServerType serverType = (IServerType) element;
				return ImageResource.getImage(serverType.getId());
			} else if (element instanceof IServer) {
				IServer server = (IServer) element;
				if (server.getServerType() == null)
					return null;
				
				return ImageResource.getImage(server.getServerType().getId());
			} else if (element instanceof IServerConfigurationType) {
				IServerConfigurationType configType = (IServerConfigurationType) element;
				return ImageResource.getImage(configType.getId());
			} else if (element instanceof IServerConfiguration) {
				IServerConfiguration config = (IServerConfiguration) element;
				return ImageResource.getImage(config.getServerConfigurationType().getId());
			} else if (element instanceof IModule) {
				IModule module = (IModule) element;
				IModuleType mt = module.getModuleType();
				return getModuleImage(mt.getId());
			}
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not get image descriptor", e);
		}
		return null;
	}

	protected String getString(String s) {
		if (s == null)
			return "";
		
		return s;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element == null)
			return "";

		if (element instanceof IRuntime) {
			return getString(((IRuntime) element).getName());
		} else if (element instanceof IServer) {
			return getString(((IServer) element).getName());
		} else if (element instanceof IServerConfiguration) {
			return getString(((IServerConfiguration) element).getName());
		} else if (element instanceof IRuntimeType) {
			return ((IRuntimeType) element).getName();
		} else if (element instanceof IServerType) {
			return ((IServerType) element).getName();
		} else if (element instanceof IServerConfigurationType) {
			return ((IServerConfigurationType) element).getName();
		} else if (element instanceof IClient) {
			return ((IClient) element).getName();
		} else if (element instanceof IModule) {
			return ((IModule) element).getName();
		} else if (element instanceof IWorkbenchAdapter) {
			return ((IWorkbenchAdapter) element).getLabel(null);
		}

		return "";
	}

	/*
	 * @see IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	/*
	 * @see IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}
	
	/*
	 * @see IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		// do nothing
	}
}