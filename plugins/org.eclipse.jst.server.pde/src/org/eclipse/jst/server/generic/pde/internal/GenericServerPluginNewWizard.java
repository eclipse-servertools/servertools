/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.pde.internal;


import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
/**
 * New Generic server plug-in wizard. 
 * @author Gorkem Ercan
 *
 */
public class GenericServerPluginNewWizard extends NewPluginTemplateWizard {

	public ITemplateSection[] createTemplateSections(){
		return new ITemplateSection[] {new GenericServerTemplate()};
	}
	
	public void init(IFieldData data) {
		super.init(data);
		setWindowTitle(Messages.windowTitleWizard);
	}
}
