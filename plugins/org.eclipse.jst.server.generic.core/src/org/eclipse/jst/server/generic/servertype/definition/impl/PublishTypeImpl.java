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
 * $Id: PublishTypeImpl.java,v 1.1 2004/11/20 21:18:10 ndai Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.jst.server.generic.servertype.definition.PublishType;
import org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Publish Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.impl.PublishTypeImpl#getTask <em>Task</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.impl.PublishTypeImpl#getClasspathReference <em>Classpath Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PublishTypeImpl extends EObjectImpl implements PublishType {
	/**
	 * The default value of the '{@link #getTask() <em>Task</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTask()
	 * @generated
	 * @ordered
	 */
	protected static final String TASK_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTask() <em>Task</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTask()
	 * @generated
	 * @ordered
	 */
	protected String task = TASK_EDEFAULT;

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
	protected PublishTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ServerTypePackage.eINSTANCE.getPublishType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTask() {
		return task;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTask(String newTask) {
		String oldTask = task;
		task = newTask;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.PUBLISH_TYPE__TASK, oldTask, task));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.PUBLISH_TYPE__CLASSPATH_REFERENCE, oldClasspathReference, classpathReference));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ServerTypePackage.PUBLISH_TYPE__TASK:
				return getTask();
			case ServerTypePackage.PUBLISH_TYPE__CLASSPATH_REFERENCE:
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
			case ServerTypePackage.PUBLISH_TYPE__TASK:
				setTask((String)newValue);
				return;
			case ServerTypePackage.PUBLISH_TYPE__CLASSPATH_REFERENCE:
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
			case ServerTypePackage.PUBLISH_TYPE__TASK:
				setTask(TASK_EDEFAULT);
				return;
			case ServerTypePackage.PUBLISH_TYPE__CLASSPATH_REFERENCE:
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
			case ServerTypePackage.PUBLISH_TYPE__TASK:
				return TASK_EDEFAULT == null ? task != null : !TASK_EDEFAULT.equals(task);
			case ServerTypePackage.PUBLISH_TYPE__CLASSPATH_REFERENCE:
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
		result.append(" (task: ");
		result.append(task);
		result.append(", classpathReference: ");
		result.append(classpathReference);
		result.append(')');
		return result.toString();
	}

} //PublishTypeImpl
