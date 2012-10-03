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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.core.internal.IRuntimeLocator;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.core.internal.facets.FacetUtil;
import org.eclipse.wst.server.ui.internal.viewers.RuntimeComposite;
import org.eclipse.wst.server.ui.internal.wizard.TaskWizard;
import org.eclipse.wst.server.ui.internal.wizard.WizardTaskUtil;
import org.eclipse.wst.server.ui.internal.wizard.fragment.NewRuntimeWizardFragment;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
/**
 * The preference page that holds server runtimes.
 */
public class RuntimePreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IRuntimeLifecycleListener {
	protected Button edit;
	protected Button remove;
	protected Label pathLabel;
	RuntimeComposite runtimeComp;
	/**
	 * RuntimePreferencesPage constructor comment.
	 */
	public RuntimePreferencePage() {
		super();
		noDefaultAndApplyButton();
		ServerCore.addRuntimeLifecycleListener(this);
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
		data.verticalIndent = 5;
		label.setLayoutData(data);
		label.setText(Messages.preferenceRuntimesTable);
		
		runtimeComp = new RuntimeComposite(composite, SWT.NONE, new RuntimeComposite.RuntimeSelectionListener() {
			public void runtimeSelected(IRuntime runtime) {
				if (runtime == null) {
					edit.setEnabled(false);
					remove.setEnabled(false);
					pathLabel.setText("");
				} else {
					IStatus status = runtime.validate(new NullProgressMonitor());
					if (status != null && status.getSeverity() == IStatus.ERROR) {
						Color c = pathLabel.getDisplay().getSystemColor(SWT.COLOR_RED);
						pathLabel.setForeground(c);
						pathLabel.setText(status.getMessage());
					} else if (runtime.getLocation() != null) {
						pathLabel.setForeground(edit.getForeground());
						pathLabel.setText(runtime.getLocation() + "");
					} else
						pathLabel.setText("");
					
					if (runtime.isReadOnly()) {
						edit.setEnabled(false);
						remove.setEnabled(false);
					} else {
						if (runtime.getRuntimeType() != null)
							edit.setEnabled(ServerUIPlugin.hasWizardFragment(runtime.getRuntimeType().getId()));
						else
							edit.setEnabled(false);
						remove.setEnabled(true);
					}
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
		final RuntimeComposite runtimeComp2 = runtimeComp;
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (showWizard(null) == Window.CANCEL)
					return;
				runtimeComp2.refresh();
			}
		});
		
		edit = SWTUtil.createButton(buttonComp, Messages.edit);
		edit.setEnabled(false);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IRuntime runtime = runtimeComp2.getSelectedRuntime();
				if (runtime != null) {
					IRuntimeWorkingCopy runtimeWorkingCopy = runtime.createWorkingCopy();
					if (showWizard(runtimeWorkingCopy) != Window.CANCEL) {
						try {
							runtimeComp2.refresh(runtime);
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
				if (removeRuntime(runtime))
					runtimeComp2.remove(runtime);
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
					final List<IRuntimeWorkingCopy> list = new ArrayList<IRuntimeWorkingCopy>();
					
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
							int size = locators.length;
							for (int i = 0; i < size; i++) {
								if (!monitor2.isCanceled())
									try {
										locators[i].searchForRuntimes(path, listener, monitor2);
									} catch (CoreException ce) {
										if (Trace.WARNING) {
											Trace.trace(Trace.STRING_WARNING,
													"Error locating runtimes: " + locators[i].getId(), ce);
										}
									}
							}
							if (Trace.INFO) {
								Trace.trace(Trace.STRING_INFO, "Done search");
							}
						}
					};
					dialog.run(true, true, runnable);
					
					if (Trace.FINER) {
						Trace.trace(Trace.STRING_FINER, "Found runtimes: " + list.size());
					}
					
					if (!monitor.isCanceled()) {
						if (list.isEmpty()) {
							EclipseUtil.openError(getShell(), Messages.infoNoRuntimesFound);
							return;
						}
						monitor.worked(5);
						if (Trace.FINER) {
							Trace.trace(Trace.STRING_FINER, "Removing duplicates");
						}
						List<IRuntime> good = new ArrayList<IRuntime>();
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
						
						if (Trace.FINER) {
							Trace.trace(Trace.STRING_FINER, "Adding runtimes: " + good.size());
						}
						Iterator iterator = good.iterator();
						while (iterator.hasNext()) {
							IRuntimeWorkingCopy wc = (IRuntimeWorkingCopy) iterator.next();
							wc.save(false, monitor);
						}
						monitor.done();
					}
					dialog.close();
				} catch (Exception ex) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Error finding runtimes", ex);
					}
				}
				runtimeComp2.refresh();
			}
		});
		
		pathLabel = new Label(parent, SWT.NONE);
		pathLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Dialog.applyDialogFont(composite);
	
		return composite;
	}
	
	protected boolean removeRuntime(IRuntime runtime) {
		if (runtime == null)
			return false;

		// check for use
		IServer[] servers = ServerCore.getServers();
		List<IServer> list = new ArrayList<IServer>();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				if (runtime.equals(servers[i].getRuntime()))
					list.add(servers[i]);
			}
		}
		
		boolean inUse = false;
		try {
			inUse = FacetUtil.isRuntimeTargeted(runtime);
		} catch (Throwable t) {
			// ignore - facet framework not found
		}
		
		if (!list.isEmpty() || inUse) {
			DeleteRuntimeDialog dialog = new DeleteRuntimeDialog(getShell(), !list.isEmpty(), inUse);
			if (dialog.open() != 0)
				return false;
			
			if (dialog.isDeleteServers()) {
				Iterator iter = list.iterator();
				while (iter.hasNext()) {
					try {
						IServer server = (IServer) iter.next();
						server.delete();
					} catch (Exception e) {
						if (Trace.SEVERE) {
							Trace.trace(Trace.STRING_SEVERE, "Error deleting server", e);
						}
					}
				}
			}
			if (dialog.isRemoveTargets()) {
				try {
					FacetUtil.removeTargets(runtime, new NullProgressMonitor());
				} catch (Throwable t) {
					// ignore - facet framework not found
				}
			}
		}
		
		try {
			runtime.delete();
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error deleting runtime", e);
			}
		}
		return true;
	}
	
	protected int showWizard(final IRuntimeWorkingCopy runtimeWorkingCopy) {
		String title = null;
		WizardFragment fragment = null;
		TaskModel taskModel = new TaskModel();
		if (runtimeWorkingCopy == null) {
			title = Messages.wizNewRuntimeWizardTitle;
			fragment = new WizardFragment() {
				protected void createChildFragments(List<WizardFragment> list) {
					list.add(new NewRuntimeWizardFragment());
					list.add(WizardTaskUtil.SaveRuntimeFragment);
				}
			};
		} else {
			title = Messages.wizEditRuntimeWizardTitle;
			final WizardFragment fragment2 = ServerUIPlugin.getWizardFragment(runtimeWorkingCopy.getRuntimeType().getId());
			if (fragment2 == null) {
				edit.setEnabled(false);
				return Window.CANCEL;
			}
			taskModel.putObject(TaskModel.TASK_RUNTIME, runtimeWorkingCopy);
			fragment = new WizardFragment() {
				protected void createChildFragments(List<WizardFragment> list) {
					list.add(fragment2);
					list.add(WizardTaskUtil.SaveRuntimeFragment);
				}
			};
		}
		TaskWizard wizard = new TaskWizard(title, fragment, taskModel);
		wizard.setForcePreviousAndNextButtons(true);
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
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
	
	@Override
	public void dispose() {
		ServerCore.removeRuntimeLifecycleListener(this);
		super.dispose();
	}

	public void runtimeChanged(final IRuntime runtime) {
		final RuntimeComposite runtimeComp2 = this.runtimeComp;
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){
				runtimeComp2.refresh(runtime);
			}
		});
	}

	public void runtimeAdded(final IRuntime runtime) {
		final RuntimeComposite runtimeComp2 = this.runtimeComp;
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){
				runtimeComp2.refresh();
			}
		});
	}
	public void runtimeRemoved(final IRuntime runtime) {
		final RuntimeComposite runtimeComp2 = this.runtimeComp;
		Display.getDefault().asyncExec(new Runnable(){
			public void run(){
				runtimeComp2.refresh();
			}
		});
	}
}