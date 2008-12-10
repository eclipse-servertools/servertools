/*******************************************************************************
 * Copyright (c) 2005,2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.provisional;

import org.eclipse.wst.server.core.IServerType;
import org.elcipse.wst.server.ui.internal.cnf.CNFManagedUIDecorator;

public class UIDecoratorManager {
	protected static UIDecorator decorator = new ManagedUIDecorator();
	protected static UIDecorator decorator2 = new CNFManagedUIDecorator();
	
	public static UIDecorator getUIDecorator(IServerType serverType) {
		return decorator;
	}
	
	public static UIDecorator getCNFUIDecorator(IServerType serverType) {
		return decorator2;
	}
}