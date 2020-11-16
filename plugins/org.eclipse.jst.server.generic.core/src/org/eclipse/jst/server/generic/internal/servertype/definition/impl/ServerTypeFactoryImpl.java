/***************************************************************************************************
 * Copyright (c) 2005-2020 Eteration A.S. and Gorkem Ercan All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan
 * Contributors: Naci Dai
 *               
 **************************************************************************************************/

package org.eclipse.jst.server.generic.internal.servertype.definition.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage;
import org.eclipse.jst.server.generic.servertype.definition.Module;
import org.eclipse.jst.server.generic.servertype.definition.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ServerTypeFactoryImpl extends EFactoryImpl implements ServerTypeFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ServerTypeFactory init() {
		try {
			ServerTypeFactory theServerTypeFactory = (ServerTypeFactory)EPackage.Registry.INSTANCE.getEFactory("http://eclipse.org/jst/server/generic/ServerTypeDefinition"); 
			if (theServerTypeFactory != null) {
				return theServerTypeFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ServerTypeFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
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
			case ServerTypePackage.ARGUMENT_PAIR: return createArgumentPair();
			case ServerTypePackage.CLASSPATH: return createClasspath();
			case ServerTypePackage.EXCLUDE_TYPE: return createExcludeType();
			case ServerTypePackage.EXTERNAL: return createExternal();
			case ServerTypePackage.FILESET_TYPE: return createFilesetType();
			case ServerTypePackage.INCLUDE_TYPE: return createIncludeType();
			case ServerTypePackage.JNDI_CONNECTION: return createJndiConnection();
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
	public ArgumentPair createArgumentPair() {
		ArgumentPairImpl argumentPair = new ArgumentPairImpl();
		return argumentPair;
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
	public ExcludeType createExcludeType() {
		ExcludeTypeImpl excludeType = new ExcludeTypeImpl();
		return excludeType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public External createExternal() {
		ExternalImpl external = new ExternalImpl();
		return external;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FilesetType createFilesetType() {
		FilesetTypeImpl filesetType = new FilesetTypeImpl();
		return filesetType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public IncludeType createIncludeType() {
		IncludeTypeImpl includeType = new IncludeTypeImpl();
		return includeType;
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
