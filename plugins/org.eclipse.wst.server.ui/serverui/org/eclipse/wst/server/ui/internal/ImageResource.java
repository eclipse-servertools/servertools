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
package org.eclipse.wst.server.ui.internal;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionDelta;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * Utility class to handle image resources.
 */
public class ImageResource {
	// the image registry
	private static ImageRegistry imageRegistry;

	// map of image descriptors since these
	// will be lost by the image registry
	private static Map<String, ImageDescriptor> imageDescriptors;

	// base urls for images
	private static URL ICON_BASE_URL;

	static {
		try {
			String pathSuffix = "icons/";
			ICON_BASE_URL = ServerUIPlugin.getInstance().getBundle().getEntry(pathSuffix);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not set icon base URL", e);
		}
	}

	private static final String URL_CLCL = "clcl16/";
	private static final String URL_CTOOL = "ctool16/";
	
	private static final String URL_ELCL = "elcl16/";
	private static final String URL_ETOOL = "etool16/";
	
	private static final String URL_DLCL = "dlcl16/";
	private static final String URL_DTOOL = "dtool16/";

	private static final String URL_OBJ = "obj16/";
	
	private static final String URL_OVR = "ovr16/";

	private static final String URL_WIZBAN = "wizban/";

	// --- constants for images ---

	// Server State Images
	public static final String IMG_CLCL_START = "IMG_CLCL_START";
	public static final String IMG_CLCL_START_DEBUG = "IMG_CLCL_START_DEBUG";
	public static final String IMG_CLCL_START_PROFILE = "IMG_CLCL_START_PROFILE";
	public static final String IMG_CLCL_STOP = "IMG_CLCL_STOP";
	public static final String IMG_CLCL_PUBLISH = "IMG_CLCL_PUBLISH";
	public static final String IMG_CLCL_DISCONNECT = "IMG_CLCL_DISCONNECT";

	public static final String IMG_ELCL_START = "IMG_ELCL_START";
	public static final String IMG_ELCL_START_DEBUG = "IMG_ELCL_START_DEBUG";
	public static final String IMG_ELCL_START_PROFILE = "IMG_ELCL_START_PROFILE";
	public static final String IMG_ELCL_STOP = "IMG_ELCL_STOP";
	public static final String IMG_ELCL_PUBLISH = "IMG_ELCL_PUBLISH";
	public static final String IMG_ELCL_DISCONNECT = "IMG_ELCL_DISCONNECT";

	public static final String IMG_DLCL_START = "IMG_DLCL_START";
	public static final String IMG_DLCL_START_DEBUG = "IMG_DLCL_START_DEBUG";
	public static final String IMG_DLCL_START_PROFILE = "IMG_DLCL_START_PROFILE";
	public static final String IMG_DLCL_STOP = "IMG_DLCL_STOP";
	public static final String IMG_DLCL_PUBLISH = "IMG_DLCL_PUBLISH";
	public static final String IMG_DLCL_DISCONNECT = "IMG_DLCL_DISCONNECT";

	// Wizard Banner Images
	public static final String IMG_WIZBAN_NEW_RUNTIME = "newServerWiz";
	public static final String IMG_WIZBAN_NEW_SERVER = "newServerWiz";
	public static final String IMG_WIZBAN_SELECT_SERVER_CLIENT = "wizClient";
	public static final String IMG_WIZBAN_SELECT_SERVER = "selectServer";
	public static final String IMG_WIZBAN_IMPORT_SERVER_CONFIGURATION = "importConfigWiz";

	public static final String IMG_SERVER_STATE_STARTED = "stateStarted";
	public static final String IMG_SERVER_STATE_STARTED_DEBUG = "stateStartedDebug";
	public static final String IMG_SERVER_STATE_STARTED_PROFILE = "stateStartedProfile";
	public static final String IMG_SERVER_STATE_STOPPED = "stateStopped";
	
	public static final String IMG_STATE_STARTED = "stateStarted2";
	public static final String IMG_STATE_STOPPED = "stateStopped2";
	
	public static final String IMG_SERVER_STATE_STARTING_1 = "stateStarting1";
	public static final String IMG_SERVER_STATE_STARTING_2 = "stateStarting2";
	public static final String IMG_SERVER_STATE_STARTING_3 = "stateStarting3";
	
	public static final String IMG_SERVER_STATE_STOPPING_1 = "stateStopping1";
	public static final String IMG_SERVER_STATE_STOPPING_2 = "stateStopping2";
	public static final String IMG_SERVER_STATE_STOPPING_3 = "stateStopping3";

