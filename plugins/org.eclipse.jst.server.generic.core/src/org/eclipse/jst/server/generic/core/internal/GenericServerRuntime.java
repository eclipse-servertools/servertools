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
import java.util.Iterator;
import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
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

	public static final String SERVER_DEFINITION_ID = "server_definition_id";
	public static final String SERVER_INSTANCE_PROPERTIES = "generic_server_instance_properties";
	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstallTypeId()
	 */
	public String getVMInstallTypeId() {
		return JavaRuntime.getDefaultVMInstall().getVMInstallType().getId();
		// TODO configurable.
	}
	
	public boolean isUsingDefaultJRE() {
		// TODO Auto-generated method stub
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstallId()
	 */
	public String getVMInstallId() {
		return JavaRuntime.getDefaultVMInstall().getId();
		// TODO configurable.
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstall()
	 */
	public IVMInstall getVMInstall() {
		return JavaRuntime.getDefaultVMInstall();
		// TODO configurable
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#validate()
	 */
	public IStatus validate() {
		if (getVMInstall() == null)
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.getString("errorJRE"), null);
		
		ServerRuntime serverTypeDefinition = getServerTypeDefinition();
        if(serverTypeDefinition == null)
		    return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.getString("errorNoServerType"), null);
        if(serverTypeDefinition.getClasspath()== null || serverTypeDefinition.getClasspath().size()<1)
            return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0 ,GenericServerCoreMessages.getString("errorNoClasspath"),null);
		Iterator cpList  = serverTypeDefinition.getClasspath().iterator();
        while (cpList.hasNext()) {
			Classpath cpth = (Classpath) cpList.next();
	        if(cpth.getArchive()== null || cpth.getArchive().size()<1)
	            return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0 ,GenericServerCoreMessages.getString("errorNoClasspath"),null);
			Iterator archIter = cpth.getArchive().iterator();
			while (archIter.hasNext()) {
				ArchiveType arch = (ArchiveType) archIter.next();
				String arcPath = serverTypeDefinition.getResolver().resolveProperties(arch.getPath());
		           File f = new File(arcPath);
		            if(f.exists()==false)
		                return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0 ,GenericServerCoreMessages.getFormattedString("errorMissingClasspathEntry",new String[]{f.getPath()} ),null);	
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
	
}
