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
package org.eclipse.jst.server.tomcat.core.internal.wst;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jst.server.tomcat.core.internal.TomcatPlugin;
import org.eclipse.jst.server.tomcat.core.internal.Trace;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.UnresolveableURIException;
import org.eclipse.wst.common.componentcore.internal.ComponentResource;
import org.eclipse.wst.common.componentcore.internal.ReferencedComponent;
import org.eclipse.wst.common.componentcore.internal.StructureEdit;
import org.eclipse.wst.common.componentcore.internal.WorkbenchComponent;
import org.eclipse.wst.common.componentcore.internal.impl.ModuleURIUtil;
import org.eclipse.wst.common.componentcore.internal.impl.PlatformURLModuleConnection;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.server.core.IModule;

/**
 * Temporary solution for https://bugs.eclipse.org/bugs/show_bug.cgi?id=103888
 */
public class ModuleTraverser {

	/**
	 * Facet type for EAR modules
	 */
    public static final String EAR_MODULE = IModuleConstants.JST_EAR_MODULE;

    /**
     * Facet type for Web modules
     */
    public static final String WEB_MODULE = IModuleConstants.JST_WEB_MODULE;

    /**
     * Facet type for utility modules
     */
    public static final String UTILITY_MODULE = IModuleConstants.JST_UTILITY_MODULE;

    /**
     * Scans the module using the specified visitor.
     * 
     * @param module module to traverse
     * @param visitor visitor to handle resources
     * @param monitor a progress monitor
     * @throws CoreException
     */
    public static void traverse(IModule module, IModuleVisitor visitor,
            IProgressMonitor monitor) throws CoreException {
        if (module == null || module.getModuleType() == null)
            return;

        String typeId = module.getModuleType().getId();
        IVirtualComponent component = ComponentCore.createComponent(module.getProject());

        if (component == null) {
            // can happen if project has been closed
            Trace.trace(Trace.WARNING, "Unable to create component for module "
                    + module.getName());
            return;
        }

        if (EAR_MODULE.equals(typeId)) {
            traverseEarComponent(component, visitor, monitor);
        } else if (WEB_MODULE.equals(typeId)) {
            traverseWebComponent(component, visitor, monitor);
        }
    }

    private static void traverseEarComponent(IVirtualComponent component,
            IModuleVisitor visitor, IProgressMonitor monitor)
            throws CoreException {
    	// Currently the JST Server portion of WTP may not depend on the JST Enterprise portion of WTP
/*        EARArtifactEdit earEdit = EARArtifactEdit
                .getEARArtifactEditForRead(component);
        if (earEdit != null) {
            IVirtualReference[] j2eeComponents = earEdit.getJ2EEModuleReferences();
            for (int i = 0; i < j2eeComponents.length; i++) {
                traverseWebComponent(
                        j2eeComponents[i].getReferencedComponent(), visitor,
                        monitor);
            }
            IVirtualReference[] jarComponents = earEdit.getUtilityModuleReferences();
            for (int i = 0; i < jarComponents.length; i++) {
                IVirtualReference jarReference = jarComponents[i];
                IVirtualComponent jarComponent = jarReference
                        .getReferencedComponent();
                IProject dependentProject = jarComponent.getProject();
                if (!dependentProject.hasNature(JavaCore.NATURE_ID))
                    continue;
                IJavaProject project = JavaCore.create(dependentProject);
                IClasspathEntry cpe = getClasspathEntry(project, jarComponent
                        .getRootFolder().getProjectRelativePath());
                visitor.visitEarResource(null, getOSPath(dependentProject,
                        project, cpe.getOutputLocation()));
            }
        }*/
        visitor.endVisitEarComponent(component);
    }

    private static void traverseWebComponent(IVirtualComponent component,
            IModuleVisitor visitor, IProgressMonitor monitor)
            throws CoreException {

        visitor.visitWebComponent(component);

        IProject proj = component.getProject();
        StructureEdit warStruct = StructureEdit.getStructureEditForRead(proj);
        try {
            WorkbenchComponent comp = warStruct.getComponent();
            if (comp == null) {
                Trace.trace(Trace.SEVERE,
                        "Error getting WorkbenchComponent from war project. IProject=\""
                                + proj + "\" StructureEdit=\"" + warStruct
                                + "\" WorkbenchComponent=\"" + comp + "\"");
                return;
            }
            traverseWebComponentLocalEntries(comp, visitor, monitor);

            // traverse referenced components
            List children = comp.getReferencedComponents();
            for (Iterator itor = children.iterator(); itor.hasNext();) {
                ReferencedComponent childRef = (ReferencedComponent) itor.next();
                IPath rtFolder = childRef.getRuntimePath();
                URI refHandle = childRef.getHandle();

                if (PlatformURLModuleConnection.CLASSPATH.equals(
                		refHandle.segment(ModuleURIUtil.ModuleURI.SUB_PROTOCOL_INDX))) {
                    IJavaProject jproj = JavaCore.create(proj);
                    String classpathKind = refHandle.segment(1);
                    IPath classpathRef = getSuffixPath(refHandle, 2);

                    visitor.visitClasspathEntry(rtFolder, getClasspathEntry(
                            jproj, classpathKind, classpathRef));
                } else {
                    try {
                        WorkbenchComponent childCom = warStruct.findComponentByURI(refHandle);
                        if (childCom == null) {
                            continue;
                        }

                        traverseDependentEntries(visitor, rtFolder, childCom,
                                monitor);
                    } catch (UnresolveableURIException e) {
                        TomcatPlugin.log(e);
                    }
                }
            }
        } finally {
            warStruct.dispose();
        }

        visitor.endVisitWebComponent(component);
    }

