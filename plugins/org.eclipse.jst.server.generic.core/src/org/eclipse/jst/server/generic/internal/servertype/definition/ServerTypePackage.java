/*******************************************************************************
 * Copyright (c) 2004, 2019 IBM Corporation and others.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * <copyright>
 * </copyright>
 *
 * $Id: ServerTypePackage.java,v 1.8 2007/05/10 04:50:47 ndai Exp $
 */
package org.eclipse.jst.server.generic.internal.servertype.definition;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.jst.server.generic.servertype.definition.ServerTypeFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.jst.server.generic.servertype.definition.ServerTypeFactory
 * @model kind="package"
 * @generated
 */
public interface ServerTypePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "definition";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://eclipse.org/jst/server/generic/ServerTypeDefinition";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "definition";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ServerTypePackage eINSTANCE = org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ArchiveTypeImpl <em>Archive Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ArchiveTypeImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getArchiveType()
	 * @generated
	 */
	int ARCHIVE_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARCHIVE_TYPE__PATH = 0;

	/**
	 * The number of structural features of the '<em>Archive Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARCHIVE_TYPE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ArgumentPairImpl <em>Argument Pair</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ArgumentPairImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getArgumentPair()
	 * @generated
	 */
	int ARGUMENT_PAIR = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT_PAIR__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT_PAIR__VALUE = 1;

	/**
	 * The number of structural features of the '<em>Argument Pair</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARGUMENT_PAIR_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl <em>Classpath</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getClasspath()
	 * @generated
	 */
	int CLASSPATH = 2;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASSPATH__GROUP = 0;

	/**
	 * The feature id for the '<em><b>Archive</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASSPATH__ARCHIVE = 1;

	/**
	 * The feature id for the '<em><b>Fileset</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASSPATH__FILESET = 2;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASSPATH__ID = 3;

	/**
	 * The number of structural features of the '<em>Classpath</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASSPATH_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExcludeTypeImpl <em>Exclude Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExcludeTypeImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getExcludeType()
	 * @generated
	 */
	int EXCLUDE_TYPE = 3;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_TYPE__NAME = 0;

	/**
	 * The number of structural features of the '<em>Exclude Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXCLUDE_TYPE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExternalImpl <em>External</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExternalImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getExternal()
	 * @generated
	 */
	int EXTERNAL = 4;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL__VALUE = 0;

	/**
	 * The feature id for the '<em><b>Os</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL__OS = 1;

	/**
	 * The number of structural features of the '<em>External</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl <em>Fileset Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getFilesetType()
	 * @generated
	 */
	int FILESET_TYPE = 5;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILESET_TYPE__GROUP = 0;

	/**
	 * The feature id for the '<em><b>Include</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILESET_TYPE__INCLUDE = 1;

	/**
	 * The feature id for the '<em><b>Exclude</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILESET_TYPE__EXCLUDE = 2;

	/**
	 * The feature id for the '<em><b>Casesensitive</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILESET_TYPE__CASESENSITIVE = 3;

	/**
	 * The feature id for the '<em><b>Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILESET_TYPE__DIR = 4;

	/**
	 * The number of structural features of the '<em>Fileset Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int FILESET_TYPE_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.IncludeTypeImpl <em>Include Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.IncludeTypeImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getIncludeType()
	 * @generated
	 */
	int INCLUDE_TYPE = 6;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INCLUDE_TYPE__NAME = 0;

	/**
	 * The number of structural features of the '<em>Include Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INCLUDE_TYPE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl <em>Jndi Connection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getJndiConnection()
	 * @generated
	 */
	int JNDI_CONNECTION = 7;

	/**
	 * The feature id for the '<em><b>Provider Url</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_CONNECTION__PROVIDER_URL = 0;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_CONNECTION__GROUP = 1;

	/**
	 * The feature id for the '<em><b>Jndi Property</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_CONNECTION__JNDI_PROPERTY = 2;

	/**
	 * The feature id for the '<em><b>Initial Context Factory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY = 3;

	/**
	 * The number of structural features of the '<em>Jndi Connection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_CONNECTION_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl <em>Launch Configuration</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getLaunchConfiguration()
	 * @generated
	 */
	int LAUNCH_CONFIGURATION = 8;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__GROUP = 0;

	/**
	 * The feature id for the '<em><b>Environment Variable</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__ENVIRONMENT_VARIABLE = 1;

	/**
	 * The feature id for the '<em><b>Group1</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__GROUP1 = 2;

	/**
	 * The feature id for the '<em><b>Program Arguments</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS = 3;

	/**
	 * The feature id for the '<em><b>Working Directory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__WORKING_DIRECTORY = 4;

	/**
	 * The feature id for the '<em><b>Main Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__MAIN_CLASS = 5;

	/**
	 * The feature id for the '<em><b>Group2</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__GROUP2 = 6;

	/**
	 * The feature id for the '<em><b>Vm Parameters</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__VM_PARAMETERS = 7;

	/**
	 * The feature id for the '<em><b>Classpath Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE = 8;

	/**
	 * The feature id for the '<em><b>Debug Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__DEBUG_PORT = 9;

	/**
	 * The feature id for the '<em><b>Group3</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__GROUP3 = 10;

	/**
	 * The feature id for the '<em><b>External</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__EXTERNAL = 11;

	/**
	 * The number of structural features of the '<em>Launch Configuration</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION_FEATURE_COUNT = 12;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl <em>Module</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getModule()
	 * @generated
	 */
	int MODULE = 9;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__TYPE = 0;

	/**
	 * The feature id for the '<em><b>Publish Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__PUBLISH_DIR = 1;

	/**
	 * The feature id for the '<em><b>Publisher Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE__PUBLISHER_REFERENCE = 2;

	/**
	 * The number of structural features of the '<em>Module</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int MODULE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PortImpl <em>Port</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.PortImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getPort()
	 * @generated
	 */
	int PORT = 10;

	/**
	 * The feature id for the '<em><b>No</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__NO = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__NAME = 1;

	/**
	 * The feature id for the '<em><b>Protocol</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT__PROTOCOL = 2;

	/**
	 * The number of structural features of the '<em>Port</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PORT_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ProjectImpl <em>Project</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ProjectImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getProject()
	 * @generated
	 */
	int PROJECT = 11;

	/**
	 * The feature id for the '<em><b>Classpath Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__CLASSPATH_REFERENCE = 0;

	/**
	 * The number of structural features of the '<em>Project</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PropertyImpl <em>Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.PropertyImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getProperty()
	 * @generated
	 */
	int PROPERTY = 12;

	/**
	 * The feature id for the '<em><b>Context</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__CONTEXT = 0;

	/**
	 * The feature id for the '<em><b>Default</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__DEFAULT = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__ID = 2;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__LABEL = 3;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY__TYPE = 4;

	/**
	 * The number of structural features of the '<em>Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_FEATURE_COUNT = 5;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherImpl <em>Publisher</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getPublisher()
	 * @generated
	 */
	int PUBLISHER = 13;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER__GROUP = 0;

	/**
	 * The feature id for the '<em><b>Publisherdata</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER__PUBLISHERDATA = 1;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER__ID = 2;

	/**
	 * The number of structural features of the '<em>Publisher</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherDataImpl <em>Publisher Data</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherDataImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getPublisherData()
	 * @generated
	 */
	int PUBLISHER_DATA = 14;

	/**
	 * The feature id for the '<em><b>Dataname</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER_DATA__DATANAME = 0;

	/**
	 * The feature id for the '<em><b>Datavalue</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER_DATA__DATAVALUE = 1;

	/**
	 * The number of structural features of the '<em>Publisher Data</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PUBLISHER_DATA_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerRuntimeImpl <em>Server Runtime</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerRuntimeImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getServerRuntime()
	 * @generated
	 */
	int SERVER_RUNTIME = 15;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__GROUP = 0;

	/**
	 * The feature id for the '<em><b>Property</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__PROPERTY = 1;

	/**
	 * The feature id for the '<em><b>Group1</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__GROUP1 = 2;

	/**
	 * The feature id for the '<em><b>Port</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__PORT = 3;

	/**
	 * The feature id for the '<em><b>Group2</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__GROUP2 = 4;

	/**
	 * The feature id for the '<em><b>Module</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__MODULE = 5;

	/**
	 * The feature id for the '<em><b>Project</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__PROJECT = 6;

	/**
	 * The feature id for the '<em><b>Start</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__START = 7;

	/**
	 * The feature id for the '<em><b>Stop</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__STOP = 8;

	/**
	 * The feature id for the '<em><b>Group3</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__GROUP3 = 9;

	/**
	 * The feature id for the '<em><b>Publisher</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__PUBLISHER = 10;

	/**
	 * The feature id for the '<em><b>Group4</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__GROUP4 = 11;

	/**
	 * The feature id for the '<em><b>Classpath</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__CLASSPATH = 12;

	/**
	 * The feature id for the '<em><b>Jndi Connection</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__JNDI_CONNECTION = 13;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__NAME = 14;

	/**
	 * The feature id for the '<em><b>Version</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME__VERSION = 15;

	/**
	 * The number of structural features of the '<em>Server Runtime</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVER_RUNTIME_FEATURE_COUNT = 16;


	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.ArchiveType <em>Archive Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Archive Type</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ArchiveType
	 * @generated
	 */
	EClass getArchiveType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.ArchiveType#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ArchiveType#getPath()
	 * @see #getArchiveType()
	 * @generated
	 */
	EAttribute getArchiveType_Path();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.ArgumentPair <em>Argument Pair</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Argument Pair</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ArgumentPair
	 * @generated
	 */
	EClass getArgumentPair();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.ArgumentPair#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ArgumentPair#getName()
	 * @see #getArgumentPair()
	 * @generated
	 */
	EAttribute getArgumentPair_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.ArgumentPair#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ArgumentPair#getValue()
	 * @see #getArgumentPair()
	 * @generated
	 */
	EAttribute getArgumentPair_Value();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath <em>Classpath</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Classpath</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Classpath
	 * @generated
	 */
	EClass getClasspath();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Classpath#getGroup()
	 * @see #getClasspath()
	 * @generated
	 */
	EAttribute getClasspath_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getArchive <em>Archive</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Archive</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Classpath#getArchive()
	 * @see #getClasspath()
	 * @generated
	 */
	EReference getClasspath_Archive();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getFileset <em>Fileset</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Fileset</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Classpath#getFileset()
	 * @see #getClasspath()
	 * @generated
	 */
	EReference getClasspath_Fileset();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Classpath#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Classpath#getId()
	 * @see #getClasspath()
	 * @generated
	 */
	EAttribute getClasspath_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.ExcludeType <em>Exclude Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Exclude Type</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ExcludeType
	 * @generated
	 */
	EClass getExcludeType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.ExcludeType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ExcludeType#getName()
	 * @see #getExcludeType()
	 * @generated
	 */
	EAttribute getExcludeType_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.External <em>External</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>External</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.External
	 * @generated
	 */
	EClass getExternal();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.External#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.External#getValue()
	 * @see #getExternal()
	 * @generated
	 */
	EAttribute getExternal_Value();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.External#getOs <em>Os</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Os</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.External#getOs()
	 * @see #getExternal()
	 * @generated
	 */
	EAttribute getExternal_Os();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType <em>Fileset Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Fileset Type</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.FilesetType
	 * @generated
	 */
	EClass getFilesetType();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.FilesetType#getGroup()
	 * @see #getFilesetType()
	 * @generated
	 */
	EAttribute getFilesetType_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getInclude <em>Include</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Include</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.FilesetType#getInclude()
	 * @see #getFilesetType()
	 * @generated
	 */
	EReference getFilesetType_Include();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getExclude <em>Exclude</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Exclude</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.FilesetType#getExclude()
	 * @see #getFilesetType()
	 * @generated
	 */
	EReference getFilesetType_Exclude();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#isCasesensitive <em>Casesensitive</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Casesensitive</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.FilesetType#isCasesensitive()
	 * @see #getFilesetType()
	 * @generated
	 */
	EAttribute getFilesetType_Casesensitive();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.FilesetType#getDir <em>Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dir</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.FilesetType#getDir()
	 * @see #getFilesetType()
	 * @generated
	 */
	EAttribute getFilesetType_Dir();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.IncludeType <em>Include Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Include Type</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.IncludeType
	 * @generated
	 */
	EClass getIncludeType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.IncludeType#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.IncludeType#getName()
	 * @see #getIncludeType()
	 * @generated
	 */
	EAttribute getIncludeType_Name();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection <em>Jndi Connection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Jndi Connection</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.JndiConnection
	 * @generated
	 */
	EClass getJndiConnection();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getProviderUrl <em>Provider Url</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Provider Url</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getProviderUrl()
	 * @see #getJndiConnection()
	 * @generated
	 */
	EAttribute getJndiConnection_ProviderUrl();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getGroup()
	 * @see #getJndiConnection()
	 * @generated
	 */
	EAttribute getJndiConnection_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getJndiProperty <em>Jndi Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Jndi Property</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getJndiProperty()
	 * @see #getJndiConnection()
	 * @generated
	 */
	EReference getJndiConnection_JndiProperty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getInitialContextFactory <em>Initial Context Factory</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Initial Context Factory</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.JndiConnection#getInitialContextFactory()
	 * @see #getJndiConnection()
	 * @generated
	 */
	EAttribute getJndiConnection_InitialContextFactory();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration <em>Launch Configuration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Launch Configuration</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration
	 * @generated
	 */
	EClass getLaunchConfiguration();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getEnvironmentVariable <em>Environment Variable</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Environment Variable</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getEnvironmentVariable()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EReference getLaunchConfiguration_EnvironmentVariable();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup1 <em>Group1</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group1</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup1()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_Group1();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments <em>Program Arguments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Program Arguments</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_ProgramArguments();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getWorkingDirectory <em>Working Directory</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Working Directory</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getWorkingDirectory()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_WorkingDirectory();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getMainClass <em>Main Class</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Main Class</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getMainClass()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_MainClass();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup2 <em>Group2</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group2</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup2()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_Group2();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getVmParameters <em>Vm Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Vm Parameters</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getVmParameters()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_VmParameters();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClasspathReference <em>Classpath Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Classpath Reference</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getClasspathReference()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_ClasspathReference();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getDebugPort <em>Debug Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Debug Port</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getDebugPort()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_DebugPort();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup3 <em>Group3</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group3</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getGroup3()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_Group3();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getExternal <em>External</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>External</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getExternal()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EReference getLaunchConfiguration_External();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.Module <em>Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Module</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Module
	 * @generated
	 */
	EClass getModule();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Module#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Module#getType()
	 * @see #getModule()
	 * @generated
	 */
	EAttribute getModule_Type();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Module#getPublishDir <em>Publish Dir</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Publish Dir</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Module#getPublishDir()
	 * @see #getModule()
	 * @generated
	 */
	EAttribute getModule_PublishDir();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Module#getPublisherReference <em>Publisher Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Publisher Reference</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Module#getPublisherReference()
	 * @see #getModule()
	 * @generated
	 */
	EAttribute getModule_PublisherReference();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Port</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Port
	 * @generated
	 */
	EClass getPort();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Port#getNo <em>No</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>No</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Port#getNo()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_No();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Port#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Port#getName()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Port#getProtocol <em>Protocol</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Protocol</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Port#getProtocol()
	 * @see #getPort()
	 * @generated
	 */
	EAttribute getPort_Protocol();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.Project <em>Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Project</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Project
	 * @generated
	 */
	EClass getProject();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Project#getClasspathReference <em>Classpath Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Classpath Reference</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Project#getClasspathReference()
	 * @see #getProject()
	 * @generated
	 */
	EAttribute getProject_ClasspathReference();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.Property <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Property
	 * @generated
	 */
	EClass getProperty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Property#getContext <em>Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Context</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Property#getContext()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Context();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Property#getDefault <em>Default</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Default</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Property#getDefault()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Default();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Property#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Property#getId()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Id();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Property#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Property#getLabel()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Label();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Property#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Property#getType()
	 * @see #getProperty()
	 * @generated
	 */
	EAttribute getProperty_Type();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.Publisher <em>Publisher</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Publisher</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Publisher
	 * @generated
	 */
	EClass getPublisher();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.Publisher#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Publisher#getGroup()
	 * @see #getPublisher()
	 * @generated
	 */
	EAttribute getPublisher_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.Publisher#getPublisherdata <em>Publisherdata</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Publisherdata</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Publisher#getPublisherdata()
	 * @see #getPublisher()
	 * @generated
	 */
	EReference getPublisher_Publisherdata();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.Publisher#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.Publisher#getId()
	 * @see #getPublisher()
	 * @generated
	 */
	EAttribute getPublisher_Id();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.PublisherData <em>Publisher Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Publisher Data</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.PublisherData
	 * @generated
	 */
	EClass getPublisherData();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.PublisherData#getDataname <em>Dataname</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dataname</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.PublisherData#getDataname()
	 * @see #getPublisherData()
	 * @generated
	 */
	EAttribute getPublisherData_Dataname();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.PublisherData#getDatavalue <em>Datavalue</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Datavalue</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.PublisherData#getDatavalue()
	 * @see #getPublisherData()
	 * @generated
	 */
	EAttribute getPublisherData_Datavalue();

	/**
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime <em>Server Runtime</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Server Runtime</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime
	 * @generated
	 */
	EClass getServerRuntime();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup <em>Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EAttribute getServerRuntime_Group();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getProperty <em>Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Property</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getProperty()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_Property();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup1 <em>Group1</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group1</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup1()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EAttribute getServerRuntime_Group1();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getPort <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Port</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getPort()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_Port();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup2 <em>Group2</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group2</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup2()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EAttribute getServerRuntime_Group2();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getModule <em>Module</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Module</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getModule()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_Module();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getProject <em>Project</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Project</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getProject()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_Project();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getStart <em>Start</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Start</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getStart()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_Start();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getStop <em>Stop</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Stop</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getStop()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_Stop();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup3 <em>Group3</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group3</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup3()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EAttribute getServerRuntime_Group3();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getPublisher <em>Publisher</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Publisher</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getPublisher()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_Publisher();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup4 <em>Group4</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Group4</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getGroup4()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EAttribute getServerRuntime_Group4();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getClasspath <em>Classpath</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Classpath</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getClasspath()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_Classpath();

	/**
	 * Returns the meta object for the containment reference '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getJndiConnection <em>Jndi Connection</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Jndi Connection</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getJndiConnection()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EReference getServerRuntime_JndiConnection();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getName()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EAttribute getServerRuntime_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getVersion <em>Version</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Version</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ServerRuntime#getVersion()
	 * @see #getServerRuntime()
	 * @generated
	 */
	EAttribute getServerRuntime_Version();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ServerTypeFactory getServerTypeFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ArchiveTypeImpl <em>Archive Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ArchiveTypeImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getArchiveType()
		 * @generated
		 */
		EClass ARCHIVE_TYPE = eINSTANCE.getArchiveType();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARCHIVE_TYPE__PATH = eINSTANCE.getArchiveType_Path();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ArgumentPairImpl <em>Argument Pair</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ArgumentPairImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getArgumentPair()
		 * @generated
		 */
		EClass ARGUMENT_PAIR = eINSTANCE.getArgumentPair();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARGUMENT_PAIR__NAME = eINSTANCE.getArgumentPair_Name();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute ARGUMENT_PAIR__VALUE = eINSTANCE.getArgumentPair_Value();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl <em>Classpath</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getClasspath()
		 * @generated
		 */
		EClass CLASSPATH = eINSTANCE.getClasspath();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASSPATH__GROUP = eINSTANCE.getClasspath_Group();

		/**
		 * The meta object literal for the '<em><b>Archive</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASSPATH__ARCHIVE = eINSTANCE.getClasspath_Archive();

		/**
		 * The meta object literal for the '<em><b>Fileset</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CLASSPATH__FILESET = eINSTANCE.getClasspath_Fileset();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CLASSPATH__ID = eINSTANCE.getClasspath_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExcludeTypeImpl <em>Exclude Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExcludeTypeImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getExcludeType()
		 * @generated
		 */
		EClass EXCLUDE_TYPE = eINSTANCE.getExcludeType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXCLUDE_TYPE__NAME = eINSTANCE.getExcludeType_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExternalImpl <em>External</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExternalImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getExternal()
		 * @generated
		 */
		EClass EXTERNAL = eINSTANCE.getExternal();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXTERNAL__VALUE = eINSTANCE.getExternal_Value();

		/**
		 * The meta object literal for the '<em><b>Os</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute EXTERNAL__OS = eINSTANCE.getExternal_Os();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl <em>Fileset Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.FilesetTypeImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getFilesetType()
		 * @generated
		 */
		EClass FILESET_TYPE = eINSTANCE.getFilesetType();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILESET_TYPE__GROUP = eINSTANCE.getFilesetType_Group();

		/**
		 * The meta object literal for the '<em><b>Include</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FILESET_TYPE__INCLUDE = eINSTANCE.getFilesetType_Include();

		/**
		 * The meta object literal for the '<em><b>Exclude</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference FILESET_TYPE__EXCLUDE = eINSTANCE.getFilesetType_Exclude();

		/**
		 * The meta object literal for the '<em><b>Casesensitive</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILESET_TYPE__CASESENSITIVE = eINSTANCE.getFilesetType_Casesensitive();

		/**
		 * The meta object literal for the '<em><b>Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute FILESET_TYPE__DIR = eINSTANCE.getFilesetType_Dir();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.IncludeTypeImpl <em>Include Type</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.IncludeTypeImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getIncludeType()
		 * @generated
		 */
		EClass INCLUDE_TYPE = eINSTANCE.getIncludeType();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INCLUDE_TYPE__NAME = eINSTANCE.getIncludeType_Name();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl <em>Jndi Connection</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getJndiConnection()
		 * @generated
		 */
		EClass JNDI_CONNECTION = eINSTANCE.getJndiConnection();

		/**
		 * The meta object literal for the '<em><b>Provider Url</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JNDI_CONNECTION__PROVIDER_URL = eINSTANCE.getJndiConnection_ProviderUrl();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JNDI_CONNECTION__GROUP = eINSTANCE.getJndiConnection_Group();

		/**
		 * The meta object literal for the '<em><b>Jndi Property</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference JNDI_CONNECTION__JNDI_PROPERTY = eINSTANCE.getJndiConnection_JndiProperty();

		/**
		 * The meta object literal for the '<em><b>Initial Context Factory</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY = eINSTANCE.getJndiConnection_InitialContextFactory();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl <em>Launch Configuration</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getLaunchConfiguration()
		 * @generated
		 */
		EClass LAUNCH_CONFIGURATION = eINSTANCE.getLaunchConfiguration();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__GROUP = eINSTANCE.getLaunchConfiguration_Group();

		/**
		 * The meta object literal for the '<em><b>Environment Variable</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LAUNCH_CONFIGURATION__ENVIRONMENT_VARIABLE = eINSTANCE.getLaunchConfiguration_EnvironmentVariable();

		/**
		 * The meta object literal for the '<em><b>Group1</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__GROUP1 = eINSTANCE.getLaunchConfiguration_Group1();

		/**
		 * The meta object literal for the '<em><b>Program Arguments</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS = eINSTANCE.getLaunchConfiguration_ProgramArguments();

		/**
		 * The meta object literal for the '<em><b>Working Directory</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__WORKING_DIRECTORY = eINSTANCE.getLaunchConfiguration_WorkingDirectory();

		/**
		 * The meta object literal for the '<em><b>Main Class</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__MAIN_CLASS = eINSTANCE.getLaunchConfiguration_MainClass();

		/**
		 * The meta object literal for the '<em><b>Group2</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__GROUP2 = eINSTANCE.getLaunchConfiguration_Group2();

		/**
		 * The meta object literal for the '<em><b>Vm Parameters</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__VM_PARAMETERS = eINSTANCE.getLaunchConfiguration_VmParameters();

		/**
		 * The meta object literal for the '<em><b>Classpath Reference</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE = eINSTANCE.getLaunchConfiguration_ClasspathReference();

		/**
		 * The meta object literal for the '<em><b>Debug Port</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__DEBUG_PORT = eINSTANCE.getLaunchConfiguration_DebugPort();

		/**
		 * The meta object literal for the '<em><b>Group3</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LAUNCH_CONFIGURATION__GROUP3 = eINSTANCE.getLaunchConfiguration_Group3();

		/**
		 * The meta object literal for the '<em><b>External</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LAUNCH_CONFIGURATION__EXTERNAL = eINSTANCE.getLaunchConfiguration_External();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl <em>Module</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getModule()
		 * @generated
		 */
		EClass MODULE = eINSTANCE.getModule();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODULE__TYPE = eINSTANCE.getModule_Type();

		/**
		 * The meta object literal for the '<em><b>Publish Dir</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODULE__PUBLISH_DIR = eINSTANCE.getModule_PublishDir();

		/**
		 * The meta object literal for the '<em><b>Publisher Reference</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute MODULE__PUBLISHER_REFERENCE = eINSTANCE.getModule_PublisherReference();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PortImpl <em>Port</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.PortImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getPort()
		 * @generated
		 */
		EClass PORT = eINSTANCE.getPort();

		/**
		 * The meta object literal for the '<em><b>No</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT__NO = eINSTANCE.getPort_No();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT__NAME = eINSTANCE.getPort_Name();

		/**
		 * The meta object literal for the '<em><b>Protocol</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PORT__PROTOCOL = eINSTANCE.getPort_Protocol();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ProjectImpl <em>Project</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ProjectImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getProject()
		 * @generated
		 */
		EClass PROJECT = eINSTANCE.getProject();

		/**
		 * The meta object literal for the '<em><b>Classpath Reference</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROJECT__CLASSPATH_REFERENCE = eINSTANCE.getProject_ClasspathReference();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PropertyImpl <em>Property</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.PropertyImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getProperty()
		 * @generated
		 */
		EClass PROPERTY = eINSTANCE.getProperty();

		/**
		 * The meta object literal for the '<em><b>Context</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__CONTEXT = eINSTANCE.getProperty_Context();

		/**
		 * The meta object literal for the '<em><b>Default</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__DEFAULT = eINSTANCE.getProperty_Default();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__ID = eINSTANCE.getProperty_Id();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__LABEL = eINSTANCE.getProperty_Label();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PROPERTY__TYPE = eINSTANCE.getProperty_Type();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherImpl <em>Publisher</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getPublisher()
		 * @generated
		 */
		EClass PUBLISHER = eINSTANCE.getPublisher();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PUBLISHER__GROUP = eINSTANCE.getPublisher_Group();

		/**
		 * The meta object literal for the '<em><b>Publisherdata</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PUBLISHER__PUBLISHERDATA = eINSTANCE.getPublisher_Publisherdata();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PUBLISHER__ID = eINSTANCE.getPublisher_Id();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherDataImpl <em>Publisher Data</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.PublisherDataImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getPublisherData()
		 * @generated
		 */
		EClass PUBLISHER_DATA = eINSTANCE.getPublisherData();

		/**
		 * The meta object literal for the '<em><b>Dataname</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PUBLISHER_DATA__DATANAME = eINSTANCE.getPublisherData_Dataname();

		/**
		 * The meta object literal for the '<em><b>Datavalue</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute PUBLISHER_DATA__DATAVALUE = eINSTANCE.getPublisherData_Datavalue();

		/**
		 * The meta object literal for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerRuntimeImpl <em>Server Runtime</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerRuntimeImpl
		 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getServerRuntime()
		 * @generated
		 */
		EClass SERVER_RUNTIME = eINSTANCE.getServerRuntime();

		/**
		 * The meta object literal for the '<em><b>Group</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_RUNTIME__GROUP = eINSTANCE.getServerRuntime_Group();

		/**
		 * The meta object literal for the '<em><b>Property</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__PROPERTY = eINSTANCE.getServerRuntime_Property();

		/**
		 * The meta object literal for the '<em><b>Group1</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_RUNTIME__GROUP1 = eINSTANCE.getServerRuntime_Group1();

		/**
		 * The meta object literal for the '<em><b>Port</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__PORT = eINSTANCE.getServerRuntime_Port();

		/**
		 * The meta object literal for the '<em><b>Group2</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_RUNTIME__GROUP2 = eINSTANCE.getServerRuntime_Group2();

		/**
		 * The meta object literal for the '<em><b>Module</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__MODULE = eINSTANCE.getServerRuntime_Module();

		/**
		 * The meta object literal for the '<em><b>Project</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__PROJECT = eINSTANCE.getServerRuntime_Project();

		/**
		 * The meta object literal for the '<em><b>Start</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__START = eINSTANCE.getServerRuntime_Start();

		/**
		 * The meta object literal for the '<em><b>Stop</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__STOP = eINSTANCE.getServerRuntime_Stop();

		/**
		 * The meta object literal for the '<em><b>Group3</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_RUNTIME__GROUP3 = eINSTANCE.getServerRuntime_Group3();

		/**
		 * The meta object literal for the '<em><b>Publisher</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__PUBLISHER = eINSTANCE.getServerRuntime_Publisher();

		/**
		 * The meta object literal for the '<em><b>Group4</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_RUNTIME__GROUP4 = eINSTANCE.getServerRuntime_Group4();

		/**
		 * The meta object literal for the '<em><b>Classpath</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__CLASSPATH = eINSTANCE.getServerRuntime_Classpath();

		/**
		 * The meta object literal for the '<em><b>Jndi Connection</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVER_RUNTIME__JNDI_CONNECTION = eINSTANCE.getServerRuntime_JndiConnection();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_RUNTIME__NAME = eINSTANCE.getServerRuntime_Name();

		/**
		 * The meta object literal for the '<em><b>Version</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SERVER_RUNTIME__VERSION = eINSTANCE.getServerRuntime_Version();

	}

} //ServerTypePackage
