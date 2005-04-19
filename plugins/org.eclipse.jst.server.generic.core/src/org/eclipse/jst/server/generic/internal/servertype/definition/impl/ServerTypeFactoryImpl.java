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
 * $Id: ServerTypeFactoryImpl.java,v 1.3 2005/04/19 17:49:01 gercan Exp $
 */
package org.eclipse.jst.server.generic.internal.servertype.definition.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage;
import org.eclipse.jst.server.generic.servertype.definition.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ServerTypeFactoryImpl extends EFactoryImpl implements ServerTypeFactory {
    /**
     * Creates and instance of the factory.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public ServerTypeFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case ServerTypePackage.ARCHIVE_TYPE: return createArchiveType();
            case ServerTypePackage.CLASSPATH: return createClasspath();
            case ServerTypePackage.JNDI_CONNECTION: return createJndiConnection();
            case ServerTypePackage.JNDI_PROPERTY: return createJndiProperty();
            case ServerTypePackage.LAUNCH_CONFIGURATION: return createLaunchConfiguration();
            case ServerTypePackage.MODULE: return createModule();
            case ServerTypePackage.PORT: return createPort();
            case ServerTypePackage.PROJECT: return createProject();
            case ServerTypePackage.PROPERTY: return createProperty();
            case ServerTypePackage.PUBLISHER: return createPublisher();
            case ServerTypePackage.PUBLISHER_DATA: return createPublisherData();
            case ServerTypePackage.SERVER_RUNTIME: return createServerRuntime();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public ArchiveType createArchiveType() {
        ArchiveTypeImpl archiveType = new ArchiveTypeImpl();
        return archiveType;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Classpath createClasspath() {
        ClasspathImpl classpath = new ClasspathImpl();
        return classpath;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JndiConnection createJndiConnection() {
        JndiConnectionImpl jndiConnection = new JndiConnectionImpl();
        return jndiConnection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public JndiProperty createJndiProperty() {
        JndiPropertyImpl jndiProperty = new JndiPropertyImpl();
        return jndiProperty;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public LaunchConfiguration createLaunchConfiguration() {
        LaunchConfigurationImpl launchConfiguration = new LaunchConfigurationImpl();
        return launchConfiguration;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Module createModule() {
        ModuleImpl module = new ModuleImpl();
        return module;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Port createPort() {
        PortImpl port = new PortImpl();
        return port;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Project createProject() {
        ProjectImpl project = new ProjectImpl();
        return project;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Property createProperty() {
        PropertyImpl property = new PropertyImpl();
        return property;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public Publisher createPublisher() {
        PublisherImpl publisher = new PublisherImpl();
        return publisher;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PublisherData createPublisherData() {
        PublisherDataImpl publisherData = new PublisherDataImpl();
        return publisherData;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public ServerRuntime createServerRuntime() {
        ServerRuntimeImpl serverRuntime = new ServerRuntimeImpl();
        return serverRuntime;
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     */
	public ServerTypePackage getServerTypePackage() {
        return (ServerTypePackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
	public static ServerTypePackage getPackage() {
        return ServerTypePackage.eINSTANCE;
    }

} //ServerTypeFactoryImpl
