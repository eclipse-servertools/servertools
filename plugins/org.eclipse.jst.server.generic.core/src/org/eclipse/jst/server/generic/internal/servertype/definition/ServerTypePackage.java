/**
 * <copyright>
 * </copyright>
 *
 * $Id: ServerTypePackage.java,v 1.3 2005/06/14 20:45:45 gercan Exp $
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
 * @generated
 */
public interface ServerTypePackage extends EPackage{
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
	 * The number of structural features of the the '<em>Archive Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ARCHIVE_TYPE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl <em>Classpath</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ClasspathImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getClasspath()
	 * @generated
	 */
	int CLASSPATH = 1;

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
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASSPATH__ID = 2;

	/**
	 * The number of structural features of the the '<em>Classpath</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CLASSPATH_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExternalTypeImpl <em>External Type</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ExternalTypeImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getExternalType()
	 * @generated
	 */
	int EXTERNAL_TYPE = 2;

	/**
	 * The feature id for the '<em><b>Os</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_TYPE__OS = 0;

	/**
	 * The number of structural features of the the '<em>External Type</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int EXTERNAL_TYPE_FEATURE_COUNT = 1;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl <em>Jndi Connection</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiConnectionImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getJndiConnection()
	 * @generated
	 */
	int JNDI_CONNECTION = 3;

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
	 * The number of structural features of the the '<em>Jndi Connection</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_CONNECTION_FEATURE_COUNT = 4;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiPropertyImpl <em>Jndi Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.JndiPropertyImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getJndiProperty()
	 * @generated
	 */
	int JNDI_PROPERTY = 4;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_PROPERTY__NAME = 0;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_PROPERTY__VALUE = 1;

	/**
	 * The number of structural features of the the '<em>Jndi Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int JNDI_PROPERTY_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl <em>Launch Configuration</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.LaunchConfigurationImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getLaunchConfiguration()
	 * @generated
	 */
	int LAUNCH_CONFIGURATION = 5;

	/**
	 * The feature id for the '<em><b>Working Directory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__WORKING_DIRECTORY = 0;

	/**
	 * The feature id for the '<em><b>Program Arguments</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS = 1;

	/**
	 * The feature id for the '<em><b>Main Class</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__MAIN_CLASS = 2;

	/**
	 * The feature id for the '<em><b>Vm Parameters</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__VM_PARAMETERS = 3;

	/**
	 * The feature id for the '<em><b>Classpath Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE = 4;

	/**
	 * The feature id for the '<em><b>Debug Port</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__DEBUG_PORT = 5;

	/**
	 * The feature id for the '<em><b>Group</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__GROUP = 6;

	/**
	 * The feature id for the '<em><b>External</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__EXTERNAL = 7;

	/**
	 * The feature id for the '<em><b>Launch Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION__LAUNCH_TYPE = 8;

	/**
	 * The number of structural features of the the '<em>Launch Configuration</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LAUNCH_CONFIGURATION_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '{@link org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl <em>Module</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ModuleImpl
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.impl.ServerTypePackageImpl#getModule()
	 * @generated
	 */
	int MODULE = 6;

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
	 * The number of structural features of the the '<em>Module</em>' class.
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
	int PORT = 7;

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
	 * The number of structural features of the the '<em>Port</em>' class.
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
	int PROJECT = 8;

	/**
	 * The feature id for the '<em><b>Classpath Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROJECT__CLASSPATH_REFERENCE = 0;

	/**
	 * The number of structural features of the the '<em>Project</em>' class.
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
	int PROPERTY = 9;

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
	 * The number of structural features of the the '<em>Property</em>' class.
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
	int PUBLISHER = 10;

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
	 * The number of structural features of the the '<em>Publisher</em>' class.
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
	int PUBLISHER_DATA = 11;

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
	 * The number of structural features of the the '<em>Publisher Data</em>' class.
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
	int SERVER_RUNTIME = 12;

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
	 * The number of structural features of the the '<em>Server Runtime</em>' class.
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
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.ExternalType <em>External Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>External Type</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ExternalType
	 * @generated
	 */
	EClass getExternalType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.ExternalType#getOs <em>Os</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Os</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.ExternalType#getOs()
	 * @see #getExternalType()
	 * @generated
	 */
	EAttribute getExternalType_Os();

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
	 * Returns the meta object for class '{@link org.eclipse.jst.server.generic.servertype.definition.JndiProperty <em>Jndi Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Jndi Property</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.JndiProperty
	 * @generated
	 */
	EClass getJndiProperty();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.JndiProperty#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.JndiProperty#getName()
	 * @see #getJndiProperty()
	 * @generated
	 */
	EAttribute getJndiProperty_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.JndiProperty#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.JndiProperty#getValue()
	 * @see #getJndiProperty()
	 * @generated
	 */
	EAttribute getJndiProperty_Value();

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
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments <em>Program Arguments</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Program Arguments</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getProgramArguments()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_ProgramArguments();

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
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getVmParameters <em>Vm Parameters</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Vm Parameters</em>'.
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
	 * Returns the meta object for the attribute '{@link org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getLaunchType <em>Launch Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Launch Type</em>'.
	 * @see org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration#getLaunchType()
	 * @see #getLaunchConfiguration()
	 * @generated
	 */
	EAttribute getLaunchConfiguration_LaunchType();

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

} //ServerTypePackage
