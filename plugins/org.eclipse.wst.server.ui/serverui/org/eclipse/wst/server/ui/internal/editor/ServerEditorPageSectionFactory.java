/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.expressions.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.editor.*;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * 
 */
public class ServerEditorPageSectionFactory implements IServerEditorPageSectionFactory {
	private IConfigurationElement element;
	private Expression fContextualLaunchExpr = null;

	/**
	 * ServerEditorPageSectionFactory constructor.
	 * 
	 * @param element a configuration element
	 */
	public ServerEditorPageSectionFactory(IConfigurationElement element) {
		super();
		this.element = element;
	}

	/**
	 * 
	 */
	protected IConfigurationElement getConfigurationElement() {
		return element;
	}

	/**
	 * Returns the id of this factory.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the order.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			String o = element.getAttribute("order");
			return Integer.parseInt(o);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Returns the insertion id of this factory.
	 *
	 * @return the insertion id
	 */
	public String getInsertionId() {
		return element.getAttribute("insertionId");
	}

	/**
	 * Return the ids of the server resource factories (specified
	 * using Java-import style) that this page may support.
	 * 
	 * @return an array of type ids
	 */
	protected String[] getTypeIds() {
		try {
			List<String> list = new ArrayList<String>();
			StringTokenizer st = new StringTokenizer(element.getAttribute("typeIds"), ",");
			while (st.hasMoreTokens()) {
				String str = st.nextToken();
				if (str != null && str.length() > 0)
					list.add(str.trim());
			}
			String[] s = new String[list.size()];
			list.toArray(s);
			return s;
		} catch (Exception e) {
			//Trace.trace("Could not get server resource from: " + element.getAttribute("serverResources"));
			return null;
		}
	}
	
	/**
	 * @see IServerEditorPageSectionFactory#supportsType(String)
	 */
	public boolean supportsType(String id) {
		if (id == null || id.length() == 0)
			return false;

		String[] s = getTypeIds();
		if (s == null)
			return false;
		
		int size = s.length;
		for (int i = 0; i < size; i++) {
			if (s[i].endsWith("*")) {
				if (id.length() >= s[i].length() && id.startsWith(s[i].substring(0, s[i].length() - 1)))
					return true;
			} else if (id.equals(s[i]))
				return true;
		}
		return false;
	}
	
	/**
	 * @see IServerEditorPageSectionFactory#shouldCreateSection(IServerWorkingCopy)
	 */
	public boolean shouldCreateSection(IServerWorkingCopy server) {
		try {
			return isEnabled(server);
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error calling delegate", e);
			}
			return false;
		}
	}

	/**
	 * @see IServerEditorPageSectionFactory#createSection()
	 */
	public ServerEditorSection createSection() {
		try {
			return (ServerEditorSection) element.createExecutableExtension("class");
		} catch (Throwable t) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Could not create server editor section", t);
			}
			return null;
		}
	}

	/**
	 * Returns an expression that represents the enablement logic for the
	 * contextual launch element of this launch shortcut description or
	 * <code>null</code> if none.
	 * @return an evaluatable expression or <code>null</code>
	 * @throws CoreException if the configuration element can't be
	 *  converted. Reasons include: (a) no handler is available to
	 *  cope with a certain configuration element or (b) the XML
	 *  expression tree is malformed.
	 */
	public Expression getContextualLaunchEnablementExpression() throws CoreException {
		if (fContextualLaunchExpr == null) {
			IConfigurationElement[] elements = element.getChildren(ExpressionTagNames.ENABLEMENT);
			IConfigurationElement enablement = elements.length > 0 ? elements[0] : null; 

			if (enablement != null)
				fContextualLaunchExpr = ExpressionConverter.getDefault().perform(enablement);
		}
		return fContextualLaunchExpr;
	}

	/**
	 * Evaluate the given expression within the given context and return
	 * the result. Returns <code>true</code> iff result is either TRUE or NOT_LOADED.
	 * This allows optimistic inclusion of shortcuts before plugins are loaded.
	 * Returns <code>false</code> if exp is <code>null</code>.
	 * 
	 * @param exp the enablement expression to evaluate or <code>null</code>
	 * @param context the context of the evaluation. Usually, the
	 *  user's selection.
	 * @return the result of evaluating the expression
	 * @throws CoreException
	 */
	protected boolean evalEnablementExpression(IEvaluationContext context, Expression exp) throws CoreException {
		return (exp != null) ? ((exp.evaluate(context)) != EvaluationResult.FALSE) : false;
	}

	/**
	 * Returns true if enabled for the given object.
	 * 
	 * @param obj an object
	 * @return <code>true</code> if enabled
	 * @throws CoreException if anything goes wrong
	 */
	public boolean isEnabled(Object obj) throws CoreException {
		if (getContextualLaunchEnablementExpression() == null)
			return true;
		IEvaluationContext context = new EvaluationContext(null, obj);
		context.setAllowPluginActivation(true);
		context.addVariable("server", obj);
		return evalEnablementExpression(context, getContextualLaunchEnablementExpression());
	}

	public String toString() {
		return "ServerEditorSection [" + getId() + ", " + getInsertionId() + ", " + getOrder() + "]";
	}
}
