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
import org.eclipse.jst.server.generic.servertype.definition.FilesetType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Fileset Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl#getInclude <em>Include</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl#getExclude <em>Exclude</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl#isCasesensitive <em>Casesensitive</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl#getDir <em>Dir</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class FilesetTypeImpl extends EObjectImpl implements FilesetType {
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
	 * The default value of the '{@link #isCasesensitive() <em>Casesensitive</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isCasesensitive()
	 * @generated
	 * @ordered
	 */
	protected static final boolean CASESENSITIVE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isCasesensitive() <em>Casesensitive</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isCasesensitive()
	 * @generated
	 * @ordered
	 */
	protected boolean casesensitive = CASESENSITIVE_EDEFAULT;

	/**
	 * This is true if the Casesensitive attribute has been set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	protected boolean casesensitiveESet;

	/**
	 * The default value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected static final String DIR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDir() <em>Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDir()
	 * @generated
	 * @ordered
	 */
	protected String dir = DIR_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected FilesetTypeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EClass eStaticClass() {
		return ServerTypePackage.Literals.FILESET_TYPE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getGroup() {
		if (group == null) {
			group = new BasicFeatureMap(this, ServerTypePackage.FILESET_TYPE__GROUP);
		}
		return group;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getInclude() {
		return getGroup().list(ServerTypePackage.Literals.FILESET_TYPE__INCLUDE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList getExclude() {
		return getGroup().list(ServerTypePackage.Literals.FILESET_TYPE__EXCLUDE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isCasesensitive() {
		return casesensitive;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCasesensitive(boolean newCasesensitive) {
		boolean oldCasesensitive = casesensitive;
		casesensitive = newCasesensitive;
		boolean oldCasesensitiveESet = casesensitiveESet;
		casesensitiveESet = true;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.FILESET_TYPE__CASESENSITIVE, oldCasesensitive, casesensitive, !oldCasesensitiveESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetCasesensitive() {
		boolean oldCasesensitive = casesensitive;
		boolean oldCasesensitiveESet = casesensitiveESet;
		casesensitive = CASESENSITIVE_EDEFAULT;
		casesensitiveESet = false;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.UNSET, ServerTypePackage.FILESET_TYPE__CASESENSITIVE, oldCasesensitive, CASESENSITIVE_EDEFAULT, oldCasesensitiveESet));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetCasesensitive() {
		return casesensitiveESet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDir() {
		return dir;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDir(String newDir) {
		String oldDir = dir;
		dir = newDir;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ServerTypePackage.FILESET_TYPE__DIR, oldDir, dir));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ServerTypePackage.FILESET_TYPE__GROUP:
				return ((InternalEList)getGroup()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.FILESET_TYPE__INCLUDE:
				return ((InternalEList)getInclude()).basicRemove(otherEnd, msgs);
			case ServerTypePackage.FILESET_TYPE__EXCLUDE:
				return ((InternalEList)getExclude()).basicRemove(otherEnd, msgs);
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
			case ServerTypePackage.FILESET_TYPE__GROUP:
				if (coreType) return getGroup();
				return ((FeatureMap.Internal)getGroup()).getWrapper();
			case ServerTypePackage.FILESET_TYPE__INCLUDE:
				return getInclude();
			case ServerTypePackage.FILESET_TYPE__EXCLUDE:
				return getExclude();
			case ServerTypePackage.FILESET_TYPE__CASESENSITIVE:
				return isCasesensitive() ? Boolean.TRUE : Boolean.FALSE;
			case ServerTypePackage.FILESET_TYPE__DIR:
				return getDir();
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
			case ServerTypePackage.FILESET_TYPE__GROUP:
				((FeatureMap.Internal)getGroup()).set(newValue);
				return;
			case ServerTypePackage.FILESET_TYPE__INCLUDE:
				getInclude().clear();
				getInclude().addAll((Collection)newValue);
				return;
			case ServerTypePackage.FILESET_TYPE__EXCLUDE:
				getExclude().clear();
				getExclude().addAll((Collection)newValue);
				return;
			case ServerTypePackage.FILESET_TYPE__CASESENSITIVE:
				setCasesensitive(((Boolean)newValue).booleanValue());
				return;
			case ServerTypePackage.FILESET_TYPE__DIR:
				setDir((String)newValue);
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
			case ServerTypePackage.FILESET_TYPE__GROUP:
				getGroup().clear();
				return;
			case ServerTypePackage.FILESET_TYPE__INCLUDE:
				getInclude().clear();
				return;
			case ServerTypePackage.FILESET_TYPE__EXCLUDE:
				getExclude().clear();
				return;
			case ServerTypePackage.FILESET_TYPE__CASESENSITIVE:
				unsetCasesensitive();
				return;
			case ServerTypePackage.FILESET_TYPE__DIR:
				setDir(DIR_EDEFAULT);
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
			case ServerTypePackage.FILESET_TYPE__GROUP:
				return group != null && !group.isEmpty();
			case ServerTypePackage.FILESET_TYPE__INCLUDE:
				return !getInclude().isEmpty();
			case ServerTypePackage.FILESET_TYPE__EXCLUDE:
				return !getExclude().isEmpty();
			case ServerTypePackage.FILESET_TYPE__CASESENSITIVE:
				return isSetCasesensitive();
			case ServerTypePackage.FILESET_TYPE__DIR:
				return DIR_EDEFAULT == null ? dir != null : !DIR_EDEFAULT.equals(dir);
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
		result.append(", casesensitive: ");
		if (casesensitiveESet) result.append(casesensitive); else result.append("<unset>");
		result.append(", dir: ");
		result.append(dir);
		result.append(')');
		return result.toString();
	}

} //FilesetTypeImpl
