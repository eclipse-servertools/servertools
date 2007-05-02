/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
/**
 * The Tomcat plugin.
 */
public class TomcatPlugin extends Plugin {
	protected static TomcatPlugin singleton;

	public static final String PLUGIN_ID = "org.eclipse.jst.server.tomcat.core";

	public static final String TOMCAT_32 = "org.eclipse.jst.server.tomcat.32";
	public static final String TOMCAT_40 = "org.eclipse.jst.server.tomcat.40";
	public static final String TOMCAT_41 = "org.eclipse.jst.server.tomcat.41";
	public static final String TOMCAT_50 = "org.eclipse.jst.server.tomcat.50";
	public static final String TOMCAT_55 = "org.eclipse.jst.server.tomcat.55";
	public static final String TOMCAT_60 = "org.eclipse.jst.server.tomcat.60";

	protected static final String VERIFY_INSTALL_FILE = "verifyInstall.properties";
	protected static VerifyResourceSpec[] verify32;
	protected static VerifyResourceSpec[] verify40;
	protected static VerifyResourceSpec[] verify41;
	protected static VerifyResourceSpec[] verify50;
	protected static VerifyResourceSpec[] verify55;
	protected static VerifyResourceSpec[] verify60;
	
	protected static final IStatus emptyInstallDirStatus = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorInstallDirEmpty, null);
	protected static final IStatus wrongDirVersionStatus = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorInstallDirWrongVersion, null);
	protected static final IStatus installDirDoesNotExist = new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorInstallDirDoesNotExist, null);

	/**
	 * TomcatPlugin constructor comment.
	 */
	public TomcatPlugin() {
		super();
		singleton = this;
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * @return org.eclipse.jst.server.tomcat.internal.TomcatPlugin
	 */
	public static TomcatPlugin getInstance() {
		return singleton;
	}

	/**
	 * Return the install location preference.
	 * 
	 * @param id a runtime type id
	 * @return the install location
	 */
	public static String getPreference(String id) {
		return getInstance().getPluginPreferences().getString(id);
	}
	
	/**
	 * Set the install location preference.
	 * 
	 * @param id the runtimt type id
	 * @param value the location
	 */
	public static void setPreference(String id, String value) {
		getInstance().getPluginPreferences().setValue(id, value);
		getInstance().savePluginPreferences();
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status a status object
	 */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	/**
	 * Returns the Tomcat home directory.
	 * @return java.lang.String
	 */
	protected static String getTomcatStateLocation() {
		try {
			return getInstance().getStateLocation().toOSString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the Tomcat version handler.
	 * 
	 * @param id
	 * @return a version handler
	 */
	public static ITomcatVersionHandler getTomcatVersionHandler(String id) {
		if (id.indexOf("runtime") > 0)
			id = id.substring(0, 30) + id.substring(38);
		//id = id.substring(0, id.length() - 8);
		if (TOMCAT_32.equals(id))
			return new Tomcat32Handler();
		else if (TOMCAT_40.equals(id))
			return new Tomcat40Handler();
		else if (TOMCAT_41.equals(id))
			return new Tomcat41Handler();
		else if (TOMCAT_50.equals(id))
			return new Tomcat50Handler();
		else if (TOMCAT_55.equals(id))
			return new Tomcat55Handler();
		else if (TOMCAT_60.equals(id))
			return new Tomcat60Handler();
		else
			return null;
	}

	/**
	 * Loads the files to verify the Tomcat installation.
	 */
	public static void loadVerifyFiles() {
		if (verify32 != null)
			return;
	
		// backup (empty) values
		verify32 = new VerifyResourceSpec[0];
		verify40 = new VerifyResourceSpec[0];
		verify41 = new VerifyResourceSpec[0];
		verify50 = new VerifyResourceSpec[0];
		verify55 = new VerifyResourceSpec[0];
		verify60 = new VerifyResourceSpec[0];
		
		try {
			URL url = getInstance().getBundle().getEntry(VERIFY_INSTALL_FILE);
			url = FileLocator.resolve(url);
			Properties p = new Properties();
			p.load(url.openStream());

			// Check backdoor system property, use internal spec if not found
			String verify = System.getProperty(PLUGIN_ID + ".verify32install");
			if (verify == null) {
				verify = p.getProperty("verify32install");
			}
			verify.replace('/', File.separatorChar);

			StringTokenizer st = new StringTokenizer(verify, ",");
			List list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(new VerifyResourceSpec(st.nextToken()));
			Trace.trace(Trace.FINEST, "Verify32: " + list.toString());
			verify32 = new VerifyResourceSpec[list.size()];
			list.toArray(verify32);

			// v4.0
			// Check backdoor system property, use internal spec if not found
			verify = System.getProperty(PLUGIN_ID + ".verify40install");
			if (verify == null) {
				verify = p.getProperty("verify40install");
			}
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(new VerifyResourceSpec(st.nextToken()));
			Trace.trace(Trace.FINEST, "Verify40: " + list.toString());
			verify40 = new VerifyResourceSpec[list.size()];
			list.toArray(verify40);
			
			// v4.1
			// Check backdoor system property, use internal spec if not found
			verify = System.getProperty(PLUGIN_ID + ".verify41install");
			if (verify == null) {
				verify = p.getProperty("verify41install");
			}
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(new VerifyResourceSpec(st.nextToken()));
			Trace.trace(Trace.FINEST, "Verify41: " + list.toString());
			verify41 = new VerifyResourceSpec[list.size()];
			list.toArray(verify41);
			
			// v5.0
			// Check backdoor system property, use internal spec if not found
			verify = System.getProperty(PLUGIN_ID + ".verify50install");
			if (verify == null) {
				verify = p.getProperty("verify50install");
			}
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(new VerifyResourceSpec(st.nextToken()));
			Trace.trace(Trace.FINEST, "Verify50: " + list.toString());
			verify50 = new VerifyResourceSpec[list.size()];
			list.toArray(verify50);

			// v5.5
			// Check backdoor system property, use internal spec if not found
			verify = System.getProperty(PLUGIN_ID + ".verify55install");
			if (verify == null) {
				verify = p.getProperty("verify55install");
			}
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(new VerifyResourceSpec(st.nextToken()));
			Trace.trace(Trace.FINEST, "Verify55: " + list.toString());
			verify55 = new VerifyResourceSpec[list.size()];
			list.toArray(verify55);

			// v6.0
			// Check backdoor system property, use internal spec if not found
			verify = System.getProperty(PLUGIN_ID + ".verify60install");
			if (verify == null) {
				verify = p.getProperty("verify60install");
			}
			verify.replace('/', File.separatorChar);

			st = new StringTokenizer(verify, ",");
			list = new ArrayList();
			while (st.hasMoreTokens())
				list.add(new VerifyResourceSpec(st.nextToken()));
			Trace.trace(Trace.FINEST, "Verify60: " + list.toString());
			verify60 = new VerifyResourceSpec[list.size()];
			list.toArray(verify60);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not load installation verification properties", e);
		}
	}

	/**
	 * Utility method to verify an installation directory according to the
	 * specified server ID.  The verification includes checking the installation
	 * directory name to see if it indicates a different version of Tomcat.
	 * 
	 * @param installPath Path to verify
	 * @param id Type ID of the server
	 * @return Status of the verification.  Will be Status.OK_STATUS, if verification
	 *    was successful, or error status if not.
	 */
	public static IStatus verifyInstallPathWithFolderCheck(IPath installPath, String id) {
		IStatus status = verifyTomcatVersionFromPath(installPath, id);
		if (status.isOK()) {
			status = verifyInstallPath(installPath, id);
		}
		return status;
	}

	/**
	 * Verify the Tomcat installation directory.
	 * 
	 * @param installPath Path to verify
	 * @param id Type ID of the server
	 * @return Status of the verification.  Will be Status.OK_STATUS, if verification
	 *    was successful, or error status if not.
	 */
	public static IStatus verifyInstallPath(IPath installPath, String id) {
		if (installPath == null)
			return emptyInstallDirStatus;
		
		String dir = installPath.toOSString();
		if (dir.trim().length() == 0)
			return emptyInstallDirStatus;

		File file = new File(dir);
		if (!file.exists())
			return installDirDoesNotExist;

		if (!dir.endsWith(File.separator))
			dir += File.separator;
		
		// look for the following files and directories
		TomcatPlugin.loadVerifyFiles();
		
		VerifyResourceSpec[] specs = null;
		if (TOMCAT_32.equals(id))
			specs = verify32;
		else if (TOMCAT_40.equals(id))
			specs = verify40;
		else if (TOMCAT_41.equals(id))
			specs = verify41;
		else if (TOMCAT_50.equals(id))
			specs = verify50;
		else if (TOMCAT_55.equals(id)) {
			specs = verify55;
		}
		else if (TOMCAT_60.equals(id)) {
			specs = verify60;
		}
		else
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorUnknownVersion, null);
		
		for (int i = 0; i < specs.length; i++) {
			VerifyResourceSpec fs = specs[i];
			IStatus status = fs.checkResource(dir);
			if (!status.isOK()) {
				return status;
			}
		}
		return Status.OK_STATUS;
	}

	public static IStatus verifyTomcatVersionFromPath(IPath installPath, String version) {
		if (version == null) 
			return new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0, Messages.errorVersionEmpty, null);
		if (installPath == null)
			return emptyInstallDirStatus;

		String s = installPath.lastSegment();
		if (s == null)
			return Status.OK_STATUS;
		if (s.indexOf("-3.2") > 0 || s.indexOf(" 3.2") > 0)
			return TOMCAT_32.equals(version) ? Status.OK_STATUS : wrongDirVersionStatus;
		if (s.indexOf("-4.0") > 0 || s.indexOf(" 4.0") > 0)
			return TOMCAT_40.equals(version) ? Status.OK_STATUS : wrongDirVersionStatus;
		if (s.indexOf("-4.1") > 0 || s.indexOf(" 4.1") > 0)
			return TOMCAT_41.equals(version) ? Status.OK_STATUS : wrongDirVersionStatus;
		if (s.indexOf("-5.0") > 0 || s.indexOf(" 5.0") > 0)
			return TOMCAT_50.equals(version) ? Status.OK_STATUS : wrongDirVersionStatus;
		if (s.indexOf("-5.5") > 0 || s.indexOf(" 5.5") > 0)
			return TOMCAT_55.equals(version) ? Status.OK_STATUS : wrongDirVersionStatus;
		if (s.indexOf("-6.0") > 0 || s.indexOf(" 6.0") > 0)
			return TOMCAT_60.equals(version) ? Status.OK_STATUS : wrongDirVersionStatus;
		return Status.OK_STATUS;
	}

	/**
	 * Return a <code>java.io.File</code> object that corresponds to the specified
	 * <code>IPath</code> in the plugin directory.
	 * 
	 * @return a file
	 */
	protected static File getPlugin() {
		try {
			URL installURL = getInstance().getBundle().getEntry("/");
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException ioe) {
			return null;
		}
	}

	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, message, null));
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
	}
}
