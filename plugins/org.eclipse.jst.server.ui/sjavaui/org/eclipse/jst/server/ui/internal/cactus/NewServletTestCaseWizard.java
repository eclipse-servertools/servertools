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

import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageOne;
import org.eclipse.jdt.junit.wizards.NewTestCaseWizardPageTwo;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jst.server.ui.internal.ImageResource;
import org.eclipse.jst.server.ui.internal.JavaServerUIPlugin;
import org.eclipse.jst.server.ui.internal.Messages;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
/**
 *
 */
public class NewServletTestCaseWizard extends Wizard implements INewWizard {
	private static final String SUPERCLASS_NAME = "org.apache.cactus.ServletTestCase"; //$NON-NLS-1$

	protected static final String[] CLASSES_TO_CHECK = {
		"org.apache.cactus.ServletTestCase", "junit.framework.TestCase",
		"org.apache.commons.logging.Log", "org.aspectj.lang.JoinPoint",
		"org.apache.commons.httpclient.HttpClient" };

	protected static final String[] REQUIRED_LIBRARIES = {
		"cactus-1.7.jar", "junit-3.8.1.jar", "aspectjrt-1.2.1.jar",
		"commons-logging-1.0.4.jar", "commons-httpclient-2.0.2.jar" };

	private IWorkbench fWorkbench;
	private IStructuredSelection fSelection;
	private NewTestCaseWizardPageTwo fPage2;
	private NewTestCaseWizardPageOne fPage1;
	
	public NewServletTestCaseWizard() {
		super();
		setWindowTitle(Messages.NewServletTestCaseWizard_WindowTitle);
		setDefaultPageImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_WIZ_CACTUS_TEST));
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		fWorkbench = workbench;
		fSelection = selection;
	}

	public void addPages() {
		super.addPages();
		fPage2 = new NewTestCaseWizardPageTwo();
		fPage1 = new NewTestCaseWizardPageOne(fPage2) {
			public void createControl(Composite parent) {
				super.createControl(parent);
				setSuperClass(SUPERCLASS_NAME, true);
			}

			protected IStatus validateIfJUnitProject() {
				IPackageFragmentRoot root = getPackageFragmentRoot();
				if (root == null)
					return Status.OK_STATUS;

				IJavaProject project = root.getJavaProject();
				try {
					for (int i = 0; i < CLASSES_TO_CHECK.length; i++) {
						IType type = project.findType(CLASSES_TO_CHECK[i]);
						if (type == null) {
							MessageDialog.openError(getShell(),
									Messages.NewServletTestCaseWizard_ErrorMessageTitleMissingLibrary,
									NLS.bind(Messages.NewServletTestCaseWizard_ErrorMessageMissingLibrary, REQUIRED_LIBRARIES));
							return Status.OK_STATUS;
						}
					}
				} catch (JavaModelException e) {
					JavaServerUIPlugin.log(e);
				}
				return Status.OK_STATUS;
			}
		};
		addPage(fPage1);
		fPage1.init(getSelection());
		addPage(fPage2);

	}

	private IStructuredSelection getSelection() {
		return fSelection;
	}

	public boolean performFinish() {
		if (finishPage(fPage1.getRunnable())) {
			IType newClass = fPage1.getCreatedType();

			IResource resource = newClass.getCompilationUnit().getResource();
			if (resource != null) {
				BasicNewResourceWizard.selectAndReveal(resource, fWorkbench
						.getActiveWorkbenchWindow());
				openResource(resource);
				// checkLibraryMissing();
			}
			return true;
		}
		return false;
	}

	/*private void checkLibraryMissing() {
		IType newClass = fPage1.getCreatedType();
		IJavaProject project = newClass.getJavaProject();
		try {
			IType type = project.findType(SUPERCLASS_NAME);
			MessageDialog.openError(getShell(), NLS.bind(
					Messages.NewServletTestCaseWizard_ErrorMessageMissingLibrary, REQUIRED_LIBRARIES),
					Messages.NewServletTestCaseWizard_ErrorMessageMissingType);
		} catch (JavaModelException e) {
			JavaServerUIPlugin.log(e);
		}
	}*/

	protected boolean finishPage(IRunnableWithProgress runnable) {
		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(runnable);
		try {
			PlatformUI.getWorkbench().getProgressService().runInUI(getContainer(), op,
					ResourcesPlugin.getWorkspace().getRoot());
		} catch (InvocationTargetException e) {
			String title = Messages.NewServletTestCaseWizard_ErrorTitleNew; // NewJUnitWizard_op_error_title
			String message = Messages.NewServletTestCaseWizard_ErrorTitleCreateOfElementFailed; // NewJUnitWizard_op_error_message
			// ExceptionHandler.handle(e, shell, title, message);
			displayMessageDialog(e, e.getMessage(), getShell(), title, message);
			return false;
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	public static void displayMessageDialog(Throwable t, String exceptionMessage,
			Shell shell, String title, String message) {
		StringWriter msg = new StringWriter();
		if (message != null) {
			msg.write(message);
			msg.write("\n\n"); //$NON-NLS-1$
		}
		if (exceptionMessage == null || exceptionMessage.length() == 0)
			msg.write(Messages.NewServletTestCaseWizard_ErrorMessageSeeErrorLog);
		else
			msg.write(exceptionMessage);
		MessageDialog.openError(shell, title, msg.toString());
	}

	protected void openResource(final IResource resource) {
		if (resource.getType() == IResource.FILE) {
			final IWorkbenchPage activePage = getActivePage();
			if (activePage != null) {
				final Display display = Display.getDefault();
				if (display != null) {
					display.asyncExec(new Runnable() {
						public void run() {
							try {
								IDE.openEditor(activePage, (IFile) resource, true);
							} catch (PartInitException e) {
								JavaServerUIPlugin.log(e);
							}
						}
					});
				}
			}
		}
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null)
			return null;
		return activeWorkbenchWindow.getActivePage();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		IWorkbench workbench = JavaServerUIPlugin.getInstance().getWorkbench();
		if (workbench != null)
			return workbench.getActiveWorkbenchWindow();
		
		return null;
	}
}