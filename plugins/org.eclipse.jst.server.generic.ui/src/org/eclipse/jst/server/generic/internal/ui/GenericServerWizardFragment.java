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
package org.eclipse.jst.server.generic.internal.ui;

import java.util.Map;
import org.eclipse.jst.server.generic.internal.core.GenericServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.jst.server.generic.ui.GenericServerUIMessages;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ITaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
/**
 * 
 *
 * @author Gorkem Ercan
 */
public class GenericServerWizardFragment extends ServerDefinitionTypeAwareWizardFragment 
{

    private ServerTypeDefinitionGroup fComposite;
    private boolean flag=false;
    private Map fProperties; 
	/* (non-Javadoc)
	 * @see com.ibm.wtp.server.ui.wizard.IWizardFragment#isComplete()
	 */
	public boolean isComplete() {
//		IServerWorkingCopy serverWorkingCopy = (IServerWorkingCopy)getTaskModel().getObject(ITaskModel.TASK_SERVER);
		
		//TODO implement
		return flag;
	}

	public void createContent(Composite parent, IWizardHandle handle){
		createBody(parent,handle);
	}
	/**
	 * 
	 */
	private void createBody(Composite parent, IWizardHandle handle) 
	{
		IServerWorkingCopy server = getServer();
		ServerRuntime definition = getServerTypeDefinitionFor(server);
		fComposite = new ServerTypeDefinitionGroup(this, definition,ServerTypeDefinitionGroup.CONTEXT_SERVER, null,parent);
		flag=true;
	}

	/**
     * @param server
     * @return
     */
    private ServerRuntime getServerTypeDefinitionFor(IServerWorkingCopy server) {
        String ID = server.getRuntime().getAttribute(GenericServerRuntime.SERVER_DEFINITION_ID,(String)null);
		Map runtimeProperties = server.getRuntime().getAttribute(GenericServerRuntime.SERVER_INSTANCE_PROPERTIES,(Map)null);
		ServerRuntime definition = getServerTypeDefinition(ID,runtimeProperties);
        return definition;
    }

    /**
     * @return
     */
    private IServerWorkingCopy getServer() {
        IServerWorkingCopy server = (IServerWorkingCopy)getTaskModel().getObject(ITaskModel.TASK_SERVER);
        return server;
    }

    public void enter() {
        IServerWorkingCopy server = getServer();
        ServerRuntime definition = getServerTypeDefinitionFor(server);
        fComposite.reset(definition,ServerTypeDefinitionGroup.CONTEXT_SERVER,null);
	}
	public void exit(){
	    fProperties = fComposite.getProperties();
	    serverDefinitionTypePropertiesChanged();
	}
	
	protected Map getServerProperties(){
	    return fProperties;
	}
	
    /* (non-Javadoc)
     * @see org.eclipse.jst.server.generic.internal.ui.ServerDefinitionTypeAwareWizardFragment#description()
     */
    public String description() {
        return  GenericServerUIMessages.getString("serverWizardDescription");
    }

    /* (non-Javadoc)
     * @see org.eclipse.jst.server.generic.internal.ui.ServerDefinitionTypeAwareWizardFragment#title()
     */
    public String title() {
        return  GenericServerUIMessages.getString("serverWizardTitle");
    }

    /* (non-Javadoc)
     * @see org.eclipse.jst.server.generic.internal.ui.ServerDefinitionTypeAwareWizardFragment#serverDefinitionTypePropertiesChanged()
     */
    public void serverDefinitionTypePropertiesChanged() {
        fProperties = fComposite.getProperties();
        IServerWorkingCopy serverWorkingCopy = getServer();
        ServerRuntime definition = getServerTypeDefinitionFor(serverWorkingCopy);
        
        serverWorkingCopy.setName(GenericServerUIMessages.getFormattedString("serverName",new String[] {definition.getName()}));
        serverWorkingCopy.setAttribute(GenericServerRuntime.SERVER_INSTANCE_PROPERTIES,getServerProperties());
    }
}
