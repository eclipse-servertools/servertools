/**********************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Igor Fedorenko & Fabrizio Giustina - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jst.server.tomcat.core.internal.wst.IModuleVisitor;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Loader;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.ServerUtil;

/**
 * Handles "publishing" for servers that can load classes and resources directly
 * from the workspace. Instead of creating and deploying jars to the webapp this
 * simply update the virtual classpath in the context xml file.
 */
public class TomcatPublishModuleVisitor implements IModuleVisitor {

    /**
     * Server base path (Catalina base).
     */
    protected final IPath baseDir;
    
    /**
     * Server instance in which to modify the context
     */
    protected final ServerInstance serverInstance;

    /**
     * Catalina.properties loader to add global classpath entries
     */
    protected final String sharedLoader;
    
    /**
     * Classpath entries added by ear configurations.
     */
    protected final List earCommonResources = new ArrayList();

    /**
     * List of classpath elements that will be used by the custom tomcat loader.
     * This set should include any class dir from referenced project.
     */
    protected Set virtualClasspathElements = new LinkedHashSet();

    /**
     * Instantiate a new TomcatPublishModuleVisitor
     * 
     * @param catalinaBase catalina base path
     * @param webModules list of
     * <code>org.eclipse.jst.server.tomcat.core.internal.WebModule</code>
     * configured for this server
     */
    TomcatPublishModuleVisitor(IPath catalinaBase, ServerInstance serverInstance, String sharedLoader) {
        this.baseDir = catalinaBase;
        this.serverInstance = serverInstance;
        this.sharedLoader = sharedLoader;
    }

    /**
     * @see IModuleVisitor#visitWebComponent(IVirtualComponent)
     */
    public void visitWebComponent(IVirtualComponent component)
            throws CoreException {
        // nothing to do, everything is done in endVisitWebComponent
    }

    /**
     * @see IModuleVisitor#visitArchiveComponent(IPath, IPath)
     */
    public void visitArchiveComponent(IPath runtimePath, IPath workspacePath) {
        addVirtualResource(runtimePath, workspacePath);
    }

    /**
     * @see IModuleVisitor#visitDependentComponent(IPath, IPath)
     */
    public void visitDependentComponent(IPath runtimePath, IPath workspacePath) {
        addVirtualResource(runtimePath, workspacePath);
    }

    /**
     * @see IModuleVisitor#visitWebResource(IPath, IPath)
     */
    public void visitWebResource(IPath runtimePath, IPath workspacePath) {
        addVirtualResource(runtimePath, workspacePath);
    }

    /**
     * @see IModuleVisitor#visitEarResource(IPath, IPath)
     */
    public void visitEarResource(IPath runtimePath, IPath workspacePath) {
        earCommonResources.add(workspacePath.toOSString());
    }

    /**
     * @see IModuleVisitor#endVisitEarComponent(IVirtualComponent)
     */
    public void endVisitEarComponent(IVirtualComponent component)
            throws CoreException {
        if (earCommonResources.size() > 0) {
            try {
                CatalinaPropertiesUtil.addGlobalClasspath(baseDir.append(
                		"conf/catalina.properties").toFile(), sharedLoader,
                		(String[]) earCommonResources.toArray(new String[earCommonResources.size()]));
            } catch (IOException e) {
                Trace.trace(Trace.WARNING, "Unable to add ear path entries to catalina.properties", e);
            } finally {
                earCommonResources.clear();
            }
        }
    }

    /**
     * @see IModuleVisitor#visitClasspathEntry(IPath, IClasspathEntry)
     */
    public void visitClasspathEntry(IPath rtFolder, IClasspathEntry entry) {
        if (entry != null) {
            addVirtualResource(rtFolder, entry.getPath());
        }
    }

