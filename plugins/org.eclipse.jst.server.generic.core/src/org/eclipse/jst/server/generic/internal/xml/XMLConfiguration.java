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

import java.util.ArrayList;

import org.xml.sax.Attributes;

public class XMLConfiguration implements IXMLTagProcessor {

	private boolean inEjbModule;
	private boolean inWebModule;
	private boolean inEar;
	private boolean inEjb;
	private boolean inWar;
	private boolean hasProjectPath = false;
	ServerTypeDefinition currentDefinition;
	ClasspathItem currentClasspathItem;
	ArrayList currentClasspath;
	boolean inAdminTool = false;
	/* (non-Javadoc)
	 * @see com.objectlearn.jdt.xml.BuildConfigInterface#assesTagEnd(java.lang.String, java.lang.String)
	 */
	public void assesTagEnd(String tagName, String content) {
		if ("webModulesDeployDirectory".equals(tagName)) {
			currentDefinition.setWebModulesDeployDirectory(content);
		} else if ("ejbModulesDeployDirectory".equals(tagName)) {
			currentDefinition.setEjbModulesDeployDirectory(content);
		} else if ("earModulesDeployDirectory".equals(tagName)) {
			currentDefinition.setEarModulesDeployDirectory(content);
		} else if ("jndiInitialContextFactory".equals(tagName)) {
			currentDefinition.setJndiInitialContextFactory(content);
		} else if ("jndiProviderUrl".equals(tagName)) {
			currentDefinition.setJndiProviderUrl(content);
		} else if ("startClass".equals(tagName)) {
			currentDefinition.setStartClass(content);
		} else if ("startWorkingDirectory".equals(tagName)) {
			currentDefinition.setStartWorkingDirectory(content);
		} else if ("startVmParameters".equals(tagName)) {
			currentDefinition.setStartVmParameters(content);
		} else if ("startProgramArguments".equals(tagName)) {
			currentDefinition.setStartProgramArguments(content);
		} else if ("stopClass".equals(tagName)) {
			currentDefinition.setStopClass(content);
		} else if ("stopWorkingDirectory".equals(tagName)) {
			currentDefinition.setStopWorkingDirectory(content);
		} else if ("stopVmParameters".equals(tagName)) {
			currentDefinition.setStopVmParameters(content);
		} else if ("stopProgramArguments".equals(tagName)) {
			currentDefinition.setStopProgramArguments(content);
		} else if ("jar".equals(tagName)) {
			currentClasspathItem.setClasspath(content);
		} else if ("serverClassPath".equals(tagName)) {
			currentDefinition.setServerClassPath(currentClasspath);
		} else if ("clientClassPath".equals(tagName)) {
			currentDefinition.setClientClassPath(currentClasspath);
		} else if ("projectClassPath".equals(tagName)) {
			currentDefinition.setProjectClassPath(currentClasspath);
			hasProjectPath = true;
		} else if ("adminToolPath".equals(tagName)) {
			currentDefinition.getAdminTool().setToolClassPath(currentClasspath);
		} else if ("serverHome".equals(tagName)) {
			currentDefinition.setServerHome(content);
		} else if ("adminTool".equals(tagName)) {
			inAdminTool = false;
			inEjbModule = false;
			inWebModule = false;
			inEar = false;
			inEjb = false;
			inWar = false;

		} else if ("serverDefinition".equals(tagName)) {
			//Finished parsing
			if (!hasProjectPath) {
				currentDefinition.setProjectClassPath(
					currentDefinition.getServerClassPath());
			}
		} else if (inAdminTool && "deploy".equals(tagName)) {
			if (inWar) {
				currentDefinition.getAdminTool().getWeb().setDeploy(content);
			} else if (inEjb) {
				currentDefinition.getAdminTool().getEjb().setDeploy(content);
			}else if (inWebModule) {
				currentDefinition.getAdminTool().getEar().getWebModules().setDeploy(content);
			} else if (inEjbModule) {
				currentDefinition.getAdminTool().getEar().getEjbModules().setDeploy(content);
			} else if (inEar && !inWebModule && !inEjbModule) {
				currentDefinition.getAdminTool().getEar().setDeploy(content);
			}
		} else if (inAdminTool && "undeploy".equals(tagName)) {
			if (inWar) {
				currentDefinition.getAdminTool().getWeb().setUndeploy(content);
			} else if (inEjb) {
				currentDefinition.getAdminTool().getEjb().setUndeploy(content);
			} else if (inEar && !inWebModule && !inEjbModule) {
				currentDefinition.getAdminTool().getEar().setUndeploy(content);
			} else if (inWebModule) {
				currentDefinition.getAdminTool().getEar().getWebModules().setUndeploy(content);
			} else if (inEjbModule) {
				currentDefinition.getAdminTool().getEar().getEjbModules().setUndeploy(content);
			}
		}else if (inAdminTool && "web".equals(tagName)) {
			inWar = false;
		}else if (inAdminTool && "ejb".equals(tagName)) {
			inEjb = false;
		}else if (inAdminTool && "ear".equals(tagName)) {
			inEar = false;
		}else if (inAdminTool && "webModule".equals(tagName)) {
			inWebModule = false;
		}else if (inAdminTool && "ejbModule".equals(tagName)) {
			inEjbModule = false;
		}
	}

