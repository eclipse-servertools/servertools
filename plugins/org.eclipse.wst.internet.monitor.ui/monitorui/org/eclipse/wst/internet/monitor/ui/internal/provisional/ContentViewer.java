/**********************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.provisional;

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
 * [issue: CS - If a ContentViewer is editable, does that mean we can edit the 
 * bytes of the message and resend it across the wire?  How is an editable ContentViewer
 * handled differently by the framework? Perhaps an example of what an editeable viewer would do. ]
 * @plannedfor 1.0
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
	 * why not pass an Request? The problem as it stands now is that
	 * it is unclear from specs how content viewers relate to anything
	 * else having to do with the monitor.]
	 * </p>
	 * 
	 * @param b the content to display, or <code>null</code> to
	 * display nothing
	 */
	public abstract void setContent(byte[] b);

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

	/**
	 * Disposes this viewer and any underlying resources such as a composite.
	 * This method will be called whenever the user switches to use another
	 * viewer or the monitor view is closed. The parent composite should not
	 * be disposed since it may be used to display another viewer. 
	 * <p>
	 * The default implementation of this method does nothing.
	 * Subclasses should override this method to provide specialized cleanup.
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
	 * Returns whether the current viewer is editable, that is, the user is able to
	 * edit the content.
	 * <p>
	 * The default implementation of this method does nothing.
	 * Subclasses should override this method to allows instances
	 * to be made editable.
	 * </p>
	 * 
	 * @return <code>true</code> if true the content can be edited,
	 *    and <code>false</code> otherwise
	 */
	public boolean getEditable() {
		return false;
	}
}