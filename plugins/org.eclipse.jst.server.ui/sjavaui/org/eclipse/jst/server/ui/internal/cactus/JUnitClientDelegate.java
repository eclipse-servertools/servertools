/*******************************************************************************
 * Copyright (c) 2005 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel R. Somerfield - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.internal.cactus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.*;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.internal.junit.launcher.JUnitBaseLaunchConfiguration;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.server.core.internal.cactus.CactusLaunchable;
import org.eclipse.jst.server.ui.internal.JavaServerUIPlugin;
import org.eclipse.jst.server.ui.internal.Messages;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ClientDelegate;
/**
 *
 */
public class JUnitClientDelegate extends ClientDelegate {
	public IStatus launch(IServer server, Object object, String launchMode, ILaunch launch) {
		CactusLaunchable launchable = (CactusLaunchable) object;
		ILaunchConfiguration config = findLaunchConfiguration(launchable, launchMode);
		if (config == null) {
			String testName = launchable.getTestName();
			testName = "".equals(testName) ? launchable.getTestClassName()
					: launchable.getTestClassName() + "." + testName + "()";
			config = createConfiguration(launchable.getProjectName(), testName,
					launchable.getTestClassName(), "", launchable.getTestName());
		}
		
		URL url = launchable.getCactusURL();
		String urlString = url.toString();
		if (urlString.endsWith("/")) {
			try {
				url = new URL(urlString.substring(0, urlString.length() - 1));
			} catch (MalformedURLException e) {
				return new Status(IStatus.ERROR, JavaServerUIPlugin.PLUGIN_ID, IStatus.ERROR, Messages.errorInternalCactus, e);
			}
		}
		
		try {
			final ILaunchConfigurationWorkingCopy copy = config.getWorkingCopy();
			String vmArgs;
			vmArgs = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "");
			copy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS,
					vmArgs + " -Dcactus.contextURL=" + url.toString());
			config = copy.doSave();
			DebugUITools.launch(config, launchMode);
		} catch (CoreException e) {
			return new Status(IStatus.ERROR, JavaServerUIPlugin.PLUGIN_ID, IStatus.ERROR, Messages.errorInternalCactus, e);
		}
		return Status.OK_STATUS;
	}

	protected ILaunchConfiguration createConfiguration(String projectName,
			String name, String mainType, String container, String testName) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getJUnitLaunchConfigType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null,
					DebugPlugin.getDefault().getLaunchManager().generateUniqueLaunchConfigurationNameFrom(name));
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, mainType);
			wc.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
			wc.setAttribute(JUnitBaseLaunchConfiguration.ATTR_KEEPRUNNING, false);
			wc.setAttribute(JUnitBaseLaunchConfiguration.LAUNCH_CONTAINER_ATTR, container);
			if (testName.length() > 0)
				wc.setAttribute(JUnitBaseLaunchConfiguration.TESTNAME_ATTR, testName);
			config = wc.doSave();
		} catch (CoreException e) {
			JavaServerUIPlugin.log(e);
		}
		return config;
	}

	private ILaunchConfiguration findLaunchConfiguration(CactusLaunchable launchable, String mode) {
		String testName = launchable.getTestName();
		String testClass = launchable.getTestClassName();
		String javaProjectName = launchable.getProjectName();
		String container = "";
		ILaunchConfigurationType configType = getJUnitLaunchConfigType();
		List candidateConfigs = Collections.EMPTY_LIST;
		try {
			ILaunchConfiguration[] configs = DebugPlugin.getDefault()
					.getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList(configs.length);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				// we should probably extract the JUnit internal stuff and
				// create a new CactusLaunchConfiguration instead
				if ((config.getAttribute(
						JUnitBaseLaunchConfiguration.LAUNCH_CONTAINER_ATTR, "").equals(container)) && //$NON-NLS-1$
						(config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
								"").equals(testClass)) && //$NON-NLS-1$
						(config.getAttribute(JUnitBaseLaunchConfiguration.TESTNAME_ATTR, "").equals(testName)) && //$NON-NLS-1$
						(config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME,
								"").equals(javaProjectName))) { //$NON-NLS-1$
					candidateConfigs.add(config);
				}
			}
		} catch (CoreException e) {
			JavaServerUIPlugin.log(e);
		}
		
		// If there are no existing configs associated with the IType, create one.
		// If there is exactly one config associated with the IType, return it.
		// Otherwise, if there is more than one config associated with the IType,
		// prompt the user to choose one.
		int candidateCount = candidateConfigs.size();
		if (candidateCount < 1)
			return null;
		else if (candidateCount == 1)
			return (ILaunchConfiguration) candidateConfigs.get(0);
		else {
			// Prompt the user to choose a config. A null result means the user
			// cancelled the dialog, in which case this method returns null,
			// since cancelling the dialog should also cancel launching anything.
			ILaunchConfiguration config = chooseConfiguration(candidateConfigs, mode);
			if (config != null)
				return config;
		}
		return null;
	}

	protected ILaunchConfiguration chooseConfiguration(List configList, String mode) {
		IDebugModelPresentation labelProvider = DebugUITools
				.newDebugModelPresentation();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				JavaServerUIPlugin.getActiveWorkbenchShell(), labelProvider);
		dialog.setElements(configList.toArray());
		dialog.setTitle(Messages.LaunchTestAction_message_selectConfiguration);
		if (mode.equals(ILaunchManager.DEBUG_MODE))
			dialog.setMessage(Messages.LaunchTestAction_message_selectDebugConfiguration);
		else
			dialog.setMessage(Messages.LaunchTestAction_message_selectRunConfiguration);
		
		dialog.setMultipleSelection(false);
		int result = dialog.open();
		labelProvider.dispose();
		if (result == Window.OK)
			return (ILaunchConfiguration) dialog.getFirstResult();
		
		return null;
	}

	protected ILaunchConfigurationType getJUnitLaunchConfigType() {
		// might want to replace this with a custom launch configuration instead
		ILaunchManager lm = DebugPlugin.getDefault().getLaunchManager();
		return lm.getLaunchConfigurationType("org.eclipse.jdt.junit.launchconfig");
	}

	public boolean supports(IServer server, Object launchable, String launchMode) {
		return launchable instanceof CactusLaunchable;
	}
}