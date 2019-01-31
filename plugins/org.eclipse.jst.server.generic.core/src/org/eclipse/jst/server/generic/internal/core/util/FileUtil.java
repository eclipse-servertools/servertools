/***************************************************************************************************
 * Copyright (c) 2005, 2007 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.internal.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

/**
 * Utilities for file operations.
 * 
 * @author Gorkem Ercan
 */
public class FileUtil {

	
	
	/**
	 * Returns a resolved equivalent of url, use with the 
	 * platform relative urls
	 * 
	 * @param url
	 * @return URL
	 */
	public static URL resolveURL(URL url){
		try{
			return FileLocator.resolve(url);
		}catch(IOException e){
			//ignore
		}
		return null;
	}
	
	
	
	/**
	 * Create a temporary file
	 * @param name
	 * @param dir
	 * @return file
	 */
	public static File createTempFile(String name, String dir){
		if(name==null || dir == null)
			return null;
		
		File temp=null;
		String filePath;
		filePath = name.replace('/', File.separatorChar);
		if (filePath.startsWith(File.separator))
			filePath = filePath.substring(1);
		temp = new File(dir, filePath);
		verifyPath(temp,true);
		temp.deleteOnExit();
		return temp;
	
	}
	
	/**
	 * Copies a file
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void copy(InputStream input, OutputStream output) throws IOException{
		byte[] buf = new byte[4096];
		int len = input.read(buf);
		while(len!=-1){
			output.write(buf,0,len);
			len= input.read(buf);
		}
	}
	
	private static void verifyPath(File path, boolean isFile) {
		// if we are expecting a file back off 1 path element
		if (isFile) {
			if (path.getAbsolutePath().endsWith(File.separator)) {
				// make sure this is a file
				path = path.getParentFile();
				isFile = false;
			}
		}
		// already exists ... just return
		if (path.exists())
			return;
		// does not exist ... ensure parent exists
		File parent = path.getParentFile();
		verifyPath(parent, false);
		// ensure directories are made. Mark files or directories for deletion
		if (!isFile)
			path.mkdir();
		path.deleteOnExit();
	}

    /**
     * Resolves a URL to a file.
     * 
     * @param url
     * @return file
     */
    public static File resolveFile(URL url){
        try {
            URL resolvedUrl = resolveURL(url);
              URI uri = new URI(resolvedUrl.getProtocol(), resolvedUrl.getHost(),resolvedUrl.getPath(), resolvedUrl.getQuery());
            return new File(uri);
        } 
        catch (URISyntaxException e) {
        	//ignore
        }
        return null;
    }
}
