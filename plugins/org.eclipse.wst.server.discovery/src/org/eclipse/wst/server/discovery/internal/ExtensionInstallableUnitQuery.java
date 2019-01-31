/*******************************************************************************
 * Copyright (c) 2008, 2010 IBM Corporation and others.
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
package org.eclipse.wst.server.discovery.internal;

import java.util.List;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.MatchQuery;

public class ExtensionInstallableUnitQuery extends MatchQuery<IInstallableUnit> {
	private List<String> list;

	public ExtensionInstallableUnitQuery(List<String> list) {
		this.list = list;
	}

	public boolean isMatch(IInstallableUnit candidate) {
		return list.contains(candidate.getId());
	}
}