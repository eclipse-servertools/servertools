/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     Naci M. Dai - initial API and implementation
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

package org.eclipse.jst.server.generic.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.ITypeNameRequestor;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.launching.JavaRuntime;
import org.osgi.framework.Bundle;
public class TestProject {
	public IProject project;
	public IJavaProject javaProject;
	private IPackageFragmentRoot sourceFolder;
	public TestProject() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject("Project-1");
		project.create(null);
		project.open(null);
		javaProject = JavaCore.create(project);
		IFolder binFolder = createBinFolder();
		setJavaNature();
		javaProject.setRawClasspath(new IClasspathEntry[0], null);
		createOutputFolder(binFolder);
		addSystemLibraries();
	}
	public IProject getProject() {
		return project;
	}
	public IJavaProject getJavaProject() {
		return javaProject;
	}
	public void addJar(String plugin, String jar) throws MalformedURLException,
			IOException, JavaModelException {
		Path result = findFileInPlugin(plugin, jar);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newLibraryEntry(result, null,
				null);
		javaProject.setRawClasspath(newEntries, null);
	}
	public IPackageFragment createPackage(String name) throws CoreException {
		if (sourceFolder == null)
			sourceFolder = createSourceFolder();
		return sourceFolder.createPackageFragment(name, false, null);
	}
	public IType createType(IPackageFragment pack, String cuName, String source)
			throws JavaModelException {
		StringBuffer buf = new StringBuffer();
		buf.append("package " + pack.getElementName() + ";\n");
		buf.append("\n");
		buf.append(source);
		ICompilationUnit cu = pack.createCompilationUnit(cuName,
				buf.toString(), false, null);
		return cu.getTypes()[0];
	}
	public void dispose() throws CoreException {
		waitForIndexer();
		project.delete(true, true, null);
	}
	private IFolder createBinFolder() throws CoreException {
		IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
		return binFolder;
	}
	
	public IFolder createWebModule(String moduleName, String contents) throws CoreException {
		IFolder moduleFolder = project.getFolder(moduleName);
		moduleFolder.create(false, true, null);
		
		IFolder folder = project.getFolder(moduleName + "/WEB-INF");
		folder.create(false, true, null);
		folder = project.getFolder(moduleName + "/WEB-INF/classes");
		folder.create(false, true, null); 
		folder = project.getFolder(moduleName + "/WEB-INF/lib");
		folder.create(false, true, null);

		ByteArrayInputStream stream = new ByteArrayInputStream(contents.getBytes());
		IFile file = project.getFile(moduleName + "/WEB-INF/web.xml");
		file.create(stream, true, null);

		return moduleFolder;
	}
	
	private void setJavaNature() throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[]{JavaCore.NATURE_ID});
		project.setDescription(description, null);
	}
	private void createOutputFolder(IFolder binFolder)
			throws JavaModelException {
		IPath outputLocation = binFolder.getFullPath();
		javaProject.setOutputLocation(outputLocation, null);
	}
	public IPackageFragmentRoot createSourceFolder() throws CoreException {
		IFolder folder = project.getFolder("src");
		folder.create(false, true, null);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
		javaProject.setRawClasspath(newEntries, null);
		return root;
	}
	private void addSystemLibraries() throws JavaModelException {
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaRuntime
				.getDefaultJREContainerEntry();
		javaProject.setRawClasspath(newEntries, null);
	}
	private Path findFileInPlugin(String plugin, String file)
			throws MalformedURLException, IOException {
		Bundle bundle = Platform.getBundle(plugin);
		URL pluginURL = bundle.getEntry("/");
		URL jarURL = new URL(pluginURL, file);
		URL localJarURL = Platform.asLocalURL(jarURL);
		return new Path(localJarURL.getPath());
	}
	private void waitForIndexer() throws JavaModelException {
		new SearchEngine().searchAllTypeNames(ResourcesPlugin.getWorkspace(),
				null, null, IJavaSearchConstants.EXACT_MATCH,
				IJavaSearchConstants.CASE_SENSITIVE,
				IJavaSearchConstants.CLASS, SearchEngine
						.createJavaSearchScope(new IJavaElement[0]),
				new ITypeNameRequestor() {
					public void acceptClass(char[] packageName,
							char[] simpleTypeName, char[][] enclosingTypeNames,
							String path) {
					}
					public void acceptInterface(char[] packageName,
							char[] simpleTypeName, char[][] enclosingTypeNames,
							String path) {
					}
				}, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
	}
	/**
	 * @return Returns the sourceFolder.
	 */
	public IPackageFragmentRoot getSourceFolder() {
		return sourceFolder;
	}
	/**
	 * @param sourceFolder The sourceFolder to set.
	 */
	public void setSourceFolder(IPackageFragmentRoot sourceFolder) {
		this.sourceFolder = sourceFolder;
	}
}