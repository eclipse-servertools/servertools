package org.eclipse.jst.server.generic.internal.core.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.eclipse.core.runtime.Platform;

/**
 * Utilities for file operations.
 * 
 * @author Gorkem Ercan
 */
public class FileUtil {

	
	
	public static URL resolveURL(URL url){
		try{
			return Platform.resolve(url);
		}catch(IOException e){
			//ignore
		}
		return null;
	}
	
	
	
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
