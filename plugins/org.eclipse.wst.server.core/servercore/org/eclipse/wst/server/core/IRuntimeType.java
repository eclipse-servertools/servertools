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
 * [issue: The term "runtime" is misleading, given that the main
 * reason is for build time classpath contributions, not for actually
 * running anything. "libraries" might be a better choice.]
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
 * [issue: It is notoriously difficult to place any kind of
 * useful order on objects that are contributed independently by
 * non-collaborating parties. The IOrdered mechanism is weak, and
 * can't really solve the problem. Issues of presentation are usually
 * best left to the UI, which can sort objects based on arbitrary
 * properties.]
 * </p>
 * <p>
 * [issue: Equality/identify for runtime types? Are IRuntimeType
 * instances guaranteed to be canonical (client can use ==),
 * or is it possible for there to be non-identical IRuntimeType
 * objects in play that both represent the same runtime type?
 * The latter is the more common; type should spec equals.]
 * </p>
 * <p>
 * <it>Caveat: The server core API is still in an early form, and is
 * likely to change significantly before the initial release.</it>
 * </p>
 * 
 * @since 1.0
 */
public interface IRuntimeType extends IOrdered {

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
	 * Returns the displayable vendor name for this runtime type.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 * <p>
	 * [issue: "vendor" attribute is optional. What is expected return
	 * when omitted? Should be empty string.]
	 * </p>
	 *
	 * @return a displayable vendor name for this runtime type
	 */
	public String getVendor();
	
	/**
	 * Returns the displayable version name for this runtime type.
	 * <p>
	 * Note that this description is appropriate for the current locale.
	 * </p>
	 * <p>
	 * [issue: "versionId" attribute is optional. What is expected return
	 * when omitted? Should be empty string.]
	 * </p>
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
	 * @return the array of module types {@link IModuleType2}
	 */
	public IModuleType2[] getModuleTypes();
	
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
	 * instantiated, and <code>false</code> if it cannot
	 * @see #createRuntime(String)
	 */
	public boolean canCreate();

	/**
	 * Creates a working copy instance of this runtime type.
	 * After setting various properties of the working copy,
	 * the client should call {@link IRuntimeWorkingCopy#save(IProgressMonitor)}
	 * to bring the runtime instance into existence.
	 * <p>
	 * [issue: This method is declared as throwing CoreException.
	 * From a clients's point of view, what are the circumstances that
	 * cause this operation to fail?]
	 * </p>
	 * 
	 * @param id the id to assign to the runtime instance; a generated
	 * id is used if id is <code>null</code> or an empty string
	 * @return a new runtime working copy with the given id
	 * @throws CoreException [missing]
	 */
	public IRuntimeWorkingCopy createRuntime(String id, IProgressMonitor monitor) throws CoreException;
}