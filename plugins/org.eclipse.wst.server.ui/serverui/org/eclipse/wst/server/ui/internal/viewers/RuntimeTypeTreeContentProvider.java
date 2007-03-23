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

import org.eclipse.wst.server.core.*;
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
		List list = new ArrayList();
		IRuntimeType[] runtimeTypes = ServerUtil.getRuntimeTypes(type, version, runtimeTypeId);
		if (runtimeTypes != null) {
			int size = runtimeTypes.length;
			for (int i = 0; i < size; i++) {
				IRuntimeType runtimeType = runtimeTypes[i];
				if (!creation || runtimeType.canCreate()) {
					TreeElement ele = getOrCreate(list, runtimeType.getVendor());
					ele.contents.add(runtimeType);
					elementToParentMap.put(runtimeType, ele);
				}
			}
		}
		elements = list.toArray();
	}
}