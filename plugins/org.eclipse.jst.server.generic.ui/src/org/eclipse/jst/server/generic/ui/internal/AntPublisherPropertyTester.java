/***************************************************************************************************
 * Copyright (c) 2007 Eteration A.S. and Gorkem Ercan. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors: Gorkem Ercan - initial API and implementation
 *               
 **************************************************************************************************/
package org.eclipse.jst.server.generic.ui.internal;

import java.util.Iterator;
import java.util.List;
import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.jst.server.generic.core.internal.ServerTypeDefinitionUtil;
import org.eclipse.jst.server.generic.core.internal.publishers.AntPublisher;
import org.eclipse.jst.server.generic.servertype.definition.Publisher;
import org.eclipse.jst.server.generic.servertype.definition.ServerRuntime;
import org.eclipse.wst.server.core.IServerWorkingCopy;

public class AntPublisherPropertyTester extends PropertyTester {

	public AntPublisherPropertyTester() {
		// TODO Auto-generated constructor stub
	}

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof IServerWorkingCopy ) {
			IServerWorkingCopy wc = (IServerWorkingCopy)receiver;
			ServerRuntime runtimeDefinition = ServerTypeDefinitionUtil.getServerTypeDefinition(wc.getRuntime());
			if ( runtimeDefinition != null ){
				List l = runtimeDefinition.getPublisher();
				Iterator r = l.iterator();
				while ( r.hasNext() ){
					Publisher publisher = (Publisher) r.next();
					if (AntPublisher.PUBLISHER_ID.equals( publisher.getId() )){
						return true;
					}
				}
			}
			
		};
		return false;
	}

}
