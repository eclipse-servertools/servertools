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
import org.eclipse.jst.server.generic.servertype.definition.Module;
import org.eclipse.jst.server.generic.servertype.definition.PublishType;
import org.eclipse.jst.server.generic.servertype.definition.Publisher;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
/**
 * Ant based publisher.
 *
 * @author Gorkem Ercan
 */

public class AntPublisher{

	/**
	 * @param parents
	 * @param module
	 * @param serverDefinition
	 */
	private List parents;
	private IModule module;
	private ServerRuntime serverTypeDefinition;
	
	public AntPublisher(List parents, IModule module, ServerRuntime serverDefinition) {
		this.parents = parents;
		this.module = module;
		this.serverTypeDefinition = serverDefinition;
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wtp.server.core.model.IPublisher#publish(org.eclipse.wtp.server.core.resources.IModuleResource[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus[] publish(IModuleArtifact[] resource,
			IProgressMonitor monitor) throws CoreException {
		Module sModule =  serverTypeDefinition.getModule("j2ee.web");
		Publisher publisher =  serverTypeDefinition.getPublisher(sModule.getPublisherReference());
		String deployAnt = ((PublishType)publisher.getPublish().get(0)).getTask();
		deployAnt = serverTypeDefinition.getResolver().resolveProperties(deployAnt);
		
		if(deployAnt == null || deployAnt.length()<1)
			return new IStatus[]{new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID,0,"AntBuildFileDoesNotExist",null)};
		
		
		IPath file = CorePlugin.getDefault().getStateLocation().append("tempAnt.xml");
		try {
			createNewFile(file.toFile(),deployAnt.getBytes());
		} catch (IOException e) {
			return null;
		}
				
		runAnt(file.toString(),new String[]{"deploy"},getPublishProperties(resource),monitor);
		
		file.toFile().delete();
		return null;
	}
	private Map getPublishProperties(IModuleArtifact[] resource)
	{
		Module module =  serverTypeDefinition.getModule("j2ee.web");

		Map props = new HashMap();
		String modDir = module.getPublishDir();
		modDir = serverTypeDefinition.getResolver().resolveProperties(modDir);

		props.put("module.name",this.module.getName());
		IWebModule webModule = (IWebModule)this.module.getAdapter(IWebModule.class);
		props.put("module.dir",webModule.getLocation().toString());
		props.put("server.publish.dir",modDir);
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

	

}
