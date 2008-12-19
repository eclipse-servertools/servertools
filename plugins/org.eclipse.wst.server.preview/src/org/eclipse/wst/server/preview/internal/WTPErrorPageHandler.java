/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.preview.internal;

import java.io.IOException;
import java.io.Writer;
import java.net.URLDecoder;

import org.mortbay.http.HttpException;
import org.mortbay.http.HttpFields;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.handler.AbstractHttpHandler;
import org.mortbay.util.ByteArrayISO8859Writer;
import org.mortbay.util.StringUtil;

public class WTPErrorPageHandler extends AbstractHttpHandler {
	private static final long serialVersionUID = 1L;

	public void handle(String pathInContext, String pathParams, HttpRequest request,
			HttpResponse response) throws HttpException, IOException {
		response.setContentType(HttpFields.__TextHtml);
		ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer(2048);
		writeErrorPage(request, writer, response.getStatus(), response.getReason());
		
		// workaround for IE, which overrides 404 errors to present its own page
		if (response.getStatus() == 404)
			response.setStatus(200, "OK"); 
		
		writer.flush();
		response.setContentLength(writer.size());
		writer.writeTo(response.getOutputStream());
		writer.destroy();
	}

	protected void writeErrorPage(HttpRequest request, Writer writer, int code,
			String message) throws IOException {
		if (message != null) {
			message = URLDecoder.decode(message, "UTF-8");
			message = StringUtil.replace(message, "<", "&lt;");
			message = StringUtil.replace(message, ">", "&gt;");
		}
		String uri = request.getPath();
		uri = StringUtil.replace(uri, "<", "&lt;");
		uri = StringUtil.replace(uri, ">", "&gt;");
		writer.write("<html>\n<head>\n<title>Error ");
		writer.write(Integer.toString(code));
		writer.write(' ');
		writer.write(message);
		writer.write("</title>\n</head>\n<body>\n<h2>HTTP ERROR: ");
		writer.write(Integer.toString(code));
		writer.write("</h2><pre>");
		writer.write(message);
		writer.write("</pre>\n");
		writer.write("<p>RequestURI=");
		writer.write(uri);
		writer.write("</p>");
		writer.write("\n</body>\n</html>\n");
	}
}