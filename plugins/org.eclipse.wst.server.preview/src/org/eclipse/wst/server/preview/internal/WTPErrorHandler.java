/*******************************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
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
package org.eclipse.wst.server.preview.internal;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.util.StringUtil;

public class WTPErrorHandler extends ErrorHandler {

  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		super.handle(target, baseRequest, request, response);
		baseRequest.setHandled(true);
	}

	protected void writeErrorPageBody(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks)
   	throws IOException {
		String uri = request.getRequestURI();
		if (uri != null) {
			uri = StringUtil.replace(uri, "&", "&amp;");
			uri = StringUtil.replace(uri, "<", "&lt;");
			uri = StringUtil.replace(uri, ">", "&gt;");
		}
		
		writeErrorPageMessage(request, writer, code, message, uri);
		if (showStacks)
			writeErrorPageStacks(request, writer);
		
		for (int i = 0; i < 20; i++)
			writer.write("<br/>                                                \n");
	}
}
