/***************************************************************************************************
 * Copyright (c) 2005, 2013 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Gorkem Ercan - initial API and implementation
 *
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal.publishers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.eclipse.ant.ui.launching.IAntLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IEnterpriseApplication;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.jst.server.generic.core.internal.CorePlugin;
import org.eclipse.jst.server.generic.core.internal.GenericPublisher;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.core.internal.GenericServerBehaviour;
import org.eclipse.jst.server.generic.core.internal.GenericServerCoreMessages;
import org.eclipse.jst.server.generic.core.internal.Trace;
import org.eclipse.jst.server.generic.internal.core.util.FileUtil;
import org.eclipse.jst.server.generic.servertype.definition.Module;
import org.eclipse.jst.server.generic.servertype.definition.PublisherData;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.osgi.framework.Bundle;

/**
 * Ant based publisher. All the properties defined in the server definition file
 * are passed into the ANT build file as properties. In addition to the
 * properties defined in the server definition <I>module.dir</I>,
 * <I>module.name,</I> and <I>server.publish.dir</I> are computed and passed
 * to the definition file.
 * <ul>
 * <li>module.dir: includes the root of the module project file</li>
 * <li>module.name: the name of the module</li>
 * <li>server.publish.dir: the directory to put the deployment units</li>
 * <li>project.working.dir: the working dir of the project that deployed module
 * is in</li>
 * </ul>
 *
 * @author Gorkem Ercan
 */

public class AntPublisher extends GenericPublisher {

	/**
	 * Copy of IExternalToolConstants.ATTR_LOCATION
	 */
	private static final String ATTR_LOCATION = "org.eclipse.ui.externaltools.ATTR_LOCATION"; //$NON-NLS-1$
	/**
	 *  Copy of the IAntUIConstants.REMOTE_ANT_PROCESS_FACTORY_ID
	 */
	private static final String REMOTE_ANT_PROCESS_FACTORY_ID= "org.eclipse.ant.ui.remoteAntProcessFactory"; //$NON-NLS-1$


	private static final String JAR_PROTOCOL_PREFIX = "jar"; //$NON-NLS-1$

	/**
	 * publisher id for ANT publisher.
	 */
	public static final String PUBLISHER_ID = "org.eclipse.jst.server.generic.antpublisher"; //$NON-NLS-1$

	protected static final String PROP_SERVER_PUBLISH_DIR = "server.publish.dir";//$NON-NLS-1$

	protected static final String PROP_PROJECT_WORKING_DIR = "project.working.dir";//$NON-NLS-1$

	protected static final String PROP_MODULE_DIR = "module.dir";//$NON-NLS-1$

	protected static final String PROP_MODULE_NAME = "module.name";//$NON-NLS-1$

	protected static final String PROP_CONTEXT_ROOT = "contextRoot";//$NON-NLS-1$

	protected static final String PROP_PROJECT_NAME = "project.name";//$NON-NLS-1$

	protected static final String PROP_PROJECT_LOCATION = "project.location"; //$NON-NLS-1$

	protected static final String MODULE_PUBLISH_TARGET_PREFIX = "target.publish."; //$NON-NLS-1$

	protected static final String MODULE_UNPUBLISH_TARGET_PREFIX = "target.unpublish.";//$NON-NLS-1$

	protected static final String DATA_NAME_BUILD_FILE = "build.file";//$NON-NLS-1$

