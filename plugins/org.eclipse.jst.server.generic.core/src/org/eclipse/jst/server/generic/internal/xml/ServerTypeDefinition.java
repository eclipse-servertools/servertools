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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;



/**
 * @author Naci Dai
 */
public class ServerTypeDefinition  {
	
	ServerAdminTool adminTool;
	private List fDescriptors;
	
	private File definitionFile;

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return Returns the vendor.
	 */
	public String getVendor() {
		return vendor;
	}
	/**
	 * @param vendor The vendor to set.
	 */
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	/**
	 * @return Returns the version.
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version The version to set.
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	private String name;
	private String id;
	private String vendor;
	private String description;
	private String version;
	
	private boolean webModules;
	private boolean ejbModules;
	private boolean earModules;

	private ArrayList properties;
	private ArrayList serverClassPath;
	private ArrayList clientClassPath;
	private ArrayList projectClassPath;

	private String webModulesDeployDirectory;
	private String ejbModulesDeployDirectory;
	private String earModulesDeployDirectory;

	private String jndiInitialContextFactory;
	private String jndiProviderUrl;

	private String startClass;
	private String startWorkingDirectory;
	private String startVmParameters;
	private String startProgramArguments;

	private String stopClass;
	private String stopWorkingDirectory;
	private String stopVmParameters;
	private String stopProgramArguments;

	private String serverHome;
	/**
	 * 
	 */
	public ServerTypeDefinition() {
		properties = new ArrayList();
		serverClassPath = new ArrayList();
		clientClassPath = new ArrayList();
		projectClassPath = new ArrayList();
		adminTool = new ServerAdminTool(this);
	}

	public void addServerClasspath(ClasspathItem classpathItem) {
		this.getServerClassPath().add(classpathItem);
	}

	public void addClientClasspath(ClasspathItem classpathItem) {
		this.getClientClassPath().add(classpathItem);
	}
	public void addProjectClasspath(ClasspathItem classpathItem) {
		this.getClientClassPath().add(classpathItem);
	}

	/**
	 * @return ArrayList
	 */
	public ArrayList getClientClassPath() {
		return clientClassPath;
	}
	
	/**
	 * @return ArrayList
	 */
	public ArrayList getProjectClassPath() {
		return projectClassPath;
	}

	/**
	 * @return String
	 */
	public String getEarModulesDeployDirectory() {
		return earModulesDeployDirectory;
	}
	
	/**
	 * @return String
	 */
	public String getEjbModulesDeployDirectory() {
		return ejbModulesDeployDirectory;
	}

	/**
	 * @return ArrayList
	 */
	public ArrayList getProperties() {
		return properties;
	}

	public String getClasspathVariableName() {

		ServerTypeDefinitionProperty prop =
			getPropertyNamed("classPathVariableName");
		if (prop != null)
			return prop.getDefaultValue();
		return null;
	}
	public String getClasspathVariable() {

		ServerTypeDefinitionProperty prop = getPropertyNamed("classPath");
		if (prop != null)
			return prop.getDefaultValue();
		return null;
	}

	public ServerTypeDefinitionProperty getPropertyNamed(String id) {
		Iterator itr = getProperties().iterator();
		while (itr.hasNext()) {
			ServerTypeDefinitionProperty element =
				(ServerTypeDefinitionProperty) itr.next();
			if (id.equals(element.getId()))
				return element;
		}
		return null;
	}
	/**
	 * @return ArrayList
	 */
	public ArrayList getServerClassPath() {
		return serverClassPath;
	}

	/**
	 * @return String
	 */
	public String getStartClass() {
		return startClass;
	}

	/**
	 * @return String
	 */
	public String getStartProgramArguments() {
		return startProgramArguments;
	}

	/**
	 * @return String
	 */
	public String getStartVmParameters() {
		return startVmParameters;
	}

	/**
	 * @return String
	 */
	public String getStopClass() {
		return stopClass;
	}

	/**
	 * @return String
	 */
	public String getStopProgramArguments() {
		return stopProgramArguments;
	}

	/**
	 * @return String
	 */
	public String getStopVmParameters() {
		return stopVmParameters;
	}

