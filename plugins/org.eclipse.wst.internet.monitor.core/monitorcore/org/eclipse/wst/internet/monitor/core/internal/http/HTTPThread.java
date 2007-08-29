/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal.http;

import java.io.*;
import org.eclipse.wst.internet.monitor.core.internal.Connection;
import org.eclipse.wst.internet.monitor.core.internal.Trace;
import org.eclipse.wst.internet.monitor.core.internal.provisional.Request;
/**
 * Monitor server I/O thread.
 */
public class HTTPThread extends Thread {
	private static final int BUFFER = 2048;
	private static final byte CR = (byte) '\r';
	private static final byte LF = (byte) '\n';
	protected static int threadCount = 0;

	private byte[] readBuffer = new byte[BUFFER];

	// buffer and index
	protected byte[] buffer = new byte[0];
	protected int bufferIndex = 0;

	protected InputStream in;
	protected OutputStream out;
	protected HTTPConnection conn;
	protected boolean isRequest;
	protected Connection conn2;
	
	protected HTTPThread request;
	protected boolean isWaiting;
	
	// user to translate the Host: header
	protected String host;
	protected int port;

	protected int contentLength = -1;
	protected byte transferEncoding = -1;
	protected String responseType = null;
	protected boolean connectionKeepAlive = false;
	protected boolean connectionClose = false;

	protected static final String[] ENCODING_STRING = new String[] {
		"chunked", "identity", "gzip", "compressed", "deflate"};

	protected static final byte ENCODING_CHUNKED = 0;
	protected static final byte ENCODING_IDENTITY = 1;
	protected static final byte ENCODING_GZIP = 2;
	protected static final byte ENCODING_COMPRESSED = 3;
	protected static final byte ENCODING_DEFLATE = 4;

/* change:
Referer: http://localhost:8081/index.html
Host: localhost:8081
*/
/* The Connection header has the following grammar:

	   Connection = "Connection" ":" 1#(connection-token)
	   connection-token  = token

   HTTP/1.1 proxies MUST parse the Connection header field before a
   message is forwarded and, for each connection-token in this field,
   remove any header field(s) from the message with the same name as the
   connection-token. */

	/**
	 * Create a new HTTP thread.
	 * 
	 * @param conn2
	 * @param in
	 * @param out
	 * @param conn
	 * @param isRequest
	 * @param host
	 * @param port
	 */
	public HTTPThread(Connection conn2, InputStream in, OutputStream out, HTTPConnection conn, boolean isRequest, String host, int port) {
		super();
		this.conn2 = conn2;
		this.in = in;
		this.out = out;
		this.conn = conn;
		this.isRequest = isRequest;
		this.host = host;
		this.port = port;
	
		setName("HTTP (" + host + ":" + port + ") " + (isRequest ? "REQUEST" : "RESPONSE") + " " + (threadCount++));
		setPriority(Thread.NORM_PRIORITY + 1);
		setDaemon(true);
		
		Trace.trace(Trace.PARSING, "Started: " + this);
	}
	
	/**
	 * Create a new HTTP thread.
	 * 
	 * @param conn2
	 * @param in
	 * @param out
	 * @param conn
	 * @param isRequest
	 * @param host
	 * @param port
	 * @param request
	 */
	public HTTPThread(Connection conn2, InputStream in, OutputStream out, HTTPConnection conn, boolean isRequest, String host, int port, HTTPThread request) {
		this(conn2, in, out, conn, isRequest, host, port);
		
		this.request = request;
	}

	/**
	 * Add a line feed to the end of the byte array.
	 * @return byte[]
	 * @param b byte[]
	 */
	protected static byte[] convert(byte[] b) {
		if (b == null || b.length == 0)
			return b;
	
		int size = b.length;
		byte[] x = new byte[size + 2];
		System.arraycopy(b, 0, x, 0, size);
		x[size] = (byte) '\r';     // CR
		x[size + 1] = (byte) '\n'; // LF
		return x;
	}

	/**
	 * Read more data into the buffer.
	 */
	protected void fillBuffer() throws IOException {
		int n = in.read(readBuffer);
	
		if (n <= 0)
			throw new IOException("End of input");
	
		// add to full buffer
		int len = buffer.length - bufferIndex;
		if (len < 0)
			len = 0;
		byte[] x = new byte[n + len];
		System.arraycopy(buffer, bufferIndex, x, 0, len);
		System.arraycopy(readBuffer, 0, x, len, n);
		bufferIndex = 0;
		buffer = x;
	}

	/**
	 * Returns the first location of a CRLF.
	 *
	 * @return int
	 */
	protected int getFirstCRLF() {
		int size = buffer.length;
		int i = bufferIndex + 1;
		while (i < size) {
			if (buffer[i - 1] == CR && buffer[i] == LF)
				return i;
			i++;
		}
		return -1;
	}

