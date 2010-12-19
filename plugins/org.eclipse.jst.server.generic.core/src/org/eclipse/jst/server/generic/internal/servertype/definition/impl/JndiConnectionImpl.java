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
import org.eclipse.jst.server.generic.servertype.definition.JndiConnection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Jndi Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl#getProviderUrl <em>Provider Url</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl#getJndiProperty <em>Jndi Property</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl#getInitialContextFactory <em>Initial Context Factory</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class JndiConnectionImpl extends EObjectImpl implements JndiConnection {
	/**
	 * The default value of the '{@link #getProviderUrl() <em>Provider Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProviderUrl()
	 * @generated
	 * @ordered
	 */
	protected static final String PROVIDER_URL_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getProviderUrl() <em>Provider Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getProviderUrl()
	 * @generated
	 * @ordered
	 */
	protected String providerUrl = PROVIDER_URL_EDEFAULT;

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
	 * The default value of the '{@link #getInitialContextFactory() <em>Initial Context Factory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitialContextFactory()
	 * @generated
	 * @ordered
	 */
	protected static final String INITIAL_CONTEXT_FACTORY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInitialContextFactory() <em>Initial Context Factory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInitialContextFactory()
	 * @generated
	 * @ordered
	 */
	protected String initialContextFactory = INITIAL_CONTEXT_FACTORY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected JndiConnectionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ServerTypePackage.Literals.JNDI_CONNECTION;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getProviderUrl() {
		return providerUrl;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setProviderUrl(String newProviderUrl) {
		String oldProviderUrl = providerUrl;
		providerUrl = newProviderUrl;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.JNDI_CONNECTION__PROVIDER_URL, oldProviderUrl, providerUrl));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, ServerTypePackage.JNDI_CONNECTION__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getJndiProperty() {
		return getGroup().list(ServerTypePackage.Literals.JNDI_CONNECTION__JNDI_PROPERTY);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getInitialContextFactory() {
		return initialContextFactory;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInitialContextFactory(String newInitialContextFactory) {
		String oldInitialContextFactory = initialContextFactory;
		initialContextFactory = newInitialContextFactory;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY, oldInitialContextFactory, initialContextFactory));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ServerTypePackage.JNDI_CONNECTION__GROUP:
				return ((InternalEList)getGroup()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
				return ((InternalEList)getJndiProperty()).basicRemove(otherEnd, msgs);
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
			case ServerTypePackage.JNDI_CONNECTION__PROVIDER_URL:
				return getProviderUrl();
			case ServerTypePackage.JNDI_CONNECTION__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
				return getJndiProperty();
			case ServerTypePackage.JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY:
				return getInitialContextFactory();
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
			case ServerTypePackage.JNDI_CONNECTION__PROVIDER_URL:
				setProviderUrl((String)newValue);
				return;
			case ServerTypePackage.JNDI_CONNECTION__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
				getJndiProperty().clear();
				getJndiProperty().addAll((Collection)newValue);
				return;
			case ServerTypePackage.JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY:
				setInitialContextFactory((String)newValue);
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
			case ServerTypePackage.JNDI_CONNECTION__PROVIDER_URL:
				setProviderUrl(PROVIDER_URL_EDEFAULT);
				return;
			case ServerTypePackage.JNDI_CONNECTION__GROUP:
				getGroup().clear();
				return;
			case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
				getJndiProperty().clear();
				return;
			case ServerTypePackage.JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY:
				setInitialContextFactory(INITIAL_CONTEXT_FACTORY_EDEFAULT);
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
			case ServerTypePackage.JNDI_CONNECTION__PROVIDER_URL:
				return PROVIDER_URL_EDEFAULT == null ? providerUrl != null : !PROVIDER_URL_EDEFAULT.equals(providerUrl);
			case ServerTypePackage.JNDI_CONNECTION__GROUP:
				return group != null && !group.isEmpty();
			case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
				return !getJndiProperty().isEmpty();
			case ServerTypePackage.JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY:
				return INITIAL_CONTEXT_FACTORY_EDEFAULT == null ? initialContextFactory != null : !INITIAL_CONTEXT_FACTORY_EDEFAULT.equals(initialContextFactory);
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
		result.append(" (providerUrl: ");
		result.append(providerUrl);
		result.append(", group: ");
		result.append(group);
		result.append(", initialContextFactory: ");
		result.append(initialContextFactory);
		result.append(')');
		return result.toString();
	}

} //JndiConnectionImpl
