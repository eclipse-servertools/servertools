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
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
/**
 * 
 */
public final class StandardJreLabelProvider implements IRuntimeComponentLabelProvider {
	private static final String TYPE = "org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType";

   private final IRuntimeComponent rc;

   public StandardJreLabelProvider(final IRuntimeComponent rc) {
		this.rc = rc;
	}

	public String getLabel() {
		String name = this.rc.getProperty("name");

		IVMInstall install = JavaRuntime.getVMInstallType(TYPE).findVMInstallByName(name);

		StringBuffer buf = new StringBuffer();
		buf.append("Standard JRE ");
		buf.append(this.rc.getRuntimeComponentVersion().getVersionString());
		buf.append(" [");
		buf.append(install.getInstallLocation().toString());
		buf.append("]");

		return buf.toString();
	}

	public static final class Factory implements IAdapterFactory {
		private static final Class[] ADAPTER_TYPES = { IRuntimeComponentLabelProvider.class };

		public Object getAdapter(final Object adaptable, final Class adapterType) {
			IRuntimeComponent rc = (IRuntimeComponent) adaptable;
			return new StandardJreLabelProvider(rc);
		}

		public Class[] getAdapterList() {
			return ADAPTER_TYPES;
		}
	}
}