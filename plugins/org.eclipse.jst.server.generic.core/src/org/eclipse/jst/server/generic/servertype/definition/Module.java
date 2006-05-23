/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.servertype.definition;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Module</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Module#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Module#getPublishDir <em>Publish Dir</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Module#getPublisherReference <em>Publisher Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getModule()
 * @model extendedMetaData="name='Module' kind='elementOnly'"
 * @generated
 */
public interface Module extends EObject{
	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getModule_Type()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='type'"
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Module#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * Returns the value of the '<em><b>Publish Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publish Dir</em>' attribute.
	 * @see #setPublishDir(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getModule_PublishDir()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='publishDir'"
	 * @generated
	 */
	String getPublishDir();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Module#getPublishDir <em>Publish Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Publish Dir</em>' attribute.
	 * @see #getPublishDir()
	 * @generated
	 */
	void setPublishDir(String value);

	/**
	 * Returns the value of the '<em><b>Publisher Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Publisher Reference</em>' attribute.
	 * @see #setPublisherReference(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getModule_PublisherReference()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='publisherReference'"
	 * @generated
	 */
	String getPublisherReference();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Module#getPublisherReference <em>Publisher Reference</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Publisher Reference</em>' attribute.
     * @see #getPublisherReference()
     * @generated
     */
//	void setPublisherReference(String value);

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Module#getPublisherReference <em>Publisher Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Publisher Reference</em>' attribute.
	 * @see #getPublisherReference()
	 * @generated
	 */
	void setPublisherReference(String value);

} // Module
