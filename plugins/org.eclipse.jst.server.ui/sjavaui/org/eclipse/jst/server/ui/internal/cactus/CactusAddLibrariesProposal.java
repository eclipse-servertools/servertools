/*******************************************************************************
 * Copyright (c) 2006 BEA Systems, Inc. and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel R. Somerfield - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.internal.cactus;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jst.server.ui.internal.JavaServerUIPlugin;
import org.eclipse.jst.server.ui.internal.Messages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.actions.CopyFilesAndFoldersOperation;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.osgi.framework.Bundle;

public class CactusAddLibrariesProposal implements IJavaCompletionProposal {
	private final IInvocationContext fContext;

	public CactusAddLibrariesProposal(IInvocationContext context) {
		fContext = context;
	}

	public int getRelevance() {
		return 0;
	}

	public void apply(IDocument document) {
		ICompilationUnit javaFile = fContext.getCompilationUnit();
		installLibraries(javaFile.getJavaProject().getProject());
	}

	public static void installLibraries(final IProject project) {
		try {
			Bundle bundle = Platform.getBundle("org.apache.cactus"); //$NON-NLS-1$
			if (bundle == null) {
				showInstallFailedDialog(Messages.CactusAddLibrariesProposal_ErrorMessageCactusBundleNotFound);
				return;
			}
			URL cactusLibDir = FileLocator.find(bundle, new Path("lib"), null); //$NON-NLS-1$
			if (cactusLibDir == null) {
				showInstallFailedDialog(Messages.CactusAddLibrariesProposal_ErrorMessageInstallDirNotFound);
				return;
			}
			URL localURL = FileLocator.toFileURL(cactusLibDir);
			final File sourceDirectory = new File(localURL.getPath());
			IVirtualComponent component = ComponentCore.createComponent(project);
			IVirtualFolder vf = component.getRootFolder().getFolder("WEB-INF/lib"); //$NON-NLS-1$
			final IContainer destinationDirectory = vf.getUnderlyingFolder();

			IFolder destinationFolder = project.getFolder(destinationDirectory
					.getProjectRelativePath());

			if (destinationFolder.exists()) {
				File[] filesToCopy = sourceDirectory.listFiles();
				String[] filesToCopyNames = new String[filesToCopy.length];
				for (int i = 0; i < filesToCopy.length; i++) {
					filesToCopyNames[i] = filesToCopy[i].getAbsolutePath();
				}
				CopyFilesAndFoldersOperation operation = new CopyFilesAndFoldersOperation(
						JavaServerUIPlugin.getActiveWorkbenchShell());
				operation.copyFiles(filesToCopyNames, destinationFolder);
			} else {
				showInstallFailedDialog(Messages.CactusAddLibrariesProposal_ErrorMessageDestDirNotFound);
				return;
			}
		} catch (IOException e) {
			JavaServerUIPlugin.log(e);
			showInstallFailedDialog(Messages.CactusAddLibrariesProposal_ErrorMessageInstallationOfLibsFailed);
		}
	}

	private static void showInstallFailedDialog(String message) {
		MessageDialog.openError(JavaServerUIPlugin.getActiveWorkbenchShell(),
				Messages.CactusAddLibrariesProposal_ErrorMessageCouldntInstallLibraries,
				message); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public Point getSelection(IDocument document) {
		return new Point(fContext.getSelectionOffset(), fContext.getSelectionLength());
	}

	public String getAdditionalProposalInfo() {
		return Messages.CactusAddLibrariesProposal_AdditionalInfoAddCactusLibraries; //$NON-NLS-1$
	}

	public String getDisplayString() {
		return Messages.CactusAddLibrariesProposal_DisplayStringAddCactusLibs; //$NON-NLS-1$
	}

	public Image getImage() {
		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}
}