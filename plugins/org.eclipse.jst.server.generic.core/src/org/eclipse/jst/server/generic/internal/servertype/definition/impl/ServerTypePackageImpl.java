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
 * $Id: ServerTypePackageImpl.java,v 1.4 2005/06/13 21:01:36 gercan Exp $
 */
package org.eclipse.jst.server.generic.internal.servertype.definition.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.emf.ecore.xml.type.XMLTypePackage;

import org.eclipse.emf.ecore.xml.type.impl.XMLTypePackageImpl;

import org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage;
import org.eclipse.jst.server.generic.servertype.definition.ArchiveType;
import org.eclipse.jst.server.generic.servertype.definition.Classpath;
import org.eclipse.jst.server.generic.servertype.definition.JndiConnection;
import org.eclipse.jst.server.generic.servertype.definition.JndiProperty;
import org.eclipse.jst.server.generic.servertype.definition.LaunchConfiguration;
import org.eclipse.jst.server.generic.servertype.definition.Module;
import org.eclipse.jst.server.generic.servertype.definition.Port;
import org.eclipse.jst.server.generic.servertype.definition.Project;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.servertype.definition.Publisher;
import org.eclipse.jst.server.generic.servertype.definition.PublisherData;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.ServerTypeFactory;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ServerTypePackageImpl extends EPackageImpl implements ServerTypePackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass archiveTypeEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass classpathEClass = null;

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass jndiConnectionEClass = null;

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass jndiPropertyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass launchConfigurationEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass moduleEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass portEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass projectEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertyEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass publisherEClass = null;

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    private EClass publisherDataEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass serverRuntimeEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.jst.server.generic.internal.servertype.definition.ServerTypePackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ServerTypePackageImpl() {
		super(eNS_URI, ServerTypeFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ServerTypePackage init() {
		if (isInited) return (ServerTypePackage)EPackage.Registry.INSTANCE.getEPackage(ServerTypePackage.eNS_URI);

		// Obtain or create and register package
		ServerTypePackageImpl theServerTypePackage = (ServerTypePackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ServerTypePackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ServerTypePackageImpl());

		isInited = true;

		// Initialize simple dependencies
		XMLTypePackageImpl.init();

		// Create package meta-data objects
		theServerTypePackage.createPackageContents();

		// Initialize created meta-data
		theServerTypePackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theServerTypePackage.freeze();

		return theServerTypePackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getArchiveType() {
		return archiveTypeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getArchiveType_Path() {
		return (EAttribute)archiveTypeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getClasspath() {
		return classpathEClass;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getClasspath_Group() {
		return (EAttribute)classpathEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getClasspath_Archive() {
		return (EReference)classpathEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getClasspath_Id() {
		return (EAttribute)classpathEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getJndiConnection() {
		return jndiConnectionEClass;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getJndiConnection_ProviderUrl() {
		return (EAttribute)jndiConnectionEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getJndiConnection_Group() {
		return (EAttribute)jndiConnectionEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getJndiConnection_JndiProperty() {
		return (EReference)jndiConnectionEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getJndiConnection_InitialContextFactory() {
		return (EAttribute)jndiConnectionEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getJndiProperty() {
		return jndiPropertyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getJndiProperty_Name() {
		return (EAttribute)jndiPropertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getJndiProperty_Value() {
		return (EAttribute)jndiPropertyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLaunchConfiguration() {
		return launchConfigurationEClass;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getLaunchConfiguration_MainClass() {
		return (EAttribute)launchConfigurationEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLaunchConfiguration_WorkingDirectory() {
		return (EAttribute)launchConfigurationEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLaunchConfiguration_ProgramArguments() {
		return (EAttribute)launchConfigurationEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLaunchConfiguration_VmParameters() {
		return (EAttribute)launchConfigurationEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLaunchConfiguration_ClasspathReference() {
		return (EAttribute)launchConfigurationEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLaunchConfiguration_External() {
		return (EAttribute)launchConfigurationEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLaunchConfiguration_DebugPort() {
		return (EAttribute)launchConfigurationEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getModule() {
		return moduleEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModule_Type() {
		return (EAttribute)moduleEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModule_PublishDir() {
		return (EAttribute)moduleEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getModule_PublisherReference() {
		return (EAttribute)moduleEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPort() {
		return portEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPort_No() {
		return (EAttribute)portEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPort_Name() {
		return (EAttribute)portEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPort_Protocol() {
		return (EAttribute)portEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProject() {
		return projectEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProject_ClasspathReference() {
		return (EAttribute)projectEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getProperty() {
		return propertyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProperty_Context() {
		return (EAttribute)propertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProperty_Default() {
		return (EAttribute)propertyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProperty_Id() {
		return (EAttribute)propertyEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProperty_Label() {
		return (EAttribute)propertyEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getProperty_Type() {
		return (EAttribute)propertyEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPublisher() {
		return publisherEClass;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getPublisher_Group() {
		return (EAttribute)publisherEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getPublisher_Publisherdata() {
		return (EReference)publisherEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getPublisher_Id() {
		return (EAttribute)publisherEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EClass getPublisherData() {
		return publisherDataEClass;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getPublisherData_Dataname() {
		return (EAttribute)publisherDataEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getPublisherData_Datavalue() {
		return (EAttribute)publisherDataEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getServerRuntime() {
		return serverRuntimeEClass;
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getServerRuntime_Group() {
		return (EAttribute)serverRuntimeEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getServerRuntime_Property() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getServerRuntime_Group1() {
		return (EAttribute)serverRuntimeEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getServerRuntime_Port() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getServerRuntime_Group2() {
		return (EAttribute)serverRuntimeEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getServerRuntime_Module() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getServerRuntime_Project() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getServerRuntime_Start() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getServerRuntime_Stop() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getServerRuntime_Group3() {
		return (EAttribute)serverRuntimeEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getServerRuntime_Publisher() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EAttribute getServerRuntime_Group4() {
		return (EAttribute)serverRuntimeEClass.getEStructuralFeatures().get(11);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getServerRuntime_Classpath() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(12);
	}

	/**
	 * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
	 * @generated
	 */
    public EReference getServerRuntime_JndiConnection() {
		return (EReference)serverRuntimeEClass.getEStructuralFeatures().get(13);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getServerRuntime_Name() {
		return (EAttribute)serverRuntimeEClass.getEStructuralFeatures().get(14);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getServerRuntime_Version() {
		return (EAttribute)serverRuntimeEClass.getEStructuralFeatures().get(15);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ServerTypeFactory getServerTypeFactory() {
		return (ServerTypeFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		archiveTypeEClass = createEClass(ARCHIVE_TYPE);
		createEAttribute(archiveTypeEClass, ARCHIVE_TYPE__PATH);

		classpathEClass = createEClass(CLASSPATH);
		createEAttribute(classpathEClass, CLASSPATH__GROUP);
		createEReference(classpathEClass, CLASSPATH__ARCHIVE);
		createEAttribute(classpathEClass, CLASSPATH__ID);

		jndiConnectionEClass = createEClass(JNDI_CONNECTION);
		createEAttribute(jndiConnectionEClass, JNDI_CONNECTION__PROVIDER_URL);
		createEAttribute(jndiConnectionEClass, JNDI_CONNECTION__GROUP);
		createEReference(jndiConnectionEClass, JNDI_CONNECTION__JNDI_PROPERTY);
		createEAttribute(jndiConnectionEClass, JNDI_CONNECTION__INITIAL_CONTEXT_FACTORY);

		jndiPropertyEClass = createEClass(JNDI_PROPERTY);
		createEAttribute(jndiPropertyEClass, JNDI_PROPERTY__NAME);
		createEAttribute(jndiPropertyEClass, JNDI_PROPERTY__VALUE);

		launchConfigurationEClass = createEClass(LAUNCH_CONFIGURATION);
		createEAttribute(launchConfigurationEClass, LAUNCH_CONFIGURATION__WORKING_DIRECTORY);
		createEAttribute(launchConfigurationEClass, LAUNCH_CONFIGURATION__PROGRAM_ARGUMENTS);
		createEAttribute(launchConfigurationEClass, LAUNCH_CONFIGURATION__MAIN_CLASS);
		createEAttribute(launchConfigurationEClass, LAUNCH_CONFIGURATION__VM_PARAMETERS);
		createEAttribute(launchConfigurationEClass, LAUNCH_CONFIGURATION__CLASSPATH_REFERENCE);
		createEAttribute(launchConfigurationEClass, LAUNCH_CONFIGURATION__EXTERNAL);
		createEAttribute(launchConfigurationEClass, LAUNCH_CONFIGURATION__DEBUG_PORT);

		moduleEClass = createEClass(MODULE);
		createEAttribute(moduleEClass, MODULE__TYPE);
		createEAttribute(moduleEClass, MODULE__PUBLISH_DIR);
		createEAttribute(moduleEClass, MODULE__PUBLISHER_REFERENCE);

		portEClass = createEClass(PORT);
		createEAttribute(portEClass, PORT__NO);
		createEAttribute(portEClass, PORT__NAME);
		createEAttribute(portEClass, PORT__PROTOCOL);

		projectEClass = createEClass(PROJECT);
		createEAttribute(projectEClass, PROJECT__CLASSPATH_REFERENCE);

		propertyEClass = createEClass(PROPERTY);
		createEAttribute(propertyEClass, PROPERTY__CONTEXT);
		createEAttribute(propertyEClass, PROPERTY__DEFAULT);
		createEAttribute(propertyEClass, PROPERTY__ID);
		createEAttribute(propertyEClass, PROPERTY__LABEL);
		createEAttribute(propertyEClass, PROPERTY__TYPE);

		publisherEClass = createEClass(PUBLISHER);
		createEAttribute(publisherEClass, PUBLISHER__GROUP);
		createEReference(publisherEClass, PUBLISHER__PUBLISHERDATA);
		createEAttribute(publisherEClass, PUBLISHER__ID);

		publisherDataEClass = createEClass(PUBLISHER_DATA);
		createEAttribute(publisherDataEClass, PUBLISHER_DATA__DATANAME);
		createEAttribute(publisherDataEClass, PUBLISHER_DATA__DATAVALUE);

		serverRuntimeEClass = createEClass(SERVER_RUNTIME);
		createEAttribute(serverRuntimeEClass, SERVER_RUNTIME__GROUP);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__PROPERTY);
		createEAttribute(serverRuntimeEClass, SERVER_RUNTIME__GROUP1);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__PORT);
		createEAttribute(serverRuntimeEClass, SERVER_RUNTIME__GROUP2);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__MODULE);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__PROJECT);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__START);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__STOP);
		createEAttribute(serverRuntimeEClass, SERVER_RUNTIME__GROUP3);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__PUBLISHER);
		createEAttribute(serverRuntimeEClass, SERVER_RUNTIME__GROUP4);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__CLASSPATH);
		createEReference(serverRuntimeEClass, SERVER_RUNTIME__JNDI_CONNECTION);
		createEAttribute(serverRuntimeEClass, SERVER_RUNTIME__NAME);
		createEAttribute(serverRuntimeEClass, SERVER_RUNTIME__VERSION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		XMLTypePackageImpl theXMLTypePackage = (XMLTypePackageImpl)EPackage.Registry.INSTANCE.getEPackage(XMLTypePackage.eNS_URI);

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(archiveTypeEClass, ArchiveType.class, "ArchiveType", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getArchiveType_Path(), theXMLTypePackage.getString(), "path", null, 0, 1, ArchiveType.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(classpathEClass, Classpath.class, "Classpath", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getClasspath_Group(), ecorePackage.getEFeatureMapEntry(), "group", null, 0, -1, Classpath.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getClasspath_Archive(), this.getArchiveType(), null, "archive", null, 1, -1, Classpath.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getClasspath_Id(), theXMLTypePackage.getString(), "id", null, 0, 1, Classpath.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(jndiConnectionEClass, JndiConnection.class, "JndiConnection", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getJndiConnection_ProviderUrl(), theXMLTypePackage.getString(), "providerUrl", null, 1, 1, JndiConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJndiConnection_Group(), ecorePackage.getEFeatureMapEntry(), "group", null, 0, -1, JndiConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getJndiConnection_JndiProperty(), this.getJndiProperty(), null, "jndiProperty", null, 0, -1, JndiConnection.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getJndiConnection_InitialContextFactory(), theXMLTypePackage.getString(), "initialContextFactory", null, 1, 1, JndiConnection.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(jndiPropertyEClass, JndiProperty.class, "JndiProperty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getJndiProperty_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, JndiProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getJndiProperty_Value(), theXMLTypePackage.getString(), "value", null, 1, 1, JndiProperty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(launchConfigurationEClass, LaunchConfiguration.class, "LaunchConfiguration", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLaunchConfiguration_WorkingDirectory(), theXMLTypePackage.getString(), "workingDirectory", null, 1, 1, LaunchConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLaunchConfiguration_ProgramArguments(), theXMLTypePackage.getString(), "programArguments", null, 1, 1, LaunchConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLaunchConfiguration_MainClass(), theXMLTypePackage.getString(), "mainClass", null, 0, 1, LaunchConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLaunchConfiguration_VmParameters(), theXMLTypePackage.getString(), "vmParameters", null, 0, 1, LaunchConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLaunchConfiguration_ClasspathReference(), theXMLTypePackage.getString(), "classpathReference", null, 0, 1, LaunchConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLaunchConfiguration_External(), theXMLTypePackage.getString(), "external", null, 0, 1, LaunchConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLaunchConfiguration_DebugPort(), theXMLTypePackage.getString(), "debugPort", null, 0, 1, LaunchConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(moduleEClass, Module.class, "Module", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getModule_Type(), theXMLTypePackage.getString(), "type", null, 1, 1, Module.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModule_PublishDir(), theXMLTypePackage.getString(), "publishDir", null, 1, 1, Module.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getModule_PublisherReference(), theXMLTypePackage.getString(), "publisherReference", null, 1, 1, Module.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(portEClass, Port.class, "Port", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPort_No(), theXMLTypePackage.getString(), "no", null, 1, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPort_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPort_Protocol(), theXMLTypePackage.getString(), "protocol", null, 1, 1, Port.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(projectEClass, Project.class, "Project", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProject_ClasspathReference(), theXMLTypePackage.getString(), "classpathReference", null, 1, 1, Project.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(propertyEClass, Property.class, "Property", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getProperty_Context(), theXMLTypePackage.getString(), "context", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProperty_Default(), theXMLTypePackage.getString(), "default", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProperty_Id(), theXMLTypePackage.getString(), "id", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProperty_Label(), theXMLTypePackage.getString(), "label", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getProperty_Type(), theXMLTypePackage.getString(), "type", null, 0, 1, Property.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(publisherEClass, Publisher.class, "Publisher", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPublisher_Group(), ecorePackage.getEFeatureMapEntry(), "group", null, 0, -1, Publisher.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getPublisher_Publisherdata(), this.getPublisherData(), null, "publisherdata", null, 1, -1, Publisher.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getPublisher_Id(), theXMLTypePackage.getString(), "id", null, 0, 1, Publisher.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(publisherDataEClass, PublisherData.class, "PublisherData", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getPublisherData_Dataname(), theXMLTypePackage.getString(), "dataname", null, 1, 1, PublisherData.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getPublisherData_Datavalue(), theXMLTypePackage.getString(), "datavalue", null, 1, 1, PublisherData.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(serverRuntimeEClass, ServerRuntime.class, "ServerRuntime", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getServerRuntime_Group(), ecorePackage.getEFeatureMapEntry(), "group", null, 0, -1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_Property(), this.getProperty(), null, "property", null, 0, -1, ServerRuntime.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getServerRuntime_Group1(), ecorePackage.getEFeatureMapEntry(), "group1", null, 0, -1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_Port(), this.getPort(), null, "port", null, 0, -1, ServerRuntime.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getServerRuntime_Group2(), ecorePackage.getEFeatureMapEntry(), "group2", null, 0, -1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_Module(), this.getModule(), null, "module", null, 1, -1, ServerRuntime.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_Project(), this.getProject(), null, "project", null, 1, 1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_Start(), this.getLaunchConfiguration(), null, "start", null, 1, 1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_Stop(), this.getLaunchConfiguration(), null, "stop", null, 1, 1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getServerRuntime_Group3(), ecorePackage.getEFeatureMapEntry(), "group3", null, 0, -1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_Publisher(), this.getPublisher(), null, "publisher", null, 0, -1, ServerRuntime.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getServerRuntime_Group4(), ecorePackage.getEFeatureMapEntry(), "group4", null, 0, -1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_Classpath(), this.getClasspath(), null, "classpath", null, 1, -1, ServerRuntime.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getServerRuntime_JndiConnection(), this.getJndiConnection(), null, "jndiConnection", null, 1, 1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getServerRuntime_Name(), theXMLTypePackage.getString(), "name", null, 1, 1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getServerRuntime_Version(), theXMLTypePackage.getString(), "version", null, 0, 1, ServerRuntime.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http:///org/eclipse/emf/ecore/util/ExtendedMetaData
		createExtendedMetaDataAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http:///org/eclipse/emf/ecore/util/ExtendedMetaData</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createExtendedMetaDataAnnotations() {
		String source = "http:///org/eclipse/emf/ecore/util/ExtendedMetaData";		
		addAnnotation
		  (archiveTypeEClass, 
		   source, 
		   new String[] {
			 "name", "archive_._type",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getArchiveType_Path(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "path"
		   });		
		addAnnotation
		  (classpathEClass, 
		   source, 
		   new String[] {
			 "name", "Classpath",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getClasspath_Group(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:0"
		   });		
		addAnnotation
		  (getClasspath_Archive(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "archive",
			 "group", "#group:0"
		   });		
		addAnnotation
		  (getClasspath_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (jndiConnectionEClass, 
		   source, 
		   new String[] {
			 "name", "jndiConnection",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getJndiConnection_ProviderUrl(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "providerUrl"
		   });		
		addAnnotation
		  (getJndiConnection_Group(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:1"
		   });		
		addAnnotation
		  (getJndiConnection_JndiProperty(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "jndiProperty",
			 "group", "#group:1"
		   });		
		addAnnotation
		  (getJndiConnection_InitialContextFactory(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "initialContextFactory"
		   });		
		addAnnotation
		  (jndiPropertyEClass, 
		   source, 
		   new String[] {
			 "name", "jndiProperty",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getJndiProperty_Name(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "name"
		   });		
		addAnnotation
		  (getJndiProperty_Value(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "value"
		   });		
		addAnnotation
		  (launchConfigurationEClass, 
		   source, 
		   new String[] {
			 "name", "LaunchConfiguration",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getLaunchConfiguration_WorkingDirectory(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "workingDirectory"
		   });		
		addAnnotation
		  (getLaunchConfiguration_ProgramArguments(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "programArguments"
		   });		
		addAnnotation
		  (getLaunchConfiguration_MainClass(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "mainClass"
		   });		
		addAnnotation
		  (getLaunchConfiguration_VmParameters(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "vmParameters"
		   });		
		addAnnotation
		  (getLaunchConfiguration_ClasspathReference(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "classpathReference"
		   });		
		addAnnotation
		  (getLaunchConfiguration_External(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "external"
		   });		
		addAnnotation
		  (getLaunchConfiguration_DebugPort(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "debugPort"
		   });		
		addAnnotation
		  (moduleEClass, 
		   source, 
		   new String[] {
			 "name", "Module",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getModule_Type(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "type"
		   });		
		addAnnotation
		  (getModule_PublishDir(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "publishDir"
		   });		
		addAnnotation
		  (getModule_PublisherReference(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "publisherReference"
		   });		
		addAnnotation
		  (portEClass, 
		   source, 
		   new String[] {
			 "name", "Port",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getPort_No(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "no"
		   });		
		addAnnotation
		  (getPort_Name(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "name"
		   });		
		addAnnotation
		  (getPort_Protocol(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "protocol"
		   });		
		addAnnotation
		  (projectEClass, 
		   source, 
		   new String[] {
			 "name", "Project",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getProject_ClasspathReference(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "classpathReference"
		   });		
		addAnnotation
		  (propertyEClass, 
		   source, 
		   new String[] {
			 "name", "Property",
			 "kind", "empty"
		   });		
		addAnnotation
		  (getProperty_Context(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "context"
		   });		
		addAnnotation
		  (getProperty_Default(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "default"
		   });		
		addAnnotation
		  (getProperty_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (getProperty_Label(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "label"
		   });		
		addAnnotation
		  (getProperty_Type(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "type"
		   });		
		addAnnotation
		  (publisherEClass, 
		   source, 
		   new String[] {
			 "name", "Publisher",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getPublisher_Group(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:0"
		   });		
		addAnnotation
		  (getPublisher_Publisherdata(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "publisherdata",
			 "group", "#group:0"
		   });		
		addAnnotation
		  (getPublisher_Id(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "id"
		   });		
		addAnnotation
		  (publisherDataEClass, 
		   source, 
		   new String[] {
			 "name", "PublisherData",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getPublisherData_Dataname(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "dataname"
		   });		
		addAnnotation
		  (getPublisherData_Datavalue(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "datavalue"
		   });			
		addAnnotation
		  (serverRuntimeEClass, 
		   source, 
		   new String[] {
			 "name", "ServerRuntime",
			 "kind", "elementOnly"
		   });		
		addAnnotation
		  (getServerRuntime_Group(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:0"
		   });		
		addAnnotation
		  (getServerRuntime_Property(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "property",
			 "group", "#group:0"
		   });		
		addAnnotation
		  (getServerRuntime_Group1(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:2"
		   });		
		addAnnotation
		  (getServerRuntime_Port(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "port",
			 "group", "#group:2"
		   });		
		addAnnotation
		  (getServerRuntime_Group2(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:4"
		   });		
		addAnnotation
		  (getServerRuntime_Module(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "module",
			 "group", "#group:4"
		   });		
		addAnnotation
		  (getServerRuntime_Project(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "project"
		   });		
		addAnnotation
		  (getServerRuntime_Start(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "start"
		   });		
		addAnnotation
		  (getServerRuntime_Stop(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "stop"
		   });		
		addAnnotation
		  (getServerRuntime_Group3(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:9"
		   });		
		addAnnotation
		  (getServerRuntime_Publisher(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "publisher",
			 "group", "#group:9"
		   });		
		addAnnotation
		  (getServerRuntime_Group4(), 
		   source, 
		   new String[] {
			 "kind", "group",
			 "name", "group:11"
		   });		
		addAnnotation
		  (getServerRuntime_Classpath(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "classpath",
			 "group", "#group:11"
		   });		
		addAnnotation
		  (getServerRuntime_JndiConnection(), 
		   source, 
		   new String[] {
			 "kind", "element",
			 "name", "jndiConnection"
		   });		
		addAnnotation
		  (getServerRuntime_Name(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "name"
		   });		
		addAnnotation
		  (getServerRuntime_Version(), 
		   source, 
		   new String[] {
			 "kind", "attribute",
			 "name", "version"
		   });
	}

} //ServerTypePackageImpl
