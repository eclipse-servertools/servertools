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

package org.eclipse.jst.server.generic.internal.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerConfiguration;
import org.eclipse.wst.server.core.IServerConfigurationWorkingCopy;
import org.eclipse.wst.server.core.model.IServerConfigurationWorkingCopyDelegate;
import org.eclipse.wst.server.core.util.ProgressUtil;


public class GenericServerConfigurationWorkingCopy implements
		IServerConfigurationWorkingCopyDelegate {
	
	IServerConfigurationWorkingCopy configurationWC;
	IServerConfiguration  configuration;

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationWorkingCopyDelegate#initialize(com.ibm.wtp.server.core.IServerConfigurationWorkingCopy)
	 */
	public void initialize(IServerConfigurationWorkingCopy configuration) {
		this.configurationWC = configuration;
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationWorkingCopyDelegate#setDefaults()
	 */
	public void setDefaults() {
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationWorkingCopyDelegate#importFromPath(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void importFromPath(IPath path, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		System.out.println("importFromPath - " + path);

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationWorkingCopyDelegate#importFromRuntime(com.ibm.wtp.server.core.IRuntime, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void importFromRuntime(IRuntime runtime, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		System.out.println("importFromRuntime - " + runtime);

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationDelegate#initialize(com.ibm.wtp.server.core.IServerConfiguration)
	 */
	public void initialize(IServerConfiguration configuration) {
		this.configuration = configuration;
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationDelegate#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub
		System.out.println("dispose - ");

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationDelegate#load(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void load(IPath path, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		System.out.println("load - " + path);

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationDelegate#load(org.eclipse.core.resources.IFolder, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void load(IFolder folder, IProgressMonitor monitor)
			throws CoreException {
		// TODO Auto-generated method stub
		System.out.println("load - " + folder);

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationDelegate#save(org.eclipse.core.runtime.IPath, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void save(IPath path, IProgressMonitor monitor) throws CoreException {

		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
			boolean forceDirty = false;
			// make sure directory exists
			if (!path.toFile().exists()) {
				forceDirty = true;
				path.toFile().mkdir();
			}
			monitor.worked(1);
	
			monitor.worked(1);
	
	
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace("Could not save Generic Server configuration to " + path.toString(), e);
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, "Could Not Save Configuration", e));
		}
			System.out.println("save - " + path);

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IServerConfigurationDelegate#save(org.eclipse.core.resources.IFolder, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void save(IFolder folder, IProgressMonitor monitor)
			throws CoreException {

		try {
			monitor = ProgressUtil.getMonitorFor(monitor);
	
			// save server.xml
			byte[] data = "".getBytes();
			InputStream in = new ByteArrayInputStream(data);
			IFile file = folder.getFile("server.xml");
			if (file.exists()) {
				file.setContents(in, true, true, ProgressUtil.getSubMonitorFor(monitor, 200));
			} else
				file.create(in, true, ProgressUtil.getSubMonitorFor(monitor, 200));
	
	
	
			if (monitor.isCanceled())
				return;
			monitor.done();
		} catch (Exception e) {
			Trace.trace("Could not save Generic Server configuration to " + folder.toString(), e);
			throw new CoreException(new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, "Could Not Save Configuration", e));
		}
		System.out.println("save - " + folder);

	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IServerConfigurationWorkingCopyDelegate#handleSave(byte, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void handleSave(byte id, IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		System.out.println("handleSave - " + id);
		
	}

}
