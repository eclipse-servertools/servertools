/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal;

import org.eclipse.ui.IActionFilter;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
/**
 * An action filter for requests.
 */
public class RequestActionFilter implements IActionFilter{
	public static final String REQUEST_SENT = "requestsent";

	/**
	 * @see IActionFilter#testAttribute(Object, String, String)
	 */
	public boolean testAttribute(Object target, String name, String value) {
		Request request = (Request) target;
		if (name.equals(REQUEST_SENT)) {
			if (value.equals("true"))
				return request.getResponseTime() != -1;
			
			return request.getResponseTime() == -1;
		}
		return false;
	}
}