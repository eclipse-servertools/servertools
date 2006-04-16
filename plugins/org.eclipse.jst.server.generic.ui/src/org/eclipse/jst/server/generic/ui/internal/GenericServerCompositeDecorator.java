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


/**
 * 
 * @author Gorkem Ercan
 */
public interface GenericServerCompositeDecorator 
{
	/**
	 * 
	 * @param composite
	 */
	public abstract void decorate(GenericServerComposite composite);
    /**
	 * Called if all the fields are valid. This gives subclasses opportunity to
	 * validate and take necessary actions.
	 * 
	 * @return
	 */
	public abstract boolean validate();
}
