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
package org.eclipse.jst.server.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
/**
 * 
 */
public final class RuntimeLabelProvider implements IRuntimeComponentLabelProvider {
	private final IRuntimeComponent rc;

	public RuntimeLabelProvider(final IRuntimeComponent rc) {
		this.rc = rc;
	}

	public String getLabel() {
		return rc.getProperty("type");
	}

	public static final class Factory implements IAdapterFactory {
		private static final Class[] ADAPTER_TYPES = { IRuntimeComponentLabelProvider.class };

		public Object getAdapter(final Object adaptable, final Class adapterType) {
			final IRuntimeComponent rc = (IRuntimeComponent) adaptable;
			return new RuntimeLabelProvider(rc);
		}

		public Class[] getAdapterList() {
			return ADAPTER_TYPES;
		}
	}
}