	/**
	 * Output the given bytes.
	 * @param b byte[]
	 */
	protected void outputBytes(byte[] b, boolean isNew) throws IOException {
		out.write(b);
		if (isRequest)
			conn.addRequest(b, isNew);
		else
			conn.addResponse(b, isNew);
	}

	/**
	 * Parse the HTTP body.
	 * 
	 * @throws IOException
	 */
	public void parseBody() throws IOException {
		Trace.trace(Trace.PARSING, "Parsing body for: " + this);
		
		if (responseType != null && ("204".equals(responseType) || "304".equals(responseType))) {
			setHTTPBody(new byte[0]);
			return;
		}
		
		if (isRequest) {
			if (contentLength != -1) {
				byte[] b = readBytes(contentLength);
				out.write(b);
				conn.addRequest(b, false);
				setHTTPBody(b);
			} else if (transferEncoding != -1 && transferEncoding != ENCODING_IDENTITY) {
				parseChunk();
			}
			
			Trace.trace(Trace.PARSING, "Done parsing request body for: " + this);
			return;
		}
		
		// just return body for HTTP 1.0 responses
		if (!isRequest && !connectionKeepAlive && contentLength == -1 && transferEncoding == -1) {
			Trace.trace(Trace.PARSING, "Assuming HTTP 1.0 for: " + this);
			int n = buffer.length - bufferIndex;
			byte[] b = readBytes(n);
			byte[] body = new byte[0];
			while (n >= 0) {
				Trace.trace(Trace.PARSING, "Bytes read: " + n + " " + this);
				if (b != null && n > 0) {
					byte[] x = null;
					if (n == b.length)
						x = b;
					else {
						x = new byte[n];
						System.arraycopy(b, 0, x, 0, n);
					}
					outputBytes(x, false);
					
					// copy to HTTP body
					byte[] temp = new byte[body.length + x.length];
					System.arraycopy(body, 0, temp, 0, body.length);
					System.arraycopy(x, 0, temp, body.length, x.length);
					body = temp;
				}
				if (b.length < BUFFER)
					b = new byte[BUFFER];
				n = in.read(b);
				Thread.yield();
			}
			out.flush();
			setHTTPBody(body);
			return;
		}
		
		// spec 4.4.1
		if (responseType != null && responseType.startsWith("1")) {
			setHTTPBody(new byte[0]);
			return;
		}
		
		// spec 4.4.2
		if (transferEncoding != -1 && transferEncoding != ENCODING_IDENTITY) {
			parseChunk();
			return;
		}
		
		// spec 4.4.3
		if (contentLength != -1) {
			byte[] b = readBytes(contentLength);
			out.write(b);
			if (isRequest)
				conn.addRequest(b, false);
			else
				conn.addResponse(b, false);
			setHTTPBody(b);
			return;
		}
		
		// spec 4.4.4 (?)
		
		Trace.trace(Trace.PARSING, "Unknown body for: " + this);
	}

	/**
	 * Parse an HTTP chunk.
	 * 
	 * @throws IOException
	 */
	public void parseChunk() throws IOException {
		Trace.trace(Trace.PARSING, "Parsing chunk for: " + this);
		boolean done = false;
		byte[] body = new byte[0];
	
		while (!done) {
			// read chunk size
			byte[] b = readLine();
	
			String s = new String(b);
			int index = s.indexOf(" ");
			int length = -1;
			try {
				if (index > 0)
					s = s.substring(0, index);
				length = Integer.parseInt(s.trim(), 16);
			} catch (Exception e) {
				Trace.trace(Trace.PARSING, "Error chunk for: " + this, e);
			}
	
			// output bytes
			outputBytes(b, false);
	
			if (length <= 0)
				done = true;
			else {
				// read and output chunk data plus CRLF
				b = readBytes(length + 2);
				outputBytes(b, false);
				
				// copy to HTTP body
				byte[] temp = new byte[body.length + b.length - 2];
				System.arraycopy(body, 0, temp, 0, body.length);
				System.arraycopy(b, 0, temp, body.length, b.length - 2);
				body = temp;
			}
		}
	
		// read trailer
		byte[] b = readLine();
		while (b.length > 2) {
			outputBytes(b, false);
			b = readLine();
		}
	
		outputBytes(b, false);
		setHTTPBody(body);
	}

