/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.internal.servertype.definition.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage;
import org.eclipse.jst.server.generic.servertype.definition.Module;




/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Module</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl#getPublishDir <em>Publish Dir</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl#getPublisherReference <em>Publisher Reference</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModuleImpl extends EObjectImpl implements Module {
	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected String type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getPublishDir() <em>Publish Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublishDir()
	 * @generated
	 * @ordered
	 */
	protected static final String PUBLISH_DIR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPublishDir() <em>Publish Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublishDir()
	 * @generated
	 * @ordered
	 */
	protected String publishDir = PUBLISH_DIR_EDEFAULT;

	/**
	 * The default value of the '{@link #getPublisherReference() <em>Publisher Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublisherReference()
	 * @generated
	 * @ordered
	 */
	protected static final String PUBLISHER_REFERENCE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPublisherReference() <em>Publisher Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPublisherReference()
	 * @generated
	 * @ordered
	 */
	protected String publisherReference = PUBLISHER_REFERENCE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModuleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ServerTypePackage.eINSTANCE.getModule();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(String newType) {
		String oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.MODULE__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPublishDir() {
		return publishDir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPublishDir(String newPublishDir) {
		String oldPublishDir = publishDir;
		publishDir = newPublishDir;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.MODULE__PUBLISH_DIR, oldPublishDir, publishDir));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPublisherReference() {
		return publisherReference;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPublisherReference(String newPublisherReference) {
		String oldPublisherReference = publisherReference;
		publisherReference = newPublisherReference;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.MODULE__PUBLISHER_REFERENCE, oldPublisherReference, publisherReference));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object eGet(EStructuralFeature eFeature, boolean resolve) {
		switch (eDerivedStructuralFeatureID(eFeature)) {
			case ServerTypePackage.MODULE__TYPE:
				return getType();
			case ServerTypePackage.MODULE__PUBLISH_DIR:
				return getPublishDir();
			case ServerTypePackage.MODULE__PUBLISHER_REFERENCE:
				return getPublisherReference();
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
			case ServerTypePackage.MODULE__TYPE:
				setType((String)newValue);
				return;
			case ServerTypePackage.MODULE__PUBLISH_DIR:
				setPublishDir((String)newValue);
				return;
			case ServerTypePackage.MODULE__PUBLISHER_REFERENCE:
				setPublisherReference((String)newValue);
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
			case ServerTypePackage.MODULE__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case ServerTypePackage.MODULE__PUBLISH_DIR:
				setPublishDir(PUBLISH_DIR_EDEFAULT);
				return;
			case ServerTypePackage.MODULE__PUBLISHER_REFERENCE:
				setPublisherReference(PUBLISHER_REFERENCE_EDEFAULT);
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
			case ServerTypePackage.MODULE__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case ServerTypePackage.MODULE__PUBLISH_DIR:
				return PUBLISH_DIR_EDEFAULT == null ? publishDir != null : !PUBLISH_DIR_EDEFAULT.equals(publishDir);
			case ServerTypePackage.MODULE__PUBLISHER_REFERENCE:
				return PUBLISHER_REFERENCE_EDEFAULT == null ? publisherReference != null : !PUBLISHER_REFERENCE_EDEFAULT.equals(publisherReference);
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
		result.append(" (type: ");
		result.append(type);
		result.append(", publishDir: ");
		result.append(publishDir);
		result.append(", publisherReference: ");
		result.append(publisherReference);
		result.append(')');
		return result.toString();
	}

} //ModuleImpl
