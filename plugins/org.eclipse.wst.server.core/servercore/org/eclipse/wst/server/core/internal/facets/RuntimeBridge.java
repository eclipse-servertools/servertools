/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 *    IBM Corporation - Support for all server types
 *******************************************************************************/
package org.eclipse.wst.server.core.internal.facets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeBridge;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponentVersion;
import org.eclipse.wst.common.project.facet.core.runtime.RuntimeManager;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.RuntimeType;
/**
 * 
 */
public class RuntimeBridge implements IRuntimeBridge {
	protected static Map mappings = new HashMap();

	static {
		initialize();
	}

	private static void addMapping(String id, String id2, String version) {
		ArrayList list = null;
		try {
			list = (ArrayList) mappings.get(id);
		} catch (Exception e) {
			// ignore
		}
		
		if (list == null)
			list = new ArrayList(2);
		
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
	}

	public Set getExportedRuntimeNames() throws CoreException {
		IRuntime[] runtimes = ServerCore.getRuntimes();
		Set result = new HashSet(runtimes.length);
		
		for (int i = 0; i < runtimes.length; i++) {
			IRuntime runtime = runtimes[i];
			IRuntimeType runtimeType = runtime.getRuntimeType();
			if (runtimeType != null && mappings.containsKey(runtimeType.getId())) {
				result.add(runtime.getName());
			}
		}
		
		return result;
	}

	public IStub bridge(String name) throws CoreException {
		if (name == null)
			throw new IllegalArgumentException();
		
		IRuntime[] runtimes = ServerCore.getRuntimes();
		int size = runtimes.length;
		for (int i = 0; i < size; i++) {
			if (runtimes[i].getName().equals(name))
				return new Stub(runtimes[i]);
		}
		return null;
	}

	private static class Stub implements IStub {
		private IRuntime runtime;

		public Stub(IRuntime runtime) {
			this.runtime = runtime;
		}

		public List getRuntimeComponents() {
			List components = new ArrayList(2);
			if (runtime == null)
				return components;
			
			// define server runtime component
			Map properties = new HashMap(5);
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

		public Map getProperties() {
			if (runtime == null)
				return new HashMap(0);
			return Collections.singletonMap("id", runtime.getId());
		}
	}
}