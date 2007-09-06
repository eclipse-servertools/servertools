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
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.internal.Trace;
/**
 * A basic label provider.
 */
public abstract class BaseLabelProvider implements ILabelProvider {
	protected ILabelDecorator decorator;
	private transient List<ILabelProviderListener> listeners;
	private ILabelProviderListener providerListener;

	/**
	 * A basic ILabelProvider with no decorator.
	 */
	public BaseLabelProvider() {
		this(null);
	}

	/**
	 * A basic ILabelProvider with support for a decorator.
	 * 
	 * @param decorator a label decorator, or null if no decorator is required
	 */
	public BaseLabelProvider(ILabelDecorator decorator) {
		super();
		if (decorator == null)
			decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		
		this.decorator = decorator;
		if (decorator != null) {
			providerListener = new ILabelProviderListener() {
				public void labelProviderChanged(LabelProviderChangedEvent event) {
					fireListener(event);
				}
			};
			decorator.addListener(providerListener);
		}
	}

	/**
	 * Use this method to avoid having a label decorator. This method is NOT API.
	 * 
	 * @param noDecorator
	 */
	public BaseLabelProvider(boolean noDecorator) {
		super();
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");
		
		if (listeners == null)
			listeners = new ArrayList<ILabelProviderListener>();
		listeners.add(listener);
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
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
				Trace.trace(Trace.WARNING, "  Error firing label change event to " + srl[i], e);
			}
		}
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		if (decorator != null)
			decorator.removeListener(providerListener);
	}

	/**
	 * @see ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object element) {
		return null;
	}

	/**
	 * @see ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object element) {
		return "";
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	protected String notNull(String s) {
		if (s == null)
			return "";
		return s;
	}
}
