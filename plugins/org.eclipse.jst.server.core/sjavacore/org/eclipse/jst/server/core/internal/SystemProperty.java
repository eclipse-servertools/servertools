/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.core.internal;
/**
 * A system property name/value pair.
 */
public class SystemProperty {
	protected String name;
	protected String value;

	/**
	 * SystemProperty constructor comment.
	 */
	public SystemProperty(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * Returns true if the object is a SystemProperty and has
	 * the same name and value.
	 *
	 * @param obj java.lang.Object
	 * @return boolean
	 */
	public boolean equals(Object obj) {
		if (!(obj instanceof SystemProperty))
			return false;
	
		SystemProperty sp = (SystemProperty) obj;
		return (name != null && name.equals(sp.getName()) &&
			value != null && value.equals(sp.getValue()));
	}

	/**
	 * Returns the property name.
	 *
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the property value.
	 *
	 * @return java.lang.String
	 */
	public String getValue() {
		return value;
	}
}