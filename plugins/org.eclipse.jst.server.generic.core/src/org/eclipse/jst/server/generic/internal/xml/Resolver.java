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

/**
 * Utility to resolve serverdef properties with the user provided data.
 * 
 * @author Gorkem Ercan
 */
public class Resolver {
	
	private static final String PROP_START2 = "%{"; //$NON-NLS-1$
	private static final String PROP_END = "}"; //$NON-NLS-1$
	private static final String PROP_START = "${"; //$NON-NLS-1$
	private Map fPropertyValues = new HashMap();
	private ServerRuntime server;

	/**
	 * @param runtime 
	 */
	public Resolver(ServerRuntime runtime) {
		this.server = runtime;
	}
	
	/**
	 * Resolves a classpath element.
	 * 
	 * @param cpList
	 * @return list
	 */
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
	/**
	 * Returns a resolved string.
	 * @param proppedString
	 * @return resolved string
	 */
	public String resolveProperties(String proppedString) {
		HashMap cache = new HashMap(getProperties().size());
		Iterator itr = getProperties().iterator();
		while (itr.hasNext()) {
			Property element =(Property) itr.next();
			String value = element.getDefault();
			if(fPropertyValues != null && fPropertyValues.containsKey(element.getId()))
			    value=(String)fPropertyValues.get(element.getId());
			if(Property.TYPE_DIRECTORY.equals(element.getType()) || Property.TYPE_FILE.equals(element.getType()))
				value = value.replace('\\','/');
			 cache.put(element.getId(), value);
		}
		//String vmPath = install.getInstallLocation().getCanonicalPath();
		//vmPath = vmPath.replace('\\', '/');
		cache.put("jrePath", "JRE"); //$NON-NLS-1$ //$NON-NLS-2$
		cache.put("pathChar", File.pathSeparator); //$NON-NLS-1$

		String str = resolvePropertiesFromCache(proppedString, cache);
		str = fixPassthroughProperties(str);
		return str;
	}

	private List getProperties() {
		return this.server.getProperty();
	}

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
			start =  str.indexOf(PROP_START,end);
			if( start < 0)
				return start;
			end = str.indexOf(PROP_END, start);
			
			String key = str.substring(start + 2, end);
			value = (String)cache.get(key);
		}
		while(value == null);
		return start;
	}
	
	private boolean isPassPropertyLeft(String str) {
		return str.indexOf(PROP_START2) >= 0;
	}

	private String resolveProperty(String proppedString, int start, HashMap cache) {
		String str = proppedString;
		start = str.indexOf(PROP_START);
		int end = str.indexOf(PROP_END, start);
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
		int start = str.indexOf(PROP_START2);
		return str.substring(0, start)
			+ PROP_START
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
