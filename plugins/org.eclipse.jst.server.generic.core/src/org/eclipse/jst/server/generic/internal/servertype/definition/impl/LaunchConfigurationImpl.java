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
 * $Id: LaunchConfigurationImpl.java,v 1.2 2005/03/27 12:55:36 gercan Exp $
 */
package org.eclipse.jst.server.generic.internal.servertype.definition.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration;
import org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Launch Configuration</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getMainClass <em>Main Class</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getWorkingDirectory <em>Working Directory</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getProgramArguments <em>Program Arguments</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getVmParameters <em>Vm Parameters</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl#getClasspathReference <em>Classpath Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LaunchConfigurationImpl extends EObjectImpl implements LaunchConfiguration {
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
     * The default value of the '{@link #getProgramArguments() <em>Program Arguments</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getProgramArguments()
     * @generated
     * @ordered
     */
	protected static final String PROGRAM_ARGUMENTS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getProgramArguments() <em>Program Arguments</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getProgramArguments()
     * @generated
     * @ordered
     */
	protected String programArguments = PROGRAM_ARGUMENTS_EDEFAULT;

    /**
     * The default value of the '{@link #getVmParameters() <em>Vm Parameters</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getVmParameters()
     * @generated
     * @ordered
     */
	protected static final String VM_PARAMETERS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getVmParameters() <em>Vm Parameters</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #getVmParameters()
     * @generated
     * @ordered
     */
	protected String vmParameters = VM_PARAMETERS_EDEFAULT;

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
        return ServerTypePackage.eINSTANCE.getLaunchConfiguration();
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
	public String getProgramArguments() {
        return programArguments;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setProgramArguments(String newProgramArguments) {
        String oldProgramArguments = programArguments;
        programArguments = newProgramArguments;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS, oldProgramArguments, programArguments));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String getVmParameters() {
        return vmParameters;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setVmParameters(String newVmParameters) {
        String oldVmParameters = vmParameters;
        vmParameters = newVmParameters;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS, oldVmParameters, vmParameters));
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
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS:
                return getMainClass();
            case ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY:
                return getWorkingDirectory();
            case ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS:
                return getProgramArguments();
            case ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS:
                return getVmParameters();
            case ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE:
                return getClasspathReference();
        }
        return eDynamicGet(eFeature, resolve);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void eSet(EStructuralFeature eFeature, Object newValue) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS:
                setMainClass((String)newValue);
                return;
            case ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY:
                setWorkingDirectory((String)newValue);
                return;
            case ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS:
                setProgramArguments((String)newValue);
                return;
            case ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS:
                setVmParameters((String)newValue);
                return;
            case ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE:
                setClasspathReference((String)newValue);
                return;
        }
        eDynamicSet(eFeature, newValue);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void eUnset(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS:
                setMainClass(MAIN_CLASS_EDEFAULT);
                return;
            case ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY:
                setWorkingDirectory(WORKING_DIRECTORY_EDEFAULT);
                return;
            case ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS:
                setProgramArguments(PROGRAM_ARGUMENTS_EDEFAULT);
                return;
            case ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS:
                setVmParameters(VM_PARAMETERS_EDEFAULT);
                return;
            case ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE:
                setClasspathReference(CLASSPATH_REFERENCE_EDEFAULT);
                return;
        }
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ServerTypePackage.LAUNCH_CONFIGURATION__MAIN_CLASS:
                return MAIN_CLASS_EDEFAULT == null ? mainClass != null : !MAIN_CLASS_EDEFAULT.equals(mainClass);
            case ServerTypePackage.LAUNCH_CONFIGURATION__WORKING_DIRECTORY:
                return WORKING_DIRECTORY_EDEFAULT == null ? workingDirectory != null : !WORKING_DIRECTORY_EDEFAULT.equals(workingDirectory);
            case ServerTypePackage.LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS:
                return PROGRAM_ARGUMENTS_EDEFAULT == null ? programArguments != null : !PROGRAM_ARGUMENTS_EDEFAULT.equals(programArguments);
            case ServerTypePackage.LAUNCH_CONFIGURATION__VM_PARAMETERS:
                return VM_PARAMETERS_EDEFAULT == null ? vmParameters != null : !VM_PARAMETERS_EDEFAULT.equals(vmParameters);
            case ServerTypePackage.LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE:
                return CLASSPATH_REFERENCE_EDEFAULT == null ? classpathReference != null : !CLASSPATH_REFERENCE_EDEFAULT.equals(classpathReference);
        }
        return eDynamicIsSet(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (mainClass: ");
        result.append(mainClass);
        result.append(", workingDirectory: ");
        result.append(workingDirectory);
        result.append(", programArguments: ");
        result.append(programArguments);
        result.append(", vmParameters: ");
        result.append(vmParameters);
        result.append(", classpathReference: ");
        result.append(classpathReference);
        result.append(')');
        return result.toString();
    }

} //LaunchConfigurationImpl
