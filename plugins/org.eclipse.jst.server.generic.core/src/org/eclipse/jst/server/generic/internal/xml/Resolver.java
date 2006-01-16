/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/

package org.eclipse.jst.server.generic.internal.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jst.server.generic.servertype.definition.ArchiveType;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;


public class Resolver {



	private Map fPropertyValues = new HashMap();
	private ServerRuntime server;

	/**
	 * @param impl
	 */
	public Resolver(ServerRuntime runtime) {
		this.server = runtime;
	}
	
	public List resolveClasspathProperties(List cpList)
	{
		ArrayList list = new ArrayList(cpList.size());
		for (int i = 0; i < cpList.size(); i++) {
			ArchiveType item = (ArchiveType) cpList.get(i);
			String cpath = resolveProperties(item.getPath());
			list.add(cpath);
		}
		return list;
	}	
	public String resolveProperties(String proppedString) {
		HashMap cache = new HashMap(getProperties().size());
		Iterator itr = getProperties().iterator();
		while (itr.hasNext()) {
			Property element =(Property) itr.next();
			String value = element.getDefault();
			if(fPropertyValues != null && fPropertyValues.containsKey(element.getId()))
			    value=(String)fPropertyValues.get(element.getId());
			if("directory".equals(element.getType()) || "file".equals(element.getType()))
				value = value.replace('\\','/');
			 cache.put(element.getId(), value);
		}
		//String vmPath = install.getInstallLocation().getCanonicalPath();
		//vmPath = vmPath.replace('\\', '/');
		cache.put("jrePath", "JRE");
		cache.put("pathChar", File.pathSeparator);

		String str = resolvePropertiesFromCache(proppedString, cache);
		str = fixPassthroughProperties(str);
		return str;
	}

	/**
	 * @return
	 */
	private List getProperties() {
		return this.server.getProperty();
	}

	/**
	 * @param str
	 * @return
	 */
	private String fixPassthroughProperties(String str) {
		String resolvedString = str;
		if (isPassPropertyLeft(resolvedString)) {
			resolvedString = fixParam(resolvedString);
			resolvedString = fixPassthroughProperties(resolvedString);
		}
		return resolvedString;
	}

	private String resolvePropertiesFromCache(
		String proppedString,
		HashMap cache) {
		String resolvedString = proppedString;
		int start = skipToProperty(resolvedString,cache);
		if (start >= 0) {
			resolvedString = resolveProperty(resolvedString, start, cache);
			resolvedString = resolvePropertiesFromCache(resolvedString, cache);
		}
		return resolvedString;
	}

	private int skipToProperty(String str,HashMap cache) {
		int start = -1; 
		int end =  0;
		String value = null;
		do {
			start =  str.indexOf("${",end);
			if( start < 0)
				return start;
			end = str.indexOf("}", start);
			
			String key = str.substring(start + 2, end);
			value = (String)cache.get(key);
		}
		while(value == null);
		return start;
	}
	
	private boolean isPassPropertyLeft(String str) {
		return str.indexOf("%{") >= 0;
	}

	private String resolveProperty(String proppedString, int start, HashMap cache) {
		String str = proppedString;
		start = str.indexOf("${");
		int end = str.indexOf("}", start);
		String key = str.substring(start + 2, end);
		String value = (String)cache.get(key);
		if(value == null )
			return str;
		return str.substring(0, start)
			+ value
			+ str.substring(end + 1);
	}
	
	private String fixParam(String proppedString) {
		String str = proppedString;
		int start = str.indexOf("%{");
		return str.substring(0, start)
			+ "${"
			+ str.substring(start+2);
	}
	
	/**
	 * @return Returns the fPropertyValues.
	 */
	public Map getPropertyValues() {
		return fPropertyValues;
	}
	/**
	 * @param propertyValues The fPropertyValues to set.
	 */
	public void setPropertyValues(Map propertyValues) {
		fPropertyValues = propertyValues;
	}
}
