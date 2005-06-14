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
 * $Id: LaunchConfiguration.java,v 1.8 2005/06/14 20:45:45 gercan Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Launch Configuration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getWorkingDirectory <em>Working Directory</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments <em>Program Arguments</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getMainClass <em>Main Class</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getVmParameters <em>Vm Parameters</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClasspathReference <em>Classpath Reference</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getDebugPort <em>Debug Port</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getExternal <em>External</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getLaunchType <em>Launch Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration()
 * @model extendedMetaData="name='LaunchConfiguration' kind='elementOnly'"
 * @generated
 */
public interface LaunchConfiguration extends EObject{
	/**
	 * Returns the value of the '<em><b>Main Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Main Class</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Main Class</em>' attribute.
	 * @see #setMainClass(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_MainClass()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='mainClass'"
	 * @generated
	 */
    String getMainClass();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getMainClass <em>Main Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Main Class</em>' attribute.
	 * @see #getMainClass()
	 * @generated
	 */
	void setMainClass(String value);

	/**
	 * Returns the value of the '<em><b>Working Directory</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Working Directory</em>' attribute.
	 * @see #setWorkingDirectory(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_WorkingDirectory()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='workingDirectory'"
	 * @generated
	 */
	String getWorkingDirectory();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getWorkingDirectory <em>Working Directory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Working Directory</em>' attribute.
	 * @see #getWorkingDirectory()
	 * @generated
	 */
	void setWorkingDirectory(String value);

	/**
	 * Returns the value of the '<em><b>Program Arguments</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Program Arguments</em>' attribute.
	 * @see #setProgramArguments(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_ProgramArguments()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='programArguments'"
	 * @generated
	 */
	String getProgramArguments();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments <em>Program Arguments</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Program Arguments</em>' attribute.
	 * @see #getProgramArguments()
	 * @generated
	 */
	void setProgramArguments(String value);

	/**
	 * Returns the value of the '<em><b>Vm Parameters</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vm Parameters</em>' attribute.
	 * @see #setVmParameters(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_VmParameters()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='vmParameters'"
	 * @generated
	 */
	String getVmParameters();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getVmParameters <em>Vm Parameters</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Vm Parameters</em>' attribute.
	 * @see #getVmParameters()
	 * @generated
	 */
	void setVmParameters(String value);

	/**
	 * Returns the value of the '<em><b>Classpath Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Classpath Reference</em>' attribute.
	 * @see #setClasspathReference(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_ClasspathReference()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='classpathReference'"
	 * @generated
	 */
	String getClasspathReference();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClasspathReference <em>Classpath Reference</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Classpath Reference</em>' attribute.
     * @see #getClasspathReference()
     * @generated
     */
//	void setClasspathReference(String value);

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClasspathReference <em>Classpath Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Classpath Reference</em>' attribute.
	 * @see #getClasspathReference()
	 * @generated
	 */
	void setClasspathReference(String value);

	/**
	 * Returns the value of the '<em><b>External</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.ExternalType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>External</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_External()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.ExternalType" containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='external' group='#group:6'"
	 * @generated
	 */
	List getExternal();

	/**
	 * Returns the value of the '<em><b>Launch Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Launch Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Launch Type</em>' attribute.
	 * @see #setLaunchType(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_LaunchType()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='attribute' name='launchType'"
	 * @generated
	 */
	String getLaunchType();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getLaunchType <em>Launch Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Launch Type</em>' attribute.
	 * @see #getLaunchType()
	 * @generated
	 */
	void setLaunchType(String value);

	/**
	 * Returns the value of the '<em><b>Debug Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Debug Port</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Debug Port</em>' attribute.
	 * @see #setDebugPort(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_DebugPort()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
	 *        extendedMetaData="kind='element' name='debugPort'"
	 * @generated
	 */
	String getDebugPort();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getDebugPort <em>Debug Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Debug Port</em>' attribute.
	 * @see #getDebugPort()
	 * @generated
	 */
	void setDebugPort(String value);

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
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:6'"
	 * @generated
	 */
	FeatureMap getGroup();

} // LaunchConfiguration
