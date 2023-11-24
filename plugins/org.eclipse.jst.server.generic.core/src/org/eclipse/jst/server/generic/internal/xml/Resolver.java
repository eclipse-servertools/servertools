/***************************************************************************************************
 * Copyright (c) 2005, 2010 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Gorkem Ercan - initial API and implementation
 *
 **************************************************************************************************/

package org.eclipse.jst.server.generic.internal.xml;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jst.server.generic.core.internal.CorePlugin;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;

/**
 * Utility to resolve serverdef/runtimedef properties with the user provided data.
 * Resolver also looks through eclipse platform provided dynamic variables to
 * resolve a property.
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
	 * Returns a resolved string.
     *
	 * @param proppedString
	 * @return resolved string
	 */
	public String resolveProperties(String proppedString) {
		HashMap<String, String> cache = new HashMap<String, String>(getProperties().size());
		Iterator<Property> itr = getProperties().iterator();
		while (itr.hasNext()) {
			Property element =itr.next();
			String value = element.getDefault();
			if(fPropertyValues != null && fPropertyValues.containsKey(element.getId()))
			    value=(String)fPropertyValues.get(element.getId());
			if(Property.TYPE_DIRECTORY.equals(element.getType()) || Property.TYPE_FILE.equals(element.getType()))
				value = value.replace('\\','/');
			 cache.put(element.getId(), value);
		}
		cache.put("pathChar", File.pathSeparator); //$NON-NLS-1$
		String str = resolvePropertiesFromCache(proppedString, cache);
		str = fixPassthroughProperties(str);
		return str;
	}

	private List<Property> getProperties() {
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
		int start = skipToProperty(resolvedString, cache);// see if there are properties to be resolved.
		if (start >= 0) {
			resolvedString = resolveProperty(resolvedString, cache);
			resolvedString = resolvePropertiesFromCache(resolvedString, cache);
		}
		return resolvedString;
	}

	private int skipToProperty(String str,HashMap cache) {
		if (str == null )return -1;
		int start = -1;
		int end =  0;
		String key=""; //$NON-NLS-1$
		do {
			start =  str.indexOf(PROP_START,end);
			if( start < 0)
				return start;
			end = str.indexOf(PROP_END, start);
			key = str.substring(start + 2, end);
		}
		while( !cache.containsKey( key ) && VariablesPlugin.getDefault().getStringVariableManager().getDynamicVariable( key ) == null );
		return start;
	}

	private boolean isPassPropertyLeft(String str) {
		return str.indexOf(PROP_START2) >= 0;
	}

	private String resolveProperty(String proppedString, HashMap cache) {
		StringBuffer str = new StringBuffer(proppedString);
		int start = str.indexOf(PROP_START);
		int end = str.indexOf(PROP_END, start);
		String key = str.substring(start + 2, end);
        String value = (String)cache.get(key);
        if(value == null ){// look in eclipse variables
            IDynamicVariable dv =  VariablesPlugin.getDefault().getStringVariableManager().getDynamicVariable( key );
            if (dv != null ){
                try {
                    value = dv.getValue( null );
                }
                catch( CoreException e ){
                    CorePlugin.getDefault().getLog().log(
                    new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 1,
                            "Can not resolve eclipse variable", e)); //$NON-NLS-1$
                }
            }
        }
		if(value == null )
			return str.toString();
		return str.replace( start, end+1, value ).toString();
	}

	private String fixParam(String proppedString) {
		String str = proppedString;
		int start = str.indexOf(PROP_START2);
		return str.substring(0, start)
			+ PROP_START
			+ str.substring(start+2);
	}

	/**
     * Set the name value pairs that the receiver resolver instance
     * uses to resolve serverdef/runtimedef properties.
     *
	 * @param propertyValues The fPropertyValues to set.
	 */
	public void setPropertyValues(Map propertyValues) {
		fPropertyValues = propertyValues;
	}
}
