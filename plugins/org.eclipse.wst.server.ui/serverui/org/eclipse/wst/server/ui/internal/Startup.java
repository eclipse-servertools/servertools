/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IStartup;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.util.PublishAdapter;
import org.eclipse.wst.server.ui.internal.audio.Audio;
/**
 *
 */
public class Startup implements IStartup {
	/**
	 * @see org.eclipse.wst.server.core.internal.IStartup#startup()
	 */
	public void startup() {
		if (Trace.FINEST) {
			Trace.trace(Trace.STRING_FINEST, "Audio startup");
		}

		final IPublishListener publishListener = new PublishAdapter() {
			public void publishFinished(IServer server, IStatus status) {
				Audio.playSound("org.eclipse.wst.server.sound.publishFinished");
			}
		};
		
		final IServerListener serverListener = new IServerListener() {
			public void serverChanged(ServerEvent event) {
				int eventKind = event.getKind();
				IServer server = event.getServer();
				if (eventKind == (ServerEvent.SERVER_CHANGE | ServerEvent.STATE_CHANGE)) {
					int state = server.getServerState();
					if (state == IServer.STATE_STARTED)
						Audio.playSound("org.eclipse.wst.server.sound.serverStart");
					else if (state == IServer.STATE_STOPPED)
						Audio.playSound("org.eclipse.wst.server.sound.serverStop");
				}
			}
		};

		IServerLifecycleListener listener = new IServerLifecycleListener() {
			public void serverAdded(IServer server) {
				server.addServerListener(serverListener);
				((Server) server).addPublishListener(publishListener);
			}
			
			public void serverChanged(IServer server) {
				// do nothing
			}

			public void serverRemoved(IServer server) {
				server.removeServerListener(serverListener);
				((Server) server).removePublishListener(publishListener);
			}
		};
		ServerCore.addServerLifecycleListener(listener);
	}
}