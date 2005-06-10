/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests.module;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.j2ee.datamodel.properties.IJ2EEComponentCreationDataModelProperties;
import org.eclipse.jst.j2ee.internal.web.archive.operations.WebComponentCreationDataModelProvider;
import org.eclipse.jst.server.tomcat.core.tests.OrderedTestSuite;
import org.eclipse.jst.server.tomcat.core.tests.TomcatRuntimeTestCase;
import org.eclipse.wst.common.componentcore.datamodel.properties.IComponentCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;

import junit.framework.Test;
import junit.framework.TestCase;

public class ModuleTestCase extends TestCase {
	protected static final String WEB_MODULE_NAME = "MyWeb";
	public static IModule webModule;

	public static Test suite() {
		return new OrderedTestSuite(TomcatRuntimeTestCase.class, "ModuleTestCase");
	}

	public void test01CreateWebModule() throws Exception {
		IDataModel dataModel = DataModelFactory.createDataModel(new WebComponentCreationDataModelProvider());
      dataModel.setProperty(IComponentCreationDataModelProperties.COMPONENT_NAME, WEB_MODULE_NAME);
      dataModel.setBooleanProperty(IJ2EEComponentCreationDataModelProperties.ADD_TO_EAR, false);
      dataModel.setIntProperty(IJ2EEComponentCreationDataModelProperties.COMPONENT_VERSION, 22); //J2EEVersionConstants.12);
		dataModel.getDefaultOperation().execute(new NullProgressMonitor(), null);
		dataModel.dispose();
	}

	public void test02CreateWebContent() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(WEB_MODULE_NAME);
		IFile file = project.getFile(new Path("WebContent").append("test.html"));
		String content = "Hello!";
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
		file.create(in, true, null);
	}

	public void test04GetModule() throws Exception {
		ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(WEB_MODULE_NAME);
		IModule[] modules = ServerUtil.getModules(project);
		if (modules == null || modules.length != 1)
			throw new Exception("Wrong number of modules in Web project");
		
		webModule = modules[0];
	}
}