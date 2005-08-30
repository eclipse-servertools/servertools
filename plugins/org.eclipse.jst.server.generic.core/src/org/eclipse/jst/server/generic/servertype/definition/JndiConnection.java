/**
 * <copyright>
 * </copyright>
 *
 * $Id: JndiConnection.java,v 1.6 2005/08/30 21:43:24 gercan Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Jndi Connection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getProviderUrl <em>Provider Url</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getJndiProperty <em>Jndi Property</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getInitialContextFactory <em>Initial Context Factory</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getJndiConnection()
 * @model extendedMetaData="name='jndiConnection' kind='elementOnly'"
 * @generated
 */
public interface JndiConnection extends EObject{
	/**
	 * Returns the value of the '<em><b>Provider Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Provider Url</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Provider Url</em>' attribute.
	 * @see #setProviderUrl(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getJndiConnection_ProviderUrl()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='providerUrl'"
	 * @generated
	 */
    String getProviderUrl();

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getProviderUrl <em>Provider Url</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Provider Url</em>' attribute.
	 * @see #getProviderUrl()
	 * @generated
	 */
	void setProviderUrl(String value);

	/**
	 * Returns the value of the '<em><b>Group</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' attribute list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getJndiConnection_Group()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='group' name='group:1'"
	 * @generated
	 */
	FeatureMap getGroup();

	/**
	 * Returns the value of the '<em><b>Jndi Property</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.ArgumentPair}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jndi Property</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Jndi Property</em>' containment reference list.
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getJndiConnection_JndiProperty()
	 * @model type="org.eclipse.jst.server.generic.servertype.definition.ArgumentPair" containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='jndiProperty' group='#group:1'"
	 * @generated
	 */
    List getJndiProperty();

	/**
	 * Returns the value of the '<em><b>Initial Context Factory</b></em>' attribute.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Initial Context Factory</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Initial Context Factory</em>' attribute.
	 * @see #setInitialContextFactory(String)
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#getJndiConnection_InitialContextFactory()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
	 *        extendedMetaData="kind='element' name='initialContextFactory'"
	 * @generated
	 */
    String getInitialContextFactory();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getInitialContextFactory <em>Initial Context Factory</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Initial Context Factory</em>' attribute.
     * @see #getInitialContextFactory()
     * @generated
     */
//    void setInitialContextFactory(String value);

	/**
	 * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getInitialContextFactory <em>Initial Context Factory</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Initial Context Factory</em>' attribute.
	 * @see #getInitialContextFactory()
	 * @generated
	 */
	void setInitialContextFactory(String value);

} // JndiConnection
