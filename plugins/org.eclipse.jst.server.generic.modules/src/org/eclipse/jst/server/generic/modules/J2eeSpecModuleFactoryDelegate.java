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

package org.eclipse.jst.server.generic.modules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.IModuleFactoryListener;
import org.eclipse.wst.server.core.model.ModuleDelegate;
import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;


public class J2eeSpecModuleFactoryDelegate extends ModuleFactoryDelegate {
	protected static final IModule[] NO_MODULES = new IModule[0];
	
	protected static J2eeSpecModuleFactoryDelegate instance;
	protected IWorkspaceRoot root;
	
	public J2eeSpecModuleFactoryDelegate() {
		root = ResourcesPlugin.getWorkspace().getRoot();
		instance = this;
	}
	
	public static J2eeSpecModuleFactoryDelegate getInstance() {
		if (instance == null)
			new J2eeSpecModuleFactoryDelegate();
		return instance;
	}
	
	public IModule getModule(String memento) {
		if (memento == null)
			return null;
		try {
			IPath modulePath = new Path(memento);
			if(!modulePath.isAbsolute())
				return null;
			return getModule(root.getContainerForLocation(modulePath));
		} catch (Exception e) {
			Trace.trace("Could not create module: " + e.getMessage());
		}
		return null;
	}


	private IModule getModule(IContainer containerForLocation) {
		IModule module=null;
		if (Utils.isValidModule((IFolder)containerForLocation)) {
			try {
				module = createModule((IFolder)containerForLocation);
			} catch (Exception e) {
				Trace.trace("Unable to get module for container,",e);
			}
		}		
		return module;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleFactoryDelegate#getModules()
	 */
	public IModule[] getModules() {
		IProject[] projects = root.getProjects();
		if (projects == null)
			return NO_MODULES;
				List list = new ArrayList();
		int size = projects.length;
		for (int i = 0; i < size; i++) {
				this.initModules(projects[i],list);
		}
		return (IModule[])list.toArray(new IModule[list.size()]);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleFactoryDelegate#addModuleFactoryListener(org.eclipse.wst.server.core.model.IModuleFactoryListener)
	 */
	public void addModuleFactoryListener(IModuleFactoryListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModuleFactoryDelegate#removeModuleFactoryListener(org.eclipse.wst.server.core.model.IModuleFactoryListener)
	 */
	public void removeModuleFactoryListener(IModuleFactoryListener listener) {
		// TODO Auto-generated method stub

	}
	
	private void initModules(IProject project, List list) {
		try {
			IResource[] res = project.members();
			initModulesIn(res,list);
		} catch (Exception e) {
			Trace.trace(e.getMessage(), e);
		}
	}
	private void initModulesIn(IResource[] res, List list) throws Exception {
		for (int j = 0; j < res.length; j++) {
			if (res[j].getType() != IResource.FOLDER)
				continue;
			IFolder moduleFolder = (IFolder) res[j];
			initModulesIn(moduleFolder,list);
		}
	}
	private void initModulesIn(IFolder moduleFolder, final List list) throws Exception {
		if (Utils.isValidModule(moduleFolder)) {
			IModule module = createModule(moduleFolder);
			if(module != null)
				list.add(module);
		} 
		
			moduleFolder.accept(new IResourceVisitor() {
				public boolean visit(IResource resource) throws CoreException {
					if (resource.getType() != IResource.FOLDER)
						return false;
					IResource res[] = ((IFolder )resource).members();
					
					for (int j = 0; j < res.length; j++) {
						if (res[j].getType() != IResource.FOLDER)
							continue;
						IFolder moduleFolder = (IFolder) res[j];
						try {
                            initModulesIn(moduleFolder,list);
                        } catch (Exception e) {
                          Trace.trace("Could not determine modules in folder", e);
                        }
					}
			
					return true;
				}
			});
		
	}
	private IModule createModule(IFolder moduleFolder) throws Exception {
		
		IModule module = null;
		if (Utils.isValidWebModule(moduleFolder)) {
			module = new WebModule(moduleFolder);
		} else if (Utils.isValidEjbModule(moduleFolder)) {
			module = new EjbModule(moduleFolder);
		} else if (Utils.isValidEarModule(moduleFolder)) {
			module = new EnterpriseApplication(moduleFolder);
		}
		return module;
	}

	/**
	 * @param resource
	 * @return
	 */
	public IModule getModule(IResource resource) {
		
		return null;
	}

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.model.ModuleFactoryDelegate#getModuleDelegate(org.eclipse.wst.server.core.IModule)
     */
    public ModuleDelegate getModuleDelegate(IModule module) {
        // TODO Auto-generated method stub
        return null;
    }




}
