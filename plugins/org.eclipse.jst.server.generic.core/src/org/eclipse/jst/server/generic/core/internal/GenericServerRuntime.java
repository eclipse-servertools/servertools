/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.core.internal;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.jst.server.generic.servertype.definition.ArchiveType;
import org.eclipse.jst.server.generic.servertype.definition.Classpath;
import org.eclipse.jst.server.generic.servertype.definition.Property;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
/**
 * Generic server runtime support.
 *
 * @author Gorkem Ercan
 */
public class GenericServerRuntime extends RuntimeDelegate implements IJavaRuntime
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
		if (getVMInstall() == null) {
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.errorJRE, null);
		}
		ServerRuntime serverTypeDefinition = getServerTypeDefinition();
        if(serverTypeDefinition == null) {
		    return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, GenericServerCoreMessages.errorNoServerType, null);
        }
		if(serverTypeDefinition.getClasspath()== null || serverTypeDefinition.getClasspath().size()<1) {
            return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0 ,GenericServerCoreMessages.errorNoClasspath,null);
		}
        return validateClasspaths(serverTypeDefinition);
	}

	/**
	 * Checks all defined classpaths.
	 */
	protected IStatus validateClasspaths(ServerRuntime serverTypeDefinition) {
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
		                return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0 ,NLS.bind(GenericServerCoreMessages.errorMissingClasspathEntry,f.getPath()),null);	
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
	
	public void setDefaults(IProgressMonitor monitor) {
		List props = this.getServerTypeDefinition().getProperty();
 		Map instancePropsMap = new HashMap();
 		for (Iterator iter = props.iterator(); iter.hasNext();) {
			Property element = (Property) iter.next();
			if(Property.CONTEXT_RUNTIME.equalsIgnoreCase(element.getContext()))
				instancePropsMap.put(element.getId(), element.getDefault());
		}
 		setServerInstanceProperties(instancePropsMap);

		
	}
}