	/**
	 * Parse an HTTP header.
	 * 
	 * @throws IOException
	 */
	public void parseHeader() throws IOException {
		Trace.trace(Trace.PARSING, "Parsing header for: " + this);
	
		// read until first blank line
		boolean isFirstLine = true;
		boolean isNew = true;
	
		byte[] b = readLine();
		while (b.length > 5) {
			Trace.trace(Trace.PARSING, "Parsing header line: '" + new String(b) + "'");
			
			if (isFirstLine) {
				String s = new String(b);
				if (isRequest) {
					setLabel(s);
					isNew = false;
				}
	
				if (!isRequest) {
					int index1 = s.indexOf(' ');
					int index2 = s.indexOf(' ', index1 + 1);
	
					try {
						responseType = s.substring(index1 + 1, index2).trim();
						Trace.trace(Trace.PARSING, "Response Type: " + this + " " + responseType);
					} catch (Exception e) {
						Trace.trace(Trace.PARSING, "Error parsing response type for: " + this, e);
					}
					if (responseType != null && responseType.equals("100")) {
						outputBytes(b, isNew);
						isNew = false;

						b = readLine();
						outputBytes(b, false);

						b = readLine();

						index1 = s.indexOf(' ');
						index2 = s.indexOf(' ', index1 + 1);

						try {
							responseType = s.substring(index1 + 1, index2).trim();
							Trace.trace(Trace.PARSING, "Response Type: " + this + " " + responseType);
						} catch (Exception e) {
							Trace.trace(Trace.PARSING, "Error parsing response type for: " + this, e);
						}
					}
				}
				isFirstLine = false;
			}
	
			// translate
			b = translateHeaderLine(b);
			
			outputBytes(b, isNew);
			isNew = false;
	
			b = readLine();
		}

		Trace.trace(Trace.PARSING, "Parsing final header line: '" + new String(b) + "'");

		outputBytes(b, false);

		Request rr = conn.getRequestResponse(isRequest);
		Trace.trace(Trace.PARSING, "Setting header length: " + rr.getRequest(Request.ALL).length);
		
		setHTTPHeader(rr);
	}

	/**
	 * Read bytes from the stream.
	 * @return byte[]
	 */
	protected byte[] readBytes(int n) throws IOException {
		Trace.trace(Trace.PARSING, "readBytes() " + n + " for: " + this);
		while (buffer.length - bufferIndex < n)
			fillBuffer();
	
		return removeFromBuffer(bufferIndex + n);
	}

	/**
	 * Read and return the next full line.
	 *
	 * @return byte[]
	 */
	protected byte[] readLine() throws IOException {
		Trace.trace(Trace.PARSING, "readLine() for: " + this);
	
		int n = getFirstCRLF();
		while (n < 0) {
			fillBuffer();
			n = getFirstCRLF();
		}
		return removeFromBuffer(n + 1);
	}

	/**
	 * Remove data from the buffer up to the absolute index n.
	 * Return the data from between bufferIndex and n.
	 *
	 * @param n the bytes to remove
	 * @return a byte array
	 */
	protected byte[] removeFromBuffer(int n) {
		// copy line out of buffer
		byte[] b = new byte[n - bufferIndex];
		System.arraycopy(buffer, bufferIndex, b, 0, n - bufferIndex);
	
		if (buffer.length > BUFFER * 2 || bufferIndex > BUFFER) {
			// remove line from buffer
			int size = buffer.length;
			byte[] x = new byte[size - n];
			System.arraycopy(buffer, n, x, 0, size - n);
			buffer = x;
			bufferIndex = 0;
		} else
			bufferIndex = n;
	
		return b;
	}

	/**
	 * Listen for input, save it, and pass to the output stream.
	 * Philosophy: Read a single line separately and translate.
	 * When blank line is reached, just pass all other data through.
	 */
	public void run() {
		try {
			try {
				while (true) {
					contentLength = -1;
					transferEncoding = -1;
					connectionKeepAlive = false;
					connectionClose = false;
					
					parseHeader();
					parseBody();
					
					if (isRequest && connectionKeepAlive)
						waitForResponse();
					
					//Request r = conn.getRequestResponse(true);
					//r.fireChangedEvent();
					
					Trace.trace(Trace.PARSING, "Done HTTP request for " + this + " " + connectionKeepAlive);
					if (!isRequest && (!request.connectionKeepAlive || connectionClose)) {
						conn2.close();
						if (request.connectionKeepAlive && connectionClose)
							request.connectionKeepAlive = false;
							notifyRequest();
						break;
					}
					
					if (!isRequest)
						notifyRequest();
					
					Thread.yield();
				}
			} catch (IOException e) {
				// reached end of input
				Trace.trace(Trace.PARSING, "End of buffer for: " + this, e);
				if (!isRequest) {
					try {
						request.connectionKeepAlive = false;
						request.conn2.close();
						notifyRequest();
					} catch (Exception ex) {
						Trace.trace(Trace.PARSING, "Error closing request in response to error: " + this, e);
					}
				}
			}
			
			// send rest of buffer
			out.write(buffer, bufferIndex, buffer.length - bufferIndex);
			out.flush();
		} catch (Exception e) {
			Trace.trace(Trace.PARSING, "Error in: " + this, e);
		}
		//if (!isRequest)
		//	conn2.close();
		
		Trace.trace(Trace.PARSING, "Closing thread " + this);
	}

