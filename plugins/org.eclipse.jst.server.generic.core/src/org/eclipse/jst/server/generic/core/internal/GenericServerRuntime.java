/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Gorkem Ercan - initial API and implementation
 *     Naci M. Dai
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.internal.IGenericRuntime;
import org.eclipse.jst.server.generic.servertype.definition.ArchiveType;
import org.eclipse.jst.server.generic.servertype.definition.Classpath;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * Generic server runtime support.
 *
 * @author Gorkem Ercan
 */
public class GenericServerRuntime extends RuntimeDelegate implements IGenericRuntime
{
	private static final String PROP_VM_INSTALL_TYPE_ID = "vm-install-type-id";
	private static final String PROP_VM_INSTALL_ID = "vm-install-id";
	public static final String SERVER_DEFINITION_ID = "server_definition_id";
	public static final String SERVER_INSTANCE_PROPERTIES = "generic_server_instance_properties";

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstallTypeId()
	 */
	public String getVMInstallTypeId() {
		return getAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
	}
	
	public boolean isUsingDefaultJRE() {
		return getVMInstallTypeId() == null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstallId()
	 */
	public String getVMInstallId() {
		return getAttribute(PROP_VM_INSTALL_ID, (String)null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstall()
	 */
	public IVMInstall getVMInstall() {
		if (getVMInstallTypeId() == null)
			return JavaRuntime.getDefaultVMInstall();
		try {
			IVMInstallType vmInstallType = JavaRuntime.getVMInstallType(getVMInstallTypeId());
			IVMInstall[] vmInstalls = vmInstallType.getVMInstalls();
			int size = vmInstalls.length;
			String id = getVMInstallId();
			for (int i = 0; i < size; i++) {
				if (id.equals(vmInstalls[i].getId()))
					return vmInstalls[i];
			}
		} catch (Exception e) {
			// ignore
		}
		return null;
	
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#validate()
	 */
	public IStatus validate() {
		if (getVMInstall() == null)
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.errorJRE, null);
		
		ServerRuntime serverTypeDefinition = getServerTypeDefinition();
        if(serverTypeDefinition == null)
		    return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.errorNoServerType, null);
        if(serverTypeDefinition.getClasspath()== null || serverTypeDefinition.getClasspath().size()<1)
            return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0 ,GenericServerCoreMessages.errorNoClasspath,null);
		Iterator cpList  = serverTypeDefinition.getClasspath().iterator();
        while (cpList.hasNext()) {
			Classpath cpth = (Classpath) cpList.next();
	        if(cpth.getArchive()== null || cpth.getArchive().size()<1)
	            return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0 ,GenericServerCoreMessages.errorNoClasspath,null);
			Iterator archIter = cpth.getArchive().iterator();
			while (archIter.hasNext()) {
				ArchiveType arch = (ArchiveType) archIter.next();
				String arcPath = serverTypeDefinition.getResolver().resolveProperties(arch.getPath());
		           File f = new File(arcPath);
		            if(f.exists()==false)
		                return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0 ,GenericServerCoreMessages.bind(GenericServerCoreMessages.errorMissingClasspathEntry,f.getPath()),null);	
			}
		}
        return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, 0, "", null);
	}
	/**
	 * Returns the ServerTypeDefinition for this runtime. 
	 * Populated with the user properties if exists. 
	 * 
	 * @return populated ServerTypeDefinition
	 */
	public ServerRuntime getServerTypeDefinition()
	{
	   String id=  getRuntime().getRuntimeType().getId();
	   Map properties = getAttribute(SERVER_INSTANCE_PROPERTIES,(Map)null);
	   if(id==null)
	       return null;
	   return CorePlugin.getDefault().getServerTypeDefinitionManager().getServerRuntimeDefinition(id,properties);
	}
	
	public void setVMInstall(IVMInstall vmInstall) {
		if (vmInstall == null) {
			setVMInstall(null, null);
		} else
			setVMInstall(vmInstall.getVMInstallType().getId(), vmInstall.getId());
	}
	
	private void setVMInstall(String typeId, String id) {
		if (typeId == null)
			setAttribute(PROP_VM_INSTALL_TYPE_ID, (String)null);
		else
			setAttribute(PROP_VM_INSTALL_TYPE_ID, typeId);
		
		if (id == null)
			setAttribute(PROP_VM_INSTALL_ID, (String)null);
		else
			setAttribute(PROP_VM_INSTALL_ID, id);
	}
	
	
	public Map getServerInstanceProperties() {
		return getAttribute(SERVER_INSTANCE_PROPERTIES, new HashMap());
	}
	
	public String getServerDefinitionId() {
		return getAttribute(SERVER_DEFINITION_ID, (String) null);
	}
	
	public void setServerInstanceProperties(Map map) {
		setAttribute(SERVER_INSTANCE_PROPERTIES, map);
	}
	
	public void setServerDefinitionId(String s) {
		setAttribute(SERVER_DEFINITION_ID, s);
	}
	
}