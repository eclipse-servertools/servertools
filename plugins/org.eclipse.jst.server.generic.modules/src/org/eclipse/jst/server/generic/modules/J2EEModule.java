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

package org.eclipse.jst.server.generic.modules;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;


public abstract class J2EEModule implements IModule {

	private IFolder folder;
	
	public J2EEModule(IFolder moduleFolder) {
		
		this.folder = moduleFolder;
	}

	public String getJ2EESpecificationVersion() {
		return "1.2";
	}

	public IPath getLocation() {
		return this.folder.getLocation();
	}

	public IProject getProject() {
		return this.getFolder().getProject();
	}


	public IFolder getFolder() {
		return folder;
	}

	public String getFactoryId() {
		return "org.eclipse.jst.server.generic.modules.j2eeSpecModules";
	}

	public String getId() {
		return getName();
	}

	public IStatus canPublish() {
		return validate(new NullProgressMonitor());
	}

	public String getName() {
		return getFolder().getName();
	}

	public boolean exists() {
		return getFolder().exists();
	}
	public String getVersion() {
		return getJ2EESpecificationVersion();
	}

	/**
	 * @param resource
	 * @return
	 */
	public IModuleArtifact getModuleObject(IResource resource) {
		return null;
	}


	

}
