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
package org.eclipse.jst.server.core;

import org.eclipse.jst.server.core.internal.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ModuleArtifactDelegate;
/**
 * An EJB bean.
 * <p>
 * <b>Provisional API:</b> This class/interface is part of an interim API that is still under development and expected to 
 * change significantly before reaching stability. It is being made available at this early stage to solicit feedback 
 * from pioneering adopters on the understanding that any code that uses this API will almost certainly be broken 
 * (repeatedly) as the API evolves.
 * </p>
 * @since 1.1
 */
public class EJBBean extends ModuleArtifactDelegate {
	/**
	 * @since 1.1
	 */
	public final static String EJB_11 = "1.1";
	/**
	 * @since 1.1
	 */
	public final static String EJB_20 = "2.0";
	/**
	 * @since 1.1
	 */
	public final static String EJB_21 = "2.1";
	/**
	 * @since 1.1
	 */
	public final static String EJB_30 = "3.0";
	
	private String jndiName;
	private String interfaceName;
	private boolean local;
	private boolean remote;
	private String version;

	/**
	 * Create a new EJBBean.
	 * 
	 * @param module the module that the EJB is contained in
	 * @param jndiName the JNDI name of the EJB
	 * @param remote <code>true</code> if the EJB has a remote interface, and
	 *    <code>false</code> otherwise
	 * @param local <code>true</code> if the EJB has a local interface, and
	 *    <code>false</code> otherwise
	 */
	public EJBBean(IModule module, String jndiName, boolean remote, boolean local) {
		super(module);
		this.jndiName = jndiName;
		this.remote = remote;
		this.local = local;
		this.version = "2.1";
	}
	
	/**
	 * Create a new EJBBean with a specific version
	 * 
	 * @param module the module that the EJB is contained in
	 * @param jndiName the JNDI name of the EJB
	 * @param remote <code>true</code> if the EJB has a remote interface, and
	 *    <code>false</code> otherwise
	 * @param local <code>true</code> if the EJB has a local interface, and
	 *    <code>false</code> otherwise
	 * @param version the level of the EJB specification that this EJB uses. Use one of the <code>EJB_xx</code> constants declared on {@link EJBBean}
	 * @since 1.1
	 */
	public EJBBean(IModule module, String jndiName, boolean remote, boolean local, String version) {
		super(module);
		this.jndiName = jndiName;
		this.remote = remote;
		this.local = local;
		this.version = version;
	}
	
	/**
	 * Create a new EJBBean with its interface name. This API is intended to be use by EJB 3.0.
	 * 
	 * @param module the module that the EJB is contained in
	 * @param jndiName the JNDI name of the EJB
	 * @param remote <code>true</code> if the EJB has a remote interface, and
	 *    <code>false</code> otherwise
	 * @param local <code>true</code> if the EJB has a local interface, and
	 *    <code>false</code> otherwise    
	 * @param version the level of the EJB specification that this EJB uses. Use one of the <code>EJB_xx</code> constants declared on {@link EJBBean}
	 * @param interfaceName the interface name of the EJB
	 * @since 1.1
	 */

	public EJBBean(IModule module, String jndiName, boolean remote, boolean local, String version, String interfaceName) {
		super(module);
		this.jndiName = jndiName;
		this.remote = remote;
		this.local = local;
		this.version = version;
		this.interfaceName = interfaceName;
	}

	/**
	 * Create a new empty EJBBean.
	 */
	public EJBBean() {
		super();
	}

	/**
	 * Returns the JNDI name of the EJB.
	 * 
	 * @return the JNDI name of the EJB
	 */
	public String getJndiName() {
		return jndiName;
	}

	/**
	 * Returns whether the EJB has a remote interface.
	 * 
	 * @return <code>true</code> if the EJB has a remote interface, and
	 *    <code>false</code> otherwise
	 */
	public boolean hasRemoteInterface() {
		return remote;
	}

	/**
	 * Returns whether the EJB has a local interface.
	 * 
	 * @return <code>true</code> if the EJB has a local interface, and
	 *    <code>false</code> otherwise
	 */
	public boolean hasLocalInterface() {
		return local;
	}
	
	/**
	 * Returns the level of the specification of the EJB
	 *
	 * @return the level of the specification of the EJB
	 * @since 1.1
	 */
	public String getVersion() {
		return version;
	}

	/*
	 * @see ModuleArtifactDelegate#getName()
	 */
	public String getName() {
		String ejbName;
		// EJB 2.0 don't have an inteface name, but they set the jndi name
		if (interfaceName != null && !(interfaceName.length()<=0)){
			ejbName = interfaceName;
		}
		else{
			ejbName = jndiName;
		}
		return NLS.bind(Messages.artifactEJB, ejbName);
	}

	/*
	 * @see ModuleArtifactDelegate#deserialize(String)
	 */
	public void deserialize(String s) {
		int ind = s.indexOf("//");
		super.deserialize(s.substring(0, ind));
		if ('T' == s.charAt(ind+2))
			local = true;
		else
			local = false;
		if ('T' == s.charAt(ind+3))
			remote = true;
		else
			remote = false;
		jndiName = s.substring(ind+4);
	}

	/*
	 * @see ModuleArtifactDelegate#serialize()
	 */
	public String serialize() {
		StringBuffer sb = new StringBuffer(super.serialize());
		sb.append("//");
		if (local)
			sb.append("T");
		else
			sb.append("F");
		if (remote)
			sb.append("T");
		else
			sb.append("F");
		sb.append(jndiName);
		return sb.toString();
	}

	/**
	 * Gets the name of interface represented by this object
	 *  
	 * @return the interface name
	 * @since 1.1
	 */
	public String getInterfaceName() {
		return interfaceName;
	}
}