/******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Konstantin Komissarchik - initial API and implementation
 ******************************************************************************/
package org.eclipse.jst.server.ui.internal;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
/**
 * 
 */
public final class TomcatLabelProvider implements IRuntimeComponentLabelProvider {
	private final IRuntimeComponent rc;

	public TomcatLabelProvider(final IRuntimeComponent rc) {
		this.rc = rc;
	}

	public String getLabel() {
		final IPath location = Path.fromPortableString(rc.getProperty("location"));

		final StringBuffer buf = new StringBuffer();

		buf.append("Apache Tomcat ");
		//buf.append(rc.getRuntimeComponentType());
		buf.append(rc.getRuntimeComponentVersion().getVersionString());
		buf.append(" [");
		buf.append(location.toOSString());
		buf.append("]");

		return buf.toString();
	}

	public static final class Factory implements IAdapterFactory {
		private static final Class[] ADAPTER_TYPES = { IRuntimeComponentLabelProvider.class };

		public Object getAdapter(final Object adaptable, final Class adapterType) {
			final IRuntimeComponent rc = (IRuntimeComponent) adaptable;
			return new TomcatLabelProvider(rc);
		}

		public Class[] getAdapterList() {
			return ADAPTER_TYPES;
		}
	}
}