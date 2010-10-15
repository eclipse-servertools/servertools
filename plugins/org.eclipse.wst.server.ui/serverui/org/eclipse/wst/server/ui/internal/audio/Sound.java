/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.audio;

import java.net.URL;
/**
 * A single sound.
 */
class Sound {
	protected String id;
	protected String name;
	public URL location;
	protected String category;

	/**
	 * Sound constructor comment.
	 */
	public Sound() {
		super();
	}

	/**
	 * Sound constructor comment.
	 * 
	 * @param id an id
	 * @param category a category
	 * @param name a name
	 * @param loc location
	 */
	public Sound(String id, String category, String name, URL loc) {
		super();
	
		this.id = id;
		this.category = category;
		this.name = name;
		this.location = loc;
	}

	/**
	 * 
	 * @return org.eclipse.audio.Category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @return java.net.URL
	 */
	public URL getLocation() {
		return location;
	}

	/**
	 * 
	 * @return java.lang.String
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param newCategory org.eclipse.audio.Category
	 */
	public void setCategory(String newCategory) {
		category = newCategory;
	}

	/**
	 * 
	 * @param newId java.lang.String
	 */
	public void setId(String newId) {
		id = newId;
	}

	/**
	 * 
	 * @param newLocation java.net.URL
	 */
	public void setLocation(URL newLocation) {
		location = newLocation;
	}

	/**
	 * 
	 * @param newName java.lang.String
	 */
	public void setName(String newName) {
		name = newName;
	}
}