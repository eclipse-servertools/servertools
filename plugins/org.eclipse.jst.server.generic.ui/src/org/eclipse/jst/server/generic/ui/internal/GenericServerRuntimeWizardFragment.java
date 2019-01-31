/***************************************************************************************************
 * Copyright (c) 2005, 2007 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import java.util.Map;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.core.internal.IInstallableRuntime;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * A serverDefinitionType aware wizard for defining runtimes.
 * 
 * @author Gorkem Ercan
 */
public class GenericServerRuntimeWizardFragment extends
        ServerDefinitionTypeAwareWizardFragment {

    private GenericServerCompositeDecorator[] fDecorators;

    /**
     * Constructor
     */
    public GenericServerRuntimeWizardFragment() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.server.ui.wizard.IWizardFragment#isComplete()
     */
    public boolean isComplete() {
        RuntimeDelegate runtime = getRuntimeDelegate();
        if( runtime == null )
            return false;
        IStatus status = runtime.validate();
        return (status != null && status.isOK());
    }

    public void createContent( Composite parent, IWizardHandle handle ) {
        Map properties = null;
        ServerRuntime definition = null;
        if( getRuntimeDelegate() != null )
        {
            properties = getRuntimeDelegate().getServerInstanceProperties();
            definition = getServerTypeDefinition( getServerDefinitionId(),
                    properties );
        }
        IInstallableRuntime ir = ServerPlugin
                .findInstallableRuntime( getRuntimeDelegate().getRuntime()
                        .getRuntimeType().getId() );
        if( ir != null )
        {
            fDecorators = new GenericServerCompositeDecorator[3];
            fDecorators[0] = new JRESelectDecorator( getRuntimeDelegate(), getWizard() );
            fDecorators[1] = new ServerTypeDefinitionRuntimeDecorator(
                    definition, properties, getWizard(), getRuntimeDelegate() );
            fDecorators[2] = new InstallableRuntimeDecorator( getWizard(),
                    getRuntimeDelegate() );
        } else
        {
            fDecorators = new GenericServerCompositeDecorator[2];
            fDecorators[0] = new JRESelectDecorator( getRuntimeDelegate(), getWizard() );
            fDecorators[1] = new ServerTypeDefinitionRuntimeDecorator(
                    definition, properties, getWizard(), getRuntimeDelegate() );
        }

        new GenericServerComposite( parent, fDecorators );
    }

    private String getServerDefinitionId() {
        String currentDefinition = null;
        if( getRuntimeDelegate() != null )
            currentDefinition = getRuntimeDelegate().getRuntime()
                    .getRuntimeType().getId();
        if( currentDefinition != null && currentDefinition.length() > 0 )
        {
            return currentDefinition;
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.wst.server.ui.wizard.IWizardFragment#enter()
     */
    public void enter() {
        if( getRuntimeDelegate() != null )
            getRuntimeDelegate().getRuntimeWorkingCopy().setName( createName() );

        for( int i = 0; i < fDecorators.length; i++ )
        {
            if( fDecorators[i].validate() )//failed do not continue
                return;
        }
    }

    public void exit() {
        // validate to save latest values
        for( int i = 0; i < fDecorators.length; i++ )
        {
            if( fDecorators[i].validate() )//failed do not continue
                return;
        }
    }

    private String createName() {
        RuntimeDelegate dl = getRuntimeDelegate();
        IRuntimeType runtimeType = dl.getRuntime().getRuntimeType();
        String name = NLS.bind(
                GenericServerUIMessages.runtimeName, runtimeType.getName() );
        IRuntime[] list = ServerCore.getRuntimes();
        int suffix = 1;
        String suffixName = name;
        for( int i = 0; i < list.length; i++ )
        {
            if( (list[i].getName().equals( name ) || list[i].getName().equals(
                    suffixName ))
                    && !list[i].equals( dl.getRuntime() ) )
                suffix++;
            suffixName = name + ' ' + suffix;
        }

        if( suffix > 1 )
            return suffixName;
        return name;
    }

    private GenericServerRuntime getRuntimeDelegate() {
        IRuntimeWorkingCopy wc = (IRuntimeWorkingCopy) getTaskModel()
                .getObject( TaskModel.TASK_RUNTIME );
        if( wc == null )
            return null;
        return (GenericServerRuntime) wc.loadAdapter(
                GenericServerRuntime.class, new NullProgressMonitor() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jst.server.generic.internal.ui.ServerDefinitionTypeAwareWizardFragment#description()
     */
    public String description() {
        String rName = getRuntimeName();
        if( rName == null || rName.length() < 1 )
            rName = "Generic"; //$NON-NLS-1$
        return NLS.bind(
                GenericServerUIMessages.runtimeWizardDescription, rName );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jst.server.generic.internal.ui.ServerDefinitionTypeAwareWizardFragment#title()
     */
    public String title() {
        String rName = getRuntimeName();
        if( rName == null || rName.length() < 1 )
            rName = "Generic"; //$NON-NLS-1$
        return NLS.bind(
                GenericServerUIMessages.runtimeWizardTitle, rName );
    }

    private String getRuntimeName() {
        if( getRuntimeDelegate() != null
                && getRuntimeDelegate().getRuntime() != null )
            return getRuntimeDelegate().getRuntime().getName();
        return null;
    }

}
