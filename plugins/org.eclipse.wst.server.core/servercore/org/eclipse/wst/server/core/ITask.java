package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * 
 * <p>This interface is not intended to be implemented by clients.</p>
 */
public interface ITask {
	/**
	 * Returns the label for this command.
	 *
	 * @return java.lang.String
	 */
	public String getName();

	/**
	 * Returns a description of this command.
	 *
	 * @return java.lang.String
	 */
	public String getDescription();

	public ITaskModel getTaskModel();

	public void setTaskModel(ITaskModel taskModel);

	public boolean canExecute();

	public void execute(IProgressMonitor monitor) throws CoreException;

	public boolean canUndo();
	
	public void undo();
}