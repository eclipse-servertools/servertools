/**
 * <copyright>
 * </copyright>
 *
 * $Id: PublisherDataImpl.java,v 1.1 2005/01/30 21:48:52 gercan Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.jst.server.generic.servertype.definition.PublisherData;
import org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Publisher Data</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.impl.PublisherDataImpl#getDataname <em>Dataname</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.impl.PublisherDataImpl#getDatavalue <em>Datavalue</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PublisherDataImpl extends EObjectImpl implements PublisherData {
    /**
     * The default value of the '{@link #getDataname() <em>Dataname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDataname()
     * @generated
     * @ordered
     */
    protected static final String DATANAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDataname() <em>Dataname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDataname()
     * @generated
     * @ordered
     */
    protected String dataname = DATANAME_EDEFAULT;

    /**
     * The default value of the '{@link #getDatavalue() <em>Datavalue</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDatavalue()
     * @generated
     * @ordered
     */
    protected static final String DATAVALUE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDatavalue() <em>Datavalue</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDatavalue()
     * @generated
     * @ordered
     */
    protected String datavalue = DATAVALUE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected PublisherDataImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected EClass eStaticClass() {
        return ServerTypePackage.eINSTANCE.getPublisherData();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDataname() {
        return dataname;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDataname(String newDataname) {
        String oldDataname = dataname;
        dataname = newDataname;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.PUBLISHER_DATA__DATANAME, oldDataname, dataname));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDatavalue() {
        return datavalue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDatavalue(String newDatavalue) {
        String oldDatavalue = datavalue;
        datavalue = newDatavalue;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.PUBLISHER_DATA__DATAVALUE, oldDatavalue, datavalue));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        switch (eDerivedStructuralFeatureID(eFeature)) {
            case ServerTypePackage.PUBLISHER_DATA__DATANAME:
                return getDataname();
            case ServerTypePackage.PUBLISHER_DATA__DATAVALUE:
                return getDatavalue();
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
            case ServerTypePackage.PUBLISHER_DATA__DATANAME:
                setDataname((String)newValue);
                return;
            case ServerTypePackage.PUBLISHER_DATA__DATAVALUE:
                setDatavalue((String)newValue);
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
            case ServerTypePackage.PUBLISHER_DATA__DATANAME:
                setDataname(DATANAME_EDEFAULT);
                return;
            case ServerTypePackage.PUBLISHER_DATA__DATAVALUE:
                setDatavalue(DATAVALUE_EDEFAULT);
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
            case ServerTypePackage.PUBLISHER_DATA__DATANAME:
                return DATANAME_EDEFAULT == null ? dataname != null : !DATANAME_EDEFAULT.equals(dataname);
            case ServerTypePackage.PUBLISHER_DATA__DATAVALUE:
                return DATAVALUE_EDEFAULT == null ? datavalue != null : !DATAVALUE_EDEFAULT.equals(datavalue);
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
        result.append(" (dataname: ");
        result.append(dataname);
        result.append(", datavalue: ");
        result.append(datavalue);
        result.append(')');
        return result.toString();
    }

} //PublisherDataImpl
