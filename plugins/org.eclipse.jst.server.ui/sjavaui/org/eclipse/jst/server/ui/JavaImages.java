package org.eclipse.jst.server.ui;
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jst.server.internal.ui.ImageResource;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public class JavaImages {
	// Java images
	public static final String IMG_JAVA_CLASSPATH_JAR = ImageResource.IMG_JAVA_CLASSPATH_JAR;
	public static final String IMG_JAVA_CLASSPATH_VAR = ImageResource.IMG_JAVA_CLASSPATH_JAR;
	public static final String IMG_JAVA_SYSTEM_PROPERTY = ImageResource.IMG_JAVA_CLASSPATH_JAR;

	/**
	 * Return the image with the given key.
	 *
	 * @param key java.lang.String
	 * @return org.eclipse.swt.graphics.Image
	 */
	public static Image getImage(String key) {
		return ImageResource.getImage(key);
	}

	/**
	 * Return the image descriptor with the given key.
	 *
	 * @param key java.lang.String
	 * @return org.eclipse.jface.resource.ImageDescriptor
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		return ImageResource.getImageDescriptor(key);
	}
}