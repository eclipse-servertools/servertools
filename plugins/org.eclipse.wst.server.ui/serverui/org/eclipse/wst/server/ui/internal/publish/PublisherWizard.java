package org.eclipse.wst.server.ui.internal.publish;
/**********************************************************************
 * Copyright (c) 2003 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 *
 **********************************************************************/
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
/**
 * PublisherWizard
 */
public class PublisherWizard extends Wizard {
	protected VisualPublisher visualPublisher;

	/**
	 * PublisherWizard constructor comment.
	 */
	public PublisherWizard(VisualPublisher visualPublisher) {
		super();
		setWindowTitle(ServerUIPlugin.getResource("%wizPublishWizardTitle"));
	
		this.visualPublisher = visualPublisher;
	}

	public void addPages() {
		PublisherWizardPage page = new PublisherWizardPage(visualPublisher);
		addPage(page);
	}

	/**
	 * Subclasses must implement this <code>IWizard</code> method 
	 * to perform any special finish processing for their wizard.
	 */
	public boolean performFinish() {
		return true;
	}
}
