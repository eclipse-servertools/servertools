/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import org.eclipse.core.runtime.IStatus;
/**
 * IStatus used as child of a PublishStatus. It is just a
 * wrapper around another IStatus, used to give information
 * about the contained IStatus.
 */
public class PublishStatusItem implements IStatus {
	// Unique identifier of plug-in.
	private String pluginId;

	// Message, localized to the current locale.
	private String message;

	private IStatus child;

	/**
	 * PublishStatus constructor comment.
	 */
	public PublishStatusItem(String pluginId, String message, IStatus child) {
		super();
		this.pluginId = pluginId;
		this.message = message;
		this.child = child;
	}

	/**
	 * Returns a list of status object immediately contained in this
	 * multi-status, or an empty list if this is not a multi-status.
	 *
	 * @return an array of status objects
	 * @see #isMultiStatus
	 */
	public IStatus[] getChildren() {
		if (child == null)
			return new IStatus[0];
		return new IStatus[] { child };
	}

	/**
	 * Returns the plug-in-specific status code describing the outcome.
	 *
	 * @return plug-in-specific status code
	 */
	public int getCode() {
		return 0;
	}

	/**
	 * Returns the relevant low-level exception, or <code>null</code> if none. 
	 * For example, when an operation fails because of a network communications
	 * failure, this might return the <code>java.io.IOException</code>
	 * describing the exact nature of that failure.
	 *
	 * @return the relevant low-level exception, or <code>null</code> if none
	 */
	public Throwable getException() {
		return null;
	}

	/**
	 * Returns the message describing the outcome.
	 * The message is localized to the current locale.
	 *
	 * @return a localized message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Returns the unique identifier of the plug-in associated with this status
	 * (this is the plug-in that defines the meaning of the status code).
	 *
	 * @return the unique identifier of the relevant plug-in
	 */
	public String getPlugin() {
		return pluginId;
	}

	/**
	 * Returns the severity. The severities are as follows (in
	 * descending order):
	 * <ul>
	 * <li><code>ERROR</code> - a serious error (most severe)</li>
	 * <li><code>WARNING</code> - a warning (less severe)</li>
	 * <li><code>INFO</code> - an informational ("fyi") message (least severe)</li>
	 * <li><code>OK</code> - everything is just fine</li>
	 * </ul>
	 * <p>
	 * The severity of a multi-status is defined to be the maximum
	 * severity of any of its children, or <code>OK</code> if it has
	 * no children.
	 * </p>
	 *
	 * @return the severity: one of <code>OK</code>,
	 *   <code>ERROR</code>, <code>INFO</code>, or <code>WARNING</code>
	 * @see #matches
	 */
	public int getSeverity() {
		if (child == null)
			return IStatus.OK;
		return child.getSeverity();
	}

	/**
	 * Returns whether this status is a multi-status.
	 * A multi-status describes the outcome of an operation
	 * involving multiple operands.
	 * <p>
	 * The severity of a multi-status is derived from the severities
	 * of its children; a multi-status with no children is
	 * <code>OK</code> by definition.
	 * A multi-status carries a plug-in identifier, a status code,
	 * a message, and an optional exception. Clients may treat
	 * multi-status objects in a multi-status unaware way.
	 * </p>
	 *
	 * @return <code>true</code> for a multi-status, 
	 *    <code>false</code> otherwise
	 * @see #getChildren
	 */
	public boolean isMultiStatus() {
		return true;
	}

	/**
	 * Returns whether this status indicates everything is okay
	 * (neither info, warning, nor error).
	 *
	 * @return <code>true</code> if this status has severity
	 *    <code>OK</code>, and <code>false</code> otherwise
	 */
	public boolean isOK() {
		return (getSeverity() == IStatus.OK);
	}

	/**
	 * Returns whether the severity of this status matches the given
	 * specification.
	 *
	 * @param severityMask a mask formed by bitwise or'ing severity mask
	 *    constants (<code>ERROR</code>, <code>WARNING</code>,
	 *    <code>INFO</code>)
	 * @return <code>true</code> if there is at least one match, 
	 *    <code>false</code> if there are no matches
	 * @see #getSeverity
	 * @see #ERROR
	 * @see #WARNING
	 * @see #INFO
	 */
	public boolean matches(int severityMask) {
		return (getSeverity() & severityMask) != 0;
	}
}