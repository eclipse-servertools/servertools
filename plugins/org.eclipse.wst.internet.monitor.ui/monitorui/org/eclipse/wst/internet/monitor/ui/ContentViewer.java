/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui;

import org.eclipse.swt.widgets.Composite;
/**
 * Editable viewer for displaying and editing requests and responses.
 * <p>
 * This abstract class is intended to be subclassed only by clients
 * to extend the <code>contentViewers</code> extension point.
 * The subclass must have a public 0-argument constructor, which will be used
 * automatically to instantiate the viewer when required.
 * Once instantiated, {@link #init(Composite)} is called to create
 * the viewer's controls. Then {@link #setContent(byte[])} is called,
 * one or more times, to cause the viewer to display particular
 * content. Finally, when the viewer is no longer needed, {@link #dispose()}
 * is called to free up any resources tied up by the viewer.
 * </p>
 * <p>
 * [issue: It's unclear how these content viewers are used.
 * Is it just that the UI provides a list of available
 * views for the user to make a manual selection from, regardless
 * of the actual content of the message traffic?]
 * </p>
 * <p>
 * [issue: Unless you have clear and pressing requirements for
 * pluggable content viewers (i.e., besides the ones that you
 * would be able to build in), I suggest you make 
 * the <code>contentViewers</code> extension point and this
 * class internal. You can always expose it later.]
 * </p>
 * <p>
 * [issue: Every content viewer should have getEditable,
 * setEditable, and getContent.
 * Particular content viewer implementation must implement
 * getContent, and decide whether instances are considered
 * editable.]
 * </p>
 * 
 * @since 1.0
 */
public abstract class ContentViewer {
	/**
	 * Creates a new content viewer instance.
	 */
	protected ContentViewer() {
		// do nothing
	}

	/**
	 * Creates the controls for this viewer as children of the given composite.
	 * <p>
	 * The default implementation of this method does nothing.
	 * Subclasses should override this method.
	 * </p>
	 * 
	 * @param parent the parent composite
	 */
	public abstract void init(Composite parent);

	/**
	 * Sets the content that the viewer should display.
	 * The parameter may be null if no content should be displayed.
	 * <p>
	 * The default implementation of this method does nothing.
	 * Subclasses should override this method.
	 * </p>
	 * <p>
	 * [issue: Since this is for displaying request-reponse messages,
	 * why not pass an IRequest? The problem as it stands now is that
	 * it is unclear from specs how content viewers relate to anything
	 * else having to do with the monitor.]
	 * </p>
	 * 
	 * @param b the content to display, or <code>null</code> to
	 * display nothing
	 */
	public abstract void setContent(byte[] b);

	/**
	 * Disposes this viewer and any underlying resources such as a composite.
	 * <p>
	 * The default implementation of this method does nothing.
	 * Subclasses should override this method to provide specialized cleanup.
	 * </p>
	 * <p>
	 * [issue: The spec needs to be clarified as to who's expected to
	 * do what. It's not clear when this is called. In general, the implementations
	 * seem to assume that they can dispose of the parent
	 * composite. This seems backwards, and wrong. I don't think
	 * the viewer should assume that it's the only child of the
	 * parent composite (even if it is).]
	 * </p>
	 */
	public void dispose() {
		// do nothing
	}

	/**
	 * Set whether the current viewer is editable, that is, the user is able to
	 * edit the content.
	 * <p>
	 * The default implementation of this method does nothing.
	 * Subclasses should override this method to allows instances
	 * to be made editable.
	 * </p>
	 * 
	 * @param editable <code>true</code> if true the content can be edited,
	 *    and <code>false</code> otherwise
	 */
	public void setEditable(boolean editable) {
		// do nothing
	}

	/**
	 * Get the content from the viewer. This is usually only interesting if the
	 * content has changed.
	 * <p>
	 * The default implementation of this method does nothing and
	 * returns null. Subclasses should override this method.
	 * </p>
	 * 
	 * @return the content from the viewer, or <code>null</code> if none
	 */
	public abstract byte[] getContent();
}