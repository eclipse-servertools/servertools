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
package org.eclipse.wst.internet.monitor.core;

import java.util.Date;

import org.eclipse.core.runtime.IAdaptable;
/**
 * Represents a request made between the client and the server.
 * <p>
 * Requests are created by a running monitor. They do not have a reference
 * back to the monitor because the monitor may have been deleted or modified
 * since the request was created.
 * </p>
 * <p>
 * [issue: Would it be fair to say that these are TCP/IP requests?]
 * [issue: Is this always a request-response pair where the request
 * went from client to server, and the response from server to client?
 * Or are there a separate IRequest objects for responses?]
 * </p>
 * <p>
 * [issue: Since the intent is that someone should be able to define 
 * a new protocol adapter, that party will need to be able to create
 * IRequest objects and register them along with the other. Currently,
 * there is no way to do this at the API. The class Request
 * is the only code that knows about the MonitorManager (also internal)
 * that maintains the global list of requests. But Request is itself internal,
 * meaning that the protocol adapter provider would be out of luck.
 * The wisdom is: If you contribute an extension to your own extension
 * point, and the intent is that other can do likewise, then it's important to
 * write your extensions following the same rules you're expect others to
 * follow.
 * ]
 * </p>
 * <p>
 * This interface is intended to be implemented only by clients
 * to extend the <code>protocolAdapters</code> extension point. 
 * </p>
 * <p>
 * [issue: It would probably be better for the API to provide a concrete, final
 * Request class that the protocol adapter delegate would simply
 * instantiate.]
 * </p>
 * 
 * @since 1.0
 */
public interface IRequest extends IAdaptable {
	/**
	 * Request content type (value 1) for the transport (header) of a request
	 * or response.
	 * 
	 * @see #getRequest(int)
	 * @see #getResponse(int)
	 */
	public static final int TRANSPORT = 1;
	
	/**
	 * Request content type (value 2) for the content (body) of a request
	 * or response.
	 * 
	 * @see #getRequest(int)
	 * @see #getResponse(int)
	 */
	public static final int CONTENT = 2;
	
	/**
	 * Request content type (value 3) for the entire content of a request
	 * or response.
	 * 
	 * @see #getRequest(int)
	 * @see #getResponse(int)
	 */
	public static final int ALL = 3;

	/**
	 * Returns the protocol responsible for creating this request.
	 * <p>
	 * [issue: If the response was linked to the monitor that's listening to
	 * the conversation, this method would not be necessary.]
	 * </p>
	 * 
	 * @return the protocol id
	 */
	public String getProtocol();

	/**
	 * Returns the time this request was made.
	 *
	 * @return the timestamp
	 */
	public Date getDate();

	/**
	 * Returns the local (client) port.
	 *
	 * @return the local port number
	 */
	public int getLocalPort();

	/**
	 * Returns the remote (server) host.
	 *
	 * @return the remote host
	 */
	public String getRemoteHost();

	/**
	 * Returns the remote (server) port.
	 *
	 * @return the remote port number
	 */
	public int getRemotePort();

	/**
	 * Returns the selected content of the request portion of this request.
	 * <p>
	 * [issue: I don't know how to explain this. For basic TCP/IP requests,
	 * distinction between transport and content is ignored.
	 * For HTTP requests, this TRANSPORT returns just the HTTP header and 
	 * CONTENT returns just the HTTP body without the headers. What would
	 * it mean for other protocols?
	 * </p>
	 *
	 * @param type the content type: one of {@link #TRANSPORT},
	 * {@link #CONTENT}, or {@link #ALL}
	 * @return the content bytes
	 */
	public byte[] getRequest(int type);

	/**
	 * Returns the selected content of the response portion of this request.
	 * <p>
	 * [issue: I don't know how to explain this. For basic TCP/IP requests,
	 * distinction between transport and content is ignored.
	 * For HTTP requests, this TRANSPORT returns just the HTTP header and 
	 * CONTENT returns just the HTTP body without the headers. What would
	 * it mean for other protocols?]
	 * </p>
	 *
	 * @param type the content type: one of {@link #TRANSPORT},
	 * {@link #CONTENT}, or {@link #ALL}
	 * @return the content bytes
	 */
	public byte[] getResponse(int type);

	/**
	 * Returns the server's response time in milliseconds. If the request
	 * has not been completed yet, -1 is returned.
	 *
	 * @return the server's response time, or -1 if there has been no
	 *    response yet
	 */
	public long getResponseTime();

	/**
	 * Returns a label for this request.
	 * <p>
	 * [issue: At the core level, these objects probably should
	 * not have labels or anything like that. They are just objects.
	 * The current implementation uses getHost()+":"+getPort(),
	 * which is not even unique between requests.
	 * If a client needs a label, it is better to let them compute
	 * one. This method should be deleted.]
	 * </p>
	 *
	 * @return the label
	 */
	public String getLabel();

	/**
	 * Sets the given key-value property on this request. To remove a property,
	 * set the value to null.
	 * <p>
	 * [issue: Is this supposed to be called by the protocol
	 * adapter (only)?]
	 * </p>
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void setProperty(String key, Object value);

	/**
	 * Returns the value of the property with the given key from this request.
	 * If the key does not exist, <code>null</code> is returned.
	 * 
	 * @param key the property key 
	 * @return the property value
	 */
	public Object getProperty(String key);

	/**
	 * Hook to allow other plug-ins that implement IRequest to fire a change event.
	 * After the internal values have changed, call this method to invoke a request
	 * change event to all registered listeners.
	 * <p>
	 * [issue: This doesn't make sense as a method on an interface, since
	 * anyone implementing this interface themselves would be unable to do
	 * anything meaningful. It only makes sense in a world where the protocol
	 * adapter is subclassing or instantiating a class that provides a
	 * built-in implementation of this method.]
	 * </p>
	 */
	//public void fireChangedEvent();

	/**
	 * Adds a resend request to this request.
	 * <p>
	 * [issue: Explain what a resend request is and how they
	 * would be used. Who is expect to call this method? The protocol adapter
	 * delegate? If yes, why does it need to be exposed at API?]
	 * </p>
	 * 
	 * @param request the resend request to add
	 */
	public void addResendRequest(IRequest request);

	/**
	 * Returns an array of resend requests based on this request. 
	 * <p>
	 * [issue: Explain what a resend request is and how they
	 * would be used. Who is expect to call this method? The protocol adapter
	 * delegate? If yes, why does it need to be exposed at API?]
	 * </p>
	 * 
	 * @return the array of resend requests based on this request
	 */
	public IResendRequest[] getResendRequests();
}