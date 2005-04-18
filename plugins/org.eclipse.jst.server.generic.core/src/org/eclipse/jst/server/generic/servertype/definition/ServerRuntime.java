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
 * $Id: ServerRuntime.java,v 1.6 2005/04/18 00:18:06 gercan Exp $
 */
package org.eclipse.jst.server.generic.servertype.definition;

import java.util.List;
import java.util.Map;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.jst.server.generic.internal.xml.Resolver;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Server Runtime</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 *       Generic Server Type Definition. It must have a list of      properties.     
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup <em>Group</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getProperty <em>Property</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup1 <em>Group1</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getPort <em>Port</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup2 <em>Group2</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getModule <em>Module</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getProject <em>Project</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getStart <em>Start</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getStop <em>Stop</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup3 <em>Group3</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getPublisher <em>Publisher</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup4 <em>Group4</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getClasspath <em>Classpath</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getJndiConnection <em>Jndi Connection</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getVersion <em>Version</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime()
 * @model extendedMetaData="name='ServerRuntime' kind='elementOnly'"
 * @generated
 */
public interface ServerRuntime extends EObject{
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
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Group()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:0'"
     * @generated
     */
//    FeatureMap getGroup();

    /**
     * Returns the value of the '<em><b>Property</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.Property}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Property</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Property</em>' containment reference list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Property()
     * @model type="org.eclipse.jst.server.generic.servertype.definition.Property" containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='property' group='#group:0'"
     * @generated
     */
    List getProperty();

    /**
     * Returns the value of the '<em><b>Group1</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group1</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group1</em>' attribute list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Group1()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:2'"
     * @generated
     */
//    FeatureMap getGroup1();

    /**
     * Returns the value of the '<em><b>Port</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.Port}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Port</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Port</em>' containment reference list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Port()
     * @model type="org.eclipse.jst.server.generic.servertype.definition.Port" containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='port' group='#group:2'"
     * @generated
     */
    List getPort();

    /**
     * Returns the value of the '<em><b>Group2</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group2</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group2</em>' attribute list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Group2()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:4'"
     * @generated
     */
//    FeatureMap getGroup2();

    /**
     * Returns the value of the '<em><b>Module</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.Module}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Module</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Module</em>' containment reference list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Module()
     * @model type="org.eclipse.jst.server.generic.servertype.definition.Module" containment="true" resolveProxies="false" required="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='module' group='#group:4'"
     * @generated
     */
    List getModule();

    /**
     * Returns the value of the '<em><b>Project</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Project</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Project</em>' containment reference.
     * @see #setProject(Project)
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Project()
     * @model containment="true" resolveProxies="false" required="true"
     *        extendedMetaData="kind='element' name='project'"
     * @generated
     */
	Project getProject();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getProject <em>Project</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Project</em>' containment reference.
     * @see #getProject()
     * @generated
     */
//	void setProject(Project value);

    /**
     * Returns the value of the '<em><b>Start</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Start</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Start</em>' containment reference.
     * @see #setStart(LaunchConfiguration)
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Start()
     * @model containment="true" resolveProxies="false" required="true"
     *        extendedMetaData="kind='element' name='start'"
     * @generated
     */
	LaunchConfiguration getStart();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getStart <em>Start</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Start</em>' containment reference.
     * @see #getStart()
     * @generated
     */
//	void setStart(LaunchConfiguration value);

    /**
     * Returns the value of the '<em><b>Stop</b></em>' containment reference.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Stop</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Stop</em>' containment reference.
     * @see #setStop(LaunchConfiguration)
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Stop()
     * @model containment="true" resolveProxies="false" required="true"
     *        extendedMetaData="kind='element' name='stop'"
     * @generated
     */
	LaunchConfiguration getStop();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getStop <em>Stop</em>}' containment reference.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Stop</em>' containment reference.
     * @see #getStop()
     * @generated
     */
