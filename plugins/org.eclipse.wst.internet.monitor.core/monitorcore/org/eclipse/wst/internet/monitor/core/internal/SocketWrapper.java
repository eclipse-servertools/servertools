/*******************************************************************************
 * Copyright (c) 2004, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
/**
 * A socket that is only used for resending requests. All input operations are
 * ignored.
 */
public class SocketWrapper extends Socket {
	private InputStream inputStream;
	private OutputStream outputStream;
	
	/**
	 * Create a new socket wrapper.
	 * 
	 * @param inputStream
	 */
	public SocketWrapper(InputStream inputStream) {
	  this.inputStream = inputStream;
	  this.outputStream = new DummyOutputStream();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#bind(java.net.SocketAddress)
	 */
	public void bind(SocketAddress arg0) throws IOException {
		// do nothing
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#close()
	 */
	public synchronized void close() throws IOException {
		// do nothing
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#connect(java.net.SocketAddress, int)
	 */
	public void connect(SocketAddress arg0, int arg1) throws IOException {
		// do nothing
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#connect(java.net.SocketAddress)
	 */
	public void connect(SocketAddress arg0) throws IOException {
		// do nothing
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getChannel()
	 */
	public SocketChannel getChannel() {
		return super.getChannel();
	}
	
	/** (non-Javadoc)
	 * @see java.net.Socket#getInetAddress()
	 */
	public InetAddress getInetAddress() {
		return super.getInetAddress();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getInputStream()
	 */
	public InputStream getInputStream() throws IOException {
		return inputStream;
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getKeepAlive()
	 */
	public boolean getKeepAlive() throws SocketException {
		return false;
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getLocalAddress()
	 */
	public InetAddress getLocalAddress() {
		return super.getLocalAddress();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getLocalPort()
	 */
	public int getLocalPort() {
		return super.getLocalPort();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getLocalSocketAddress()
	 */
	public SocketAddress getLocalSocketAddress() {
		return super.getLocalSocketAddress();
	}
	
	/** (non-Javadoc)
	 * @see java.net.Socket#getOOBInline()
	 */
	public boolean getOOBInline() throws SocketException {
		return super.getOOBInline();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getOutputStream()
	 */
	public OutputStream getOutputStream() throws IOException {
		return outputStream;
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getPort()
	 */
	public int getPort() {
		return super.getPort();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getReceiveBufferSize()
	 */
	public synchronized int getReceiveBufferSize() throws SocketException {
		return super.getReceiveBufferSize();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getRemoteSocketAddress()
	 */
	public SocketAddress getRemoteSocketAddress() {
		return super.getRemoteSocketAddress();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getReuseAddress()
	 */
	public boolean getReuseAddress() throws SocketException {
		return super.getReuseAddress();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getSendBufferSize()
	 */
	public synchronized int getSendBufferSize() throws SocketException {
		return super.getSendBufferSize();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getSoLinger()
	 */
	public int getSoLinger() throws SocketException {
		return super.getSoLinger();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getSoTimeout()
	 */
	public synchronized int getSoTimeout() throws SocketException {
		return super.getSoTimeout();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getTcpNoDelay()
	 */
	public boolean getTcpNoDelay() throws SocketException {
		return super.getTcpNoDelay();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#getTrafficClass()
	 */
	public int getTrafficClass() throws SocketException {
		return super.getTrafficClass();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#isBound()
	 */
	public boolean isBound() {
		return super.isBound();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#isClosed()
	 */
	public boolean isClosed() {
		return false;
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#isConnected()
	 */
	public boolean isConnected() {
		return true;
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#isInputShutdown()
	 */
	public boolean isInputShutdown() {
		return false;
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#isOutputShutdown()
	 */
	public boolean isOutputShutdown() {
		return false;
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#sendUrgentData(int)
	 */
	public void sendUrgentData(int arg0) throws IOException {
		super.sendUrgentData(arg0);
	}
	
	/** (non-Javadoc)
	 * @see java.net.Socket#setKeepAlive(boolean)
	 */
	public void setKeepAlive(boolean arg0) throws SocketException {
		super.setKeepAlive(arg0);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#setOOBInline(boolean)
	 */
	public void setOOBInline(boolean arg0) throws SocketException {
		super.setOOBInline(arg0);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#setReceiveBufferSize(int)
	 */
	public synchronized void setReceiveBufferSize(int arg0) throws SocketException {
		super.setReceiveBufferSize(arg0);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#setReuseAddress(boolean)
	 */
	public void setReuseAddress(boolean arg0) throws SocketException {
		super.setReuseAddress(arg0);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#setSendBufferSize(int)
	 */
	public synchronized void setSendBufferSize(int arg0) throws SocketException {
		super.setSendBufferSize(arg0);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#setSoLinger(boolean, int)
	 */
	public void setSoLinger(boolean arg0, int arg1) throws SocketException {
		super.setSoLinger(arg0, arg1);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#setSoTimeout(int)
	 */
	public synchronized void setSoTimeout(int arg0) throws SocketException {
		super.setSoTimeout(arg0);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#setTcpNoDelay(boolean)
	 */
	public void setTcpNoDelay(boolean arg0) throws SocketException {
		super.setTcpNoDelay(arg0);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#setTrafficClass(int)
	 */
	public void setTrafficClass(int arg0) throws SocketException {
		super.setTrafficClass(arg0);
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#shutdownInput()
	 */
	public void shutdownInput() throws IOException {
		inputStream.close();
	}

	/** (non-Javadoc)
	 * @see java.net.Socket#shutdownOutput()
	 */
	public void shutdownOutput() throws IOException {
		// do nothing
	}

	/**
	 * A dummy OutputStream that allows us to fake output for a socket.
	 */
	public class DummyOutputStream extends OutputStream {
		/** (non-Javadoc)
		 * @see java.io.OutputStream#close()
		 */
		public void close() throws IOException {
			// do nothing
		}
		
		/** (non-Javadoc)
		 * @see java.io.OutputStream#flush()
		 */
		public void flush() throws IOException {
			// do nothing
		}
	
		/** (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		public void write(byte[] arg0, int arg1, int arg2) throws IOException {
			// do nothing
		}
	
		/** (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[])
		 */
		public void write(byte[] arg0) throws IOException {
			// do nothing
		}

		/** (non-Javadoc)
		 * @see java.io.OutputStream#write(int)
		 */
		public void write(int arg0) throws IOException {
			// do nothing
		}
	}
}