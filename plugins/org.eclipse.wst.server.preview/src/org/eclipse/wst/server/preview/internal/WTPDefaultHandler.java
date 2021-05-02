/*******************************************************************************
 * Copyright (c) 2008, 2021 IBM Corporation and others.
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
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;
import org.eclipse.jetty.util.StringUtil;

public class WTPDefaultHandler extends AbstractHandler {
	protected int port;
	protected Module[] modules;

	public WTPDefaultHandler(int port, Module[] modules) {
		this.port = port;
		this.modules = modules;
	}

	public void handle(String target, Request baseRequest, HttpServletRequest request,
      HttpServletResponse response) throws IOException, ServletException {
		if (response.isCommitted() || baseRequest.isHandled())
			return;
		baseRequest.setHandled(true);
		
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setContentType(MimeTypes.Type.TEXT_HTML.toString());
		
		try (ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(1500)) {
		
			String uri = request.getRequestURI();
			uri = StringUtil.replace(uri, "<", "&lt;");
			uri = StringUtil.replace(uri, ">", "&gt;");
			
			writer.write("<HTML>\n<HEAD>\n<TITLE>Error 404 - Not Found");
			writer.write("</TITLE>\n<BODY>\n<H2>Error 404 - Not Found</H2>\n");
			writer.write("No context on this server matched or handled this request.<BR>");
			writer.write("Contexts known to this server are: <ul>");
			
			for (Module module : modules) {
				writer.write("<li>");
				writer.write(module.getName());
				writer.write("(<a href=\"http://localhost:" + port + module.getContext() + "\">");
				writer.write(module.getContext());
				writer.write("</a>)</li>");
			}
			
			for (int i = 0; i < 10; i++) {
				writer.write("\n<!-- Padding for IE                  -->");
			}
			
			writer.write("\n</BODY>\n</HTML>\n");
			writer.flush();
			response.setContentLength(writer.size());
			OutputStream out = response.getOutputStream();
			writer.writeTo(out);
			out.close();
		}
	}

}
