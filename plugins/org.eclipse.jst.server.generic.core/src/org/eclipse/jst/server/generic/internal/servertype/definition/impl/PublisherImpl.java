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

package org.eclipse.jst.server.generic.internal.servertype.definition.impl;

import java.util.Collection;

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
import org.eclipse.jst.server.generic.servertype.definition.Publisher;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Publisher</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherImpl#getPublisherdata <em>Publisherdata</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherImpl#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PublisherImpl extends EObjectImpl implements Publisher {
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
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PublisherImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ServerTypePackage.Literals.PUBLISHER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, ServerTypePackage.PUBLISHER__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getPublisherdata() {
		return getGroup().list(ServerTypePackage.Literals.PUBLISHER__PUBLISHERDATA);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.PUBLISHER__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ServerTypePackage.PUBLISHER__GROUP:
				return ((InternalEList)getGroup()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.PUBLISHER__PUBLISHERDATA:
				return ((InternalEList)getPublisherdata()).basicRemove(otherEnd, msgs);
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
			case ServerTypePackage.PUBLISHER__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case ServerTypePackage.PUBLISHER__PUBLISHERDATA:
				return getPublisherdata();
			case ServerTypePackage.PUBLISHER__ID:
				return getId();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ServerTypePackage.PUBLISHER__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case ServerTypePackage.PUBLISHER__PUBLISHERDATA:
				getPublisherdata().clear();
				getPublisherdata().addAll((Collection)newValue);
				return;
			case ServerTypePackage.PUBLISHER__ID:
				setId((String)newValue);
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
			case ServerTypePackage.PUBLISHER__GROUP:
				getGroup().clear();
				return;
			case ServerTypePackage.PUBLISHER__PUBLISHERDATA:
				getPublisherdata().clear();
				return;
			case ServerTypePackage.PUBLISHER__ID:
				setId(ID_EDEFAULT);
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
			case ServerTypePackage.PUBLISHER__GROUP:
				return group != null && !group.isEmpty();
			case ServerTypePackage.PUBLISHER__PUBLISHERDATA:
				return !getPublisherdata().isEmpty();
			case ServerTypePackage.PUBLISHER__ID:
				return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
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
		result.append(", id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //PublisherImpl
