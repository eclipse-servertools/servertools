/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.server.generic.core.internal.GenericServer;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * 
 * 
 * @author Gorkem Ercan
 */
public class GenericServerWizardFragment extends
        ServerDefinitionTypeAwareWizardFragment {
    private GenericServerCompositeDecorator[] fDecorators;

    /**
     * 
     */
    public boolean isComplete() {

        ServerRuntime serverRuntime = getServerTypeDefinitionFor( getServer() );
        if( serverRuntime == null )
            return false;

        IServerWorkingCopy server = getServer();
        GenericServer dl = (GenericServer) server.loadAdapter(
                GenericServer.class, null );

        IStatus status = dl.validate();
        return (status != null && status.isOK());

    }

    public void createContent( Composite parent, IWizardHandle handle ) {
        IServerWorkingCopy server = getServer();
        GenericServer dl = (GenericServer) server.loadAdapter(
                GenericServer.class, null );
        ServerRuntime definition = getServerTypeDefinitionFor( server );
        fDecorators = new GenericServerCompositeDecorator[1];
        fDecorators[0] = new ServerTypeDefinitionServerDecorator( definition,
                null, getWizard(), dl );
        new GenericServerComposite( parent, fDecorators );

    }

    /**
     * @param server
     * @return
     */
    private ServerRuntime getServerTypeDefinitionFor( IServerWorkingCopy server ) {
        GenericServerRuntime runtime = (GenericServerRuntime) server
                .getRuntime().getAdapter( GenericServerRuntime.class );
        if( runtime == null )
        {
            IRuntime wc = (IRuntime) getTaskModel().getObject(
                    TaskModel.TASK_RUNTIME );
            runtime = (GenericServerRuntime) wc
                    .getAdapter( GenericServerRuntime.class );
            if( runtime == null )
                runtime = (GenericServerRuntime) wc.loadAdapter(
                        GenericServerRuntime.class, new NullProgressMonitor() );
        }
        String serverTyepId = server.getServerType().getId();
        String runtimeTypeId = runtime.getRuntime().getRuntimeType().getId();
        if( runtimeTypeId == null )
        {
            return null;
        }
        Map runtimeProperties = runtime.getServerInstanceProperties();
        ServerRuntime definition = getServerTypeDefinition( serverTyepId,
                runtimeTypeId, runtimeProperties );
        return definition;
    }

    /**
     * @return
     */
    private IServerWorkingCopy getServer() {
        IServerWorkingCopy server = (IServerWorkingCopy) getTaskModel()
                .getObject( TaskModel.TASK_SERVER );
        return server;
    }

    public void enter() {
        if(fDecorators == null ){
            return;
        }
        for( int i = 0; i < fDecorators.length; i++ )
        {
            if( fDecorators[i].validate() )//failed do not continue
                return;
        }
    }

    public void exit() {
        if(fDecorators == null ){
            return;
        }
        // validate needed to save the latest values.
        for( int i = 0; i < fDecorators.length; i++ )
        {
            if( fDecorators[i].validate() )//failed do not continue
                return;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jst.server.generic.internal.ui.ServerDefinitionTypeAwareWizardFragment#description()
     */
    public String description() {
        String sName = getServerName();
        if( sName == null || sName.length() < 1 )
            sName = "Generic"; //$NON-NLS-1$
        return NLS.bind(
                GenericServerUIMessages.serverWizardDescription, sName );
    }

    private String getServerName() {
        if( getServer() != null && getServer().getRuntime() != null )
            return getServer().getRuntime().getRuntimeType().getName();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jst.server.generic.internal.ui.ServerDefinitionTypeAwareWizardFragment#title()
     */
    public String title() {
        String sName = getServerName();
        if( sName == null || sName.length() < 1 )
            sName = "Generic"; //$NON-NLS-1$
        return NLS.bind(
                GenericServerUIMessages.serverWizardTitle, sName );
    }
}
