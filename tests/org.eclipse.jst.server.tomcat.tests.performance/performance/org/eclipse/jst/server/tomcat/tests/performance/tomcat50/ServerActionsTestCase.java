/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
  *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.jst.server.tomcat.tests.performance.tomcat50;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.wst.server.tests.performance.common.AbstractServerActionsTestCase;

public class ServerActionsTestCase extends AbstractServerActionsTestCase {
  public static Test suite() {
    return new TestSuite(ServerActionsTestCase.class, "ServerActionsTestCase");
  }

  protected String getServerTypeId() {
    return "org.eclipse.jst.server.tomcat.50";
  }
}