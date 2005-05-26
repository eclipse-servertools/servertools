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
package org.eclipse.jst.server.generic.core.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.ant.internal.ui.IAntUIConstants;
import org.eclipse.ant.internal.ui.launchConfigurations.IAntLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jst.server.generic.internal.core.util.FileUtil;
import org.eclipse.jst.server.generic.servertype.definition.Module;
import org.eclipse.jst.server.generic.servertype.definition.PublisherData;
import org.eclipse.jst.server.core.IEJBModule;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.ui.externaltools.internal.model.IExternalToolConstants;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.osgi.framework.Bundle;
/**
 * Ant based publisher.
 * All the properties defined in the server definition file are
 * passed into the ANT build file as properties.
 * In addition to the properties defined in the server definition
 * <I>module.dir</I>, <I>module.name,</I> and <I>server.publish.dir</I> are computed and passed to the 
 * definition file.
 * <ul>
 * <li>module.dir: includes the root of the module project file</li>
 * <li>module.name: the name of the module</li>
 * <li>server.publish.dir: the directory to put the deployment units</li>
 * </ul>
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
            CorePlugin.getDefault().getLog().log(s);
            return new IStatus[] {s};
        }
		return null;
	}


    /**
     * @return
     */
    private File computeBuildFile() {
        Bundle bundle = Platform.getBundle(getServerRuntime().getServerTypeDefinition().getConfigurationElementNamespace());
        File file = FileUtil.resolveFileFrom(bundle,getBuildFile());
        return file;
    }
    
   
    /**
     * @return
     */
    private String getPublishTargetsForModule() {
    	return doGetTargets(MODULE_PUBLISH_TARGET_PREFIX+getModuleTypeId());
    }

    /**
     * @return
     */
    private String getUnpublishTargetsForModule() {
        return doGetTargets(MODULE_UNPUBLISH_TARGET_PREFIX+getModuleTypeId());
    }
    
    /**
     * @param dataname
     * @return
     */
    private String doGetTargets(String dataname) {
    	StringBuffer buffer = new StringBuffer();
    	Iterator iterator = getServerRuntime().getServerTypeDefinition().getPublisher(PUBLISHER_ID).getPublisherdata().iterator();
        while(iterator.hasNext()){
            PublisherData data = (PublisherData)iterator.next();
            if(dataname.equals(data.getDataname())) {
                if(buffer.length()>0)
                	buffer.append(",");
            	buffer.append(data.getDatavalue());
            }   
        }
        return buffer.toString();
    }

    private String getModuleTypeId(){
        return getModule()[0].getModuleType().getId();
    }
    
	private String getBuildFile()
    {
        Iterator iterator = getServerRuntime().getServerTypeDefinition().getPublisher(PUBLISHER_ID).getPublisherdata().iterator();
        while(iterator.hasNext())
        {
            PublisherData data = (PublisherData)iterator.next();
            if(DATA_NAME_BUILD_FILE.equals(data.getDataname()))
                return getServerRuntime().getServerTypeDefinition().getResolver().resolveProperties(data.getDatavalue());
        }
        return null;
    }
	private Map getPublishProperties()
	{
        Map props = new HashMap();
        
        // pass all properties to build file.
        Map properties = getServerRuntime().getServerTypeDefinition().getResolver().getPropertyValues();
        Iterator propertyIterator = properties.keySet().iterator();
        while(propertyIterator.hasNext())
        {
            String property = (String)propertyIterator.next();
            props.put(property,properties.get(property));
        }
        
        Module module =  getServerRuntime().getServerTypeDefinition().getModule(getModuleTypeId());
		String modDir = module.getPublishDir();
		modDir = getServerRuntime().getServerTypeDefinition().getResolver().resolveProperties(modDir);

		IWebModule webModule = (IWebModule)getModule()[0].getAdapter(IWebModule.class);
        IEJBModule ejbModule = (IEJBModule)getModule()[0].getAdapter(IEJBModule.class);
		String moduleName="unknownmodule";
        String moduleDir="";
        if(webModule!=null){    
            moduleName = this.guessModuleName(webModule);
            moduleDir = webModule.getLocation().toString();
        }
        if(ejbModule!=null){  
            moduleName = getModule()[0].getName();
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
		String moduleName = getModule()[0].getName(); 
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
	private void runAnt(String buildFile,String targets,Map properties ,IProgressMonitor monitor)throws CoreException{
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = launchManager.getLaunchConfigurationType(IAntLaunchConfigurationConstants.ID_ANT_LAUNCH_CONFIGURATION_TYPE);

		ILaunchConfigurationWorkingCopy wc= type.newInstance(null,properties.get("module.name")+" module publisher");
		wc.setContainer(null);
		wc.setAttribute(IExternalToolConstants.ATTR_LOCATION, buildFile);
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,"org.eclipse.ant.ui.AntClasspathProvider");
		wc.setAttribute(IAntLaunchConfigurationConstants.ATTR_ANT_TARGETS,targets);
		wc.setAttribute(IAntLaunchConfigurationConstants.ATTR_ANT_PROPERTIES,properties);
		wc.setAttribute(IDebugUIConstants.ATTR_LAUNCH_IN_BACKGROUND,false);
		wc.setAttribute(IDebugUIConstants.ATTR_CAPTURE_IN_CONSOLE,true);
		wc.setAttribute(IDebugUIConstants.ATTR_PRIVATE,true);
		
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_SOURCE_PATH_PROVIDER, "org.eclipse.ant.ui.AntClasspathProvider"); 
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME,getServerRuntime().getVMInstall().getName());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE,getServerRuntime().getVMInstall().getVMInstallType().getId());
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, "org.eclipse.ant.internal.ui.antsupport.InternalAntRunner");
		wc.setAttribute(DebugPlugin.ATTR_PROCESS_FACTORY_ID, IAntUIConstants.REMOTE_ANT_PROCESS_FACTORY_ID);
		
		ILaunchConfiguration launchConfig = wc.doSave();
        launchConfig.launch("run",monitor);
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
