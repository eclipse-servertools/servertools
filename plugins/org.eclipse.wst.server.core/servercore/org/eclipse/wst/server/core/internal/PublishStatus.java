/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core.internal;

import java.util.*;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.server.core.IPublishStatus;
import org.eclipse.wst.server.core.model.IModule;
/**
 * IStatus used during publishing. It contains the project
 * or configuration that was published as extra information.
 */
public class PublishStatus implements IPublishStatus {
	// Unique identifier of plug-in.
	private String pluginId;

	// Message, localized to the current locale.
	private String message;

	private List children;

	private long time;
	
	private IModule module;
	
	private Throwable throwable;
	
	/**
	 * PublishStatus constructor comment.
	 */
	public PublishStatus(String pluginId, String message, IModule module) {
		super();
		this.pluginId = pluginId;
		this.message = message;
		this.module = module;
	}
	
	/**
	 * PublishStatus constructor comment.
	 */
	public PublishStatus(String pluginId, String message, IModule module, Throwable throwable) {
		this(pluginId, message, module);
		this.throwable = throwable;
	}

	/**
	 * 
	 * @param status org.eclipse.core.runtime.IStatus
	 */
	public void addChild(IStatus status) {
		getChildList().add(status);
	}

	/**
	 * 
	 * @return java.util.List
	 */
	protected List getChildList() {
		if (children == null)
			children = new ArrayList();
		return children;
	}

	/**
	 * Returns a list of status object immediately contained in this
	 * multi-status, or an empty list if this is not a multi-status.
	 *
	 * @return an array of status objects
	 * @see #isMultiStatus
	 */
	public IStatus[] getChildren() {
		int size = getChildList().size();
		IStatus[] status = new IStatus[size];
		getChildList().toArray(status);
		return status;
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
		return throwable;
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
	 * 
	 * @return org.eclipse.core.resources.IProject
	 */
	public IModule getModule() {
		return module;
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
		int sev = IStatus.OK;
	
		Iterator iterator = getChildList().iterator();
		while (iterator.hasNext()) {
			IStatus status = (IStatus) iterator.next();
			sev = Math.max(sev, status.getSeverity());
		}
		return sev;
	}

	/**
	 * 
	 * @return long
	 */
	public long getTime() {
		return time;
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

	/**
	 * 
	 * @param time long
	 */
	public void setTime(long time) {
		this.time = time;
	}

	public String toString() {
		return "PublishStatus [" + getSeverity() + " " + getCode() + " " + getMessage() + "]";
	}
}
