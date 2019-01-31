/*******************************************************************************
 * Copyright (c) 2005,2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.provisional;

import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.ui.internal.cnf.CNFManagedUIDecorator;

public class UIDecoratorManager {
	protected static UIDecorator decorator = new ManagedUIDecorator();
	protected static UIDecorator decorator2 = new CNFManagedUIDecorator();
	
	@Deprecated
	public static UIDecorator getUIDecorator(IServerType serverType) {
		return decorator;
	}
	@Deprecated
	public static UIDecorator getCNFUIDecorator(IServerType serverType) {
		return decorator2;
	}
	
	public static UIDecorator getUIDecorator(){
		return decorator;
	}
	
	public static UIDecorator getCNFUIDecorator() {
		return decorator2;
	}
}