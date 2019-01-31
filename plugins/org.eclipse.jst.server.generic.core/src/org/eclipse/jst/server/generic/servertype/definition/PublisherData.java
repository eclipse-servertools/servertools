/***************************************************************************************************
 * Copyright (c) 2005-2007 Eteration A.S. and Gorkem Ercan All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan
 * Contributors: Naci Dai
 *               
 **************************************************************************************************/

package org.eclipse.jst.server.generic.servertype.definition;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Publisher Data</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.PublisherData#getDataname <em>Dataname</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.PublisherData#getDatavalue <em>Datavalue</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getPublisherData()
 * @model extendedMetaData="name='PublisherData' kind='elementOnly'"
 * @generated
 */
public interface PublisherData extends EObject {
	/**
	 * Returns the value of the '<em><b>Dataname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dataname</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dataname</em>' attribute.
	 * @see #setDataname(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getPublisherData_Dataname()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='dataname'"
	 * @generated
	 */
	String getDataname();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.PublisherData#getDataname <em>Dataname</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dataname</em>' attribute.
	 * @see #getDataname()
	 * @generated
	 */
	void setDataname(String value);

	/**
	 * Returns the value of the '<em><b>Datavalue</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Datavalue</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Datavalue</em>' attribute.
	 * @see #setDatavalue(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getPublisherData_Datavalue()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='datavalue'"
	 * @generated
	 */
	String getDatavalue();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.PublisherData#getDatavalue <em>Datavalue</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Datavalue</em>' attribute.
	 * @see #getDatavalue()
	 * @generated
	 */
	void setDatavalue(String value);

} // PublisherData
