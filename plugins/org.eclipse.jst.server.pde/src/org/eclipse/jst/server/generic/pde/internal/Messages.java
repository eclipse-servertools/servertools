package org.eclipse.jst.server.generic.pde.internal;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.jst.server.generic.pde.internal.messages"; //$NON-NLS-1$

	private Messages() {
	}

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String labelJavaLaunchConfiguration;

	public static String labelExternalLaunchConfiguration;

	public static String labelServerName;

	public static String labelServerVendor;

	public static String labelServerVesion;

	public static String labelLaunchType;

	public static String labelStartBeforePublish;

	public static String pageTitle;

	public static String pageDescription;

	public static String windowTitleWizard;
}
