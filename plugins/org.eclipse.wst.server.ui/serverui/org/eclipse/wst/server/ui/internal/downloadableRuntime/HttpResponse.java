/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.downloadableRuntime;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.wst.server.ui.internal.Trace;

public class HttpResponse {
	protected URLConnection connection;

	protected URL url;

	protected InputStream in;

	protected long lastModified;

	protected long offset;

	protected HttpResponse(URL url) {
		this.url = url;
	}

	protected URLConnection getConnection() throws IOException {
		if (connection == null)
			connection = url.openConnection();
		//if (offset > 0)
		//	connection.setRequestProperty("Range", "bytes=" + offset + "-");
		return connection;
	}

	public InputStream getInputStream() throws IOException {
		if (in == null && url != null) {
			if (connection == null || offset > 0)
				connection = getConnection();
			if (offset > 0)
				connection.setRequestProperty("Range", "bytes=" + offset + "-");
			try {
				in = connection.getInputStream();
			} catch (IOException ioe) {
				connection = null;
				throw ioe;
			}
			//checkOffset();
		}
		return in;
	}

	public void close() {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				// ignore
			}
			in = null;
		}
		if (connection != null) {
			((HttpURLConnection) connection).disconnect();
			connection = null;
		}
	}

	public long getContentLength() {
		if (connection != null)
			return connection.getContentLength();
		return 0;
	}

	public int getStatusCode() {
		try {
			getConnection();
			return ((HttpURLConnection) connection).getResponseCode();
		} catch (IOException e) {
			Trace.trace(Trace.SEVERE, "Error getting status code", e);
		}
		
		return HttpURLConnection.HTTP_BAD_REQUEST;
	}

	public String getStatusMessage() {
		try {
			if (connection != null)
				return ((HttpURLConnection) connection).getResponseMessage();
		} catch (IOException e) {
			Trace.trace(Trace.SEVERE, "Error getting status message", e);
		}
		
		return "";
	}

	public long getLastModified() {
		if (lastModified == 0) {
			try {
				if (connection == null)
					connection = getConnection();
				
				lastModified = connection.getLastModified();
			} catch (IOException e) {
				Trace.trace(Trace.SEVERE, "Error opening connection", e);
			}
		}
		return lastModified;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

/*	private void checkOffset() throws IOException {
		if (offset == 0)
			return;
		String range = connection.getHeaderField("Content-Range");
		//System.out.println("Content-Range=" + range);
		if (range == null) {
			//System.err.println("Server does not support ranges");
			throw new IOException(Messages.HttpResponse_rangeExpected);
		} else if (!range.startsWith("bytes " + offset + "-")) {
			//System.err.println("Server returned wrong range");
			throw new IOException(Messages.HttpResponse_wrongRange);
		}
	}*/
}