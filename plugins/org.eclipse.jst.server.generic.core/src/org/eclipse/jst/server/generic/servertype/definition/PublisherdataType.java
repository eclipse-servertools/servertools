/**
 * <copyright>
 * </copyright>
 *
 * $Id: PublisherdataType.java,v 1.1 2005/01/30 21:47:27 gercan Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Publisherdata Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.PublisherdataType#getDataName <em>Data Name</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.PublisherdataType#getDataValue <em>Data Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getPublisherdataType()
 * @model 
 * @generated
 */
public interface PublisherdataType extends EObject {
    /**
     * Returns the value of the '<em><b>Data Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Data Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Data Name</em>' attribute.
     * @see #setDataName(String)
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getPublisherdataType_DataName()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     * @generated
     */
    String getDataName();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.PublisherdataType#getDataName <em>Data Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Data Name</em>' attribute.
     * @see #getDataName()
     * @generated
     */
    void setDataName(String value);

    /**
     * Returns the value of the '<em><b>Data Value</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Data Value</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Data Value</em>' attribute.
     * @see #setDataValue(String)
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getPublisherdataType_DataValue()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     * @generated
     */
    String getDataValue();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.PublisherdataType#getDataValue <em>Data Value</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Data Value</em>' attribute.
     * @see #getDataValue()
     * @generated
     */
    void setDataValue(String value);

} // PublisherdataType