	/* (non-Javadoc)
	 * @see com.objectlearn.jdt.xml.BuildConfigInterface#assesTagStart(java.lang.String)
	 */
	public void assesTagStart(String tagName, Attributes attributes) {
		if ("serverDefinition".equals(tagName)) {
			hasProjectPath = false;
			currentDefinition = new ServerTypeDefinition();
			currentDefinition.setName(attributes.getValue("name"));
			currentDefinition.setWebModules(false);
			if ("true".equals(attributes.getValue("webModules")))
				currentDefinition.setWebModules(true);
			currentDefinition.setEjbModules(false);
			if ("true".equals(attributes.getValue("webModules")))
				currentDefinition.setEjbModules(true);
			currentDefinition.setEarModules(false);
			if ("true".equals(attributes.getValue("earModules")))
				currentDefinition.setEarModules(true);

		} else if ("property".equals(tagName)) {
			ServerTypeDefinitionProperty property = new ServerTypeDefinitionProperty();
			property.setId(attributes.getValue("id"));
			property.setLabel(attributes.getValue("label"));
			property.setDefaultValue(attributes.getValue("default"));
			property.setTypeFromStr(attributes.getValue("type"));
			currentDefinition.getProperties().add(property);
		} else if ("serverClassPath".equals(tagName)) {
			currentClasspath = new ArrayList();
		} else if ("clientClassPath".equals(tagName)) {
			currentClasspath = new ArrayList();
		} else if ("projectClassPath".equals(tagName)) {
			currentClasspath = new ArrayList();
		} else if ("adminToolPath".equals(tagName)) {
			currentClasspath = new ArrayList();
		} else if ("jar".equals(tagName)) {
			currentClasspathItem = new ClasspathItem();
			currentClasspathItem.setTypeStr(attributes.getValue("type"));
			currentClasspath.add(currentClasspathItem);
		} else if ("adminTool".equals(tagName)) {
			inAdminTool = true;
			inEjbModule = false;
			inWebModule = false;
			inEar = false;
			inEjb = false;
			inWar = false;
			currentDefinition.getAdminTool().setDefined(true);
		} else if ("web".equals(tagName)) {
			if (inAdminTool)
				inWar = true;
		} else if ("ejb".equals(tagName)) {
			if (inAdminTool)
				inEjb = true;
		} else if ("ear".equals(tagName)) {
			if (inAdminTool)
				inEar = true;
		} else if ("webModule".equals(tagName)) {
			if (inAdminTool)
				inWebModule = true;
		} else if ("ejbModule".equals(tagName)) {
			if (inAdminTool)
				inEjbModule = true;
		}
	}

	/* (non-Javadoc)
	 * @see com.objectlearn.jdt.xml.BuildConfigInterface#getConfigObject()
	 */
	public Object getConfigObject() {
		return currentDefinition;
	}

}