//	void setStop(LaunchConfiguration value);

    /**
     * Returns the value of the '<em><b>Group3</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group3</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group3</em>' attribute list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Group3()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:9'"
     * @generated
     */
//    FeatureMap getGroup3();

    /**
     * Returns the value of the '<em><b>Publisher</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.Publisher}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Publisher</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Publisher</em>' containment reference list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Publisher()
     * @model type="org.eclipse.jst.server.generic.servertype.definition.Publisher" containment="true" resolveProxies="false" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='publisher' group='#group:9'"
     * @generated
     */
    List getPublisher();

    /**
     * Returns the value of the '<em><b>Group4</b></em>' attribute list.
     * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Group4</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Group4</em>' attribute list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Group4()
     * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
     *        extendedMetaData="kind='group' name='group:11'"
     * @generated
     */
//    FeatureMap getGroup4();

    /**
     * Returns the value of the '<em><b>Classpath</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.jst.server.generic.servertype.definition.Classpath}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Classpath</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Classpath</em>' containment reference list.
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Classpath()
     * @model type="org.eclipse.jst.server.generic.servertype.definition.Classpath" containment="true" resolveProxies="false" required="true" transient="true" volatile="true" derived="true"
     *        extendedMetaData="kind='element' name='classpath' group='#group:11'"
     * @generated
     */
    List getClasspath();

    /**
     * Returns the value of the '<em><b>Jndi Connection</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Jndi Connection</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Jndi Connection</em>' containment reference.
     * @see #setJndiConnection(JndiConnection)
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_JndiConnection()
     * @model containment="true" resolveProxies="false" required="true"
     *        extendedMetaData="kind='element' name='jndiConnection'"
     * @generated
     */
    JndiConnection getJndiConnection();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getJndiConnection <em>Jndi Connection</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Jndi Connection</em>' containment reference.
     * @see #getJndiConnection()
     * @generated
     */
//    void setJndiConnection(JndiConnection value);

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Name()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String" required="true"
     *        extendedMetaData="kind='attribute' name='name'"
     * @generated
     */
	String getName();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
//	void setName(String value);

    /**
     * Returns the value of the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Version</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
     * @return the value of the '<em>Version</em>' attribute.
     * @see #setVersion(String)
     * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypePackage#getServerRuntime_Version()
     * @model unique="false" dataType="org.eclipse.emf.ecore.xml.type.String"
     *        extendedMetaData="kind='attribute' name='version'"
     * @generated
     */
	String getVersion();

    /**
     * Sets the value of the '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getVersion <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @param value the new value of the '<em>Version</em>' attribute.
     * @see #getVersion()
     * @generated
     */
//	void setVersion(String value);

	/**
	 * @param id the id of the '<em>Publisher</em>' attribute.
	 * @return
	 * @generated NOT
	 */
	Publisher getPublisher(String id);

	/**
	 * @param type the type of the '<em>Module</em>' attribute.
	 * @return
	 * @generated NOT
	 */
	Module getModule(String type);

	/**
	 * @param cpRef the reference id of the '<em>Classpath</em>' attribute.
	 * @return
	 * @generated NOT
	 */
	Classpath getClasspath(String cpRef);

    
    /**
     * @generated NOT
     */
    String getId();
    
    /**
     * @generated NOT
     * @param id
     */
    void setId(String id);
    
    
    
    
	/**
	 * @generated NOT
	 */
	String getFilename();

    
	/**
	 * @generated NOT
 */
	void setFilename(String fn);

	/**
	 * @generated NOT
	 */
	void setPropertyValues(Map properties);

	/**
	 * @generated NOT
	 */
	Resolver getResolver();
	
    /**
     * @generated NOT
     */
    String getConfigurationElementNamespace();
    /**
     * @generated NOT
     * @param namespace
     */
    void setConfigurationElementNamespace(String namespace);
    
} // ServerRuntime
