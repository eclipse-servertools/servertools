/*******************************************************************************
 * Copyright (c) 2004 Eteration Bilisim A.S. All rights reserved.   This program
 * and the accompanying materials are made available under the terms of the
 * Common Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation 
 *               Naci M. Dai
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ETERATIOn A.S.
 * OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the Eteration Bilisim A.S. For more information on eteration,
 * please see <http://www.eteration.com/>.
 ******************************************************************************/
package org.eclipse.jst.server.generic.internal.core;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.wst.server.core.IServerState;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITask;
import org.eclipse.wst.server.core.model.IModule;
import org.eclipse.wst.server.core.model.IModuleEvent;
import org.eclipse.wst.server.core.model.IModuleFactoryEvent;
import org.eclipse.wst.server.core.model.IPublisher;
import org.eclipse.wst.server.core.model.IServerWorkingCopyDelegate;
import org.eclipse.wst.server.core.resources.IModuleResourceDelta;

public class GenericServerWorkingCopy extends GenericServer implements
		IServerWorkingCopyDelegate {

	protected IServerWorkingCopy workingCopy;

	private IServerState liveServer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerWorkingCopyDelegate#initialize(com.ibm.wtp.server.core.IServerWorkingCopy)
	 */
	public void initialize(IServerWorkingCopy workingCopy) {
		this.workingCopy = workingCopy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerWorkingCopyDelegate#setDefaults()
	 */
	public void setDefaults() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerWorkingCopyDelegate#modifyModules(com.ibm.wtp.server.core.model.IModule[],
	 *      com.ibm.wtp.server.core.model.IModule[],
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void modifyModules(IModule[] add, IModule[] remove,
			IProgressMonitor monitor) throws CoreException {
	    //todo implement module add/remove.
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#initialize(com.ibm.wtp.server.core.IServerState)
	 */
	public void initialize(IServerState liveServer) {
		this.liveServer = liveServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#getPublisher(java.util.List,
	 *      com.ibm.wtp.server.core.model.IModule)
	 */
	public IPublisher getPublisher(List parents, IModule module) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#updateConfiguration()
	 */
	public void updateConfiguration() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#updateModule(com.ibm.wtp.server.core.model.IModule,
	 *      com.ibm.wtp.server.core.resources.IModuleResourceDelta)
	 */
	public void updateModule(IModule module, IModuleResourceDelta delta) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#publishStart(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus publishStart(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#publishConfiguration(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus publishConfiguration(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#publishStop(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public IStatus publishStop(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#canModifyModules(com.ibm.wtp.server.core.model.IModule[],
	 *      com.ibm.wtp.server.core.model.IModule[])
	 */
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		// TODO Auto-generated method stub
		return Status.OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#getModules()
	 */
	public IModule[] getModules() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#getRepairCommands(com.ibm.wtp.server.core.model.IModuleFactoryEvent[],
	 *      com.ibm.wtp.server.core.model.IModuleEvent[])
	 */
	public ITask[] getRepairCommands(IModuleFactoryEvent[] factoryEvent,
			IModuleEvent[] moduleEvent) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#getChildModules(com.ibm.wtp.server.core.model.IModule)
	 */
	public List getChildModules(IModule module) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#getParentModules(com.ibm.wtp.server.core.model.IModule)
	 */
	public List getParentModules(IModule module) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.wtp.server.core.model.IServerDelegate#setLaunchDefaults(org.eclipse.debug.core.ILaunchConfigurationWorkingCopy)
	 */
	public void setLaunchDefaults(ILaunchConfigurationWorkingCopy workingCopy) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerWorkingCopyDelegate#handleSave(byte,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void handleSave(byte id, IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.server.core.model.IServerDelegate#getModuleState(org.eclipse.wst.server.core.model.IModule)
	 */
	public byte getModuleState(IModule module) {
		// TODO Auto-generated method stub
		return 0;
	}

}