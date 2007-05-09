/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
/**
 * A facet runtime label provider that can be used as an implementation of
 * org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider in the
 * org.eclipse.wst.common.project.facet.core.runtimes extension point.
 */
public class FacetRuntimeComponentLabelProvider implements IAdapterFactory {
	private static final Class[] ADAPTER_TYPES = { IRuntimeComponentLabelProvider.class };

	public final class RuntimeLabelProvider implements IRuntimeComponentLabelProvider {
		private final IRuntimeComponent rc;

		public RuntimeLabelProvider(IRuntimeComponent rc) {
			this.rc = rc;
		}

		public String getLabel() {
			return rc.getProperty("type");
		}
	}

	public Object getAdapter(Object adaptable, Class adapterType) {
		IRuntimeComponent rc = (IRuntimeComponent) adaptable;
		return new RuntimeLabelProvider(rc);
	}

	public Class[] getAdapterList() {
		return ADAPTER_TYPES;
	}
}