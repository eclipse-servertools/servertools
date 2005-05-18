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
package org.eclipse.wst.server.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
/**
 * Represents a (server) runtime type from which runtime instances can be
 * created.
 * <p>
 * The server core framework supports
 * an open-ended set of runtime types, which are contributed via
 * the <code>runtimeTypes</code> extension point in the server core
 * plug-in. Runtime type objects carry no state (all information is
 * read-only and is supplied by the server runtime type declaration).
 * The global list of known runtime types is available via
 * {@link ServerCore#getRuntimeTypes()}.
 * </p>
 * <p>
 * This interface is not intended to be implemented by clients.
 * </p>
 * <p>
 * [issue: What value do runtimes add?
 * It's main role is for setting up the Java build classpath
 * for projects holding modules that must be Java compiled.
 * If the notion of module is to transcend the vagaries of particular
 * types of server, and, indeed, be published to multiple servers
 * simultaneously, then matters of build classpath had better not
 * be tied to the particular servers involved.]
 * </p>
 * <p>
 * Two runtime types are identical if and only if they have the same id.
 * </p>
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * 
 * @since 1.0
 */
public interface IRuntimeType {
	/**
	 * Returns the id of this runtime type.
	 * Each known server runtime type has a distinct id. 
	 * Ids are intended to be used internally as keys; they are not
	 * intended to be shown to end users.
	 * 
	 * @return the runtime type id
	 */
	public String getId();

	/**
	 * Returns the displayable name for this runtime type.
	 * <p>
	 * Note that this name is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable name for this runtime type
	 */
	public String getName();

	/**
	 * Returns the displayable description for this runtime type.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable description for this runtime type
	 */
	public String getDescription();
	
	/**
	 * Returns the displayable vendor name for this runtime type. If the
	 * runtime type did not specific a vendor, an empty string is returned.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable vendor name for this runtime type
	 */
	public String getVendor();
	
	/**
	 * Returns the displayable version name for this runtime type. If the
	 * runtime type did not specific a vendor, an empty string is returned.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 *
	 * @return a displayable version name for this runtime type
	 */
	public String getVersion();

	/**
	 * Returns an array of module types that this runtime type can support.
	 * <p>
	 * A new array is returned on each call, so clients may store or modify the result.
	 * </p>
	 * 
	 * @return the array of module types {@link IModuleType}
	 */
	public IModuleType[] getModuleTypes();

	/**
	 * Returns whether this runtime type can be instantiated.
	 * <p>
	 * [issue: It's unclear what this method is for.
	 * The implementation checks whether the "class"
	 * and "workingCopyClass" attributes (both optional) were specified.
	 * What would be the point of a runtime type that didn't
	 * have both of these attributes and could not be "created"?]
	 * </p>
	 * 
	 * @return <code>true</code> if this type of runtime can be
	 *    instantiated, and <code>false</code> if it cannot
	 * @see #createRuntime(String, IProgressMonitor)
	 */
	public boolean canCreate();

	/**
	 * Creates a working copy instance of this runtime type.
	 * After setting various properties of the working copy,
	 * the client should call {@link IRuntimeWorkingCopy#save(boolean, IProgressMonitor)}
	 * to bring the runtime instance into existence.
	 * <p>
	 * Default values are set by calling the instance's delegate.
	 * Clients should assume that the location and other properties are
	 * not set and must be explicitly set by the client.
	 * </p>
	 * 
	 * @param id the id to assign to the runtime instance; a generated
	 *    id is used if id is <code>null</code> or an empty string
	 * @param monitor a progress monitor, or <code>null</code> if progress
	 *    reporting and cancellation are not desired
	 * @return a new runtime working copy with the given id
	 * @throws CoreException if an exception occurs while creating this runtime
	 *    or setting it's default values
	 */
	public IRuntimeWorkingCopy createRuntime(String id, IProgressMonitor monitor) throws CoreException;
}