    private static IPath getSuffixPath(URI uri, int index) {
        StringBuffer result = new StringBuffer();
        String[] segments = uri.segments();
        for (int i = index; i < segments.length; i++) {
            if (i > index)
                result.append('/');
            result.append(segments[i]);
        }
        return new Path(result.toString());
    }

    private static void traverseWebComponentLocalEntries(
            WorkbenchComponent comp, IModuleVisitor visitor,
            IProgressMonitor monitor) throws CoreException {
        IProject warProject = StructureEdit.getContainingProject(comp);
        if (warProject == null || !warProject.hasNature(JavaCore.NATURE_ID)) {
            return;
        }
        IJavaProject project = JavaCore.create(warProject);

        List res = comp.getResources();
        for (Iterator itorRes = res.iterator(); itorRes.hasNext();) {
            ComponentResource childComp = (ComponentResource) itorRes.next();
            IClasspathEntry cpe = getClasspathEntry(project, childComp.getSourcePath());
            if (cpe == null)
                continue;
            visitor.visitWebResource(childComp.getRuntimePath(), getOSPath(
                    warProject, project, cpe.getOutputLocation()));
        }
    }

    private static void traverseDependentEntries(IModuleVisitor visitor,
            IPath runtimeFolder, WorkbenchComponent component,
            IProgressMonitor monitor) throws CoreException {
        IProject dependentProject = StructureEdit.getContainingProject(component);
        if (!dependentProject.hasNature(JavaCore.NATURE_ID))
            return;
        IJavaProject project = JavaCore.create(dependentProject);

        String name = component.getName(); // assume it is the same as URI

        // go thru all entries
        List res = component.getResources();
        for (Iterator itorRes = res.iterator(); itorRes.hasNext();) {
            ComponentResource childComp = (ComponentResource) itorRes.next();
            IPath rtPath = childComp.getRuntimePath();
            IClasspathEntry cpe = getClasspathEntry(project, childComp.getSourcePath());
            if (cpe == null)
                continue;
            visitor.visitDependentComponent(runtimeFolder.append(rtPath)
                    .append(name + ".jar"), getOSPath(dependentProject,
                    project, cpe.getOutputLocation()));
        }
    }

    private static IClasspathEntry getClasspathEntry(IJavaProject project,
            IPath sourcePath) throws JavaModelException {
        sourcePath = project.getPath().append(sourcePath);
        IClasspathEntry[] cp = project.getRawClasspath();
        for (int i = 0; i < cp.length; i++) {
            if (sourcePath.equals(cp[i].getPath()))
                return JavaCore.getResolvedClasspathEntry(cp[i]);
        }
        return null;
    }

    private static IClasspathEntry getClasspathEntry(IJavaProject project,
            String classpathKind, IPath classpathRef) throws JavaModelException {
        int entryKind;
        if (VirtualArchiveComponent.LIBARCHIVETYPE.equals(classpathKind)) {
            entryKind = IClasspathEntry.CPE_LIBRARY;
        } else if (VirtualArchiveComponent.VARARCHIVETYPE.equals(classpathKind)) {
            entryKind = IClasspathEntry.CPE_VARIABLE;
        } else {
            return null;
        }
        IClasspathEntry[] cp = project.getRawClasspath();
        for (int i = 0; i < cp.length; i++) {
            if (entryKind == cp[i].getEntryKind()
                    && classpathRef.equals(cp[i].getPath())) {
                return JavaCore.getResolvedClasspathEntry(cp[i]);
            }
        }
        return null;
    }

    private static IPath getOSPath(IProject project, IJavaProject javaProject,
            IPath outputPath) throws JavaModelException {
        if (outputPath == null)
            outputPath = javaProject.getOutputLocation();
        return ResourcesPlugin.getWorkspace().getRoot().getFolder(outputPath)
                .getLocation();
    }

}
