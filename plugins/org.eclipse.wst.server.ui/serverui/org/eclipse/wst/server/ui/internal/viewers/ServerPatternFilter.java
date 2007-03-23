/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;

public class ServerPatternFilter extends PatternFilter {
	protected boolean isLeafMatch(Viewer viewer, Object element) {
		boolean b = super.isLeafMatch(viewer, element);
		if (b)
			return true;
		
		Object parent = ((ITreeContentProvider) ((AbstractTreeViewer) viewer)
				.getContentProvider()).getParent(element);
		if (parent != null) {
			String labelText = ((ILabelProvider) ((StructuredViewer) viewer).getLabelProvider())
					.getText(parent);
			
			if (labelText != null && wordMatches(labelText))
				return true;
		}
		
		if (element instanceof IRuntimeType && matchesRuntimeType((IRuntimeType) element))
			return true;
		if (element instanceof IRuntime && matchesRuntime((IRuntime) element))
			return true;
		if (element instanceof IServerType && matchesServerType((IServerType) element))
			return true;
		if (element instanceof IServer && matchesServer((IServer) element))
			return true;
		
		return false;
	}

	protected boolean matchesModuleType(IModuleType mt) {
		String text = mt.getName();
		if (wordMatches(text))
			return true;
		text = mt.getVersion();
		if (wordMatches(text))
			return true;
		return false;
	}

	protected boolean matchesRuntimeType(IRuntimeType rt) {
		String text = rt.getName();
		if (wordMatches(text))
			return true;
		text = rt.getDescription();
		if (wordMatches(text))
			return true;
		text = rt.getVendor();
		if (wordMatches(text))
			return true;
		text = rt.getVersion();
		if (wordMatches(text))
			return true;
		
		IModuleType[] mts = rt.getModuleTypes();
		if (mts != null) {
			int size = mts.length;
			for (int i = 0; i < size; i++) {
				if (matchesModuleType(mts[i]))
					return true;
			}
		}
		return false;
	}

	protected boolean matchesRuntime(IRuntime r) {
		String text = r.getName();
		if (wordMatches(text))
			return true;
		if (r.getLocation() != null) {
			text = r.getLocation().toPortableString();
			if (wordMatches(text))
				return true;
			text = r.getLocation().toOSString();
			if (wordMatches(text))
				return true;
		}
		if (r.getRuntimeType() != null && matchesRuntimeType(r.getRuntimeType()))
			return true;
		return false;
	}

	protected boolean matchesServerType(IServerType st) {
		String text = st.getDescription();
		if (wordMatches(text))
			return true;
		if (st.getRuntimeType() != null && matchesRuntimeType(st.getRuntimeType()))
			return true;
		return false;
	}

	protected boolean matchesServer(IServer st) {
		String text = st.getName();
		if (wordMatches(text))
			return true;
		if (st.getServerType() != null && matchesServerType(st.getServerType()))
			return true;
		return false;
	}
}