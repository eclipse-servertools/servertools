/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.wst.server.core.*;
/**
 * 
 */
public class TaskModel implements ITaskModel {
	protected Map map = new HashMap();

	public Object getObject(String id) {
		try {
			return map.get(id);
		} catch (Exception e) {
			return null;
		}
	}
	
	public void putObject(String id, Object obj) {
		map.put(id, obj);
	}
}