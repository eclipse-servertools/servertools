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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.ant.core.AntRunner;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.jst.server.generic.internal.core.util.FileUtil;
import org.eclipse.jst.server.generic.servertype.definition.Module;
import org.eclipse.jst.server.generic.servertype.definition.PublisherData;
import org.eclipse.jst.server.core.IEJBModule;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.osgi.framework.Bundle;
/**
 * Ant based publisher.
 *
 * @author Gorkem Ercan
 */

public class AntPublisher extends GenericPublisher{

    private static final String MODULE_PUBLISH_TARGET_PREFIX = "target.publish.";
    private static final String MODULE_UNPUBLISH_TARGET_PREFIX = "target.unpublish.";
    public static final String PUBLISHER_ID="org.eclipse.jst.server.generic.antpublisher"; 
    private static final String DATA_NAME_BUILD_FILE="build.file";

    /* (non-Javadoc)
	 * @see org.eclipse.wtp.server.core.model.IPublisher#publish(org.eclipse.wtp.server.core.resources.IModuleResource[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus[] publish(IModuleArtifact[] resource,
			IProgressMonitor monitor){
        
        File file = computeBuildFile();
        try{
            runAnt(file.toString(),getPublishTargetsForModule(),getPublishProperties(),monitor);
        }
        catch(CoreException e){
            IStatus s = new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID,0,"Publish failed using Ant publisher",e);
            return new IStatus[] {s};
        }
		return null;
	}


    /**
     * @return
     */
    private File computeBuildFile() {
        Bundle bundle = Platform.getBundle(fServerRuntime.getConfigurationElementNamespace());
        File file = FileUtil.resolveFileFrom(bundle,getBuildFile());
        return file;
    }
    
   
    /**
     * @return
     */
    private String[] getPublishTargetsForModule() {
        String dataname = MODULE_PUBLISH_TARGET_PREFIX+getModuleTypeId();
        return doGetTargets(dataname);
    }


    /**
     * @param dataname
     * @return
     */
    private String[] doGetTargets(String dataname) {
        ArrayList list = new ArrayList();
        Iterator iterator = fServerRuntime.getPublisher(PUBLISHER_ID).getPublisherdata().iterator();
        while(iterator.hasNext()){
            PublisherData data = (PublisherData)iterator.next();
            if(dataname.equals(data.getDataname())) {
                list.add(data.getDatavalue());
            }   
        }
        return (String[])list.toArray(new String[list.size()]);
    }

    /**
     * @return
     */
    private String[] getUnpublishTargetsForModule() {
        
        return doGetTargets(MODULE_UNPUBLISH_TARGET_PREFIX+getModuleTypeId());
    }
    
    
    private String getModuleTypeId()
    {
        return fModule.getModuleType().getId();
    }
    
	private String getBuildFile()
    {
        Iterator iterator = fServerRuntime.getPublisher(PUBLISHER_ID).getPublisherdata().iterator();
        while(iterator.hasNext())
        {
            PublisherData data = (PublisherData)iterator.next();
            if(DATA_NAME_BUILD_FILE.equals(data.getDataname()))
                return fServerRuntime.getResolver().resolveProperties(data.getDatavalue());
        }
        return null;
    }
	private Map getPublishProperties()
	{
        Map props = new HashMap();
        //publish dir
        Module module =  fServerRuntime.getModule(getModuleTypeId());
		String modDir = module.getPublishDir();
		modDir = fServerRuntime.getResolver().resolveProperties(modDir);

		IWebModule webModule = (IWebModule)fModule.getAdapter(IWebModule.class);
        IEJBModule ejbModule = (IEJBModule)fModule.getAdapter(IEJBModule.class);
		String moduleName="unknownmodule";
        String moduleDir="";
        if(webModule!=null){    
            moduleName = this.guessModuleName(webModule);
            moduleDir = webModule.getLocation().toString();
        }
        if(ejbModule!=null){  
            moduleName = fModule.getName();
            moduleDir= ejbModule.getLocation().toString();
        }
		props.put("module.name",moduleName);
		props.put("module.dir",moduleDir);
		props.put("server.publish.dir",modDir);
		return props;
		
		
	}
	/**
	 * @param module2
	 * @param webModule
	 * @return
	 */
	private String guessModuleName(IWebModule webModule) {
		String moduleName = fModule.getName(); 
		//Default to project name but not a good guess
		//may have blanks etc.
		
		// A better choice is to use the context root
		// For wars most appservers use the module name
		// as the context root
		String contextRoot = webModule.getContextRoot();
		if(contextRoot.charAt(0) == '/')
			moduleName = contextRoot.substring(1);
		return moduleName;
	}
	private void runAnt(String buildFile,String[] targets,Map properties ,IProgressMonitor monitor)throws CoreException
	{
		AntRunner runner = new AntRunner();
		runner.setBuildFileLocation(buildFile);
		runner.setExecutionTargets(targets);
		runner.addUserProperties(properties);
		runner.run(monitor);
	}

    /* (non-Javadoc)
     * @see org.eclipse.jst.server.generic.internal.core.GenericPublisher#unpublish(org.eclipse.wst.server.core.IModule, org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus[] unpublish(IProgressMonitor monitor) {
        File file = computeBuildFile();
        try {
            runAnt(file.toString(),getUnpublishTargetsForModule(),getPublishProperties(),monitor);
        } catch (CoreException e) {
            IStatus s = new Status(IStatus.ERROR,CorePlugin.PLUGIN_ID,0,"Remove module failed using Ant publisher",e);
            return new IStatus[] {s};
 
        }
        return null;
    }



}
