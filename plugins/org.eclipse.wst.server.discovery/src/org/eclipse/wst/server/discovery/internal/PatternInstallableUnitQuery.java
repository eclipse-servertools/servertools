/*******************************************************************************
 * Copyright (c) 2009, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.discovery.internal;

import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.query.MatchQuery;

public class PatternInstallableUnitQuery extends MatchQuery<IInstallableUnit> {
	private String categoryId;

	public PatternInstallableUnitQuery(String categoryId) {
		this.categoryId = categoryId;
	}

	public boolean isMatch(IInstallableUnit candidate) {
		if (categoryId != null && candidate.getId().endsWith(categoryId))
			return true;
		return false;
	}
}