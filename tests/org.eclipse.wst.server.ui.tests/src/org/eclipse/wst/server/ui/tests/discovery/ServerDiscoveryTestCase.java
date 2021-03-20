/*******************************************************************************
 * Copyright (c) 2015, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.server.ui.tests.discovery;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.discovery.internal.model.ExtensionUpdateSite;
import org.eclipse.wst.server.discovery.internal.model.IServerExtension;
import org.eclipse.wst.server.ui.tests.TestsPlugin;
import org.eclipse.wst.server.ui.tests.internal.util.ZipUtil;

import junit.framework.TestCase;

public class ServerDiscoveryTestCase extends TestCase {
	
	/*
	 * Tests the server adapter discovery.
	 * 
	 * Note: the test repositories do not actually install properly. They are there purely for the detection. The actual
	 * install of real server adapters is not possible because it requires the restart of Eclipse after for verifications.
	 */
	
	public static IPath metadataPath = TestsPlugin.getDefault().getStateLocation();
	protected static final String resourcesPathName = "resources"; //$NON-NLS-1$
	protected static final String updateSiteServerAdapterWithSiteXML = "ServerAdapterWithSiteXML"; //$NON-NLS-1$
	protected static final String updateSiteServerAdapterWithServerAdapterProperty = "ServerAdapterWithServerAdapterProperty"; //$NON-NLS-1$
	protected static final String updateSiteServerAdapterWithP2GeneratedFromCategoryXMLFeature = "ServerAdapterWithP2GeneratedFromCategoryXMLFeature"; //$NON-NLS-1$
	protected static final String updateSiteInvalid = "ServerAdapterMissingAnyServerAdapterDiscovery"; //$NON-NLS-1$
	protected static final String zipExtension = ".zip"; //$NON-NLS-1$
	protected static final String serverAdapterSiteName = "serverAdapterSites.xml"; //$NON-NLS-1$
	
	// A helper method for retrieving the extensions
	protected List<IServerExtension> getExtensions(File filePath){
		try {
			String finalPath = filePath.toString();
			
			finalPath = filePath.toURI().toURL().toString();
			
			ExtensionUpdateSite extensionUpdateSite = new ExtensionUpdateSite(finalPath, null, null);

			List<IServerExtension> foundExtension = extensionUpdateSite.getExtensions(new NullProgressMonitor());
			return foundExtension;
		}
		catch (Exception e){
			// Print stack trace for diagnostics
			e.printStackTrace();
		}
		return null;
	}
	
	// this test no longer works so disable it
	/* public void testServerAdapterWithSiteXML() throws CoreException{
		ZipUtil.copyArchiveToMetadataDir(resourcesPathName + File.separator + updateSiteServerAdapterWithSiteXML + zipExtension);
		File file = new File(metadataPath + File.separator + updateSiteServerAdapterWithSiteXML);
		assertTrue("Update site does not exist",file.exists()); //$NON-NLS-1$
		
		List<IServerExtension> extensionList = getExtensions(file);
		assertNotNull("Extension list cannot be null",extensionList); //$NON-NLS-1$
		assertTrue("Failed to find the expected server adapter from " + file.toString(),!extensionList.isEmpty()); //$NON-NLS-1$
		IServerExtension e = extensionList.get(0);
		assertNotNull("Extension found should not be null", e); //$NON-NLS-1$
		assertTrue("Failed to find expected server adapter's name. Found : " + e.getName(), (updateSiteServerAdapterWithSiteXML.equals(e.getName()))); //$NON-NLS-1$
	} */
	
	public void testServerAdapterWithServerAdapterProperty(){
		ZipUtil.copyArchiveToMetadataDir(resourcesPathName + File.separator + updateSiteServerAdapterWithServerAdapterProperty + zipExtension);
		File file = new File(metadataPath + File.separator + updateSiteServerAdapterWithServerAdapterProperty);
		assertTrue("Update site does not exist",file.exists()); //$NON-NLS-1$
		
		List<IServerExtension> extensionList = getExtensions(file);		
		assertNotNull("Extension list cannot be null",extensionList); //$NON-NLS-1$		
		assertTrue("Failed to find the expected server adapter from " + file.toString(),!extensionList.isEmpty()); //$NON-NLS-1$
		IServerExtension e = extensionList.get(0);
		assertNotNull("Extension found should not be null", e); //$NON-NLS-1$
		assertTrue("Failed to find expected server adapter's name. Found : " + e.getName(), (updateSiteServerAdapterWithServerAdapterProperty.equals(e.getName()))); //$NON-NLS-1$
	}	
	
	public void testServerAdapterWithP2GeneratedFromCategoryXMLFeature(){
		ZipUtil.copyArchiveToMetadataDir(resourcesPathName + File.separator + updateSiteServerAdapterWithP2GeneratedFromCategoryXMLFeature + zipExtension);
		File file = new File(metadataPath + File.separator + updateSiteServerAdapterWithP2GeneratedFromCategoryXMLFeature);
		assertTrue("Update site does not exist",file.exists()); //$NON-NLS-1$
		
		List<IServerExtension> extensionList = getExtensions(file);		
		assertNotNull("Extension list cannot be null",extensionList); //$NON-NLS-1$		
		assertTrue("Failed to find the expected server adapter from " + file.toString(),!extensionList.isEmpty()); //$NON-NLS-1$
		IServerExtension e = extensionList.get(0);
		assertNotNull("Extension found should not be null", e); //$NON-NLS-1$
		assertTrue("Failed to find expected server adapter's name. Found : " + e.getName() , (updateSiteServerAdapterWithP2GeneratedFromCategoryXMLFeature.equals(e.getName()))); //$NON-NLS-1$
	}
	
	public void testServerAdapterMissingAnyServerAdapterDiscovery(){
		// We expect no Extension to be returned
		ZipUtil.copyArchiveToMetadataDir(resourcesPathName + File.separator + updateSiteInvalid + zipExtension);
		File file = new File(metadataPath + File.separator + updateSiteInvalid);
		assertTrue("Update site does not exist",file.exists()); //$NON-NLS-1$
		
		List<IServerExtension> extensionList = getExtensions(file);		
		assertNotNull("Extension list cannot be null",extensionList); //$NON-NLS-1$		
		assertTrue("No extension should be found since update site is invalid",extensionList.isEmpty()); //$NON-NLS-1$		
	}
}