	/**
	 * Sets the title of the call.
	 *
	 * @param s java.lang.String
	 */
	protected void setLabel(String s) {
		try {
			int index1 = s.indexOf(' ');
			if (index1 < 0 || index1 > 15)
				return;
			int index2 = s.indexOf(' ', index1 + 1);
			if (index2 < 0)
				return;
	
			conn.setLabel(s.substring(index1 + 1, index2), true);
		} catch (Exception e) {
			// ignore
		}
	}

	/**
	 * Translate the header line.
	 * 
	 * @return byte[]
	 * @param b byte[]
	 */
	protected byte[] translateHeaderLine(byte[] b) {
		String s = new String(b);
	
		if (isRequest && s.startsWith("Host: ")) {
			String t = "Host: " + host;
			if (port != 80)
				t += ":" + port;
			return convert(t.getBytes());
		} else if (s.startsWith("Content-Length: ")) {
			try {
				contentLength = Integer.parseInt(s.substring(16).trim());
				Trace.trace(Trace.PARSING, "Content length: " + this + " " + contentLength);
			} catch (Exception e) {
				Trace.trace(Trace.PARSING, "Content length error", e);
			}
		} else if (s.startsWith("Connection: ")) {
			try {
				String t = s.substring(11).trim();
				if (t.equalsIgnoreCase("Keep-Alive"))
					connectionKeepAlive = true;
				// response contains "Connection: close" header
				// close connection to the client even if "keepalive" had been requested
				// we can't just reset request.keepAlive - it's used as indicator whether
				// the request thread is (going to) wait for the response thread
				// (and must be notified (only then)),
				// so we have to let it alone
				if (t.equalsIgnoreCase("close"))
					connectionClose = true;
				Trace.trace(Trace.PARSING, "Keep alive: " + connectionKeepAlive);
			} catch (Exception e) {
				Trace.trace(Trace.PARSING, "Error getting Connection: from header", e);
			}
		} else if (s.startsWith("Transfer-Encoding: ")) {
			String t = s.substring(19).trim();
			int size = ENCODING_STRING.length;
			for (int i = 0; i < size; i++) {
				if (ENCODING_STRING[i].equalsIgnoreCase(t)) {
					transferEncoding = (byte) i;
					Trace.trace(Trace.PARSING, "Transfer encoding: " + ENCODING_STRING[i]);
				}
			}
		}
	
		return b;
	}
	
	protected void close() {
		try {
			Trace.trace(Trace.PARSING, "Closing: " + this);
			out.close();
		} catch (Exception e) {
			Trace.trace(Trace.PARSING, "Error closing connection " + this, e);
		}
	}

	protected void waitForResponse() {
		Trace.trace(Trace.PARSING, "Waiting for response " + this);
		synchronized (this) {
			try {
				isWaiting = true;
				wait();
			} catch (Exception e) {
				Trace.trace(Trace.PARSING, "Error in waitForResponse() " + this, e);
			}
			isWaiting = false;
		}
		Trace.trace(Trace.PARSING, "Done waiting for response " + this);
	}

	protected void notifyRequest() {
		Trace.trace(Trace.PARSING, "Notifying request " + this);
		while (request.connectionKeepAlive && !request.isWaiting) {
			Trace.trace(Trace.PARSING, "Waiting for request " + this);
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				// ignore
			}
		}
		synchronized (request) {
			try {
				request.notify();
			} catch (Exception e) {
				Trace.trace(Trace.PARSING, "Error in notifyRequest() " + this, e);
			}
		}
		Trace.trace(Trace.PARSING, "Done notifying request " + this);
	}

	protected void setHTTPHeader(Request rr) {
		if (isRequest) {
			byte[] b = rr.getRequest(Request.ALL);
			byte[] h = new byte[b.length];
			System.arraycopy(b, 0, h, 0, b.length);
			rr.setProperty(HTTPRequest.HTTP_REQUEST_HEADER, h);
		} else {
			byte[] b = rr.getResponse(Request.ALL);
			byte[] h = new byte[b.length];
			System.arraycopy(b, 0, h, 0, b.length);
			rr.setProperty(HTTPRequest.HTTP_RESPONSE_HEADER, h);
		}
	}

	protected void setHTTPBody(byte[] b) {
		Request rr = conn.getRequestResponse(isRequest);
		if (isRequest)
			rr.setProperty(HTTPRequest.HTTP_REQUEST_BODY, b);
		else
			rr.setProperty(HTTPRequest.HTTP_RESPONSE_BODY, b);
	}
}