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
 * A representation of the model object '<em><b>Project</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Project#getClasspathReference <em>Classpath Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getProject()
 * @model extendedMetaData="name='Project' kind='elementOnly'"
 * @generated
 */
public interface Project extends EObject{
	/**
	 * Returns the value of the '<em><b>Classpath Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Classpath Reference</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Classpath Reference</em>' attribute.
	 * @see #setClasspathReference(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getProject_ClasspathReference()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='classpathReference'"
	 * @generated
	 */
	String getClasspathReference();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Project#getClasspathReference <em>Classpath Reference</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Classpath Reference</em>' attribute.
     * @see #getClasspathReference()
     * @generated
     */
//	void setClasspathReference(String value);

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Project#getClasspathReference <em>Classpath Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Classpath Reference</em>' attribute.
	 * @see #getClasspathReference()
	 * @generated
	 */
	void setClasspathReference(String value);

} // Project
