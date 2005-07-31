/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Gorkem Ercan - initial API and implementation
 *     Naci M. Dai
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL ETERATION A.S. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Eteration Bilisim A.S.  For more
 * information on eteration, please see
 * <http://www.eteration.com/>.
 ***************************************************************************/
package org.eclipse.jst.server.generic.core.internal;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.FolderSourceContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * SourcePathComputer for the GenericLaunchConfiguration.
 * 
 * @author Gorkem Ercan
 */
public class GenericServerSourcePathComputerDelegate implements ISourcePathComputerDelegate  {

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate#computeSourceContainers(org.eclipse.debug.core.ILaunchConfiguration, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISourceContainer[] computeSourceContainers(ILaunchConfiguration configuration, IProgressMonitor monitor) throws CoreException {
		
		IRuntimeClasspathEntry[] unresolvedEntries = JavaRuntime.computeUnresolvedSourceLookupPath(configuration);
		List sourcefolderList = new ArrayList();
		
		IServer server =  ServerUtil.getServer(configuration);
		IModule[] modules = server.getModules();
		List javaProjectList = new ArrayList();
		
		for (int i = 0; i < modules.length; i++) {
			IProject project = modules[i].getProject();
			if (project != null) {
				IFolder moduleFolder = project.getFolder(modules[i].getName());
				if (moduleFolder.exists()) {
					sourcefolderList.add(new FolderSourceContainer(moduleFolder, true));
				} else {
					try {
						if (project.hasNature(JavaCore.NATURE_ID)) {
							IJavaProject javaProject = (IJavaProject) project.getNature(JavaCore.NATURE_ID);
							javaProjectList.add(javaProject);
						}
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}

		IRuntimeClasspathEntry[] projectEntries = new IRuntimeClasspathEntry[javaProjectList.size()];
		for (int i = 0; i < javaProjectList.size(); i++) {
			projectEntries[i] = JavaRuntime.newProjectRuntimeClasspathEntry((IJavaProject)javaProjectList.get(i)); 
		}
		IRuntimeClasspathEntry[] entries =  new IRuntimeClasspathEntry[projectEntries.length+unresolvedEntries.length]; 
		System.arraycopy(unresolvedEntries,0,entries,0,unresolvedEntries.length);
		System.arraycopy(projectEntries,0,entries,unresolvedEntries.length,projectEntries.length);
		
		IRuntimeClasspathEntry[] resolved = JavaRuntime.resolveSourceLookupPath(entries, configuration);
		ISourceContainer[] javaSourceContainers = JavaRuntime.getSourceContainers(resolved);
		
		if (!sourcefolderList.isEmpty()) {
			ISourceContainer[] combinedSourceContainers = new ISourceContainer[javaSourceContainers.length + sourcefolderList.size()];
			sourcefolderList.toArray(combinedSourceContainers);
			System.arraycopy(javaSourceContainers, 0, combinedSourceContainers, sourcefolderList.size(), javaSourceContainers.length);
			javaSourceContainers = combinedSourceContainers;
		}
		
		return javaSourceContainers;
		
	}
}
