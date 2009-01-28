/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal;

import java.util.Iterator;
import java.util.List;

import org.eclipse.equinox.internal.provisional.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.internal.provisional.p2.query.Collector;
import org.eclipse.equinox.internal.provisional.p2.query.Query;

public class ExtensionInstallableUnitQuery implements Query {
	private List<String> list;

	public ExtensionInstallableUnitQuery(List<String> list) {
		this.list = list;
	}

	public boolean isMatch(Object object) {
		if (!(object instanceof IInstallableUnit))
			return false;
		IInstallableUnit candidate = (IInstallableUnit) object;
		if (list.contains(candidate.getId()))
			return true;
		return false;
	}

	public Collector perform(Iterator iterator, Collector result) {
		// TODO Auto-generated method stub
		return null;
	}
}