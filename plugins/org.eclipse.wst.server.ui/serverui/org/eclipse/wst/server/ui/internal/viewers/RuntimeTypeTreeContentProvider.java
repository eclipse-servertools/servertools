/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.IModuleType2;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * Runtime type content provider.
 */
public class RuntimeTypeTreeContentProvider extends AbstractTreeContentProvider {
	public static final byte STYLE_VENDOR = 1;
	public static final byte STYLE_VERSION = 2;
	public static final byte STYLE_MODULE_TYPE = 3;
	
	protected boolean creation;
	protected String type;
	protected String version;
	protected String runtimeTypeId;

	/**
	 * RuntimeTypeContentProvider constructor comment.
	 */
	public RuntimeTypeTreeContentProvider(byte style, boolean creation) {
		super(style);
		this.creation = creation;
	}
	
	public RuntimeTypeTreeContentProvider(byte style, boolean creation, String type, String version, String runtimeTypeId) {
		super(style, false);
		this.type = type;
		this.version = version;
		this.runtimeTypeId = runtimeTypeId;
		this.creation = creation;
		
		fillTree();
	}
	
	public void fillTree() {
		clean();
		List list = new ArrayList();
		if (style != STYLE_FLAT) {
			Iterator iterator = ServerUtil.getRuntimeTypes(type, version, runtimeTypeId).iterator();
			while (iterator.hasNext()) {
				IRuntimeType runtimeType = (IRuntimeType) iterator.next();
				if (!creation || runtimeType.canCreate()) {
					if (runtimeType.getOrder() > initialSelectionOrder) {
						initialSelection = runtimeType;
						initialSelectionOrder = runtimeType.getOrder();
					}
					TreeElement ele = null;
					if (style == STYLE_VENDOR) {
						ele = getOrCreate(list, runtimeType.getVendor());
						ele.contents.add(runtimeType);
						elementToParentMap.put(runtimeType, ele);
					} else if (style == STYLE_VERSION) {
						ele = getOrCreate(list, runtimeType.getVersion());
						ele.contents.add(runtimeType);
						elementToParentMap.put(runtimeType, ele);
					} else if (style == STYLE_MODULE_TYPE) {
						IModuleType2[] moduleTypes = runtimeType.getModuleTypes();
						if (moduleTypes != null) {
							int size = moduleTypes.length;
							for (int i = 0; i < size; i++) {
								IModuleType2 mb = moduleTypes[i];
								IModuleType mt = ServerCore.getModuleType(mb.getType());
								if (mt != null) {
									ele = getOrCreate(list, mt.getName());
									TreeElement ele2 = getOrCreate(ele.contents, mt.getName() + "/" + mb.getVersion(), mb.getVersion());
									ele2.contents.add(runtimeType);
									elementToParentMap.put(runtimeType, ele2);
									elementToParentMap.put(ele2, ele);
								}
							}
						}
					}
				}
			}
		} else {
			Iterator iterator = ServerUtil.getRuntimeTypes(type, version, runtimeTypeId).iterator();
			while (iterator.hasNext()) {
				IRuntimeType runtimeType = (IRuntimeType) iterator.next();
				if (!creation || runtimeType.canCreate()) {
					if (runtimeType.getOrder() > initialSelectionOrder) {
						initialSelection = runtimeType;
						initialSelectionOrder = runtimeType.getOrder();
					}
					list.add(runtimeType);
				}
			}
		}
		elements = list.toArray();
	}
}