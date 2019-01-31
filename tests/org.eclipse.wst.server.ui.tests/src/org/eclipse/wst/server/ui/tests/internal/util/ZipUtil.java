/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
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


package org.eclipse.wst.server.ui.tests.internal.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.wst.server.ui.tests.TestsPlugin;
import org.osgi.framework.Bundle;

public class ZipUtil {
	/**
	 * Extracts the contents of a zip file in this plugin into the metadata dir
	 * 
	 * @param pathToArchiveToCopy the relative path to the zip file in the plugin. For example: resources/ServerAdapterWithSiteXML.zip
	 */
	public static void copyArchiveToMetadataDir(String pathToArchiveToCopy){
		try {
			Bundle bundle = Platform.getBundle(TestsPlugin.PLUGIN_ID);
			if (bundle == null){
				return;
			}
			InputStream stream = FileLocator.openStream(bundle, new Path(pathToArchiveToCopy), false);
			
			TestsPlugin plugin = TestsPlugin.getDefault();
			IPath location = null;
			String dir = null;
			
			byte[] buffer = new byte[1024];
			
			if (plugin != null){
				location = plugin.getStateLocation();
				if (location != null){
					dir = location.toOSString();
					if (dir != null){
						File fDir = new File(dir);
						if(!fDir.exists())
							fDir.mkdirs();
												
						ZipInputStream zis = new ZipInputStream(stream);
						ZipEntry entry = zis.getNextEntry();
						 
				    	while(entry!=null){
				    		String fileName = entry.getName();
				    		File newFile = new File(dir + File.separator + fileName);

				            new File(newFile.getParent()).mkdirs();
				            
				            if(!entry.isDirectory()){
					            FileOutputStream fileOutputStream = new FileOutputStream(newFile);             
					 
					            int len;
					            while ((len = zis.read(buffer)) > 0) {
					            	fileOutputStream.write(buffer, 0, len);
					            }
					 
					            fileOutputStream.close();
				            }   
				            entry = zis.getNextEntry();
				    	}
				 
				        zis.closeEntry();
				    	zis.close();
						
					}
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}	
}
