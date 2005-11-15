/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 *    IBM Corporation - Support for all server types
 ******************************************************************************/
package org.eclipse.jst.server.core.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IJavaRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerCore;
/**
 * 
 */
public final class RuntimeBridge implements IRuntimeBridge {
    protected static final String CLASSPATH = "classpath";

    private static Map mappings = new HashMap();

    static {
        initialize();
    }

    private static void addMapping(String id, String id2, String version) {
        try {
            mappings.put(id, RuntimeManager.getRuntimeComponentType(id2).getVersion(version));
        } catch (Exception e) {
            // ignore
        }
    }

    private static void initialize() {
        RuntimeFacetMapping[] rfms = JavaServerPlugin.getRuntimeFacetMapping();
        int size = rfms.length;
        for (int i = 0; i < size; i++)
            addMapping(rfms[i].getRuntimeTypeId(), rfms[i].getRuntimeComponent(), rfms[i].getVersion());
        
        // generic runtimes
        addMapping("org.eclipse.jst.server.generic.runtime.weblogic81", "org.eclipse.jst.server.generic.runtime.weblogic", "8.1");
        
        addMapping("org.eclipse.jst.server.generic.runtime.weblogic90", "org.eclipse.jst.server.generic.runtime.weblogic", "9.0");
        
        addMapping("org.eclipse.jst.server.generic.runtime.jboss323", "org.eclipse.jst.server.generic.runtime.jboss", "3.2.3");
        
        addMapping("org.eclipse.jst.server.generic.runtime.jonas4", "org.eclipse.jst.server.generic.runtime.jonas", "4.0");
        
        addMapping("org.eclipse.jst.server.generic.runtime.oracle1013dp4", "org.eclipse.jst.server.generic.runtime.oracle", "1013dp4");
        
        addMapping("org.eclipse.jst.server.generic.runtime.websphere.6", "org.eclipse.jst.server.generic.runtime.websphere", "6.0");
    }
    
    public Set getExportedRuntimeNames() throws CoreException
    {
        final IRuntime[] runtimes = ServerCore.getRuntimes();
        final Set result = new HashSet();
        
        for( int i = 0; i < runtimes.length; i++ )
        {
            final IRuntime r = runtimes[ i ];
            
            if( mappings.containsKey( r.getRuntimeType().getId() ) )
            {
                result.add( r.getName() );
            }
        }
        
        return result;
    }

    public IStub bridge( final String name ) throws CoreException
    {
        return new Stub( ServerCore.findRuntime( name ) );
    }
    
    private static final class Stub implements IStub
    {
        private final IRuntime runtime;
        
        public Stub( final IRuntime runtime )
        {
            this.runtime = runtime;
        }
        
        public List getRuntimeComponents()
        {
            final List components = new ArrayList();
            
            // define server runtime component
            
            String typeId = runtime.getRuntimeType().getId();
            IRuntimeComponentVersion mapped = (IRuntimeComponentVersion) mappings.get(typeId);
            
            Map properties = new HashMap();
            properties.put("location", this.runtime.getLocation().toPortableString());
            properties.put("name", this.runtime.getName());
            properties.put("type", this.runtime.getRuntimeType().getName());
            properties.put("id", this.runtime.getId());
            
            RuntimeClasspathProviderWrapper rcpw = JavaServerPlugin.findRuntimeClasspathProvider(runtime.getRuntimeType());
            if (rcpw != null) {
                IPath path = new Path(RuntimeClasspathContainer.SERVER_CONTAINER);
                path = path.append(rcpw.getId()).append(this.runtime.getName());
                properties.put(CLASSPATH, path.toPortableString());
            }
            
            components.add(RuntimeManager.createRuntimeComponent(mapped, properties));
            
            // define JRE component
            IJavaRuntime javaRuntime = (IJavaRuntime) this.runtime.loadAdapter(IJavaRuntime.class, null);
            if (javaRuntime != null) {
                IVMInstall vmInstall = javaRuntime.getVMInstall();
                IVMInstall2 vmInstall2 = (IVMInstall2) vmInstall;
                
                String jvmver = vmInstall2.getJavaVersion();
                IRuntimeComponentVersion rcv;
                
                if (jvmver.startsWith("1.4")) {
                    rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("1.4");
                } else if (jvmver.startsWith("1.5")) {
                    rcv = RuntimeManager.getRuntimeComponentType("standard.jre").getVersion("5.0");
                } else
                    throw new IllegalStateException();  // TODO: Handle this better.
                
                properties = new HashMap();
                properties.put("name", vmInstall.getName());
                IPath path = new Path(JavaRuntime.JRE_CONTAINER);
                path.append(vmInstall.getVMInstallType().getId()).append(vmInstall.getName());
                properties.put(CLASSPATH, path.toPortableString());
                components.add(RuntimeManager.createRuntimeComponent(rcv, properties));
            }
            
            return components;
        }

        public Map getProperties()
        {
            return Collections.singletonMap("id", this.runtime.getId());
        }
    }

}