package org.eclipse.wst.server.core.tests.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;

final class EmptyDelegate extends ServerDelegate {

    public EmptyDelegate() {
    }

    @Override
    public IStatus canModifyModules(IModule[] add, IModule[] remove) {
        return new Status(IStatus.ERROR, "org.eclipse.wst.server.core.tests", "No modifications are allowed on this dummy server");
    }

    @Override
    public IModule[] getChildModules(IModule[] module) {
        return new IModule[0];
    }

    @Override
    public IModule[] getRootModules(IModule module) throws CoreException {
        return new IModule[0];
    }

    /** Should not be called
     * @see {@link #canModifyModules(IModule[], IModule[])} 
    **/
    @Override
    public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
        throw new CoreException(canModifyModules(add, remove));
    }

}
