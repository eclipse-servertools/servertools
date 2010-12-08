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
package org.eclipse.wst.server.core.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.expressions.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.model.InternalInitializer;
import org.eclipse.wst.server.core.model.ModuleFactoryDelegate;
/**
 * 
 */
public class ModuleFactory implements IOrdered {
	private IConfigurationElement element;
	private Expression fContextualLaunchExpr = null;
	
	public ModuleFactoryDelegate delegate;
	private List<IModuleType> moduleTypes;

	/**
	 * ModuleFactory constructor comment.
	 * 
	 * @param element a configuration element
	 */
	public ModuleFactory(IConfigurationElement element) {
		super();
		this.element = element;
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
	 * Returns the index (ordering) of this task.
	 *
	 * @return int
	 */
	public int getOrder() {
		try {
			return Integer.parseInt(element.getAttribute("order"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Return the supported module types.
	 * 
	 * @return an array of module types
	 */
	public IModuleType[] getModuleTypes() {
		if (moduleTypes == null)
			moduleTypes = ServerPlugin.getModuleTypes(element.getChildren("moduleType"));
		
		IModuleType[] mt = new IModuleType[moduleTypes.size()];
		moduleTypes.toArray(mt);
		return mt;
	}

	/**
	 * Returns true if this modules factory produces project modules.
	 *
	 * @return boolean
	 */
	public boolean isProjectModuleFactory() {
		return "true".equalsIgnoreCase(element.getAttribute("projects"));
	}

	/*
	 * @see IModuleFactoryDelegate#getDelegate()
	 */
	public ModuleFactoryDelegate getDelegate(IProgressMonitor monitor) {
		if (delegate == null) {
			try {
				long time = System.currentTimeMillis();
				delegate = (ModuleFactoryDelegate) element.createExecutableExtension("class");
				InternalInitializer.initializeModuleFactoryDelegate(delegate, this, monitor);
				Trace.trace(Trace.PERFORMANCE, "ModuleFactory.getDelegate(): <" + (System.currentTimeMillis() - time) + "> " + getId());
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not create delegate " + toString(), t);
			}
		}
		return delegate;
	}

	/*
	 * @see ModuleFactoryDelegate#getModules()
	 */
	public IModule[] getModules() {
		return getModules(null);
	}

	/*
	 * @see ModuleFactoryDelegate#getModules()
	 */
	public IModule[] getModules(IProgressMonitor monitor) {
		try {
			IModule[] modules = getDelegate(monitor).getModules();
			if (hasInvalidModules(modules))
				modules = filter(modules);
			return modules;
		} catch (Throwable t) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), t);
			return new IModule[0];
		}
	}

	/*
	 * @see ModuleFactoryDelegate#getModules(IProject)
	 */
	public IModule[] getModules(IProject project, IProgressMonitor monitor) {
		try {
			IModule[] modules = getDelegate(monitor).getModules(project);
			if (hasInvalidModules(modules))
				modules = filter(modules);
			return modules;
		} catch (Throwable t) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), t);
			return new IModule[0];
		}
	}
	
	/**
	 * Returns an expression that represents the enablement logic for the
	 * contextual project of this module factory <code>null</code> if none.
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
	 * the result. Returns <code>true</code> if result is either TRUE or NOT_LOADED.
	 * This allows optimistic inclusion before plugins are loaded.
	 * Returns <code>true</code> if exp is <code>null</code>.
	 * 
	 * @param exp the enablement expression to evaluate or <code>null</code>
	 * @param context the context of the evaluation. 
	 * @return the result of evaluating the expression
	 * @throws CoreException
	 */
	protected boolean evalEnablementExpression(IEvaluationContext context, Expression exp) throws CoreException {
		// for compatibility with the current behaviour, if the exp == null we return true. Meaning that the factory doesn't
		// implement an expresion and should be enabled for all cases.
		return (exp != null) ? ((exp.evaluate(context)) != EvaluationResult.FALSE) : true;
	}
	
	/*
	 * @see ModuleFactoryDelegate#getModules(IProject)
	 */
	public boolean isEnabled(IProject project, IProgressMonitor monitor) {
		try {
			IEvaluationContext context = new EvaluationContext(null, project);
			context.addVariable("project", project);
			
			return evalEnablementExpression(context, getContextualLaunchEnablementExpression());
		} catch (Throwable t) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), t);
			return false;
		}
	}

	/*
	 * @see ModuleFactoryDelegate#findModule(String)
	 */
	public IModule findModule(String id, IProgressMonitor monitor) {
		try {
			IModule module = getDelegate(monitor).findModule(id);
			if (module == null)
				return null;
			
			getModuleTypes();
			if (!moduleTypes.contains(module.getModuleType()))
				return null;
			
			return module;
		} catch (Throwable t) {
			Trace.trace(Trace.SEVERE, "Error calling delegate " + toString(), t);
			return null;
		}
	}

	private boolean hasInvalidModules(IModule[] modules) {
		if (modules == null)
			return false;
		
		getModuleTypes();
		
		int size = modules.length;
		for (int i = 0; i < size; i++) {
			if (!moduleTypes.contains(modules[i].getModuleType()))
				return true;
		}
		return false;
	}

	private IModule[] filter(IModule[] modules) {
		if (modules == null)
			return modules;
		
		getModuleTypes();
		List<IModule> list = new ArrayList<IModule>();
		
		int size = modules.length;
		for (int i = 0; i < size; i++) {
			IModule m = modules[i];
			if (moduleTypes.contains(m.getModuleType()))
				list.add(m);
			else
				Trace.trace(Trace.WARNING, "Invalid module returned from factory, ignored: " + m);
		}
		
		IModule[] m = new IModule[list.size()];
		list.toArray(m);
		return m;
	}

	/**
	 * Return a string representation of this object.
	 * 
	 * @return java.lang.String
	 */
	public String toString() {
		return "ModuleFactory[" + getId() + "]";
	}
}