package org.eclipse.wst.internet.monitor.core.internal;

import java.net.Socket;
/**
 * 
 */
public class Connection {
	protected Socket in;
	protected Socket out;
	
	public Connection(Socket in, Socket out) {
		this.in = in;
		this.out = out;
	}

	public void close() {
		Trace.trace(Trace.FINEST, "Closing connection");
		try {
			in.getOutputStream().flush();
			in.shutdownInput();
			in.shutdownOutput();
			
			out.getOutputStream().flush();
			out.shutdownInput();
			out.shutdownOutput();
			Trace.trace(Trace.FINEST, "Connection closed");
		} catch (Exception ex) {
			Trace.trace(Trace.WARNING, "Error closing connection " + this + " " + ex.getMessage());
		}
	}
}