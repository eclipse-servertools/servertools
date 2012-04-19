/*******************************************************************************
 * Copyright (c) 2003, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServer.IOperationListener;
/**
 * Dialog that prompts a user to delete server(s) and/or server configuration(s).
 */
public class DeleteServerDialog extends MessageDialog {
	protected IServer[] servers;
	protected IFolder[] configs;

	protected List<IServer> runningServersList;
	protected boolean runningServerCanStop;

	protected Button checkDeleteConfigs;
	protected Button checkDeleteRunning;
	protected Button checkDeleteRunningStop;

	/**
	 * DeleteServerDialog constructor comment.
	 * 
	 * @param parentShell a shell
	 * @param servers an array of servers
	 * @param configs an array of server configurations
	 */
	public DeleteServerDialog(Shell parentShell, IServer[] servers, IFolder[] configs) {
		super(parentShell, Messages.deleteServerDialogTitle, null, null, QUESTION,
				new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
		
		if (servers == null || configs == null)
			throw new IllegalArgumentException();
		
		this.servers = servers;
		this.configs = configs;
		
		runningServersList = new ArrayList<IServer>();
		for (int i = 0 ; i < servers.length ; ++i) {
			if (servers[i].getServerState() != IServer.STATE_STOPPED)
				runningServersList.add(servers[i]);
			
			if (servers[i].canStop().isOK())
				runningServerCanStop = true;
		}
		
		if (servers.length == 1)
			message = NLS.bind(Messages.deleteServerDialogMessage, servers[0].getName());
		else
			message = NLS.bind(Messages.deleteServerDialogMessageMany, servers.length + "");
	}

	/**
	 * 
	 */
	protected Control createCustomArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, ContextIds.DELETE_SERVER_DIALOG);
		
		if (configs.length > 0) {
			checkDeleteConfigs = new Button(composite, SWT.CHECK);
			checkDeleteConfigs.setText(NLS.bind(Messages.deleteServerDialogLooseConfigurations, configs[0].getName()));
			checkDeleteConfigs.setSelection(true);
		}
		
		// prompt for stopping running servers
		int size = runningServersList.size();
		if (size > 0) {
			if (servers.length > 1) {
				checkDeleteRunning = new Button(composite, SWT.CHECK);
				checkDeleteRunning.setText(Messages.deleteServerDialogRunningServer);
				checkDeleteRunning.setSelection(true);
			}
			
			if (runningServerCanStop) {
				checkDeleteRunningStop = new Button(composite, SWT.CHECK);
				checkDeleteRunningStop.setText(Messages.deleteServerDialogRunningServerStop);
				checkDeleteRunningStop.setSelection(true);
				GridData data = new GridData();
				if (checkDeleteRunning != null) {
					// Only indent the checkbox if the delete running servers checkbox is available.
					data.horizontalIndent = 15;
					checkDeleteRunning.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							checkDeleteRunningStop.setEnabled(checkDeleteRunning.getSelection());
						}
					});
				}
				checkDeleteRunningStop.setLayoutData(data);				
			}
		}
		
		Dialog.applyDialogFont(composite);
		
		return composite;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == OK) {
			final boolean checked = (checkDeleteConfigs != null && checkDeleteConfigs.getSelection());
			final boolean deleteRunning = (checkDeleteRunning == null || checkDeleteRunning.getSelection());
			final boolean deleteRunningStop = (checkDeleteRunningStop != null && checkDeleteRunningStop.getSelection());
			
			Thread t = new Thread("Delete servers") {
				public void run() {
					if (runningServersList.size() > 0) {
						// stop servers and/or updates servers' list
						prepareForDeletion(deleteRunning, deleteRunningStop);
					}
					
					Job job = new Job(Messages.deleteServerTask) {
						protected IStatus run(IProgressMonitor monitor) {
							if (servers.length == 0) {
								// all servers have been deleted from list
								return Status.OK_STATUS;
							}
							try {
								if (monitor.isCanceled())
									return Status.CANCEL_STATUS;
								
								int size = servers.length;
								for (int i = 0; i < size; i++)
									servers[i].delete();
								
								if (monitor.isCanceled())
									return Status.CANCEL_STATUS;
								
								if (checked) {
									size = configs.length;
									for (int i = 0; i < size; i++) {
										configs[i].refreshLocal(IResource.DEPTH_INFINITE, monitor);
										configs[i].delete(true, true, monitor);
									}
								}
							} catch (Exception e) {
								if (Trace.SEVERE) {
									Trace.trace(Trace.STRING_SEVERE, "Error while deleting resources", e);
								}
								return new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, 0, e.getMessage(), e); 
							}
							
							return Status.OK_STATUS;
						}
					};
					
					// set rule for workspace and servers
					int size = servers.length;
					ISchedulingRule[] rules = new ISchedulingRule[size+1];
					for (int i = 0; i < size; i++)
						rules[i] = servers[i];
					IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
					rules[size] = ruleFactory.createRule(ResourcesPlugin.getWorkspace().getRoot());
					job.setRule(MultiRule.combine(rules));
					job.setPriority(Job.BUILD);
					
					job.schedule();
				}
			};
			t.setDaemon(true);
			t.start();
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * Updates servers' & configs' lists. If <code>deleteRunning</code> is <code>true</code>
	 * 	and a server can't be stopped, it isn't removed.
	 * @param deleteRunning if <code>true</code> running servers will be stopped
	 * 	before being deleted, if <code>false</code> running servers will be removed
	 *    from deletion list.
	 */
	protected void prepareForDeletion(boolean deleteRunning, boolean stopRunning) {
		// converts servers & configs to list to facilitate removal
		List<IServer> serversList = new LinkedList<IServer>(Arrays.asList(servers));
		List<IFolder> configsList = new LinkedList<IFolder>(Arrays.asList(configs));
		if (!deleteRunning) {
			// don't delete servers or configurations
			int size = runningServersList.size();
			for (int i = 0; i < size; i++) {
				IServer server = runningServersList.get(i);
				serversList.remove(server);
				if (server.getServerConfiguration() != null)
					configsList.remove(server.getServerConfiguration());
			}
		} else {
			if (stopRunning) {
				// stop running servers and wait for them (stop is asynchronous)
				IServer s;
				MultiServerStopListener listener = new MultiServerStopListener();
				int expected = 0;
				Iterator iter = runningServersList.iterator();
				while (iter.hasNext()) {
					s = (IServer) iter.next();
					if (s.canStop().isOK()) {
						++expected;
						s.stop(false, listener);
					} else {
						// server can't be stopped, don't delete it
						serversList.remove(s);
						configsList.remove(s.getServerConfiguration());
					}
				}
				try {
					while (expected != listener.getNumberStopped()) {
						Thread.sleep(200);
					}
				} catch (InterruptedException e) {
					if (Trace.WARNING) {
						Trace.trace(Trace.STRING_WARNING, "Interrupted while waiting for servers stop");
					}
				}
			}
		}
		servers = new IServer[serversList.size()];
		serversList.toArray(servers);
		configs = new IFolder[configsList.size()];
		configsList.toArray(configs);
	}

	/**
	 * Class used to wait all servers stop. Use one instance
	 * for a group of servers and loop to see if the number stopped
	 * equals the number of servers waiting to stop.
	 */
	class MultiServerStopListener implements IOperationListener {
		protected int num; 

		public void done(IStatus result) {
			num++;
		}

		public int getNumberStopped() {
			return num;
		}
	}
}