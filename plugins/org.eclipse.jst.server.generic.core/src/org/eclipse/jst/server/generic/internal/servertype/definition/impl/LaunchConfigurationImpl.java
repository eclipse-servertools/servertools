/***************************************************************************************************
 * Copyright (c) 2005-2010 Eteration A.S. and Gorkem Ercan All rights reserved. This program and the
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

package org.eclipse.jst.server.generic.internal.servertype.definition.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage;
import org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Launch Configuration</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getEnvironmentVariable <em>Environment Variable</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getGroup1 <em>Group1</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getProgramArguments <em>Program Arguments</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getWorkingDirectory <em>Working Directory</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getMainClass <em>Main Class</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getGroup2 <em>Group2</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getVmParameters <em>Vm Parameters</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getClasspathReference <em>Classpath Reference</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getDebugPort <em>Debug Port</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getGroup3 <em>Group3</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getExternal <em>External</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LaunchConfigurationImpl extends EObjectImpl implements LaunchConfiguration {
	/**
	 * The cached value of the '{@link #getGroup() <em>Group</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroup()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap group;

	/**
	 * The cached value of the '{@link #getGroup1() <em>Group1</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroup1()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap group1;

	/**
	 * The default value of the '{@link #getWorkingDirectory() <em>Working Directory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWorkingDirectory()
	 * @generated
	 * @ordered
	 */
	protected static final String WORKING_DIRECTORY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWorkingDirectory() <em>Working Directory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWorkingDirectory()
	 * @generated
	 * @ordered
	 */
	protected String workingDirectory = WORKING_DIRECTORY_EDEFAULT;

	/**
	 * The default value of the '{@link #getMainClass() <em>Main Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMainClass()
	 * @generated
	 * @ordered
	 */
	protected static final String MAIN_CLASS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getMainClass() <em>Main Class</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMainClass()
	 * @generated
	 * @ordered
	 */
	protected String mainClass = MAIN_CLASS_EDEFAULT;

	/**
	 * The cached value of the '{@link #getGroup2() <em>Group2</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroup2()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap group2;

	/**
	 * The default value of the '{@link #getClasspathReference() <em>Classpath Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClasspathReference()
	 * @generated
	 * @ordered
	 */
	protected static final String CLASSPATH_REFERENCE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getClasspathReference() <em>Classpath Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getClasspathReference()
	 * @generated
	 * @ordered
	 */
	protected String classpathReference = CLASSPATH_REFERENCE_EDEFAULT;

	/**
	 * The default value of the '{@link #getDebugPort() <em>Debug Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDebugPort()
	 * @generated
	 * @ordered
	 */
	protected static final String DEBUG_PORT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDebugPort() <em>Debug Port</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDebugPort()
	 * @generated
	 * @ordered
	 */
	protected String debugPort = DEBUG_PORT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getGroup3() <em>Group3</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGroup3()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap group3;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LaunchConfigurationImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ServerTypePackage.Literals.LAUNCH_CONFIGURATION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, ServerTypePackage.LAUNCH_CONFIGURATION__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getEnvironmentVariable() {
		return getGroup().list(ServerTypePackage.Literals.LAUNCH_CONFIGURATION__ENVIRONMENT_VARIABLE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup1() {
		if (group1 == null) {
			group1 = new BasicFeatureMap(this, ServerTypePackage.LAUNCH_CONFIGURATION__GROUP1);
		}
		return group1;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getProgramArguments() {
		return getGroup1().list(ServerTypePackage.Literals.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getWorkingDirectory() {
		return workingDirectory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWorkingDirectory(String newWorkingDirectory) {
		String oldWorkingDirectory = workingDirectory;
		workingDirectory = newWorkingDirectory;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY, oldWorkingDirectory, workingDirectory));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * @generated NOT
	 * @return
	 */
//	private String cleanWhiteSpace(String string)
//	{
//		if(string==null)
//			return null;
//        char[] chars = string.toCharArray();
//		for (int i = 0; i < chars.length; i++) {
//			if(Character.isWhitespace(chars[i]))
//				chars[i]=' ';
//		}
//		return (new String(chars)).trim();
//		
//	}
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMainClass(String newMainClass) {
		String oldMainClass = mainClass;
		mainClass = newMainClass;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS, oldMainClass, mainClass));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup2() {
		if (group2 == null) {
			group2 = new BasicFeatureMap(this, ServerTypePackage.LAUNCH_CONFIGURATION__GROUP2);
		}
		return group2;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getVmParameters() {
		return getGroup2().list(ServerTypePackage.Literals.LAUNCH_CONFIGURATION__VM_PARAMETERS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getClasspathReference() {
		return classpathReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setClasspathReference(String newClasspathReference) {
		String oldClasspathReference = classpathReference;
		classpathReference = newClasspathReference;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE, oldClasspathReference, classpathReference));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDebugPort() {
		return debugPort;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDebugPort(String newDebugPort) {
		String oldDebugPort = debugPort;
		debugPort = newDebugPort;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.LAUNCH_CONFIGURATION__DEBUG_PORT, oldDebugPort, debugPort));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup3() {
		if (group3 == null) {
			group3 = new BasicFeatureMap(this, ServerTypePackage.LAUNCH_CONFIGURATION__GROUP3);
		}
		return group3;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getExternal() {
		return getGroup3().list(ServerTypePackage.Literals.LAUNCH_CONFIGURATION__EXTERNAL);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP:
				return ((InternalEList)getGroup()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.LAUNCH_CONFIGURATION__ENVIRONMENT_VARIABLE:
				return ((InternalEList)getEnvironmentVariable()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP1:
				return ((InternalEList)getGroup1()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP2:
				return ((InternalEList)getGroup2()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP3:
				return ((InternalEList)getGroup3()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.LAUNCH_CONFIGURATION__EXTERNAL:
				return ((InternalEList)getExternal()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case ServerTypePackage.LAUNCH_CONFIGURATION__ENVIRONMENT_VARIABLE:
				return getEnvironmentVariable();
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP1:
				if (coreType) return getGroup1();
				return ((FeatureMap.Internal)getGroup1()).getWrapper();
			case ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS:
				return getProgramArguments();
			case ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY:
				return getWorkingDirectory();
			case ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS:
				return getMainClass();
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP2:
				if (coreType) return getGroup2();
				return ((FeatureMap.Internal)getGroup2()).getWrapper();
			case ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS:
				return getVmParameters();
			case ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE:
				return getClasspathReference();
			case ServerTypePackage.LAUNCH_CONFIGURATION__DEBUG_PORT:
				return getDebugPort();
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP3:
				if (coreType) return getGroup3();
				return ((FeatureMap.Internal)getGroup3()).getWrapper();
			case ServerTypePackage.LAUNCH_CONFIGURATION__EXTERNAL:
				return getExternal();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__ENVIRONMENT_VARIABLE:
				getEnvironmentVariable().clear();
				getEnvironmentVariable().addAll((Collection)newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP1:
				((FeatureMap.Internal)getGroup1()).set(newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS:
				getProgramArguments().clear();
				getProgramArguments().addAll((Collection)newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY:
				setWorkingDirectory((String)newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS:
				setMainClass((String)newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP2:
				((FeatureMap.Internal)getGroup2()).set(newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS:
				getVmParameters().clear();
				getVmParameters().addAll((Collection)newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE:
				setClasspathReference((String)newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__DEBUG_PORT:
				setDebugPort((String)newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP3:
				((FeatureMap.Internal)getGroup3()).set(newValue);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__EXTERNAL:
				getExternal().clear();
				getExternal().addAll((Collection)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eUnset(int featureID) {
		switch (featureID) {
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP:
				getGroup().clear();
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__ENVIRONMENT_VARIABLE:
				getEnvironmentVariable().clear();
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP1:
				getGroup1().clear();
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS:
				getProgramArguments().clear();
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY:
				setWorkingDirectory(WORKING_DIRECTORY_EDEFAULT);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS:
				setMainClass(MAIN_CLASS_EDEFAULT);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP2:
				getGroup2().clear();
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS:
				getVmParameters().clear();
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE:
				setClasspathReference(CLASSPATH_REFERENCE_EDEFAULT);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__DEBUG_PORT:
				setDebugPort(DEBUG_PORT_EDEFAULT);
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP3:
				getGroup3().clear();
				return;
			case ServerTypePackage.LAUNCH_CONFIGURATION__EXTERNAL:
				getExternal().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP:
				return group != null && !group.isEmpty();
			case ServerTypePackage.LAUNCH_CONFIGURATION__ENVIRONMENT_VARIABLE:
				return !getEnvironmentVariable().isEmpty();
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP1:
				return group1 != null && !group1.isEmpty();
			case ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS:
				return !getProgramArguments().isEmpty();
			case ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY:
				return WORKING_DIRECTORY_EDEFAULT == null ? workingDirectory != null : !WORKING_DIRECTORY_EDEFAULT.equals(workingDirectory);
			case ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS:
				return MAIN_CLASS_EDEFAULT == null ? mainClass != null : !MAIN_CLASS_EDEFAULT.equals(mainClass);
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP2:
				return group2 != null && !group2.isEmpty();
			case ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS:
				return !getVmParameters().isEmpty();
			case ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE:
				return CLASSPATH_REFERENCE_EDEFAULT == null ? classpathReference != null : !CLASSPATH_REFERENCE_EDEFAULT.equals(classpathReference);
			case ServerTypePackage.LAUNCH_CONFIGURATION__DEBUG_PORT:
				return DEBUG_PORT_EDEFAULT == null ? debugPort != null : !DEBUG_PORT_EDEFAULT.equals(debugPort);
			case ServerTypePackage.LAUNCH_CONFIGURATION__GROUP3:
				return group3 != null && !group3.isEmpty();
			case ServerTypePackage.LAUNCH_CONFIGURATION__EXTERNAL:
				return !getExternal().isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (group: ");
		result.append(group);
		result.append(", group1: ");
		result.append(group1);
		result.append(", workingDirectory: ");
		result.append(workingDirectory);
		result.append(", mainClass: ");
		result.append(mainClass);
		result.append(", group2: ");
		result.append(group2);
		result.append(", classpathReference: ");
		result.append(classpathReference);
		result.append(", debugPort: ");
		result.append(debugPort);
		result.append(", group3: ");
		result.append(group3);
		result.append(')');
		return result.toString();
	}

	/**
	 * @generated NOT
	 */
	public String getProgramArgumentsAsString() {
		return concatList(getProgramArguments());
	}
	
	/**
	 * @generated NOT
	 */
	public String getVmParametersAsString() {
		return concatList(getVmParameters());
	}
	
    /**
     * @generated NOT
     */
	private String concatList(List list){
		StringBuffer concatString = new StringBuffer();
		Iterator iterator = list.iterator();
		while(iterator.hasNext()){
			concatString.append(iterator.next());
			concatString.append(' ');
		}
		return concatString.toString();
	}
	
} //LaunchConfigurationImpl
