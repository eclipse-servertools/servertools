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
 * $Id: ClasspathImpl.java,v 1.3 2005/03/27 12:55:36 gercan Exp $
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
import org.eclipse.jst.server.generic.servertype.definition.Classpath;
import org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Classpath</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl#getArchive <em>Archive</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl#isIsLibrary <em>Is Library</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ClasspathImpl extends EObjectImpl implements Classpath {
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
     * The default value of the '{@link #isIsLibrary() <em>Is Library</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isIsLibrary()
     * @generated
     * @ordered
     */
	protected static final boolean IS_LIBRARY_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isIsLibrary() <em>Is Library</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @see #isIsLibrary()
     * @generated
     * @ordered
     */
	protected boolean isLibrary = IS_LIBRARY_EDEFAULT;

    /**
     * This is true if the Is Library attribute has been set.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	protected boolean isLibraryESet = false;

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected ClasspathImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	protected EClass eStaticClass() {
        return ServerTypePackage.eINSTANCE.getClasspath();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public FeatureMap getGroup() {
        if (group == null) {
            group = new BasicFeatureMap(this, ServerTypePackage.CLASSPATH__GROUP);
        }
        return group;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public List getArchive() {
        return ((FeatureMap)getGroup()).list(ServerTypePackage.eINSTANCE.getClasspath_Archive());
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
            eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.CLASSPATH__ID, oldId, id));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public boolean isIsLibrary() {
        return isLibrary;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void setIsLibrary(boolean newIsLibrary) {
        boolean oldIsLibrary = isLibrary;
        isLibrary = newIsLibrary;
        boolean oldIsLibraryESet = isLibraryESet;
        isLibraryESet = true;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.CLASSPATH__IS_LIBRARY, oldIsLibrary, isLibrary, !oldIsLibraryESet));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public void unsetIsLibrary() {
        boolean oldIsLibrary = isLibrary;
        boolean oldIsLibraryESet = isLibraryESet;
        isLibrary = IS_LIBRARY_EDEFAULT;
        isLibraryESet = false;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.UNSET, ServerTypePackage.CLASSPATH__IS_LIBRARY, oldIsLibrary, IS_LIBRARY_EDEFAULT, oldIsLibraryESet));
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public boolean isSetIsLibrary() {
        return isLibraryESet;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, Class baseClass, NotificationChain msgs) {
        if (featureID >= 0) {
            switch (eDerivedStructuralFeatureID(featureID, baseClass)) {
                case ServerTypePackage.CLASSPATH__GROUP:
                    return ((InternalEList)getGroup()).basicRemove(otherEnd, msgs);
                case ServerTypePackage.CLASSPATH__ARCHIVE:
                    return ((InternalEList)getArchive()).basicRemove(otherEnd, msgs);
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
            case ServerTypePackage.CLASSPATH__GROUP:
                return getGroup();
            case ServerTypePackage.CLASSPATH__ARCHIVE:
                return getArchive();
            case ServerTypePackage.CLASSPATH__ID:
                return getId();
            case ServerTypePackage.CLASSPATH__IS_LIBRARY:
                return isIsLibrary() ? Boolean.TRUE : Boolean.FALSE;
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
            case ServerTypePackage.CLASSPATH__GROUP:
                getGroup().clear();
                getGroup().addAll((Collection)newValue);
                return;
            case ServerTypePackage.CLASSPATH__ARCHIVE:
                getArchive().clear();
                getArchive().addAll((Collection)newValue);
                return;
            case ServerTypePackage.CLASSPATH__ID:
                setId((String)newValue);
                return;
            case ServerTypePackage.CLASSPATH__IS_LIBRARY:
                setIsLibrary(((Boolean)newValue).booleanValue());
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
            case ServerTypePackage.CLASSPATH__GROUP:
                getGroup().clear();
                return;
            case ServerTypePackage.CLASSPATH__ARCHIVE:
                getArchive().clear();
                return;
            case ServerTypePackage.CLASSPATH__ID:
                setId(ID_EDEFAULT);
                return;
            case ServerTypePackage.CLASSPATH__IS_LIBRARY:
                unsetIsLibrary();
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
            case ServerTypePackage.CLASSPATH__GROUP:
                return group != null && !group.isEmpty();
            case ServerTypePackage.CLASSPATH__ARCHIVE:
                return !getArchive().isEmpty();
            case ServerTypePackage.CLASSPATH__ID:
                return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
            case ServerTypePackage.CLASSPATH__IS_LIBRARY:
                return isSetIsLibrary();
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
        result.append(" (group: ");
        result.append(group);
        result.append(", id: ");
        result.append(id);
        result.append(", isLibrary: ");
        if (isLibraryESet) result.append(isLibrary); else result.append("<unset>");
        result.append(')');
        return result.toString();
    }

} //ClasspathImpl
