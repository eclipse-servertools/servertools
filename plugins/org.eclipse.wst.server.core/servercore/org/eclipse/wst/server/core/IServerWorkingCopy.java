package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IServerWorkingCopyDelegate;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface IServerWorkingCopy extends IServer, IElementWorkingCopy {	
	public void setServerConfiguration(IServerConfiguration configuration);
	
	public IServer getOriginal();
	
	public IServerWorkingCopyDelegate getWorkingCopyDelegate();

	public IServer save(IProgressMonitor monitor) throws CoreException;

	public IServer saveAll(IProgressMonitor monitor) throws CoreException;

	public void setRuntime(IRuntime runtime);

	public void setHostname(String host);

	/**
	 * Add the given module to this configuration. The
	 * module must exist, should not already be deployed
	 * within the configuration, and canModifyModules()
	 * should have returned true. The configuration must assume
	 * any default settings and add the module without any UI.
	 * 
	 * Removes the given module from this configuration.
	 * The module must already exist in the configuration.
	 * When this method is called, the module may no
	 * longer exist in the workbench or filesystem.
	 *
	 * @param add org.eclipse.wst.server.core.model.IModule[]
	 * @param remove org.eclipse.wst.server.core.model.Module[]
	 * @param monitor org.eclipse.core.runtime.IProgressMonitor
	 */
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException;
}