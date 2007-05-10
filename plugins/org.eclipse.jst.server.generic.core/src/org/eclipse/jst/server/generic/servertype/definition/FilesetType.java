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
 * A representation of the model object '<em><b>Fileset Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getInclude <em>Include</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getExclude <em>Exclude</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#isCasesensitive <em>Casesensitive</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getDir <em>Dir</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getFilesetType()
 * @model extendedMetaData="name='fileset_._type' kind='elementOnly'"
 * @generated
 */
public interface FilesetType extends EObject {
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
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getFilesetType_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:0'"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>Include</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.IncludeType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Include</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Include</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getFilesetType_Include()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.IncludeType" containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='include' group='#group:0'"
	 * @generated
	 */
	EList getInclude();

	/**
	 * Returns the value of the '<em><b>Exclude</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.ExcludeType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exclude</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exclude</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getFilesetType_Exclude()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.ExcludeType" containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='exclude' group='#group:0'"
	 * @generated
	 */
	EList getExclude();

	/**
	 * Returns the value of the '<em><b>Casesensitive</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Casesensitive</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Casesensitive</em>' attribute.
	 * @see #isSetCasesensitive()
	 * @see #unsetCasesensitive()
	 * @see #setCasesensitive(boolean)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getFilesetType_Casesensitive()
	 * @model unique="false" unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean"
	 *        extendedMetaData="kind='attribute' name='casesensitive'"
	 * @generated
	 */
	boolean isCasesensitive();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#isCasesensitive <em>Casesensitive</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Casesensitive</em>' attribute.
	 * @see #isSetCasesensitive()
	 * @see #unsetCasesensitive()
	 * @see #isCasesensitive()
	 * @generated
	 */
	void setCasesensitive(boolean value);

	/**
	 * Unsets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#isCasesensitive <em>Casesensitive</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetCasesensitive()
	 * @see #isCasesensitive()
	 * @see #setCasesensitive(boolean)
	 * @generated
	 */
	void unsetCasesensitive();

	/**
	 * Returns whether the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#isCasesensitive <em>Casesensitive</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Casesensitive</em>' attribute is set.
	 * @see #unsetCasesensitive()
	 * @see #isCasesensitive()
	 * @see #setCasesensitive(boolean)
	 * @generated
	 */
	boolean isSetCasesensitive();

	/**
	 * Returns the value of the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dir</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dir</em>' attribute.
	 * @see #setDir(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getFilesetType_Dir()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='attribute' name='dir'"
	 * @generated
	 */
	String getDir();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getDir <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dir</em>' attribute.
	 * @see #getDir()
	 * @generated
	 */
	void setDir(String value);

} // FilesetType
