/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Naci M. Dai - initial API and implementation
 *     
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
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
 ***************************************************************************/

package org.eclipse.jst.server.generic.internal.xml;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
/**
 * @author Naci Dai 
 */
public class XMLUtils {
	
	ArrayList definitions;
	File sourceDir;
	
	public XMLUtils(URL installUrl)
	{
		String serversPath = installUrl.getPath()+"/servers";
		URI uri;
		try {
			uri = new URI(installUrl.getProtocol(),installUrl.getHost(),serversPath,installUrl.getQuery());
			sourceDir = new File(uri);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		refresh();
	}
	
	
	public void refresh()
	{
		definitions = scanFiles(sourceDir);
	}
	public void update(ServerTypeDefinition element)
	{
		toFile(element);
	}	
	
	public void update()
	{
		Iterator defs = definitions.iterator();
		while (defs.hasNext()) {
			ServerTypeDefinition element = (ServerTypeDefinition) defs.next();
			update(element);
		}
	}
	
	private void toFile(ServerTypeDefinition def)
	{
		try {
			def.getDefinitionFile().renameTo(new File(def.getDefinitionFile().getCanonicalFile()+".bak"));
		} catch (IOException e) {
		}
		
		try {
			FileOutputStream out = new FileOutputStream(def.getDefinitionFile());
			out.write(def.toString().getBytes());
			out.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	private  ArrayList scanFiles(File dir)
	{
		ArrayList all = new ArrayList();
		try {
			if(dir.isDirectory()){
				File[] allServers = dir.listFiles(new FilenameFilter(){
					/* (non-Javadoc)
					 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
					 */
					public boolean accept(File dir, String name) {
						if(name.endsWith(".server"))
							return true;
						return false;
					}
			
				});
				
				for (int i = 0; i < allServers.length; i++) {
					File file = allServers[i];
					ServerTypeDefinition def = (ServerTypeDefinition)XMLReader.getServerDefinition(file.getCanonicalPath());
					if(def != null){
						def.setDefinitionFile(file);
						all.add(def);
					}
				
				}
			}
		} catch (IOException e) {
		}
		
		return all;
	}

	/**
	 * @return ArrayList
	 */
	public ArrayList getServerTypeDefinitions() {
		return definitions;
	}

	/**
	 * @return ArrayList
	 */
	public ServerTypeDefinition getServerTypeDefinitionNamed(String name) {
		refresh();
		Iterator defs = getServerTypeDefinitions().iterator();
		while (defs.hasNext()) {
			ServerTypeDefinition elem = (ServerTypeDefinition) defs.next();
			if(name.equals(elem.getName()))
				return elem;			
		}		
		return null;
	}

	/**
	 * Sets the definitions.
	 * @param definitions The definitions to set
	 */
	public void setDefinitions(ArrayList definitions) {
		this.definitions = definitions;
	}

}
