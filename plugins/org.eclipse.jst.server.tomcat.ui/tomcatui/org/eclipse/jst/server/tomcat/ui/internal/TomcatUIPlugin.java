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
package org.eclipse.jst.server.tomcat.ui.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.server.core.IServer;
/**
 * The actual Tomcat plugin. It allows the rest of the classes
 * to load images and get a handle to the desktop.
 */
public class TomcatUIPlugin extends AbstractUIPlugin {
	protected static TomcatUIPlugin singleton;

	protected Map imageDescriptors = new HashMap();

	// base url for icons
	private static URL ICON_BASE_URL;

	private static final String URL_OBJ = "obj16/";
	private static final String URL_WIZBAN = "wizban/";

	public static final String PLUGIN_ID = "org.eclipse.jst.server.tomcat.ui";

	public static final String IMG_WIZ_TOMCAT = "wizTomcat";

	public static final String IMG_WEB_MODULE = "webModule";
	public static final String IMG_MIME_MAPPING = "mimeMapping";
	public static final String IMG_MIME_EXTENSION = "mimeExtension";
	public static final String IMG_PORT = "port";
	public static final String IMG_PROJECT_MISSING = "projectMissing";

	public static final String PREF_TOMCAT32_INSTALL_DIR = "tomcat32install";
	public static final String PREF_TOMCAT40_INSTALL_DIR = "tomcat40install";
	public static final String PREF_TOMCAT41_INSTALL_DIR = "tomcat41install";
	public static final String PREF_TOMCAT50_INSTALL_DIR = "tomcat50install";
	public static final String PREF_JDK_INSTALL_DIR = "jdkinstall";

	/**
	 * TomcatUIPlugin constructor comment.
	 */
	public TomcatUIPlugin() {
		super();
		singleton = this;
	}

	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = new ImageRegistry();
		
		registerImage(registry, IMG_WIZ_TOMCAT, URL_WIZBAN + "tomcat_wiz.png");
		
		registerImage(registry, IMG_WEB_MODULE, URL_OBJ + "web_module.gif");
		registerImage(registry, IMG_MIME_MAPPING, URL_OBJ + "mime_mapping.gif");
		registerImage(registry, IMG_MIME_EXTENSION, URL_OBJ + "mime_extension.gif");
		registerImage(registry, IMG_PORT, URL_OBJ + "port.gif");
		registerImage(registry, IMG_PROJECT_MISSING, URL_OBJ + "project_missing.gif");
	
		return registry;
	}

	/**
	 * Return the image with the given key from the image registry.
	 * @param key java.lang.String
	 * @return org.eclipse.jface.parts.IImage
	 */
	public static Image getImage(String key) {
		return getInstance().getImageRegistry().get(key);
	}

	/**
	 * Return the image with the given key from the image registry.
	 * @param key java.lang.String
	 * @return org.eclipse.jface.parts.IImage
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		try {
			getInstance().getImageRegistry();
			return (ImageDescriptor) getInstance().imageDescriptors.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the singleton instance of this plugin.
	 * @return org.eclipse.jst.server.tomcat.internal.TomcatUIPlugin
	 */
	public static TomcatUIPlugin getInstance() {
		return singleton;
	}

	/**
	 * Convenience method for logging.
	 *
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	public static void log(IStatus status) {
		getInstance().getLog().log(status);
	}

	/**
	 * Convenience method to get a Display. The method first checks, if
	 * the thread calling this method has an associated display. If so, this
	 * display is returned. Otherwise the method returns the default display.
	 * 
	 * @return the display
	 */
	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		return display;		
	}	
	
	/**
	 * Convenience method to display an error dialog.
	 * 
	 * @param title title for the dialog or null for default title
	 * @param message primary message to display
	 * @param status reason for the error
	 */
	public static void openError(final String title, final String message, final IStatus status) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				Shell shell = getShell();
				ErrorDialog.openError(shell,
						title != null ? title : Messages.errorDefaultDialogTitle,
						message, status);
			}
		});
	}
	
	public static boolean queryCleanTermination(IServer server) {
		CleanTerminationRunnable tr = new CleanTerminationRunnable(server);
		Display.getDefault().syncExec(tr);
		return tr.shouldTerminate();
	}
	
	public static class CleanTerminationRunnable implements Runnable {
		IServer server;
		boolean terminate;
		
		CleanTerminationRunnable(IServer server) {
			this.server = server;
		}

		public void run() {
			Shell shell = getShell();
			TerminationDialog dialog = new TerminationDialog(shell,
					Messages.cleanTerminateServerDialogTitle,
					NLS.bind(Messages.cleanTerminateServerDialogMessage, server.getName()));
			dialog.open();
			if (dialog.getReturnCode() == IDialogConstants.OK_ID) {
				terminate = true;
			}
		}
	
		boolean shouldTerminate() {
			return terminate;
		}
	}

	/**
	 * Convenience method to get a shell
	 *
	 * @return Shell
	 */
	public static Shell getShell() {
		return getStandardDisplay().getActiveShell();
	}
	
	/**
	 * Register an image with the registry.
	 * @param key java.lang.String
	 * @param partialURL java.lang.String
	 */
	private void registerImage(ImageRegistry registry, String key, String partialURL) {
		if (ICON_BASE_URL == null) {
			String pathSuffix = "icons/";
			ICON_BASE_URL = singleton.getBundle().getEntry(pathSuffix);
		}

		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(ICON_BASE_URL, partialURL));
			registry.put(key, id);
			imageDescriptors.put(key, id);
		} catch (Exception e) {
			Trace.trace(Trace.WARNING, "Error registering image", e);
		}
	}
}