	/**
	 * @return String
	 */
	public String getWebModulesDeployDirectory() {
		return this.resolveProperties(webModulesDeployDirectory);
	}

	/**
	 * Sets the clientClassPath.
	 * @param clientClassPath The clientClassPath to set
	 */
	public void setClientClassPath(ArrayList clientClassPath) {
		this.clientClassPath = clientClassPath;
	}

	/**
	 * Sets the earModulesDeployDirectory.
	 * @param earModulesDeployDirectory The earModulesDeployDirectory to set
	 */
	public void setEarModulesDeployDirectory(String earModulesDeployDirectory) {
		this.earModulesDeployDirectory = earModulesDeployDirectory;
	}

	/**
	 * Sets the ejbModulesDeployDirectory.
	 * @param ejbModulesDeployDirectory The ejbModulesDeployDirectory to set
	 */
	public void setEjbModulesDeployDirectory(String ejbModulesDeployDirectory) {
		this.ejbModulesDeployDirectory = ejbModulesDeployDirectory;
	}

	/**
	 * Sets the properties.
	 * @param properties The properties to set
	 */
	public void setProperties(ArrayList properties) {
		this.properties = properties;
	}

	/**
	 * Sets the serverClassPath.
	 * @param serverClassPath The serverClassPath to set
	 */
	public void setServerClassPath(ArrayList serverClassPath) {
		this.serverClassPath = serverClassPath;
	}


	/**
	 * Sets the ProjectClassPath.
	 * @param serverClassPath The ProjectClassPath to set
	 */
	public void setProjectClassPath(ArrayList projectClassPath) {
		this.projectClassPath = projectClassPath;
	}

	/**
	 * Sets the startClass.
	 * @param startClass The startClass to set
	 */
	public void setStartClass(String startClass) {
		this.startClass = startClass;
	}

	/**
	 * Sets the startProgramArguments.
	 * @param startProgramArguments The startProgramArguments to set
	 */
	public void setStartProgramArguments(String startProgramArguments) {
		this.startProgramArguments = startProgramArguments;
	}

	/**
	 * Sets the startVmParameters.
	 * @param startVmParameters The startVmParameters to set
	 */
	public void setStartVmParameters(String startVmParameters) {
		this.startVmParameters = startVmParameters;
	}

	/**
	 * Sets the stopClass.
	 * @param stopClass The stopClass to set
	 */
	public void setStopClass(String stopClass) {
		this.stopClass = stopClass;
	}

	/**
	 * Sets the stopProgramArguments.
	 * @param stopProgramArguments The stopProgramArguments to set
	 */
	public void setStopProgramArguments(String stopProgramArguments) {
		this.stopProgramArguments = stopProgramArguments;
	}

	/**
	 * Sets the stopVmParameters.
	 * @param stopVmParameters The stopVmParameters to set
	 */
	public void setStopVmParameters(String stopVmParameters) {
		this.stopVmParameters = stopVmParameters;
	}

	/**
	 * Sets the webModulesDeployDirectory.
	 * @param webModulesDeployDirectory The webModulesDeployDirectory to set
	 */
	public void setWebModulesDeployDirectory(String webModulesDeployDirectory) {
		this.webModulesDeployDirectory = webModulesDeployDirectory;
	}

	/**
	 * @return boolean
	 */
	public boolean isEarModules() {
		return earModules;
	}

	/**
	 * @return boolean
	 */
	public boolean isEjbModules() {
		return ejbModules;
	}

	/**
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return boolean
	 */
	public boolean isWebModules() {
		return webModules;
	}

	/**
	 * Sets the earModules.
	 * @param earModules The earModules to set
	 */
	public void setEarModules(boolean earModules) {
		this.earModules = earModules;
	}

