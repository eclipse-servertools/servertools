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