	// Server Client Images
	public static final String IMG_CTOOL_RUN_ON_SERVER = "IMG_CTOOL_CLIENT";
	public static final String IMG_CTOOL_DEBUG_ON_SERVER = "IMG_CTOOL_CLIENT2";
	public static final String IMG_CTOOL_PROFILE_ON_SERVER = "IMG_CTOOL_CLIENT3";
	public static final String IMG_CTOOL_NEW_SERVER = "IMG_CTOOL_NEW_SERVER";
	public static final String IMG_CTOOL_NEW_SERVER_INSTANCE = "IMG_CTOOL_NEW_SERVER_INSTANCE";
	public static final String IMG_CTOOL_MODIFY_MODULES = "IMG_CTOOL_MODIFY_MODULES";

	public static final String IMG_ETOOL_RUN_ON_SERVER = "IMG_ETOOL_CLIENT";
	public static final String IMG_ETOOL_DEBUG_ON_SERVER = "IMG_ETOOL_CLIENT2";
	public static final String IMG_ETOOL_PROFILE_ON_SERVER = "IMG_ETOOL_CLIENT3";
	public static final String IMG_ETOOL_MODIFY_MODULES = "IMG_ETOOL_MODIFY_MODULES";
	public static final String IMG_ETOOL_RESET_DEFAULT = "IMG_ETOOL_RESET_DEFAULT";

	public static final String IMG_DTOOL_RUN_ON_SERVER = "IMG_DTOOL_CLIENT";
	public static final String IMG_DTOOL_DEBUG_ON_SERVER = "IMG_DTOOL_CLIENT2";
	public static final String IMG_DTOOL_PROFILE_ON_SERVER = "IMG_DTOOL_CLIENT3";
	public static final String IMG_DTOOL_MODIFY_MODULES = "IMG_DTOOL_MODIFY_MODULES";
	public static final String IMG_DTOOL_RESET_DEFAULT = "IMG_DTOOL_RESET_DEFAULT";

	// General Object Images
	public static final String IMG_SERVER = "server";
	public static final String IMG_SERVER_CONFIGURATION_NONE = "noConfiguration";
	public static final String IMG_SERVER_CONFIGURATION_MISSING = "configurationMissing";
	public static final String IMG_PROJECT_MISSING = "projectMissing";
	public static final String IMG_REPAIR_CONFIGURATION = "repairConfiguration";

	public static final String IMG_PUBLISH_ENABLED = "publishEnabled";
	public static final String IMG_PUBLISH_DISABLED = "publishDisabled";
	
	public static final String IMG_MONITOR_ON = "monitorOn";
	public static final String IMG_MONITOR_OFF = "monitorOff";
	
	public static final String IMG_DEFAULT_SERVER_OVERLAY = "defaultServerOverlay";
	
	// Audio images
	public static final String IMG_AUDIO_SOUND = "sound";
	public static final String IMG_AUDIO_CATEGORY = "category";

	/**
	 * Cannot construct an ImageResource. Use static methods only.
	 */
	private ImageResource() {
		// do nothing
	}
	
	/**
	 * Dispose of element images that were created.
	 */
	protected static void dispose() {
		// do nothing
	}

