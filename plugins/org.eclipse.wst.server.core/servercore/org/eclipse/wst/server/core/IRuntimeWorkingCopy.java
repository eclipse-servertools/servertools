/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.model.IRuntimeWorkingCopyDelegate;
/**
 * A working copy runtime object used for formulating changes
 * to a runtime instance ({@link IRuntime}).
 * <p>
 * [issue: The default value of location and test environment
 * should be specified here (or in IServerType.createRuntime).
 * If the initial value is unsuitable for actual use, then
 * save needs to deal with the case where the client forgets
 * to initialize this property.]
 * </p>
 * <p>
 * [issue: There can be other runtime-type-specific properties.
 * The default values for these need to be specified somewhere
 * too (probably in the API subclass of IRuntimeWorkingCopyDelegate).]
 * </p>
 * <p>
 * [issue: IElementWorkingCopy and IElement support an open-ended set
 * of attribute-value pairs. What is relationship between these
 * attributes and (a) the get/setXXX methods found on this interface,
 * and (b) get/setXXX methods provided by specific server types?
 * Is it the case that these attribute-values pairs are the only
 * information about a runtime instance that can be preserved
 * between workbench sessions? That is, any information recorded
 * just in instance fields of an IRuntimeDelegate implementation
 * will be lost when the session ends.]
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IRuntimeWorkingCopy extends IRuntime, IElementWorkingCopy {	

	/**
	 * Returns the runtime instance that this working copy is
	 * associated with.
	 * <p>
	 * For a runtime working copy created by a call to
	 * {@link IRuntime#getWorkingCopy()},
	 * <code>this.getOriginal()</code> returns the original
	 * runtime object. For a runtime working copy just created by
	 * a call to {@link IRuntimeType#createRuntime(String)},
	 * <code>this.getOriginal()</code> returns <code>null</code>.
	 * </p>
	 * 
	 * @return the associated runtime instance, or <code>null</code> if none
	 */
	public IRuntime getOriginal();
	
	/**
	 * Returns the delegate for this runtime working copy.
	 * The runtime working copy delegate is a
	 * runtime-type-specific object. By casting the runtime working copy
	 * delegate to the type prescribed in the API documentation for that
	 * particular runtime working copy type, the client can access
	 * runtime-type-specific properties and methods.
	 * <p>
	 * [issue: Exposing IRuntimeWorkingCopyDelegate to clients
	 * of IRuntimeWorkingCopy is same problem as exposing
	 * IRuntimeDelegate to clients of IRuntime. The suggested fix 
	 * is to replace this method with something like
	 * getRuntimeWorkingCopyExtension() which
	 * returns an IRuntimeWorkingCopyExtension.]
	 * </p>
	 * <p>
	 * [issue: runtimeTypes schema, workingCopyClass attribute is optional.
	 * This suggests that a runtime need not provide a working copy
	 * delegate class. Like the class attribute, this seems implausible.
	 * I've spec'd this method as if working copy delegate is mandatory.]
	 * </p>
	 * 
	 * @return the delegate for the runtime working copy
	 */
	public IRuntimeWorkingCopyDelegate getWorkingCopyDelegate();
	
	/**
	 * Sets the location of this runtime.
	 * <p>
	 * [issue: Explain what this "location" is.]
	 * </p>
	 * 
	 * @param path the location of this runtime, or <code>null</code> if none
	 * @see IRuntime#getLocation()
	 */
	public void setLocation(IPath path);
	
	/**
	 * Commits the changes made in this working copy. If there is
	 * no extant runtime instance with a matching id and runtime
	 * type, this will create a runtime instance with attributes
	 * taken from this working copy. If there an existing runtime
	 * instance with a matching id and runtime type, this will
	 * change the runtime instance accordingly.
	 * <p>
	 * [issue: What is relationship to 
	 * this.getOriginal() and the IRuntime returned by this.save()?
	 * The answer should be: they're the same runtime, for an
	 * appropriate notion of "same". As currently implemented, they
	 * are different IRuntime instances but have the same runtime
	 * id and same runtime types. Client that are hanging on to
	 * the old runtime instance will not see the changes. 
	 * If IRuntime were some kind of handle object as elsewhere in 
	 * Eclipse Platform, this kind of change could be done much
	 * more smoothly.]
	 * </p>
	 * <p>
	 * [issue: What if this object has already been saved
	 * or released?]
	 * </p>
	 * <p>
	 * [issue: What is lifecycle for IRuntimeWorkingCopyDelegate
	 * associated with this working copy?]
	 * </p>
	 * 
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new runtime instance
	 * @throws CoreException [missing]
	 */
	public IRuntime save(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Sets whether this runtime can be used as a test environment.
	 * 
	 * @param b <code>true</code> if this runtime can be use as a
	 * test environment, and <code>false</code> if it cannot
	 * @see IRuntime#isTestEnvironment()
	 */
	public void setTestEnvironment(boolean b);
}