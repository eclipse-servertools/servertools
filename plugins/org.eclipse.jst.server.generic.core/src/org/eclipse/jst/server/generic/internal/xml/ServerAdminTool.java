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
 ***************************************************************************/

package org.eclipse.jst.server.generic.internal.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.RuntimeClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Naci Dai
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ServerAdminTool {
	final public static int NONE = 0;
	final public static int WEB = 1;
	final public static int EJB = 2;
	final public static int EAR = 3;

	final public static String NOTDEFINED = "<NOT DEFINED>";
	final public static String DEFINED = "<DEFINED>";
	private String mainClass;

	public class Module {

		private int type = -1;
		private String deploy = "";
		private String undeploy = "";
		private Module webModules = null;
		private Module ejbModules = null;
		/**
		 * @return
		 */
		public String getDeploy() {
			return deploy;
		}

		/**
		 * @return
		 */
		public Module getEjbModules() {
			return ejbModules;
		}

		/**
		 * @return
		 */
		public int getType() {
			return type;
		}

		/**
		 * @return
		 */
		public String getUndeploy() {
			return undeploy;
		}

		/**
		 * @return
		 */
		public Module getWebModules() {
			return webModules;
		}

		/**
		 * @param string
		 */
		public void setDeploy(String string) {
			if (string == null)
				deploy = "";
			else
				deploy = string;
		}

		/**
		 * @param string
		 */
		public void setEjbModules(Module mod) {
			ejbModules = mod;
		}

		/**
		 * @param i
		 */
		public void setType(int i) {
			type = i;
		}

		/**
		 * @param string
		 */
		public void setUndeploy(String string) {
			if (string == null)
				undeploy = "";
			else
				undeploy = string;
		}

		/**
		 * @param string
		 */
		public void setWebModules(Module mod) {
			webModules = mod;
		}

	}

	private Module web;
	private Module ejb;
	private Module ear;
	private boolean defined = false;

	private ArrayList toolClassPath;
	private ServerTypeDefinition definition;

	public ServerAdminTool(ServerTypeDefinition def) {
		mainClass = NOTDEFINED;
		definition = def;
		toolClassPath = new ArrayList();
		web = new Module();
		web.setType(WEB);
		ejb = new Module();
		ejb.setType(EJB);
		ear = new Module();
		ear.setType(EJB);
		ear.setWebModules(new Module());
		ear.setEjbModules(new Module());

	}

	public void addServerClasspath(ClasspathItem classpathItem) {
		this.getToolClassPath().add(classpathItem);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String xml = "";
		xml
			+= ("\n<adminTool>"
				+ "\n\t<web>"
				+ (this.getWeb().getDeploy().length() > 0
					? "\n\t\t<deploy><![CDATA["
						+ this.getWeb().getDeploy()
						+ "]]></deploy>"
					: "<deploy />")
				+ (this.getWeb().getUndeploy().length() > 0
					? "\n\t\t<undeploy><![CDATA["
						+ this.getWeb().getUndeploy()
						+ "]]></undeploy>"
					: "<undeploy />")
				+ "\n\t</web>"
				+ "\n\t<ejb>"
				+ (this.getEjb().getDeploy().length() > 0
					? "\n\t\t<deploy><![CDATA["
						+ this.getEjb().getDeploy()
						+ "]]></deploy>"
					: "<deploy />")
				+ (this.getEjb().getUndeploy().length() > 0
					? "\n\t\t<undeploy><![CDATA["
						+ this.getEjb().getUndeploy()
						+ "]]></undeploy>"
					: "<undeploy />")
				+ "\n\t</ejb>"
				+ "\n\t<ear>"
				+ (this.getEar().getDeploy().length() > 0
					? "\n\t\t<deploy><![CDATA["
						+ this.getEar().getDeploy()
						+ "]]></deploy>"
					: "<deploy />")
				+ (this.getEar().getUndeploy().length() > 0
					? "\n\t\t<undeploy><![CDATA["
						+ this.getEar().getUndeploy()
						+ "]]></undeploy>"
					: "<undeploy />")
				+ "\n\t\t<webModule>"
				+ (this.getEar().getWebModules().getDeploy().length() > 0
					? "\n\t\t\t<deploy><![CDATA["
						+ this.getEar().getWebModules().getDeploy()
						+ "]]></deploy>"
					: "<deploy />")
				+ (this.getEar().getWebModules().getUndeploy().length() > 0
					? "\n\t\t\t<undeploy><![CDATA["
						+ this.getEar().getWebModules().getUndeploy()
						+ "]]></undeploy>"
					: "<undeploy />")
				+ "\n\t\t</webModule>"
				+ "\n\t\t<ejbModule>"
				+ (this.getEar().getEjbModules().getDeploy().length() > 0
					? "\n\t\t\t<deploy><![CDATA["
						+ this.getEar().getEjbModules().getDeploy()
						+ "]]></deploy>"
					: "<deploy />")
				+ (this.getEar().getEjbModules().getUndeploy().length() > 0
					? "\n\t\t\t<undeploy><![CDATA["
						+ this.getEar().getEjbModules().getUndeploy()
						+ "]]></undeploy>"
					: "<undeploy />")
				+ "\n\t\t</ejbModule>"
				+ "\n\t</ear>"
				+ "\n</adminTool>");

		xml += "\n<adminToolPath>";
		Iterator props = this.getToolClassPath().iterator();
		while (props.hasNext()) {
			xml += "\n\t" + props.next().toString();
		}
		xml += "\n</adminToolPath>";

		return xml;

	}

	/**
	 * @return
	 */
	public boolean isDefined() {
		return defined;
	}

	public String getRuntimeClasspath(IJavaProject proj) throws CoreException {
		Iterator iter = this.getToolClassPath().iterator();
		ArrayList newPath = new ArrayList();
		String cp = "";
		while (iter.hasNext()) {
			ClasspathItem element = (ClasspathItem) iter.next();
			String lib =
				this.definition.resolveProperties(element.getClasspath());
			switch (element.getType()) {
				case ClasspathItem.VARIABLE :
					IClasspathEntry entry =
						JavaCore.newVariableEntry(new Path(lib), null, null);
					RuntimeClasspathEntry rentry =
						new RuntimeClasspathEntry(entry);
					newPath.add(rentry);
					break;

				default :
					IClasspathEntry lentry =
						JavaCore.newLibraryEntry(new Path(lib), null, null);
					RuntimeClasspathEntry lrentry =
						new RuntimeClasspathEntry(lentry);
					newPath.add(lrentry);
					break;
			}
		}

		IRuntimeClasspathEntry[] resultList =
			(IRuntimeClasspathEntry[]) newPath.toArray(
				new IRuntimeClasspathEntry[newPath.size()]);
		// 1. remove bootpath entries
		// 2. resolve & translate to local file system paths
		List resolved = new ArrayList(resultList.length);
		for (int i = 0; i < resultList.length; i++) {
			IRuntimeClasspathEntry entry = resultList[i];
			if (entry.getClasspathProperty()
				== IRuntimeClasspathEntry.USER_CLASSES) {
				IRuntimeClasspathEntry[] entries =
					JavaRuntime.resolveRuntimeClasspathEntry(entry, proj);
				for (int j = 0; j < entries.length; j++) {
					String location = entries[j].getLocation();
					if (location != null) {
						resolved.add(location);
						cp += location + File.pathSeparator;
					}
				}
			}
		}
		//return (String[])resolved.toArray(new String[resolved.size()]);
		return cp;

	}

	/**
	 * @return
	 */
	public ServerTypeDefinition getDefinition() {
		return definition;
	}

	/**
	 * @return
	 */
	public Module getEar() {
		return ear;
	}

	/**
	 * @return
	 */
	public Module getEjb() {
		return ejb;
	}

	/**
	 * @return
	 */
	public String getMainClass() {
		return mainClass;
	}

	/**
	 * @return
	 */
	public ArrayList getToolClassPath() {
		return toolClassPath;
	}

	/**
	 * @return
	 */
	public Module getWeb() {
		return web;
	}

	/**
	 * @param definition
	 */
	public void setDefinition(ServerTypeDefinition definition) {
		this.definition = definition;
	}

	/**
	 * @param module
	 */
	public void setEar(Module module) {
		ear = module;
	}

	/**
	 * @param module
	 */
	public void setEjb(Module module) {
		ejb = module;
	}

	/**
	 * @param string
	 */
	public void setMainClass(String string) {
		mainClass = string;
	}

	/**
	 * @param list
	 */
	public void setToolClassPath(ArrayList list) {
		toolClassPath = list;
	}

	/**
	 * @param module
	 */
	public void setWeb(Module module) {
		web = module;
	}

	/**
	 * @param b
	 */
	public void setDefined(boolean b) {
		defined = b;
	}

}
