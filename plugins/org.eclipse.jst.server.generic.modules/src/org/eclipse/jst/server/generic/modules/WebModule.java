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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.j2ee.IWebModule;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IModuleArtifact;
import org.eclipse.wst.server.core.IModuleType;
import org.eclipse.wst.server.core.internal.ModuleType;
import org.eclipse.wst.server.core.model.IModuleListener;

public class WebModule extends J2EEModule implements IWebModule {
	
	private String servletSpec = null;
	private String jspSpec     = null;

	public WebModule(IFolder moduleFolder) {
		super(moduleFolder);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.j2ee.IWebModule#getServletSpecificationVersion()
	 */
	public String getServletSpecificationVersion() {
//		  web-app_2_4.xsd
//        web-app_2_3.dtd
//		  else 2.2
		if(servletSpec == null)
			servletSpec = "2.2";
		return servletSpec;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.j2ee.IWebModule#getJSPSpecificationVersion()
	 */
	public String getJSPSpecificationVersion() {
//		if servlet 2.4  -> 2.0
//      else servlet 2.3 -> 1.2
//		else 1.1;
		if(servletSpec == null)
			servletSpec = "1.1";
		return jspSpec;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.j2ee.IWebModule#getContextRoot()
	 */
	public String getContextRoot() {
		return this.getFolder().getName();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.j2ee.IWebModule#isPublishRequired()
	 */
	public boolean isPublishRequired() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.j2ee.IJ2EEModule#isBinary()
	 */
	public boolean isBinary() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModule#members()
	 */
	public IModuleArtifact[] members() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModule#addModuleListener(org.eclipse.wst.server.core.model.IModuleListener)
	 */
	public void addModuleListener(IModuleListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModule#removeModuleListener(org.eclipse.wst.server.core.model.IModuleListener)
	 */
	public void removeModuleListener(IModuleListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IModule#getChildModules()
	 */
	public IModule[] getChildModules() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.IModuleType#getType()
	 */
	public String getType() {
		return "j2ee.web";
	}

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.IModule#validate(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IStatus validate(IProgressMonitor monitor) {
		try {
			if(Utils.isValidWebModule(this.getFolder()))
				return new Status(IStatus.OK,ModulesPlugin.ID, 0,"",null);
		} catch (Exception e) {
			Trace.trace("Unale to validate Web Module", e);
		}
		return new Status(IStatus.ERROR,ModulesPlugin.ID, 0,"",null);
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.IModule#getModuleType()
     */
    public IModuleType getModuleType() {
        // TODO Auto-generated method stub
        return new ModuleType(getType(), getJ2EESpecificationVersion());
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.IModule#getChildModules(org.eclipse.core.runtime.IProgressMonitor)
     */
    public IModule[] getChildModules(IProgressMonitor monitor) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }
}
