/**
 * <copyright>
 * </copyright>
 *
 * $Id: JndiConnectionImpl.java,v 1.2 2005/04/19 17:49:01 gercan Exp $
 */
package org.eclipse.jst.server.generic.internal.servertype.definition.impl;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
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
    protected FeatureMap group = null;

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
        return ServerTypePackage.eINSTANCE.getJndiConnection();
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
    public List getJndiProperty() {
        return ((FeatureMap)getGroup()).list(ServerTypePackage.eINSTANCE.getJndiConnection_JndiProperty());
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
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case ServerTypePackage.JNDI_CONNECTION__GROUP:
                    return ((InternalEList)getGroup()).basicRemove(otherEnd, msgs);
                case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
                    return ((InternalEList)getJndiProperty()).basicRemove(otherEnd, msgs);
                default:
                    return eDynamicInverseRemove(otherEnd, featureID, baseClass, msgs);
            }
        }
        return eBasicSetContainer(null, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ServerTypePackage.JNDI_CONNECTION__PROVIDER_URL:
                return getProviderUrl();
            case ServerTypePackage.JNDI_CONNECTION__GROUP:
                return getGroup();
            case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
                return getJndiProperty();
            case ServerTypePackage.JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY:
                return getInitialContextFactory();
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
            case ServerTypePackage.JNDI_CONNECTION__PROVIDER_URL:
                setProviderUrl((String)newValue);
                return;
            case ServerTypePackage.JNDI_CONNECTION__GROUP:
                getGroup().clear();
                getGroup().addAll((Collection)newValue);
                return;
            case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
                getJndiProperty().clear();
                getJndiProperty().addAll((Collection)newValue);
                return;
            case ServerTypePackage.JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY:
                setInitialContextFactory((String)newValue);
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
        eDynamicUnset(eFeature);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean eIsSet(EStructuralFeature eFeature) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ServerTypePackage.JNDI_CONNECTION__PROVIDER_URL:
                return PROVIDER_URL_EDEFAULT == null ? providerUrl != null : !PROVIDER_URL_EDEFAULT.equals(providerUrl);
            case ServerTypePackage.JNDI_CONNECTION__GROUP:
                return group != null && !group.isEmpty();
            case ServerTypePackage.JNDI_CONNECTION__JNDI_PROPERTY:
                return !getJndiProperty().isEmpty();
            case ServerTypePackage.JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY:
                return INITIAL_CONTEXT_FACTORY_EDEFAULT == null ? initialContextFactory != null : !INITIAL_CONTEXT_FACTORY_EDEFAULT.equals(initialContextFactory);
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