    /**
     * {@inheritDoc}
     */
    public void endVisitWebComponent(IVirtualComponent component)
            throws CoreException {

        // track context changes, don't rewrite if not needed
        boolean dirty = false;

        IModule module = ServerUtil.getModule(component.getProject());

        // we need this for the user-specified context path
        Context context = findContext(module);
        if (context == null) {
        	String name = module != null ? module.getName() : component.getName();
    		Trace.trace(Trace.SEVERE, "Could not find context for module " + name);
    		throw new CoreException(new Status(IStatus.ERROR, TomcatPlugin.PLUGIN_ID, 0,
    				NLS.bind(Messages.errorPublishContextNotFound, name), null));
        }

        String contextName = null;
        boolean reloadable = true;

        contextName = context.getPath();
        reloadable = Boolean.valueOf(context.getReloadable()).booleanValue();

        // now strip initial /
        if (contextName.startsWith("/")) {
            contextName = contextName.substring(1);
        }

        // root context is deployed with the "ROOT" name in tomcat
        if ("".equals(contextName)) {
            contextName = "ROOT";
        }

        // handle project context.xml
        Context projectContext = getProjectContextXml(component);

        if (projectContext != null) {
            // copy configuration to server context
            projectContext.copyChildrenTo(context);

            Map attrs = projectContext.getAttributes();
            Iterator iter = attrs.keySet().iterator();
            while (iter.hasNext()) {
                String name = (String) iter.next();
                if (!name.equalsIgnoreCase("path")
                        && !name.equalsIgnoreCase("docBase")
                        && !name.equalsIgnoreCase("source")) {
                    String value = (String) attrs.get(name);
                    String actualValue = context.getAttributeValue(name);
                    if (!value.equals(actualValue)) {
                        context.setAttributeValue(name, value);
                        dirty = true;
                    }
                }
            }
        }

        // handle changes in docBase
        String docBase = component.getRootFolder().getUnderlyingFolder()
                .getLocation().toOSString();
        if (!docBase.equals(context.getDocBase())) {
            dirty = true;
            context.setDocBase(docBase);
        }

        // handle changes in reloadable flag
        if (reloadable != (Boolean.valueOf((context.getReloadable()))
                .booleanValue())) {
            dirty = true;
            context.setReloadable(Boolean.toString(reloadable));
        }

        String path = (contextName.equals("ROOT") ? "" : "/" + contextName);
        // handle changes in the path
        // PATH is required for tomcat 5.0, but ignored in tomcat 5.5
        if (!path.equals(context.getPath())) {
            dirty = true;
            context.setPath(path);
        }

        context.getResources().setClassName(
                "org.eclipse.jst.server.tomcat.loader.WtpDirContext");

        Loader loader = context.getLoader();

        loader.setClassName("org.eclipse.jst.server.tomcat.loader.WtpWebappLoader");

        // required for tomcat 5.5.20 due to the change in
        // http://issues.apache.org/bugzilla/show_bug.cgi?id=39704
        loader.setUseSystemClassLoaderAsParent(Boolean.FALSE.toString());

        // write down the virtual classPath
        StringBuffer buffer = new StringBuffer();
        for (Iterator iterator = virtualClasspathElements.iterator(); iterator
                .hasNext();) {
            buffer.append(iterator.next());
            if (iterator.hasNext()) {
                buffer.append(";");
            }
        }
        virtualClasspathElements.clear();

        String vcp = buffer.toString();

        String oldVcp = loader.getVirtualClasspath();

        if (!vcp.equals(oldVcp)) {
            // save only if needed
            dirty = true;
            loader.setVirtualClasspath(vcp);
            context.getResources().setVirtualClasspath(vcp);
        }

        if (dirty) {
        	//TODO If writing to separate context XML files, save "dirty" status for later use
        }
    }

    private void addVirtualResource(IPath runtimePath, IPath workspacePath) {
        virtualClasspathElements.add(workspacePath.toOSString());
    }

    /**
     * Load a META-INF/context.xml file from project, if available
     * 
     * @param component web component containing the context.xml
     * @return context element containing the context.xml
     * @throws CoreException
     */
    protected Context getProjectContextXml(IVirtualComponent component)
            throws CoreException {

        // load or create module's context.xml document
        IVirtualFile contextFile = (IVirtualFile) component.getRootFolder()
                .findMember("META-INF/context.xml");

        Context contextElement = null;

        if (contextFile != null && contextFile.exists()) {

            Factory factory = new Factory();
            factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");

            InputStream fis = null;
            try {
                fis = contextFile.getUnderlyingFile().getContents();
                contextElement = (Context) factory.loadDocument(fis);
            } catch (Exception e) {
                Trace.trace(Trace.SEVERE, "Exception reading " + contextFile, e);
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return contextElement;
    }

    /**
     * Returns the given module from the config.
     * 
     * @param module a web module
     * @return a web module
     */
    protected Context findContext(IModule module) {
        if (module == null) {
            return null;
        }

        String source = module.getId();
        
        Context [] contexts = serverInstance.getContexts();
        for (int i = 0; i < contexts.length; i++) {
        	if (source.equals(contexts[i].getSource()))
        		return contexts[i];
		}
        return null;
    }
}
