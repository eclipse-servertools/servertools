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

import org.eclipse.emf.common.util.EList;
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
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getEnvironmentVariable <em>Environment Variable</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup1 <em>Group1</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments <em>Program Arguments</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getWorkingDirectory <em>Working Directory</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getMainClass <em>Main Class</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup2 <em>Group2</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getVmParameters <em>Vm Parameters</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClasspathReference <em>Classpath Reference</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getDebugPort <em>Debug Port</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup3 <em>Group3</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getExternal <em>External</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration()
 * @model extendedMetaData="name='LaunchConfiguration' kind='elementOnly'"
 * @generated
 */
public interface LaunchConfiguration extends EObject {
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
	 *        extendedMetaData="kind='group' name='group:0'"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>Environment Variable</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.ArgumentPair}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Environment Variable</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Environment Variable</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_EnvironmentVariable()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.ArgumentPair" containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='environmentVariable' group='#group:0'"
	 * @generated
	 */
	EList getEnvironmentVariable();

	/**
	 * Returns the value of the '<em><b>Group1</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group1</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group1</em>' attribute list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_Group1()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:2'"
	 * @generated
	 */
	FeatureMap getGroup1();

	/**
	 * Returns the value of the '<em><b>Program Arguments</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Program Arguments</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Program Arguments</em>' attribute list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_ProgramArguments()
	 * @model type="java.lang.String" unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='programArguments' group='#group:2'"
	 * @generated
	 */
	EList getProgramArguments();

	/**
	 * Returns a concatenated String of all Program Arguments
	 * @generated NOT
	 * @return
	 */
	String getProgramArgumentsAsString();
	

	/**
	 * Returns the value of the '<em><b>Working Directory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Working Directory</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
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
	 * Returns the value of the '<em><b>Group2</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group2</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group2</em>' attribute list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_Group2()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:6'"
	 * @generated
	 */
	FeatureMap getGroup2();

	/**
	 * Returns the value of the '<em><b>Vm Parameters</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Vm Parameters</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Vm Parameters</em>' attribute list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_VmParameters()
	 * @model type="java.lang.String" unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='vmParameters' group='#group:6'"
	 * @generated
	 */
	EList getVmParameters();

	/**
	 * Returns a concatenated String of all Vm parameters.
	 * @generated NOT
	 * @return
	 */
	String getVmParametersAsString();

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
	void setClasspathReference(String value);

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
	 * Returns the value of the '<em><b>Group3</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group3</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group3</em>' attribute list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_Group3()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:10'"
	 * @generated
	 */
	FeatureMap getGroup3();

	/**
	 * Returns the value of the '<em><b>External</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.External}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>External</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>External</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getLaunchConfiguration_External()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.External" containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='external' group='#group:10'"
	 * @generated
	 */
	EList getExternal();

} // LaunchConfiguration