	/**
	 * Sets the ejbModules.
	 * @param ejbModules The ejbModules to set
	 */
	public void setEjbModules(boolean ejbModules) {
		this.ejbModules = ejbModules;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the webModules.
	 * @param webModules The webModules to set
	 */
	public void setWebModules(boolean webModules) {
		this.webModules = webModules;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String xml =
			"\n<serverDefinition "
				+ "\n\t name=\""
				+ getName()
				+ "\""
				+ "\n\t ejbModules=\""
				+ isEjbModules()
				+ "\""
				+ "\n\t webModules=\""
				+ isWebModules()
				+ "\""
				+ "\n\t earModules=\""
				+ isEarModules()
				+ "\">";
		Iterator props = getProperties().iterator();
		while (props.hasNext()) {
			xml += props.next().toString();
		}

		xml
			+= ("\n<serverHome>"
				+ getServerHome()
				+ "</serverHome>"
				+ "\n<webModulesDeployDirectory>"
				+ getWebModulesDeployDirectory()
				+ "</webModulesDeployDirectory>"
				+ "\n<ejbModulesDeployDirectory>"
				+ getEjbModulesDeployDirectory()
				+ "</ejbModulesDeployDirectory>"
				+ "\n<earModulesDeployDirectory>"
				+ getEarModulesDeployDirectory()
				+ "</earModulesDeployDirectory>"
				+ "\n<jndiInitialContextFactory>"
				+ getJndiInitialContextFactory()
				+ "</jndiInitialContextFactory>"
				+ "\n<jndiProviderUrl>"
				+ getJndiProviderUrl()
				+ "</jndiProviderUrl>"
				+ "\n<startClass>"
				+ getStartClass()
				+ "</startClass>"
				+ "\n<startWorkingDirectory>"
				+ getStartWorkingDirectory()
				+ "</startWorkingDirectory>"
				+ "\n<startVmParameters>"
				+ getStartVmParameters()
				+ "</startVmParameters>"
				+ "\n<startProgramArguments>"
				+ getStartProgramArguments()
				+ "</startProgramArguments>"
				+ "\n<stopClass>"
				+ getStopClass()
				+ "</stopClass>"
				+ "\n<stopWorkingDirectory>"
				+ getStopWorkingDirectory()
				+ "</stopWorkingDirectory>"
				+ "\n<stopVmParameters>"
				+ getStopVmParameters()
				+ "</stopVmParameters>"
				+ "\n<stopProgramArguments>"
				+ getStopProgramArguments()
				+ "</stopProgramArguments>");

		xml += "\n<serverClassPath>";
		props = getServerClassPath().iterator();
		while (props.hasNext()) {
			xml += "\n\t" + props.next().toString();
		}
		xml += "\n</serverClassPath>";

		xml += "\n<clientClassPath>";
		props = getClientClassPath().iterator();
		while (props.hasNext()) {
			xml += "\n\t" + props.next().toString();
		}
		xml += "\n</clientClassPath>";

		xml += "\n<projectClassPath>";
		props = getProjectClassPath().iterator();
		while (props.hasNext()) {
			xml += "\n\t" + props.next().toString();
		}
		xml += "\n</projectClassPath>";

		if( getAdminTool().isDefined() )
			xml += getAdminTool().toString();				
		
		xml += "\n</serverDefinition>";
		return xml;
	}

	/**
	 * @return String
	 */
	public String getStartWorkingDirectory() {
		return startWorkingDirectory;
	}

	/**
	 * @return String
	 */
	public String getStopWorkingDirectory() {
		return stopWorkingDirectory;
	}

	/**
	 * Sets the startWorkingDirectory.
	 * @param startWorkingDirectory The startWorkingDirectory to set
	 */
	public void setStartWorkingDirectory(String startWorkingDirectory) {
		this.startWorkingDirectory = startWorkingDirectory;
	}

	/**
	 * Sets the stopWorkingDirectory.
	 * @param stopWorkingDirectory The stopWorkingDirectory to set
	 */
	public void setStopWorkingDirectory(String stopWorkingDirectory) {
		this.stopWorkingDirectory = stopWorkingDirectory;
	}

	/**
	 * @return String
	 */
	public String getJndiInitialContextFactory() {
		return jndiInitialContextFactory;
	}

	/**
	 * @return String
	 */
	public String getJndiProviderUrl() {
		return jndiProviderUrl;
	}

	/**
	 * Sets the jndiInitialContextFactory.
	 * @param jndiInitialContextFactory The jndiInitialContextFactory to set
	 */
	public void setJndiInitialContextFactory(String jndiInitialContextFactory) {
		this.jndiInitialContextFactory = jndiInitialContextFactory;
	}

	/**
	 * Sets the jndiProviderUrl.
	 * @param jndiProviderUrl The jndiProviderUrl to set
	 */
	public void setJndiProviderUrl(String jndiProviderUrl) {
		this.jndiProviderUrl = jndiProviderUrl;
	}

	/**
	 * @return File
	 */
	public File getDefinitionFile() {
		return definitionFile;
	}

	/**
	 * Sets the definitionFile.
	 * @param definitionFile The definitionFile to set
	 */
	public void setDefinitionFile(File definitionFile) {
		this.definitionFile = definitionFile;
	}


	public String resolveProperties(
		String proppedString) {
		HashMap cache = new HashMap(getProperties().size());
		Iterator itr = getProperties().iterator();
		while (itr.hasNext()) {
			ServerTypeDefinitionProperty element =
				(ServerTypeDefinitionProperty) itr.next();
			cache.put(element.getId(), element.getDefaultValue());
		}
		//String vmPath = install.getInstallLocation().getCanonicalPath();
		//vmPath = vmPath.replace('\\', '/');
		cache.put("jrePath", "JRE");

		String str = resolvePropertiesFromCache(proppedString, cache);
		str = fixPassthroughProperties(str);
		return str;
	}

	/**
	 * @param str
	 * @return
	 */
	private String fixPassthroughProperties(String str) {
		String resolvedString = str;
		if (isPassPropertyLeft(resolvedString)) {
			resolvedString = fixParam(resolvedString);
			resolvedString = fixPassthroughProperties(resolvedString);
		}
		return resolvedString;
	}

	private String resolvePropertiesFromCache(
		String proppedString,
		HashMap cache) {
		String resolvedString = proppedString;
		if (isPropertyLeft(resolvedString)) {
			resolvedString = resolveProperty(resolvedString, cache);
			resolvedString = resolvePropertiesFromCache(resolvedString, cache);
		}
		return resolvedString;
	}

	private boolean isPropertyLeft(String str) {
		return str.indexOf("${") >= 0;
	}
	private boolean isPassPropertyLeft(String str) {
		return str.indexOf("%{") >= 0;
	}

	private String resolveProperty(String proppedString, HashMap cache) {
		String str = proppedString;
		int start = str.indexOf("${");
		int end = str.indexOf("}", start);
		String key = str.substring(start + 2, end);

		return str.substring(0, start)
			+ cache.get(key)
			+ str.substring(end + 1);
	}
	
	private String fixParam(String proppedString) {
		String str = proppedString;
		int start = str.indexOf("%{");
		return str.substring(0, start)
			+ "${"
			+ str.substring(start+2);
	}
	
	/**
	 * @return String
	 */
	public String getServerHome() {
		return serverHome;
	}

	public void setServerHome(String serverHome) {
		this.serverHome = serverHome;
	}

	/**
	 * @return
	 */
	public ServerAdminTool getAdminTool() {
		return adminTool;
	}

	/**
	 * @param tool
	 */
	public void setAdminTool(ServerAdminTool tool) {
		adminTool = tool;
	}



	public Object getPropertyValue(Object id)
	{
		ServerTypeDefinitionProperty prop = getPropertyNamed((String)id);
		if(prop != null)
			return prop.getDefaultValue();
		else
			return null;
	}

	public boolean isPropertySet(Object id)
	{
		ServerTypeDefinitionProperty prop = getPropertyNamed((String)id);
		return (prop != null);
	}

	public void setPropertyValue(Object id, Object value)
	{
		ServerTypeDefinitionProperty prop = getPropertyNamed((String)id);
		int idx = getProperties().indexOf(prop);
		if(idx >= 0)
		{
			prop.setDefaultValue((String)value);
			getProperties().set(idx, prop);
		}
	}
	/**
	 * @return
	 */
	public String getPort() {
		ServerTypeDefinitionProperty prop =
			getPropertyNamed("port");
		if (prop != null)
			return prop.getDefaultValue();
		return null;
	}


}
