package org.eclipse.jst.server.websphere.core;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class AntPublisher extends org.eclipse.jst.server.generic.core.internal.AntPublisher {
	public static final String PUBLISHER_ID="org.eclipse.jst.server.generic.websphere.antpublisher"; //$NON-NLS-1$

	protected void setupAntLaunchConfiguration(ILaunchConfigurationWorkingCopy wc) {
		String wasProfile = (String)this.getServerRuntime().getServerInstanceProperties().get("wasProfile");
		wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,"-Duser.install.root="+wasProfile);

	}
}
