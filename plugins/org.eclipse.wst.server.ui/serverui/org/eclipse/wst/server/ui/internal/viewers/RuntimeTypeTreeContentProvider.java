/*******************************************************************************
 * Copyright (c) 2003, 2016 IBM Corporation and others.
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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.internal.RuntimeType;
/**
 * Runtime type content provider.
 */
public class RuntimeTypeTreeContentProvider extends AbstractTreeContentProvider {
	protected boolean creation;
	protected String type;
	protected String version;
	protected String runtimeTypeId;

	/**
	 * RuntimeTypeContentProvider constructor.
	 * 
	 * @param creation true to include runtimes that can be created
	 */
	public RuntimeTypeTreeContentProvider(boolean creation) {
		super();
		this.creation = creation;
	}

	public RuntimeTypeTreeContentProvider(boolean creation, String type, String version, String runtimeTypeId) {
		super(false);
		this.type = type;
		this.version = version;
		this.runtimeTypeId = runtimeTypeId;
		this.creation = creation;
		
		fillTree();
	}

	public void fillTree() {
		clean();
		List<TreeElement> list = new ArrayList<TreeElement>();
		IRuntimeType[] runtimeTypes = ServerUtil.getRuntimeTypes(type, version, runtimeTypeId);
		if (runtimeTypes != null) {
			int size = runtimeTypes.length;
			for (int i = 0; i < size; i++) {
				IRuntimeType runtimeType = runtimeTypes[i];
				if (!creation || runtimeType.canCreate()) {
					try {
						if (!((RuntimeType)runtimeType).supportsManualCreation()) {
							// Hide this runtime type from the list.
							continue;
						}
					} catch (Exception e) {
						// Do nothing since all IRuntimeType should be instance of RuntimeType.
					}
					
					TreeElement ele = getOrCreate(list, runtimeType.getVendor());
					ele.contents.add(runtimeType);
					elementToParentMap.put(runtimeType, ele);
				}
			}
		}
		elements = list.toArray();
	}
	
	protected void deferredAdapterInitialize(final TreeViewer treeViewer, IProgressMonitor monitor) {
		List<TreeElement> list = new ArrayList<TreeElement>();
		IRuntimeType[] runtimeTypes = ServerCore.getDownloadableRuntimeTypes(monitor);
		if (runtimeTypes != null) {
			int size = runtimeTypes.length;
			for (int i = 0; i < size; i++) {
				IRuntimeType runtimeType = runtimeTypes[i];
				try {
					if (!((RuntimeType)runtimeType).supportsManualCreation()) {
						// Hide this runtime type from the list.
						continue;
					}
				} catch (Exception e) {
					// Do nothing since all IRuntimeType should be instance of RuntimeType.
				}
				
				TreeElement ele = getOrCreate(list, runtimeType.getVendor());
				ele.contents.add(runtimeType);
				elementToParentMap.put(runtimeType, ele);
			}
		}
		if (list.size() >0) {
			List<Object> newList = new ArrayList<Object>();
			newList.addAll(Arrays.asList(elements));
			newList.addAll(list);
			elements = newList.toArray();
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					if (!treeViewer.getTree().isDisposed())
						treeViewer.refresh("root");
				}
			});
		}
	}
}