	/**
	 * Return the image with the given key.
	 *
	 * @param key java.lang.String
	 * @return org.eclipse.swt.graphics.Image
	 */
	public static Image getImage(String key) {
		if (imageRegistry == null)
			initializeImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null) {
			imageRegistry.put(key, ImageDescriptor.getMissingImageDescriptor());
			image = imageRegistry.get(key);
		}
		return image;
	}

	/**
	 * Return the image descriptor with the given key.
	 *
	 * @param key java.lang.String
	 * @return org.eclipse.jface.resource.ImageDescriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		if (imageRegistry == null)
			initializeImageRegistry();
		ImageDescriptor id = imageDescriptors.get(key);
		if (id != null)
			return id;
		
		return ImageDescriptor.getMissingImageDescriptor();
	}

	/**
	 * Initialize the image resources.
	 */
	protected static void initializeImageRegistry() {
		imageRegistry = new ImageRegistry();
		imageDescriptors = new HashMap<String, ImageDescriptor>();

		// wizard banners
		registerImage(IMG_WIZBAN_NEW_SERVER, URL_WIZBAN + "new_server_wiz.png");
		registerImage(IMG_WIZBAN_IMPORT_SERVER_CONFIGURATION, URL_WIZBAN + "import_configuration_wiz.png");
		registerImage(IMG_WIZBAN_SELECT_SERVER_CLIENT, URL_WIZBAN + "select_client_wiz.png");
		registerImage(IMG_WIZBAN_SELECT_SERVER, URL_WIZBAN + "select_server_wiz.png");

		// client images
		registerImage(IMG_ETOOL_RUN_ON_SERVER, URL_ETOOL + "run_on_server.gif");
		registerImage(IMG_ETOOL_DEBUG_ON_SERVER, URL_ETOOL + "debug_on_server.gif");
		registerImage(IMG_ETOOL_PROFILE_ON_SERVER, URL_ETOOL + "profile_on_server.gif");
		registerImage(IMG_ETOOL_MODIFY_MODULES, URL_ETOOL + "wiz_modify_modules.gif");
		registerImage(IMG_ETOOL_RESET_DEFAULT, URL_ETOOL + "clear.gif");

		registerImage(IMG_CTOOL_RUN_ON_SERVER, URL_CTOOL + "run_on_server.gif");
		registerImage(IMG_CTOOL_DEBUG_ON_SERVER, URL_CTOOL + "debug_on_server.gif");
		registerImage(IMG_CTOOL_PROFILE_ON_SERVER, URL_CTOOL + "profile_on_server.gif");
		registerImage(IMG_CTOOL_NEW_SERVER, URL_CTOOL + "wiz_new_server.gif");
		registerImage(IMG_CTOOL_NEW_SERVER_INSTANCE, URL_CTOOL + "wiz_new_instance.gif");
		registerImage(IMG_CTOOL_MODIFY_MODULES, URL_CTOOL + "wiz_modify_modules.gif");

		registerImage(IMG_DTOOL_RUN_ON_SERVER, URL_DTOOL + "run_on_server.gif");
		registerImage(IMG_DTOOL_DEBUG_ON_SERVER, URL_DTOOL + "debug_on_server.gif");
		registerImage(IMG_DTOOL_PROFILE_ON_SERVER, URL_DTOOL + "profile_on_server.gif");
		registerImage(IMG_DTOOL_MODIFY_MODULES, URL_DTOOL + "wiz_modify_modules.gif");
		registerImage(IMG_DTOOL_RESET_DEFAULT, URL_DTOOL + "clear.gif");
	
		// load server state images
		registerImage(IMG_SERVER_STATE_STARTED, URL_OBJ + "server_started.gif");
		registerImage(IMG_SERVER_STATE_STARTED_DEBUG, URL_OBJ + "server_started_debug.gif");
		registerImage(IMG_SERVER_STATE_STARTED_PROFILE, URL_OBJ + "server_started_profile.gif");
		registerImage(IMG_SERVER_STATE_STOPPED, URL_OBJ + "server_stopped.gif");
		
		registerImage(IMG_STATE_STARTED, URL_OBJ + "state_started.gif");
		registerImage(IMG_STATE_STOPPED, URL_OBJ + "state_stopped.gif");
		
		registerImage(IMG_SERVER_STATE_STARTING_1, URL_OBJ + "server_starting1.gif");
		registerImage(IMG_SERVER_STATE_STARTING_2, URL_OBJ + "server_starting2.gif");
		registerImage(IMG_SERVER_STATE_STARTING_3, URL_OBJ + "server_starting3.gif");
		
		registerImage(IMG_SERVER_STATE_STOPPING_1, URL_OBJ + "server_stopping1.gif");
		registerImage(IMG_SERVER_STATE_STOPPING_2, URL_OBJ + "server_stopping2.gif");
		registerImage(IMG_SERVER_STATE_STOPPING_3, URL_OBJ + "server_stopping3.gif");
	
		// load action images
		registerImage(IMG_ELCL_PUBLISH, URL_ELCL + "launch_publish.gif");
		registerImage(IMG_ELCL_START, URL_ELCL + "launch_run.gif");
		registerImage(IMG_ELCL_START_DEBUG, URL_ELCL + "launch_debug.gif");
		registerImage(IMG_ELCL_START_PROFILE, URL_ELCL + "launch_profile.gif");
		registerImage(IMG_ELCL_STOP, URL_ELCL + "launch_stop.gif");
		registerImage(IMG_ELCL_DISCONNECT, URL_ELCL + "launch_disconnect.gif");
	
		registerImage(IMG_CLCL_PUBLISH, URL_CLCL + "launch_publish.gif");
		registerImage(IMG_CLCL_START, URL_CLCL + "launch_run.gif");
		registerImage(IMG_CLCL_START_DEBUG, URL_CLCL + "launch_debug.gif");
		registerImage(IMG_CLCL_START_PROFILE, URL_CLCL + "launch_profile.gif");
		registerImage(IMG_CLCL_STOP, URL_CLCL + "launch_stop.gif");
		registerImage(IMG_CLCL_DISCONNECT, URL_CLCL + "launch_disconnect.gif");
	
		registerImage(IMG_DLCL_PUBLISH, URL_DLCL + "launch_publish.gif");
		registerImage(IMG_DLCL_START, URL_DLCL + "launch_run.gif");
		registerImage(IMG_DLCL_START_DEBUG, URL_DLCL + "launch_debug.gif");
		registerImage(IMG_DLCL_START_PROFILE, URL_DLCL + "launch_profile.gif");
		registerImage(IMG_DLCL_STOP, URL_DLCL + "launch_stop.gif");
		registerImage(IMG_DLCL_DISCONNECT, URL_DLCL + "launch_disconnect.gif");
	
		// load general object images
		registerImage(IMG_SERVER, URL_OBJ + "server.gif");
		registerImage(IMG_SERVER_CONFIGURATION_NONE, URL_OBJ + "configuration_none.gif");
		registerImage(IMG_SERVER_CONFIGURATION_MISSING, URL_OBJ + "configuration_missing.gif");
		registerImage(IMG_PROJECT_MISSING, URL_OBJ + "project_missing.gif");
		registerImage(IMG_REPAIR_CONFIGURATION, URL_OBJ + "repair_config.gif");
	
		registerImage(IMG_PUBLISH_ENABLED, URL_OBJ + "publish_enabled.gif");
		registerImage(IMG_PUBLISH_DISABLED, URL_OBJ + "publish_disabled.gif");
		
		registerImage(IMG_MONITOR_ON, URL_OBJ + "monitorOn.gif");
		registerImage(IMG_MONITOR_OFF, URL_OBJ + "monitorOff.gif");
		
		registerImage(IMG_DEFAULT_SERVER_OVERLAY, URL_OVR + "default_server_ovr.gif");
		
		// audio images
		registerImage(IMG_AUDIO_SOUND, URL_OBJ + "audio_sound.gif");
		registerImage(IMG_AUDIO_CATEGORY, URL_OBJ + "audio_category.gif");
		
		loadServerImages();
		
		PlatformUI.getWorkbench().getProgressService().registerIconForFamily(
				getImageDescriptor(IMG_SERVER), ServerUtil.SERVER_JOB_FAMILY);
	}

	/**
	 * Register an image with the registry.
	 *
	 * @param key java.lang.String
	 * @param partialURL java.lang.String
	 */
	private static void registerImage(String key, String partialURL) {
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(ICON_BASE_URL, partialURL));
			imageRegistry.put(key, id);
			imageDescriptors.put(key, id);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error registering image " + key + " from " + partialURL, e);
		}
	}

	/**
	 * Load the server images.
	 */
	private static void loadServerImages() {
		Trace.trace(Trace.CONFIG, "->- Loading .serverImages extension point ->-");
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		loadServerImages(registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, ServerUIPlugin.EXTENSION_SERVER_IMAGES));
		ServerUIPlugin.addRegistryListener();
		Trace.trace(Trace.CONFIG, "-<- Done loading .serverImages extension point -<-");
	}

	/**
	 * Load the server images.
	 */
	private static void loadServerImages(IConfigurationElement[] cf) {
		int size = cf.length;
		for (int i = 0; i < size; i++) {
			try {
				String name = cf[i].getDeclaringExtension().getContributor().getName();
				String iconPath = cf[i].getAttribute("icon");
				ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(name, iconPath);
				if (imageDescriptor == null && iconPath != null && iconPath.length() > 0)
					imageDescriptor = ImageDescriptor.getMissingImageDescriptor();
				
				if (imageDescriptor != null) {
					String[] typeIds = ServerUIPlugin.tokenize(cf[i].getAttribute("typeIds"), ",");
					int size2 = typeIds.length;
					for (int j = 0; j < size2; j++) {
						imageRegistry.put(typeIds[j], imageDescriptor);		
						imageDescriptors.put(typeIds[j], imageDescriptor);
					}
				}
				Trace.trace(Trace.CONFIG, "  Loaded serverImage: " + cf[i].getAttribute("id"));
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "  Could not load serverImage: " + cf[i].getAttribute("id"), t);
			}
		}
	}

	protected static void handleServerImageDelta(IExtensionDelta delta) {
		if (imageRegistry == null) // not loaded yet
			return;
		
		IConfigurationElement[] cf = delta.getExtension().getConfigurationElements();
		
		if (delta.getKind() == IExtensionDelta.ADDED)
			loadServerImages(cf);
		else {
			int size = cf.length;
			for (int i = 0; i < size; i++) {
				String typeId = cf[i].getAttribute("typeIds");
				imageRegistry.remove(typeId);
				imageDescriptors.remove(typeId);
			}
		}
	}
}