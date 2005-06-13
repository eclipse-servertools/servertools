/**
 * <copyright>
 *******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ETERATION A.S. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Eteration Bilisim A.S.  For more
 * information on eteration, please see
 * <http://www.eteration.com/>.
 ***************************************************************************
 * </copyright>
 *
 * $Id: Classpath.java,v 1.8 2005/06/13 21:01:36 gercan Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

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
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getClasspath()
 * @model extendedMetaData="name='Classpath' kind='elementOnly'"
 * @generated
 */
public interface Classpath extends EObject{
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
     * @model type="org.eclipse.jst.server.generic.servertype.definition.ArchiveType" containment="true" resolveProxies="false" required="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='archive' group='#group:0'"
     * @generated
     */
	List getArchive();

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

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#isIsLibrary <em>Is Library</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Is Library</em>' attribute.
     * @see #isSetIsLibrary()
     * @see #unsetIsLibrary()
     * @see #isIsLibrary()
     * @generated
     */
//	void setIsLibrary(boolean value);

    /**
     * Unsets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#isIsLibrary <em>Is Library</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isSetIsLibrary()
     * @see #isIsLibrary()
     * @see #setIsLibrary(boolean)
     * @generated
     */
//	void unsetIsLibrary();

    /**
     * Returns whether the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#isIsLibrary <em>Is Library</em>}' attribute is set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @return whether the value of the '<em>Is Library</em>' attribute is set.
     * @see #unsetIsLibrary()
     * @see #isIsLibrary()
     * @see #setIsLibrary(boolean)
     * @generated
     */
//	boolean isSetIsLibrary();

} // Classpath
