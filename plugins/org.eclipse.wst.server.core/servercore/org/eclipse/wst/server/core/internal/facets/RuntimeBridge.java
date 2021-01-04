/*******************************************************************************
 * Copyright (c) 2007, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 *    IBM Corporation - Support for all server types
 *******************************************************************************/
package org.eclipse.wst.server.core.internal.facets;

import java.util.*;

import org.eclipse.core.runtime.*;
import org.eclipse.wst.common.project.facet.core.runtime.*;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Runtime;
import org.eclipse.wst.server.core.internal.RuntimeType;
/**
 * 
 */
public class RuntimeBridge implements IRuntimeBridge {
	protected static Map<String, List<IRuntimeComponentVersion>> mappings = new HashMap<String, List<IRuntimeComponentVersion>>();

	static {
		initialize();
	}

	private static void addMapping(String id, String id2, String version) {
		List<IRuntimeComponentVersion> list = null;
		try {
			list = mappings.get(id);
		} catch (Exception e) {
			// ignore
		}
		
		if (list == null)
			list = new ArrayList<IRuntimeComponentVersion>(2);
		
		try {
			list.add(RuntimeManager.getRuntimeComponentType(id2).getVersion(version));
			mappings.put(id, list);
		} catch (Exception e) {
			// ignore
		}
	}

	private static void initialize() {
		// add runtime mappings
		IRuntimeType[] runtimeTypes = ServerCore.getRuntimeTypes();
		int size = runtimeTypes.length;
		for (int i = 0; i < size; i++) {
			RuntimeType rt = (RuntimeType) runtimeTypes[i];
			String component = rt.getFacetRuntimeComponent();
			String version = rt.getFacetRuntimeVersion();
			if (component != null && !"".equals(component) && version != null && !"".equals(version))
				addMapping(rt.getId(), component, version);
		}
		
		// add extension mappings
		RuntimeFacetMapping[] rfms = FacetMappingUtil.getRuntimeFacetMapping();
		size = rfms.length;
		for (int i = 0; i < size; i++)
			addMapping(rfms[i].getRuntimeTypeId(), rfms[i].getRuntimeComponent(), rfms[i].getVersion());
		
		// generic runtimes
		addMapping("org.eclipse.jst.server.generic.runtime.jboss323", "org.eclipse.jst.server.generic.runtime.jboss", "3.2.3");

		addMapping("org.eclipse.jst.server.generic.runtime.oracle1013", "org.eclipse.jst.server.generic.runtime.oracle", "10.1.3");
	}

	public Set<String> getExportedRuntimeNames() throws CoreException {
		IRuntime[] runtimes = ServerCore.getRuntimes();
		Set<String> result = new HashSet<String>(runtimes.length);
		
		for (int i = 0; i < runtimes.length; i++) {
			IRuntime runtime = runtimes[i];
			IRuntimeType runtimeType = runtime.getRuntimeType();
			if (runtimeType != null && mappings.containsKey(runtimeType.getId())) {
				result.add(runtime.getId());
			}
		}
		
		return result;
	}

	public IStub bridge(String name) throws CoreException {
		if (name == null)
			throw new IllegalArgumentException();
		return new Stub(name);
	}

	private static class Stub extends IRuntimeBridge.Stub {
		private String id;

		public Stub(String id) {
			this.id = id;
		}

		public List<IRuntimeComponent> getRuntimeComponents() {
			List<IRuntimeComponent> components = new ArrayList<IRuntimeComponent>(2);
			final IRuntime runtime = findRuntime( this.id );
			
			if (runtime == null)
				return components;
			
			// define server runtime component
			Map<String, String> properties = new HashMap<String, String>(5);
			if (runtime.getLocation() != null)
				properties.put("location", runtime.getLocation().toPortableString());
			else
				properties.put("location", "");
			properties.put("name", runtime.getName());
			properties.put("id", runtime.getId());
			if (runtime.getRuntimeType() != null) {
				properties.put("type", runtime.getRuntimeType().getName());
				properties.put("type-id", runtime.getRuntimeType().getId());
			}
			
			String typeId = runtime.getRuntimeType().getId();
			if (mappings.containsKey(typeId)) {
				ArrayList list = (ArrayList) mappings.get(typeId);
				int size = list.size();
				for (int i = 0; i < size; i++) {
					IRuntimeComponentVersion mapped = (IRuntimeComponentVersion) list.get(i);
					components.add(RuntimeManager.createRuntimeComponent(mapped, properties));
				}
			}
			
			FacetMappingUtil.addFacetRuntimeComponents(runtime, components);
			
			return components;
		}

		public Map<String, String> getProperties() {
			final Map<String, String> props = new HashMap<String, String>();
            final IRuntime runtime = findRuntime( this.id );
			if (runtime != null) {
				props.put("id", runtime.getId());
				props.put("localized-name", runtime.getName());
				String s = ((Runtime)runtime).getAttribute("alternate-names", (String)null);
				if (s != null)
				    props.put("alternate-names", s);
			}
			return props;
		}
		
		public IStatus validate(final IProgressMonitor monitor) {
            final IRuntime runtime = findRuntime( this.id );
            if( runtime != null ) {
                return runtime.validate( monitor );
            }
            return Status.OK_STATUS; 
		}
		
		private static final IRuntime findRuntime( final String id )
		{
	        IRuntime[] runtimes = ServerCore.getRuntimes();
	        int size = runtimes.length;

	        for (int i = 0; i < size; i++) {
	            if (runtimes[i].getId().equals(id))
	                return runtimes[i];
	            if (runtimes[i].getName().equals(id))
	                return runtimes[i];

	        }
	        return null;
	    }
	}
}
