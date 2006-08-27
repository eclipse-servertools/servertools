package org.eclipse.jst.server.generic.ui.internal;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jst.server.generic.core.internal.GenericServerRuntime;
import org.eclipse.jst.server.generic.core.internal.Trace;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.wst.server.core.internal.IInstallableRuntime;
import org.eclipse.wst.server.core.internal.ServerPlugin;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

public class InstallableRuntimeDecorator implements
		GenericServerCompositeDecorator {

	private GenericServerRuntime fRuntime;
	private IWizardHandle fWizard;

	public InstallableRuntimeDecorator(IWizardHandle wizard, GenericServerRuntime runtime) {
		fRuntime = runtime;
		fWizard = wizard;
	}

	public void decorate(final GenericServerComposite composite) {
		final IInstallableRuntime ir = ServerPlugin
				.findInstallableRuntime(fRuntime.getRuntime().getRuntimeType()
						.getId());

		Button install = SWTUtil.createButton(composite, GenericServerUIMessages.installServerButton);
		install.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				DirectoryDialog dialog = new DirectoryDialog(composite
						.getShell());
				dialog.setMessage(GenericServerUIMessages.installationDirectory);
				final String selectedDirectory = dialog.open();
				if (selectedDirectory != null) {
					
					IRunnableWithProgress runnable = new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor)
								throws InvocationTargetException,
								InterruptedException {
							try {
								ir.install(new Path(selectedDirectory),
										new NullProgressMonitor());
							} catch (CoreException e) {
								Trace.trace(Trace.SEVERE,
										"Error installing runtime", e); //$NON-NLS-1$
							}
						}
					};
					
					try {
						fWizard.run(true, false, runnable);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					} catch (Exception e) {
						Trace.trace(Trace.SEVERE,
								"Error installing runtime", e); //$NON-NLS-1$
					}

				}
			}
		});
	}

	public boolean validate() {
		return false;
	}

}
