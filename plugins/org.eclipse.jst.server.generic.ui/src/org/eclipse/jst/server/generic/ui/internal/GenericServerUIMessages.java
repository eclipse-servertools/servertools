package org.eclipse.jst.server.generic.ui.internal;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Helper class to get messages
 * 
 * @author Gorkem Ercan
 */
public class GenericServerUIMessages {

	private static final String RESOURCE_BUNDLE= "org.eclipse.jst.server.generic.ui.internal.GenericServerUIMessages";//$NON-NLS-1$

	private static ResourceBundle fResourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

	private GenericServerUIMessages() {
	}

	/**
	 * Return string from the resource bundle.
	 * 
	 * @param key the string used to get the bundle value, must not be <code>null</code>
	 * @return the string from the resource bundle
	 */
	public static String getString(String key) {
		try {
			return fResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";
		}
	}
	
	/**
	 * Gets a string from the resource bundle that is 
	 * formatted it with the given argument.
	 * 
	 * @param key the string used to get the bundle value, must not be null
	 * @param arg the argument used to format the string
	 * @return the formatted string
	 */
	public static String getFormattedString(String key, Object[] arg) {
		String format= null;
		try {
			format= fResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return "!" + key + "!";
		}
		if (arg == null)
			arg= new Object[0];
		return MessageFormat.format(format,arg );
	}
	
	/**
	 * Returns a resource bundle.
	 * 
	 * @return the resource bundle
	 */
	public static ResourceBundle getResourceBundle() {
		return fResourceBundle;
	}
}
