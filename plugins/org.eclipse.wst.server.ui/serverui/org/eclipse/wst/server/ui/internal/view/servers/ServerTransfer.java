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
package org.eclipse.wst.server.ui.internal.view.servers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
/*
 * The element serialization format is:
 *  (int) number of servers
 * Then, the following for each server:
 *  (String) id
 */
public class ServerTransfer extends ByteArrayTransfer {
	private static final ServerTransfer instance = new ServerTransfer();

	// Create a unique ID to make sure that different Eclipse
	// applications use different "types" of <code>JavaElementTransfer</code>
	private static final String TYPE_NAME = "server-transfer-format:" + System.currentTimeMillis() + ":" + instance.hashCode();

	private static final int TYPEID = registerType(TYPE_NAME);

	private ServerTransfer() {
		// do nothing
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return the singleton instance
	 */
	public static ServerTransfer getInstance() {
		return instance;
	}

	/* (non-Javadoc)
	 * Method declared on Transfer.
	 */
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	/* (non-Javadoc)
	 * Method declared on Transfer.
	 */
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#javaToNative(java.lang.Object, org.eclipse.swt.dnd.TransferData)
	 */
	protected void javaToNative(Object data, TransferData transferData) {
		if (!(data instanceof IServer[]))
			return;
		
		IServer[] servers = (IServer[]) data;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(out);

			// write the number of elements
			dataOut.writeInt(servers.length);

			// write each element
			for (int i= 0; i < servers.length; i++)
				dataOut.writeUTF(servers[i].getId());

			// cleanup
			dataOut.close();
			out.close();
			byte[] bytes = out.toByteArray();
			super.javaToNative(bytes, transferData);
		} catch (IOException e) {
			// it's best to send nothing if there were problems
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.swt.dnd.Transfer#nativeToJava(org.eclipse.swt.dnd.TransferData)
	 */
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		if (bytes == null)
			return null;
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			int count = in.readInt();
			IServer[] results = new IServer[count];
			for (int i = 0; i < count; i++) {
				String id = in.readUTF();
				results[i] = ServerCore.findServer(id);
			}
			return results;
		} catch (IOException e) {
			return null;
		}
	}
}