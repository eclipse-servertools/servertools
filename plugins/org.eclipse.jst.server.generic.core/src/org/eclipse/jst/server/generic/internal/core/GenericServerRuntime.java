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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jst.server.core.IGenericRuntime;
import org.eclipse.jst.server.generic.core.CorePlugin;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.ServerUtil;
/**
 * Generic server runtime support.
 *
 * @author Gorkem Ercan
 */
public class GenericServerRuntime implements IGenericRuntime
{

	private IRuntime fRuntime;
	public static final String SERVER_DEFINITION_ID = "server_definition_id";
	public static final String SERVER_INSTANCE_PROPERTIES = "generic_server_instance_properties";
	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstallTypeId()
	 */
	public String getVMInstallTypeId() {
		return JavaRuntime.getDefaultVMInstall().getVMInstallType().getId();
		// TODO configurable.
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstallId()
	 */
	public String getVMInstallId() {
		return JavaRuntime.getDefaultVMInstall().getId();
		// TODO configurable.
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#getVMInstall()
	 */
	public IVMInstall getVMInstall() {
		return JavaRuntime.getDefaultVMInstall();
		// TODO configurable
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jst.server.core.IGenericRuntime#validate()
	 */
	public IStatus validate() {
		if (fRuntime.getName() == null || fRuntime.getName().length() == 0)
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, CorePlugin.getResourceString("%errorName"), null);

		if (ServerUtil.isNameInUse(fRuntime))
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, CorePlugin.getResourceString("%errorDuplicateRuntimeName"), null);
		
//		IPath path = fRuntime.getLocation();
//		if (path == null || path.isEmpty())
//			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, "", null);
//		else if (!path.toFile().exists())
//			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, CorePlugin.getResourceString("%errorLocation"), null);
//		else 
		if (getVMInstall() == null)
			return new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, 0, CorePlugin.getResourceString("%errorJRE"), null);
		return new Status(IStatus.OK, CorePlugin.PLUGIN_ID, 0, "", null);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntimeDelegate#initialize(org.eclipse.wst.server.core.IRuntime)
	 */
	public void initialize(IRuntime runtime) {
		this.fRuntime = runtime;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.server.core.model.IRuntimeDelegate#dispose()
	 */
	public void dispose() {
		this.fRuntime=null;
	}
	
}