	protected static final String PROP_MODULE_ARCHIVE_NAME = "module.archive.name"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.wtp.server.core.model.IPublisher#publish(org.eclipse.wtp.server.core.resources.IModuleResource[],
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus[] publish(IModuleArtifact[] resource, IProgressMonitor monitor) {

		if (getModule().length > 1 || // only respond to root module calls.
				!publishNeeded() ||
				monitor.isCanceled()) return null;
		try {
			assembleModule(monitor);
			File file = getCustomBuildFile();
			if ( file == null){// no user selected build file use the adapter default.
				file = computeBuildFile();
			}
			runAnt(file.toString(), getPublishTargetsForModule(), getPublishProperties(), monitor);
		} catch (CoreException e) {
			IStatus s = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.errorPublishAntpublisher, e);
			CorePlugin.getDefault().getLog().log(s);
			return new IStatus[] { s };
		}
		return null;
	}
	/**
	 * Checks if the Ant publisher actually needs to publish.
	 * For ear modules it also checks if any of the children modules requires publishing.
	 * @return true if ant publisher needs to publish.
	 */
	private boolean publishNeeded() {
		if ( getKind() != IServer.PUBLISH_INCREMENTAL && getKind() != IServer.PUBLISH_AUTO )
			return true;
		if (getDeltaKind() != ServerBehaviourDelegate.NO_CHANGE )
			return true;
		if ( isModuleType(getModule()[0], "jst.ear") ){ //$NON-NLS-1$
			IEnterpriseApplication earModule = (IEnterpriseApplication)getModule()[0].loadAdapter(IEnterpriseApplication.class, new NullProgressMonitor());
			IModule[] childModules = earModule.getModules();
			for (int i = 0; i < childModules.length; i++) {
				IModule module = childModules[i];
			    IModule[] modules ={getModule()[0], module};
			    if (IServer.PUBLISH_STATE_NONE != this.getServer().getServer().getModulePublishState(modules))
			    	return true;
			}
		}
		return false;
	}

	protected void assembleModule(IProgressMonitor monitor) throws CoreException {
		long time = System.currentTimeMillis();
		AbstractModuleAssembler assembler = AbstractModuleAssembler.Factory.getModuleAssembler(getModule()[0], getServer());
		assembler.assemble(monitor);
		Trace.trace(Trace.PERFORMANCE, "AntPublisher.assembleModule(): <" + (System.currentTimeMillis()-time) + "ms> module: "+getModule()[0] ); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Returns the custom build file that user selected. Or returns null;
	 * @return
	 * @throws CoreException
	 */
	private File getCustomBuildFile() throws CoreException {
		String filename = (String)getServer().getServerInstanceProperties().get( GenericServer.PROP_CUSTOM_BUILD_SCRIPT );
		if( filename != null && filename.length()>0 ){
			filename = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution( filename );
			File file = new File(filename);
			if ( !file.exists() ){
				throw new CoreException(new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID,
						"Selected build file does not exist.")); //$NON-NLS-1$
			}
			return file;
		}
		return null;
	}
	/**
	 *
	 * @return file
	 * @throws CoreException
	 */
	private File computeBuildFile() throws CoreException {
		Bundle bundle = Platform.getBundle(getServerRuntime().getServerTypeDefinition().getConfigurationElementNamespace());
		URL bundleUrl = bundle.getEntry(getBuildFile());
		URL fileURL = FileUtil.resolveURL(bundleUrl);
		if (fileURL.getProtocol().equals(JAR_PROTOCOL_PREFIX)) {
			OutputStream os = null;
			InputStream is = null;
			try {
				String filename = fileURL.getPath();
				String jarname = fileURL.getFile().substring(0, filename.indexOf('!'));

				File jarFile = new File(new URL(jarname).getFile());
				JarFile jar = new JarFile(jarFile);
				File tmpFile = FileUtil.createTempFile(getBuildFile(), CorePlugin.getDefault().getStateLocation().toOSString());
				os = new FileOutputStream(tmpFile);
				String entryname = getBuildFile();
				if (entryname.startsWith("/"))//$NON-NLS-1$
					entryname = entryname.substring(1);
				JarEntry entry = jar.getJarEntry(entryname);
				is = jar.getInputStream(entry);
				FileUtil.copy(is, os);
				return tmpFile;
			} catch (IOException e) {
				IStatus s = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, "error creating temporary build file", e);//$NON-NLS-1$
				CorePlugin.getDefault().getLog().log(s);
				throw new CoreException(s);
			} finally {
				try {
					if (is != null)
						is.close();
					if (os != null)
						os.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return FileUtil.resolveFile(fileURL);

	}

	private String getPublishTargetsForModule() {
		return doGetTargets(MODULE_PUBLISH_TARGET_PREFIX + getModuleTypeId());
	}

	private String getUnpublishTargetsForModule() {
		return doGetTargets(MODULE_UNPUBLISH_TARGET_PREFIX + getModuleTypeId());
	}

	private String doGetTargets(String dataname) {
		StringBuffer buffer = new StringBuffer();
		Iterator iterator = getServerRuntime().getServerTypeDefinition().getPublisher(PUBLISHER_ID).getPublisherdata().iterator();
		while (iterator.hasNext()) {
			PublisherData data = (PublisherData) iterator.next();
			if (dataname.equals(data.getDataname())) {
				if (buffer.length() > 0)
					buffer.append(",");//$NON-NLS-1$
				buffer.append(data.getDatavalue());
			}
		}
		return buffer.toString();
	}

	private String getModuleTypeId() {
		return getModule()[0].getModuleType().getId();
	}

	private String getBuildFile() {
		Iterator iterator = getServerRuntime().getServerTypeDefinition().getPublisher(PUBLISHER_ID).getPublisherdata().iterator();
		while (iterator.hasNext()) {
			PublisherData data = (PublisherData) iterator.next();
			if (DATA_NAME_BUILD_FILE.equals(data.getDataname()))
				return getServerRuntime().getServerTypeDefinition().getResolver().resolveProperties(data.getDatavalue());
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map getPublishProperties() {
		Map props = new HashMap();
		// pass all properties to build file.
		Map serverProperties = getServer().getServerInstanceProperties();
		Map properties = getServerRuntime().getServerInstanceProperties();
		properties.putAll(serverProperties);
		Iterator propertyIterator = properties.keySet().iterator();
		while (propertyIterator.hasNext()) {
			String property = (String) propertyIterator.next();
			String value = (String) properties.get(property);
			if (value != null && value.trim().length() > 0)
				props.put(property, properties.get(property));
		}
		Module module = getServerRuntime().getServerTypeDefinition().getModule(getModuleTypeId());
		String modDir = module.getPublishDir();
		modDir = getServerRuntime().getServerTypeDefinition().getResolver().resolveProperties(modDir);
		IModule webModule = getModule()[0];

		String moduleName = guessModuleName(webModule);
		String contextRoot = guessContextRoot(webModule);
		props.put(PROP_PROJECT_WORKING_DIR, getProjectWorkingLocation().toString());
		props.put(PROP_MODULE_NAME, moduleName);
		props.put(PROP_CONTEXT_ROOT, contextRoot);

		if (isModuleType(webModule, "jst.ear")) {//$NON-NLS-1$
			props.put(PROP_MODULE_ARCHIVE_NAME, moduleName + ".ear"); //$NON-NLS-1$
		} else if (isModuleType(webModule, "jst.web")) { //$NON-NLS-1$
			props.put(PROP_MODULE_ARCHIVE_NAME, moduleName + ".war"); //$NON-NLS-1$
		} else if (isModuleType(webModule, "jst.ejb")) { //$NON-NLS-1$
			props.put(PROP_MODULE_ARCHIVE_NAME, moduleName + ".jar"); //$NON-NLS-1$
		}
		if (webModule.getProject() != null) {
			props.put(PROP_MODULE_DIR, getModuleWorkingDir().toString());
			props.put(PROP_PROJECT_NAME, webModule.getProject().getName());
			props.put(PROP_PROJECT_LOCATION, webModule.getProject().getLocation().toString());
		}
		props.put(PROP_SERVER_PUBLISH_DIR, modDir);
		return props;
	}

	private IPath getModuleWorkingDir() {
		return getProjectWorkingLocation().append(getModule()[0].getProject().getName());
	}

	private IPath getProjectWorkingLocation() {
		GenericServerBehaviour genericServer = (GenericServerBehaviour) getServer().getServer().loadAdapter(ServerBehaviourDelegate.class, new NullProgressMonitor());
		return genericServer.getTempDirectory();
	}

	private String guessModuleName(IModule module) {
		String deployName = ServerUtil.getModuleDisplayName(module);
		if ("jst.web".equals(getModuleTypeId())) { //$NON-NLS-1$
			IWebModule webModule = (IWebModule) getModule()[0].loadAdapter(IWebModule.class, null);
			if (webModule == null) {
				return deployName;
			}
			String contextRoot = webModule.getURI(module);
			deployName = contextRoot.substring(0, contextRoot.lastIndexOf('.'));
		}
		return deployName;
	}

	private String guessContextRoot(IModule module) {
		String moduleName = guessModuleName(module);
		String contextRoot = moduleName;
		if ("jst.web".equals(getModuleTypeId())) { //$NON-NLS-1$
			IWebModule webModule = (IWebModule) getModule()[0].loadAdapter(IWebModule.class, null);
			if (webModule != null) {
				contextRoot = webModule.getContextRoot();
				if (contextRoot == null || contextRoot.length() == 0) {
					contextRoot = moduleName;
				}
			}
		}
		return contextRoot;
	}

	private void runAnt(String buildFile, String targets, Map properties, IProgressMonitor monitor) throws CoreException {
		long time = System.currentTimeMillis();
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = launchManager
				.getLaunchConfigurationType(IAntLaunchConfigurationConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE);
		if (type == null) {
			IStatus s = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.antLauncherMissing, null);
			throw new CoreException(s);
		}
		ILaunchConfigurationWorkingCopy wc = type.newInstance(null, properties.get(PROP_MODULE_NAME) + " module publisher"); //$NON-NLS-1$
		wc.setContainer(null);
		wc.setAttribute(ATTR_LOCATION, buildFile);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER, "org.eclipse.ant.ui.AntClasspathProvider"); //$NON-NLS-1$
		wc.setAttribute(IAntLaunchConfigurationConstants.ATTR_ANT_TARGETS, targets);
		wc.setAttribute(IAntLaunchConfigurationConstants.ATTR_ANT_PROPERTIES, properties);
		wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND, false);
		wc.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE, true);
		wc.setAttribute(IDebugUIConstants.ATTR_PRIVATE, true);

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH_PROVIDER, "org.eclipse.ant.ui.AntClasspathProvider"); //$NON-NLS-1$

		IVMInstall vmInstall = getServerRuntime().getVMInstall();
		if(vmInstall == null )//fallback to default VM if null.
			vmInstall = JavaRuntime.getDefaultVMInstall();
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, JavaRuntime.newJREContainerPath(vmInstall).toPortableString());

		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				"org.eclipse.ant.internal.ui.antsupport.InternalAntRunner"); //$NON-NLS-1$
		wc.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, REMOTE_ANT_PROCESS_FACTORY_ID);

		setupAntLaunchConfiguration(wc);

        if ( !monitor.isCanceled() )
        {
            ILaunchConfiguration launchConfig = wc.doSave();
            launchConfig.launch(ILaunchManager.RUN_MODE, monitor, false, true);
            Trace.trace(Trace.PERFORMANCE, "AntPublisher.runAnt():<" + (System.currentTimeMillis()-time) + "ms> module: "+getModule()[0] ); //$NON-NLS-1$ //$NON-NLS-2$
        }


	}

	/**
	 * Hook method for subclasses.
	 *
	 * @param wc
	 */
	protected void setupAntLaunchConfiguration(ILaunchConfigurationWorkingCopy wc) {
		// nothing to do
	}

	private static boolean isModuleType(IModule module, String moduleTypeId) {
		if (module.getModuleType() != null && moduleTypeId.equals(module.getModuleType().getId()))
			return true;
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.jst.server.generic.internal.core.GenericPublisher#unpublish(org.eclipse.wst.server.core.IModule,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus[] unpublish(IProgressMonitor monitor) {

		if (getModule().length > 1)// only respond to root module calls.
			return null;
		try {
			File file = computeBuildFile();
			runAnt(file.toString(), getUnpublishTargetsForModule(), getPublishProperties(), monitor);
		} catch (CoreException e) {
			IStatus s = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.errorRemoveModuleAntpublisher, e);
			return new IStatus[] { s };
		}
		return null;
	}
}
