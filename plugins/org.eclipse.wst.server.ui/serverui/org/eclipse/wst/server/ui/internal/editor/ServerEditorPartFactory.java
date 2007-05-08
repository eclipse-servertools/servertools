/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.ui.IEditorPart;
/**
 * 
 */
public class ServerEditorPartFactory implements IServerEditorPartFactory {
	private IConfigurationElement element;
	private Expression fContextualLaunchExpr = null;

	/**
	 * ServerEditorPartFactory constructor.
	 * 
	 * @param element a configuration element
	 */
	public ServerEditorPartFactory(IConfigurationElement element) {
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
	 * Returns the id of this part factory.
	 *
	 * @return java.lang.String
	 */
	public String getId() {
		return element.getAttribute("id");
	}
	
	/**
	 * Returns the name of this part factory. 
	 *
	 * @return java.lang.String
	 */
	public String getName() {
		return element.getAttribute("name");
	}

	protected String[] getInsertionIds() {
		try {
			String insertionIds = element.getAttribute("insertionIds");
			List list = new ArrayList();
			if (insertionIds != null && insertionIds.length() > 0) {
				StringTokenizer st = new StringTokenizer(insertionIds, ",");
				while (st.hasMoreTokens()) {
					String str = st.nextToken();
					if (str != null && str.length() > 0)
						list.add(str.trim());
				}
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
	 * @see IServerEditorPartFactory#supportsInsertionId(String)
	 */
	public boolean supportsInsertionId(String id) {
		if (id == null || id.length() == 0)
			return false;

		String[] s = getInsertionIds();
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
	 * Return the ids of the server and server configuration type ids (specified
	 * using Java-import style) that this page may support.
	 * 
	 * @return java.lang.String[]
	 */
	protected String[] getTypeIds() {
		try {
			List list = new ArrayList();
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
	 * @see IServerEditorPartFactory#supportsType(String)
	 */
	public boolean supportsType(String id) {
		String[] s = getTypeIds();
		if (s == null || s.length == 0 || "*".equals(s[0]))
			return true;
		
		if (id == null || id.length() == 0)
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
	 * @see IServerEditorPartFactory#shouldCreatePage(IServerWorkingCopy)
	 */
	public boolean shouldCreatePage(IServerWorkingCopy server) {
		try {
			return isEnabled(server);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate", e);
			return false;
		}
	}

	/**
	 * @see IServerEditorPartFactory#createPage()
	 */
	public IEditorPart createPage() {
		try {
			return (IEditorPart) element.createExecutableExtension("class");
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate", e);
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
		context.addVariable("server", obj);
		return evalEnablementExpression(context, getContextualLaunchEnablementExpression());
	}
}