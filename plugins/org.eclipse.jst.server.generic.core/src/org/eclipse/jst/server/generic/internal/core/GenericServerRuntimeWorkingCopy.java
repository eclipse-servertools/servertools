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
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jst.server.core.IGenericRuntimeWorkingCopy;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;

public class GenericServerRuntimeWorkingCopy implements
		IGenericRuntimeWorkingCopy {

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.java.core.IGenericRuntimeWorkingCopy#setVMInstall(java.lang.String, java.lang.String)
	 */
	public void setVMInstall(String typeId, String id) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.java.core.IGenericRuntime#getVMInstallTypeId()
	 */
	public String getVMInstallTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.java.core.IGenericRuntime#getVMInstallId()
	 */
	public String getVMInstallId() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.java.core.IGenericRuntime#getVMInstall()
	 */
	public IVMInstall getVMInstall() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IRuntimeDelegate#validate()
	 */
	public IStatus validate() {
		// TODO Auto-generated method stub
		return Status.OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IRuntimeWorkingCopyDelegate#initialize(com.ibm.wtp.server.core.IRuntimeWorkingCopy)
	 */
	public void initialize(IRuntimeWorkingCopy runtime) {
		
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IRuntimeWorkingCopyDelegate#setDefaults()
	 */
	public void setDefaults() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IRuntimeWorkingCopyDelegate#handleSave(byte, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void handleSave(byte id, IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IRuntimeDelegate#initialize(com.ibm.wtp.server.core.IRuntime)
	 */
	public void initialize(IRuntime runtime) {
		
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.core.model.IRuntimeDelegate#dispose()
	 */
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
