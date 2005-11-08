/***************************************************************************************************
 * Copyright (c) 2005 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class GenericServerComposite extends Composite {

	private GenericServerCompositeDecorator[] fDecorators;
	public GenericServerComposite(Composite parent, GenericServerCompositeDecorator[] decorators) {
		super(parent, SWT.NONE);
		fDecorators = decorators;
		GridLayout layout =new GridLayout(3,false); 
		setLayout(layout);
        setLayoutData(new GridData(GridData.FILL_BOTH));
		createControl();
	}

	private void createControl(){
		for (int i = 0; fDecorators!= null && i < fDecorators.length; i++) {
			fDecorators[i].decorate(this);
		}
	}
}
