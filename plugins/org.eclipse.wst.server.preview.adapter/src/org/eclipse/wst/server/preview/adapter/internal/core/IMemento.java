/**********************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.preview.adapter.internal.core;
/**
 * Interface to a memento used for saving the important state of an object
 * in a form that can be persisted in the file system.
 * <p>
 * Mementos were designed with the following requirements in mind:
 * <ol>
 *  <li>Certain objects need to be saved and restored across platform sessions.
 *    </li>
 *  <li>When an object is restored, an appropriate class for an object might not
 *    be available. It must be possible to skip an object in this case.</li>
 *  <li>When an object is restored, the appropriate class for the object may be
 *    different from the one when the object was originally saved. If so, the
 *    new class should still be able to read the old form of the data.</li>
 * </ol>
 * </p>
 * <p>
 * Mementos meet these requirements by providing support for storing a
 * mapping of arbitrary string keys to primitive values, and by allowing
 * mementos to have other mementos as children (arranged into a tree).
 * A robust external storage format based on XML is used.
 * </p><p>
 * This interface is not intended to be implemented by clients.
 * </p>
 */
public interface IMemento {
	/**
	 * Creates a new child of this memento with the given type.
	 * <p>
	 * The <code>getChild</code> and <code>getChildren</code> methods
	 * are used to retrieve children of a given type.
	 * </p>
	 *
	 * @param type the type
	 * @return a new child memento
	 */
	public IMemento createChild(String type);

	/**
	 * Sets the value of the given key to the given integer.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void putInteger(String key, int value);

	/**
	 * Sets the value of the given key to the given string.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void putString(String key, String value);
}