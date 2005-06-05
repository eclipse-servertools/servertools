/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IRuntimeLocator;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.internal.viewers.RuntimeComposite;
import org.eclipse.wst.server.ui.internal.wizard.ClosableWizardDialog;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewRuntimeWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * The preference page that holds server runtimes.
 */
public class RuntimePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected Button edit;
	protected Button remove;
	protected Label pathLabel;

	/**
	 * RuntimePreferencesPage constructor comment.
	 */
	public RuntimePreferencePage() {
		super();
		noDefaultAndApplyButton();
	}
	
	/**
	 * Create the preference options.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, ContextIds.PREF_GENERAL);
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(3);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);
		
		Label label = new Label(composite, SWT.WRAP);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		label.setText(Messages.preferenceRuntimesDescription);
		
		label = new Label(composite, SWT.WRAP);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		label.setText(Messages.preferenceRuntimesTable);
		
		final RuntimeComposite runtimeComp = new RuntimeComposite(composite, SWT.NONE, new RuntimeComposite.RuntimeSelectionListener() {
			public void runtimeSelected(IRuntime runtime) {
				if (runtime == null) {
					edit.setEnabled(false);
					remove.setEnabled(false);
					pathLabel.setText("");
				} else if (runtime.isReadOnly()) {
					edit.setEnabled(false);
					remove.setEnabled(false);
					pathLabel.setText(runtime.getLocation() + "");
				} else {
					edit.setEnabled(true);
					remove.setEnabled(true);
					pathLabel.setText(runtime.getLocation() + "");
				}
			}
		});
		runtimeComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL));
		
		Composite buttonComp = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = convertVerticalDLUsToPixels(3);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		buttonComp.setLayout(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
		buttonComp.setLayoutData(data);
		
		Button add = SWTUtil.createButton(buttonComp, Messages.add);
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (showWizard(null) == Window.CANCEL)
					return;
				runtimeComp.refresh();
			}
		});
		
		edit = SWTUtil.createButton(buttonComp, Messages.edit);
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IRuntime runtime = runtimeComp.getSelectedRuntime();
				if (runtime != null) {
					IRuntimeWorkingCopy runtimeWorkingCopy = runtime.createWorkingCopy();
					if (showWizard(runtimeWorkingCopy) != Window.CANCEL) {
						try {
							runtimeWorkingCopy.save(false, new NullProgressMonitor());
							runtimeComp.refresh(runtime);
						} catch (Exception ex) {
							// ignore
						}
					}
				}
			}
		});
		
		remove = SWTUtil.createButton(buttonComp, Messages.remove);
		remove.setEnabled(false);
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IRuntime runtime = runtimeComp.getSelectedRuntime();
				if (shouldRemoveRuntime(runtime))
					try {
						runtime.delete();
						runtimeComp.remove(runtime);
					} catch (Exception ex) {
						// ignore
					}
			}
		});
		
		Button search = SWTUtil.createButton(buttonComp, Messages.search);
		data = (GridData) search.getLayoutData();
		data.verticalIndent = 9;
		search.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					// select a target directory for the search
					DirectoryDialog directoryDialog = new DirectoryDialog(getShell());
					directoryDialog.setMessage(Messages.dialogRuntimeSearchMessage);
					directoryDialog.setText(Messages.dialogRuntimeSearchTitle);

					String pathStr = directoryDialog.open();
					if (pathStr == null)
						return;
					
					final IPath path = new Path(pathStr);
					
					final ProgressMonitorDialog dialog = new ProgressMonitorDialog(getShell());
					dialog.setBlockOnOpen(false);
					dialog.setCancelable(true);
					dialog.open();
					final IProgressMonitor monitor = dialog.getProgressMonitor();
					final IRuntimeLocator[] locators = ServerPlugin.getRuntimeLocators();
					monitor.beginTask(Messages.dialogRuntimeSearchProgress, 100 * locators.length + 10);
					final List list = new ArrayList();
					
					final IRuntimeLocator.IRuntimeSearchListener listener = new IRuntimeLocator.IRuntimeSearchListener() {
						public void runtimeFound(final IRuntimeWorkingCopy runtime) {
							dialog.getShell().getDisplay().syncExec(new Runnable() {
								public void run() {
									monitor.subTask(runtime.getName());
								}
							});
							list.add(runtime);
						}
					};
					
					IRunnableWithProgress runnable = new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor2) {
							if (locators != null) {
								int size = locators.length;
								for (int i = 0; i < size; i++) {
									if (!monitor2.isCanceled())
										try {
											locators[i].searchForRuntimes(path, listener, monitor2);
										} catch (CoreException ce) {
											Trace.trace(Trace.WARNING, "Error locating runtimes: " + locators[i].getId(), ce);
										}
								}
							}
							Trace.trace(Trace.INFO, "Done search");
						}
					};
					dialog.run(true, true, runnable);
					
					Trace.trace(Trace.FINER, "Found runtimes: " + list.size());
					
					if (!monitor.isCanceled()) {
						if (list.isEmpty()) {
							EclipseUtil.openError(getShell(), Messages.infoNoRuntimesFound);
							return;
						}
						monitor.worked(5);
						// remove duplicates from list (based on location)
						Trace.trace(Trace.FINER, "Removing duplicates");
						List good = new ArrayList();
						Iterator iterator2 = list.iterator();
						while (iterator2.hasNext()) {
							boolean dup = false;
							IRuntime wc = (IRuntime) iterator2.next();
							
							IRuntime[] runtimes = ServerCore.getRuntimes();
							if (runtimes != null) {
								int size = runtimes.length;
								for (int i = 0; i < size; i++) {
									if (runtimes[i].getLocation() != null && runtimes[i].getLocation().equals(wc.getLocation()))
										dup = true;
								}
							}
							if (!dup)
								good.add(wc);
						}
						monitor.worked(5);
						
						// add to list
						Trace.trace(Trace.FINER, "Adding runtimes: " + good.size());
						Iterator iterator = good.iterator();
						while (iterator.hasNext()) {
							IRuntimeWorkingCopy wc = (IRuntimeWorkingCopy) iterator.next();
							wc.save(false, monitor);
						}
						monitor.done();
					}
					dialog.close();
				} catch (Exception ex) {
					Trace.trace(Trace.SEVERE, "Error finding runtimes", ex);
				}
				runtimeComp.refresh();
			}
		});
		
		pathLabel = new Label(parent, SWT.NONE);
		pathLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}
	
	protected boolean shouldRemoveRuntime(IRuntime runtime) {
		if (runtime == null)
			return false;

		// check for use
		boolean inUse = false;
	
		IServer[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (runtime.equals(servers[i].getRuntime()))
					inUse = true;
			}
		}
		
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		if (projects != null) {
			int size = projects.length;
			for (int i = 0; i < size; i++) {
				IProjectProperties props = ServerCore.getProjectProperties(projects[i]);
				if (runtime.equals(props.getRuntimeTarget()))
					inUse = true;
			}
		}
		
		if (inUse) {
			if (!MessageDialog.openConfirm(getShell(), Messages.defaultDialogTitle, Messages.dialogRuntimeInUse))
				return false;
		}
		
		return true;
	}
	
	protected int showWizard(final IRuntimeWorkingCopy runtimeWorkingCopy) {
		String title = null;
		WizardFragment fragment = null;
		if (runtimeWorkingCopy == null) {
			title = Messages.wizNewRuntimeWizardTitle;
			fragment = new WizardFragment() {
				protected void createChildFragments(List list) {
					list.add(new NewRuntimeWizardFragment());
					list.add(new WizardFragment() {
						public void performFinish(IProgressMonitor monitor) throws CoreException {
							WizardTaskUtil.saveRuntime(getTaskModel(), monitor);
						}
					});
				}
			};
		} else {
			title = Messages.wizEditRuntimeWizardTitle;
			final WizardFragment fragment2 = ServerUIPlugin.getWizardFragment(runtimeWorkingCopy.getRuntimeType().getId());
			if (fragment2 == null) {
				edit.setEnabled(false);
				return Window.CANCEL;
			}
			fragment = new WizardFragment() {
				protected void createChildFragments(List list) {
					list.add(new WizardFragment() {
						public void enter() {
							getTaskModel().putObject(TaskModel.TASK_RUNTIME, runtimeWorkingCopy);
						}
					});
					list.add(fragment2);
					list.add(new WizardFragment() {
						public void performFinish(IProgressMonitor monitor) throws CoreException {
							WizardTaskUtil.saveRuntime(getTaskModel(), monitor);
						}
					});
				}
			};
		}
		TaskWizard wizard = new TaskWizard(title, fragment);
		wizard.setForcePreviousAndNextButtons(true);
		ClosableWizardDialog dialog = new ClosableWizardDialog(getShell(), wizard);
		return dialog.open();
	}
	
	protected IRuntime getSelection(ISelection sel2) {
		IStructuredSelection sel = (IStructuredSelection) sel2;
		return (IRuntime) sel.getFirstElement();
	}
	
	/**
	 * Initializes this preference page using the passed workbench.
	 *
	 * @param workbench the current workbench
	 */
	public void init(IWorkbench workbench) {
		// do nothing
	}

	/** 
	 * @see org.eclipse.jface.preference.PreferencePage#performOk()
	 */
	public boolean performOk() {
		// TODO - should not save until user hits ok 
		return true;
	}

	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
	 */
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			setTitle(Messages.preferenceRuntimesTitleLong);
	}
}