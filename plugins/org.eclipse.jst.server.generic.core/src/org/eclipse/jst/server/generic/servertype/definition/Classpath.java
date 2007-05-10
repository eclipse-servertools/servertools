/***************************************************************************************************
 * Copyright (c) 2005-2007 Eteration A.S. and Gorkem Ercan All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan
 * Contributors: Naci Dai
 *               
 **************************************************************************************************/

package org.eclipse.jst.server.generic.servertype.definition;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Classpath</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getArchive <em>Archive</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getFileset <em>Fileset</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getClasspath()
 * @model extendedMetaData="name='Classpath' kind='elementOnly'"
 * @generated
 */
public interface Classpath extends EObject {
	/**
	 * Returns the value of the '<em><b>Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' attribute list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getClasspath_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:0'"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>Archive</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.ArchiveType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Archive</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Archive</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getClasspath_Archive()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.ArchiveType" containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='archive' group='#group:0'"
	 * @generated
	 */
	EList getArchive();

	/**
	 * Returns the value of the '<em><b>Fileset</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.FilesetType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fileset</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fileset</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getClasspath_Fileset()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.FilesetType" containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='fileset' group='#group:0'"
	 * @generated
	 */
	EList getFileset();

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see #setId(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getClasspath_Id()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='id'"
	 * @generated
	 */
	String getId();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getId <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Id</em>' attribute.
	 * @see #getId()
	 * @generated
	 */
	void setId(String value);

} // Classpath
