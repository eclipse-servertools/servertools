package org.eclipse.jst.server.tomcat.core.internal;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
/**
 * 
 */
public class TomcatConfigurationUtil {
	public static final int CONFIGURATION_V32 = 0;
	public static final int CONFIGURATION_V40 = 20;
	public static final int CONFIGURATION_V41 = 40;
	public static final int CONFIGURATION_V50 = 60;
	public static final int CONFIGURATION_V55 = 80;

	/**
	 * 
	 */
	protected static boolean verifyConfiguration(TomcatConfiguration config, int version) {
		if (version == CONFIGURATION_V32) {
			return config instanceof Tomcat32Configuration;
		} else {
			Server server = null;
			if (config instanceof Tomcat40Configuration) {
				server = ((Tomcat40Configuration)config).server;
			} else if (config instanceof Tomcat41Configuration) {
				server = ((Tomcat41Configuration)config).server;
			} else if (config instanceof Tomcat50Configuration) {
				server = ((Tomcat50Configuration)config).server;
			} else if (config instanceof Tomcat55Configuration) {
				server = ((Tomcat55Configuration)config).server;
			}

			/*if (Tomcat50Configuration.verifyConfiguration(server))
				return (version == CONFIGURATION_V50);
*/
			if (Tomcat41Configuration.hasMDBListener(server))
				return (version == CONFIGURATION_V41
						|| version == CONFIGURATION_V50
						|| version == CONFIGURATION_V55);

			if (version == CONFIGURATION_V40)
				return true;
		}

		return false;
	}
}