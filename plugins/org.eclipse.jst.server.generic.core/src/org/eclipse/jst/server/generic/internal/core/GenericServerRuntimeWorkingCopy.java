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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jst.server.core.IGenericRuntimeWorkingCopy;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;

/**
 * Working copy. This does not really do much delegates 
 * to GenericServerRuntime.
 * 
 * @author Gorkem Ercan
 * @see org.eclipse.jst.server.generic.internal.core.GenericServerRuntime
 */
public class GenericServerRuntimeWorkingCopy extends GenericServerRuntime implements IGenericRuntimeWorkingCopy 
{
//	private IRuntimeWorkingCopy wc;
	
	public void initialize(IRuntimeWorkingCopy runtime) {
//		wc = runtime;
	}
    
    public IStatus validate() {
        return super.validate();
    }

    /* (non-Javadoc)
     * @see org.eclipse.jst.server.core.IGenericRuntimeWorkingCopy#setVMInstall(java.lang.String, java.lang.String)
     */
    public void setVMInstall(String typeId, String id) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.model.IRuntimeWorkingCopyDelegate#setDefaults()
     */
    public void setDefaults() {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.wst.server.core.model.IRuntimeWorkingCopyDelegate#handleSave(byte, org.eclipse.core.runtime.IProgressMonitor)
     */
    public void handleSave(byte id, IProgressMonitor monitor) {
        // TODO Auto-generated method stub
        
    }
}
