package org.eclipse.wst.server.core.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerEvent;

import junit.framework.TestCase;

public class ServerTestCase extends TestCase {
    public void testServerChangeNotification() throws CoreException {
        IServerType type = ServerCore.findServerType("org.eclipse.wst.server.core.tests.empty");
        IServerWorkingCopy workingCopy = type.createServer("test_server", null, null);
        IServer saved = workingCopy.saveAll(true, null);
        class Listener implements IServerListener {
            public volatile boolean hit = false; 
            public void serverChanged(ServerEvent event) {
                hit = true;
            }
        }
        
        Listener listener1 = new Listener();
        Listener listener2 = new Listener();
        saved.addServerListener(listener1);
        saved.addServerListener(listener2, ServerEvent.SERVER_CHANGE | ServerEvent.ATTRIBUTE_CHANGE);
        workingCopy = saved.createWorkingCopy();
        workingCopy.setHost("host");
        workingCopy.saveAll(true, null);
        assertTrue(listener1.hit);
        assertTrue(listener2.hit);
    }
}
