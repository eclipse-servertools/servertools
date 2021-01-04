/*******************************************************************************
 * Copyright (c) 2003, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IClient;
import org.eclipse.wst.server.ui.internal.view.servers.ModuleServer;
/**
 * A label provider for all server related objects.
 */
public class ServerLabelProvider implements ILabelProvider, IColorProvider, IWorkbenchAdapter {
	private ILabelDecorator decorator;
	protected transient List<ILabelProviderListener> listeners;
	protected ILabelProviderListener providerListener;

	public ServerLabelProvider() {
		decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		providerListener = new ILabelProviderListener() {
			public void labelProviderChanged(LabelProviderChangedEvent event) {
				fireListener(event);
			}
		};
		decorator.addListener(providerListener);
	}

	public void addListener(ILabelProviderListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		
		if (listeners == null)
			listeners = new ArrayList<ILabelProviderListener>();
		listeners.add(listener);
	}

	public void removeListener(ILabelProviderListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		
		if (listeners != null)
			listeners.remove(listener);
	}

	protected void fireListener(LabelProviderChangedEvent event) {
		if (listeners == null || listeners.isEmpty())
			return;
		
		int size = listeners.size();
		ILabelProviderListener[] srl = new ILabelProviderListener[size];
		listeners.toArray(srl);
		
		for (int i = 0; i < size; i++) {
			try {
				srl[i].labelProviderChanged(event);
			} catch (Exception e) {
				if (Trace.WARNING) {
					Trace.trace(Trace.STRING_WARNING, "  Error firing label change event to " + srl[i], e);
				}
			}
		}
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
			} else if (element instanceof IModule) {
				IModule module = (IModule) element;
				IModuleType mt = module.getModuleType();
				return getModuleImageDescriptor(mt.getId());
			} else if (element instanceof IModule[]) {
				IModule[] modules = (IModule[]) element;
				IModule module = modules[modules.length - 1];
				IModuleType mt = module.getModuleType();
				return getModuleImageDescriptor(mt.getId());
			} else if (element instanceof IWorkbenchAdapter) {
				return ((IWorkbenchAdapter) element).getImageDescriptor(null);
			}
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not get image descriptor", e);
			}
		}
		return null;
	}

	private Image decorate(Image image, Object obj) {
		Image dec = decorator.decorateImage(image, obj);
		if (dec != null)
			return dec;
		return image;
	}

	private String decorate(String text, Object obj) {
		String dec = decorator.decorateText(text, obj);
		if (dec != null)
			return dec;
		return text;
	}

	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		try {
			if (element instanceof IRuntimeType) {
				IRuntimeType runtimeType = (IRuntimeType) element;
				return decorate(ImageResource.getImage(runtimeType.getId()), runtimeType);
			} else if (element instanceof IRuntime) {
				IRuntime runtime = (IRuntime) element;
				return decorate(ImageResource.getImage(runtime.getRuntimeType().getId()), runtime);
			} else if (element instanceof IServerType) {
				IServerType serverType = (IServerType) element;
				return decorate(ImageResource.getImage(serverType.getId()), serverType);
			} else if (element instanceof IServer) {
				IServer server = (IServer) element;
				if (server.getServerType() == null)
					return null;
				
				return decorate(ImageResource.getImage(server.getServerType().getId()), server);
			} else if (element instanceof IModule) {
				IModule module = (IModule) element;
				IModuleType mt = module.getModuleType();
				if (mt == null)
					return null;
				
				return decorate(getModuleImage(mt.getId()), module);
			} else if (element instanceof IModule[]) {
				IModule[] modules = (IModule[]) element;
				IModule module = modules[modules.length - 1];
				IModuleType mt = module.getModuleType();
				if (mt == null)
					return null;
				
				return decorate(getModuleImage(mt.getId()), modules);
			} else if (element instanceof ModuleServer) {
				ModuleServer ms = (ModuleServer) element;
				IModule module = ms.module[ms.module.length - 1];
				IModuleType mt = module.getModuleType();
				if (mt == null)
					return null;
				
				return decorate(getModuleImage(mt.getId()), ms);
			}
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not get image descriptor", e);
			}
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
			IRuntime runtime = (IRuntime) element;
			return decorate(getString((runtime).getName()), runtime);
		} else if (element instanceof IServer) {
			IServer server = (IServer) element;
			return decorate(getString((server).getName()), server);
		} else if (element instanceof IRuntimeType) {
			IRuntimeType rt = (IRuntimeType) element;
			return decorate(rt.getName(), rt);
		} else if (element instanceof IServerType) {
			IServerType st = (IServerType) element;
			return decorate(st.getName(), st);
		} else if (element instanceof IClient) {
			IClient client = (IClient) element;
			return decorate(client.getName(), client);
		} else if (element instanceof IModule) {
			IModule module = (IModule) element;
			return decorate(module.getName(), module);
		} else if (element instanceof IModule[]) {
			IModule[] modules = (IModule[]) element;
			IModule module = modules[modules.length - 1];
			return decorate(module.getName(), modules);
		} else if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer) element;
			return decorate(ms.getModuleDisplayName(), ms);
		} else if (element instanceof IWorkbenchAdapter) {
			return ((IWorkbenchAdapter) element).getLabel(null);
		}
		
		return "";
	}

	/*
	 * @see IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * @see IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		decorator.removeListener(providerListener);
	}

	public Color getBackground(Object element) {		
		return null;
	}

	public Color getForeground(Object element) {
		if (element instanceof ModuleServer) {
			ModuleServer ms = (ModuleServer)element;
			
			IModule module = ms.module[0];
			if (module.isExternal()) {
				Color c = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
				return c;
			}
		}
		return null;
	}

	public Object[] getChildren(Object o) { 
		return null;
	}

	public String getLabel(Object o) {
		return getText(o);
	}

	public Object getParent(Object o) {
		return null;
	}
}