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
package org.eclipse.jst.server.generic.internal.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.jst.server.generic.internal.xml.ServerTypeDefinition;
import org.eclipse.jst.server.generic.modules.WebModule;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IPublisher;
import org.eclipse.wst.server.core.resources.IModuleFolder;
import org.eclipse.wst.server.core.resources.IModuleResource;
import org.eclipse.wst.server.core.resources.IRemoteResource;
/**
 * Ant based publisher.
 *
 * @author Gorkem Ercan
 */

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
	 * @see org.eclipse.wtp.server.core.model.IPublisher#getMappedLocation(org.eclipse.wtp.server.core.resources.IModuleResource)
	 */
	public IPath getMappedLocation(IModuleResource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wtp.server.core.model.IPublisher#shouldMapMembers(org.eclipse.wtp.server.core.resources.IModuleFolder)
	 */
	public boolean shouldMapMembers(IModuleFolder folder) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wtp.server.core.model.IPublisher#getRemoteResources(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IRemoteResource[] getRemoteResources(IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wtp.server.core.model.IPublisher#delete(org.eclipse.wtp.server.core.resources.IRemoteResource[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus[] delete(IRemoteResource[] resource, IProgressMonitor monitor)
			throws CoreException {
		
		return new IStatus[]{new Status(IStatus.OK,CorePlugin.PLUGIN_ID,0,"DeleteResource",null)};
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wtp.server.core.model.IPublisher#publish(org.eclipse.wtp.server.core.resources.IModuleResource[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus[] publish(IModuleResource[] resource,
			IProgressMonitor monitor) throws CoreException {
		String deployAnt = serverTypeDefinition.getAdminTool().getWeb().getDeploy();
		if(deployAnt == null || deployAnt.length()<1)
			return new IStatus[]{new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID,0,"AntBuildFileDoesNotExist",null)};
		
		
		IPath file = CorePlugin.getDefault().getStateLocation().append("tempAnt.xml");
		try {
			createNewFile(file.toFile(),deployAnt.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		runAnt(file.toString(),new String[]{"deploy"},getPublishProperties(resource),monitor);
		
		file.toFile().delete();
		
		// TODO Auto-generated method stub
		return null;
	}
	private Map getPublishProperties(IModuleResource[] resource)
	{
		Map props = new HashMap();
		props.put("deploymentUnitName",this.module.getName());
		props.put("moduleDir",((WebModule)this.module).getLocation().toString());
		props.put("deployDir",serverTypeDefinition.getWebModulesDeployDirectory());
		return props;
		
		
	}
	private void runAnt(String buildFile,String[] targets,Map properties ,IProgressMonitor monitor)throws CoreException
	{
		AntRunner runner = new AntRunner();
		runner.setBuildFileLocation(buildFile);
		runner.setExecutionTargets(targets);
		runner.addUserProperties(properties);
		runner.run(monitor);
	}
	
	
	

    public boolean createNewFile(File f,byte[] content) throws IOException {
        if (f != null) {
            if (f.exists()) {
                return false;
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(f);
                fos.write(content);
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
            return true;
        }
        return false;
    }
	/* (non-Javadoc)
	 * @see org.eclipse.wtp.server.core.model.IPublisher#deleteAll(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus deleteAll(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
	

}
