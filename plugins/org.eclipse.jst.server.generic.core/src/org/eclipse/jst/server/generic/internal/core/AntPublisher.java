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
package org.eclipse.jst.server.generic.internal.core;

import java.util.List;

import org.eclipse.ant.core.AntRunner;
import org.eclipse.ant.core.TargetInfo;
import org.eclipse.ant.internal.ui.launchConfigurations.AntLaunchShortcut;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinition;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IPublisher;
import org.eclipse.wst.server.core.resources.IModuleFolder;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.core.resources.IRemoteResource;


public class AntPublisher implements IPublisher {

	/**
	 * @param parents
	 * @param module
	 * @param serverDefinition
	 */
	private List parents;
	private IModule module;
	private ServerTypeDefinition serverTypeDefinition;
	
	public AntPublisher(List parents, IModule module, ServerTypeDefinition serverDefinition) {
		this.parents = parents;
		this.module = module;
		this.serverTypeDefinition = serverDefinition;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublisher#getMappedLocation(org.eclipse.wst.server.core.resources.IModuleResource)
	 */
	public IPath getMappedLocation(IModuleResource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublisher#shouldMapMembers(org.eclipse.wst.server.core.resources.IModuleFolder)
	 */
	public boolean shouldMapMembers(IModuleFolder folder) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublisher#getRemoteResources(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IRemoteResource[] getRemoteResources(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublisher#delete(org.eclipse.wst.server.core.resources.IRemoteResource[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus[] delete(IRemoteResource[] resource, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublisher#publish(org.eclipse.wst.server.core.resources.IModuleResource[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus[] publish(IModuleResource[] resource,
			IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IPublisher#deleteAll(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus deleteAll(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
    class AntUtil
    {
    	public void runTask(IFile buildFile, String[] tasks, String arguments, String buildDir) {
     		if (buildFile == null)
    			return;
 
    		try {
    			String taskName = findExecutableTasks(tasks, buildFile);
    			if (taskName != null) {

    				AntLaunchShortcut antLaunchShortcut = new AntLaunchShortcut();
    				antLaunchShortcut.setShowDialog(false);
    				antLaunchShortcut.launch(buildFile, "run", taskName);
    			}
    		} catch (Exception e) {
    			Trace.trace("Failed to launch ant publisher task",e);
    		}

    	}
    	private String findExecutableTasks(String[] tasks, IFile buildFile)
    			throws CoreException {
    		AntRunner antRunner = new AntRunner();

    		antRunner.setBuildFileLocation(buildFile.getLocation().toOSString());
    		TargetInfo targetInfo[] = antRunner.getAvailableTargets();

    		String taskName = null;
    		for (int i = 0; i < tasks.length; i++) {
    			for (int j = 0; j < targetInfo.length; j++) {
    				TargetInfo info = targetInfo[j];
    				if (info.getName().equals(tasks[i])) {
    					if (taskName != null)
    						taskName += ",";
    					else
    						taskName = "";
    					taskName += tasks[i];
    					break;
    				}
    			}
    		}
    		return taskName;
    	}    	
    }

}
