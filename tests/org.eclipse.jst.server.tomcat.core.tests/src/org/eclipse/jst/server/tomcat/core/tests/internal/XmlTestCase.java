/*******************************************************************************
 * Copyright (c) 2007 SAS Institute, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *     Larry Isaacs - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.tomcat.core.tests.internal;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.jst.server.tomcat.core.internal.xml.Factory;
import org.eclipse.jst.server.tomcat.core.internal.xml.server32.ContextManager;
import org.eclipse.jst.server.tomcat.core.internal.xml.server32.Parameter;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Connector;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Context;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Engine;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Host;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Listener;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Server;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.ServerInstance;
import org.eclipse.jst.server.tomcat.core.internal.xml.server40.Service;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Test case for XML utility code.
 *
 */
public class XmlTestCase extends TestCase {
	protected static Properties testProperties;

	protected void setUp() throws Exception {
		if (testProperties == null) {
			testProperties = new Properties();
			InputStream is = getClass().getResourceAsStream("XmlTests.properties");
			testProperties.load(is);
		}
	}
	
	private InputStream getXmlInputStream(String testId) {
		if (testProperties == null) 
			fail("XmlTests.properties file was not loaded.");
		
		String xml = testProperties.getProperty(testId);
		if (xml == null)
			fail("XML data for test " + testId + " was not found.");

		return new ByteArrayInputStream(xml.getBytes());
	}
	
	private Server getXml40Server(String testId) {
		Factory factory = new Factory();
		factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
		try {
			return (Server)factory.loadDocument(getXmlInputStream(testId));
		} catch (Exception e) {
			fail("Exception occurred loading " + testId + " XML: " + e.getMessage());
			return null;
		}
	}

	private Context getXml40Context(String testId) {
		Factory factory = new Factory();
		factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server40");
		try {
			return (Context)factory.loadDocument(getXmlInputStream(testId));
		} catch (Exception e) {
			fail("Exception occurred loading " + testId + " XML: " + e.getMessage());
			return null;
		}
	}
	
	private org.eclipse.jst.server.tomcat.core.internal.xml.server32.Server getXml32Server(String testId) {
		Factory factory = new Factory();
		factory.setPackageName("org.eclipse.jst.server.tomcat.core.internal.xml.server32");
		try {
			return (org.eclipse.jst.server.tomcat.core.internal.xml.server32.Server)factory.loadDocument(getXmlInputStream(testId));
		} catch (Exception e) {
			fail("Exception occurred loading " + testId + " XML: " + e.getMessage());
			return null;
		}
	}
	/**
	 * Test reading of the default server.xml provided by the
	 * current Tomcat 5.0 release (28).
	 */
	public void testDefaultServerXml50() {
		Server server = getXml40Server("default.serverxml.50");
		assertNotNull(server);
		// Check contents of XML
		String port = server.getPort();
		assertEquals("8005", port);
		
		assertEquals(server.getListenerCount(), 2);
		Listener listener = server.getListener(0);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.mbeans.ServerLifecycleListener", listener.getClassName());
		listener = server.getListener(1);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.mbeans.GlobalResourcesLifecycleListener", listener.getClassName());
		
		assertEquals(1, server.getServiceCount());
		Service service = server.getService(0);
		assertNotNull(service);
		assertEquals("Catalina", service.getName());
		
		assertEquals(2, service.getConnectorCount());
		Connector connector = service.getConnector(0);
		assertNotNull(connector);
		assertEquals("8080", connector.getPort());
		assertNull(connector.getProtocol());
		connector = service.getConnector(1);
		assertNotNull(connector);
		assertEquals("8009", connector.getPort());
		assertEquals("AJP/1.3", connector.getProtocol());
		
		Engine engine = service.getEngine();
		assertNotNull(engine);
		assertEquals("Catalina", engine.getName());
		assertEquals("localhost", engine.getDefaultHost());
		
		assertEquals(engine.getHostCount(), 1);
		Host host = engine.getHost(0);
		assertNotNull(host);
		assertEquals("localhost", host.getName());
		assertEquals("webapps", host.getAppBase());
		assertEquals("true", host.getAttributeValue("unpackWARs"));
		assertEquals("true", host.getAttributeValue("autoDeploy"));
		
		assertEquals(0, host.getContextCount());
	}

	/**
	 * Test reading of the default server.xml provided by the
	 * current Tomcat 5.5 release (20).
	 */
	public void testDefaultServerXml55() {
		Server server = getXml40Server("default.serverxml.55");
		assertNotNull(server);
		// Check contents of XML
		String port = server.getPort();
		assertEquals("8005", port);
		
		assertEquals(server.getListenerCount(), 4);
		Listener listener = server.getListener(0);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.core.AprLifecycleListener", listener.getClassName());
		listener = server.getListener(1);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.mbeans.ServerLifecycleListener", listener.getClassName());
		listener = server.getListener(2);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.mbeans.GlobalResourcesLifecycleListener", listener.getClassName());
		listener = server.getListener(3);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.storeconfig.StoreConfigLifecycleListener", listener.getClassName());
		
		assertEquals(server.getServiceCount(), 1);
		Service service = server.getService(0);
		assertNotNull(service);
		assertEquals("Catalina", service.getName());
		
		assertEquals(2, service.getConnectorCount());
		Connector connector = service.getConnector(0);
		assertNotNull(connector);
		assertEquals("8080", connector.getPort());
		assertNull(connector.getProtocol());
		connector = service.getConnector(1);
		assertNotNull(connector);
		assertEquals("8009", connector.getPort());
		assertEquals("AJP/1.3", connector.getProtocol());
		
		Engine engine = service.getEngine();
		assertNotNull(engine);
		assertEquals("Catalina", engine.getName());
		assertEquals("localhost", engine.getDefaultHost());
		
		assertEquals(1, engine.getHostCount());
		Host host = engine.getHost(0);
		assertNotNull(host);
		assertEquals("localhost", host.getName());
		assertEquals("webapps", host.getAppBase());
		assertEquals("true", host.getAttributeValue("unpackWARs"));
		assertEquals("true", host.getAttributeValue("autoDeploy"));
		
		assertEquals(0, host.getContextCount());
	}
	
	/**
	 * Test reading of the default server.xml provided by the
	 * current Tomcat 6.0 release.
	 */
	public void testDefaultServerXml60() {
		Server server = getXml40Server("default.serverxml.60");
		assertNotNull(server);
		// Check contents of XML
		String port = server.getPort();
		assertEquals("8005", port);
		
		assertEquals(4, server.getListenerCount());
		Listener listener = server.getListener(0);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.core.AprLifecycleListener", listener.getClassName());
		listener = server.getListener(1);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.core.JasperListener", listener.getClassName());
		listener = server.getListener(2);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.mbeans.ServerLifecycleListener", listener.getClassName());
		listener = server.getListener(3);
		assertNotNull(listener);
		assertEquals("org.apache.catalina.mbeans.GlobalResourcesLifecycleListener", listener.getClassName());
		
		assertEquals(1, server.getServiceCount());
		Service service = server.getService(0);
		assertNotNull(service);
		assertEquals("Catalina", service.getName());
		
		assertEquals(2, service.getConnectorCount());
		Connector connector = service.getConnector(0);
		assertNotNull(connector);
		assertEquals("8080", connector.getPort());
		assertEquals("HTTP/1.1", connector.getProtocol());
		connector = service.getConnector(1);
		assertNotNull(connector);
		assertEquals("8009", connector.getPort());
		assertEquals("AJP/1.3", connector.getProtocol());
		
		Engine engine = service.getEngine();
		assertNotNull(engine);
		assertEquals("Catalina", engine.getName());
		assertEquals("localhost", engine.getDefaultHost());
		
		assertEquals(1, engine.getHostCount());
		Host host = engine.getHost(0);
		assertNotNull(host);
		assertEquals("localhost", host.getName());
		assertEquals("webapps", host.getAppBase());
		assertEquals("true", host.getAttributeValue("unpackWARs"));
		assertEquals("true", host.getAttributeValue("autoDeploy"));
		
		assertEquals(0, host.getContextCount());
	}
	
	/**
	 * Test reading of the default server.xml provided by the
	 * current Tomcat 5.0 release using ServerInstance.
	 */
	public void testServerInstance50() {
		Server server = getXml40Server("default.serverxml.50");
		assertNotNull(server);
		ServerInstance si = new ServerInstance(server, null, null);
		
		assertEquals(2, server.getListenerCount());
		Listener [] listeners = si.getListeners();
		assertEquals("org.apache.catalina.mbeans.ServerLifecycleListener", listeners[0].getClassName());
		assertEquals("org.apache.catalina.mbeans.GlobalResourcesLifecycleListener", listeners[1].getClassName());
		
		Service service = si.getService();
		assertNotNull(service);
		assertEquals("Catalina", service.getName());
		
		assertEquals("8080", si.getConnector(0).getPort());
		assertNull(si.getConnector(0).getProtocol());
		assertEquals("8009", si.getConnector(1).getPort());
		assertEquals("AJP/1.3", si.getConnector(1).getProtocol());
		
		Connector [] connectors = si.getConnectors();
		assertEquals(2, connectors.length);
		assertEquals("8080", connectors[0].getPort());
		assertNull(connectors[0].getProtocol());
		assertEquals("8009", connectors[1].getPort());
		assertEquals("AJP/1.3", connectors[1].getProtocol());

		Engine engine = si.getEngine();
		assertNotNull(engine);
		assertEquals("Catalina", engine.getName());
		assertEquals("localhost", engine.getDefaultHost());
		
		Host host = si.getHost();
		assertNotNull(host);
		assertEquals("localhost", host.getName());
		assertEquals("webapps", host.getAppBase());
		assertEquals("true", host.getAttributeValue("unpackWARs"));
		assertEquals("true", host.getAttributeValue("autoDeploy"));

		Context [] contexts = si.getContexts();
		assertEquals(0, contexts.length);
	}
	
	/**
	 * Test behavior of ServerInstance
	 */
	public void testServerInstance1() {
		Server server = getXml40Server("serverxml.test1");
		assertNotNull(server);

		ServerInstance si = new ServerInstance(server, "nonexistent_service", null);
		assertNull(si.getService());
		assertEquals("Service \"nonexistent_service\" was not found.", si.getStatus().getMessage());
		
		si = new ServerInstance(server, null, null);
		assertNotNull(si.getService());
		assertEquals("Service", si.getService().getName());

		si = new ServerInstance(server, "Service", null);
		assertNotNull(si.getService());
		assertNull(si.getEngine());
		assertEquals("Engine element not found under Service \"Service\".", si.getStatus().getMessage());
	}

	/**
	 * Test behavior of ServerInstance
	 */
	public void testServerInstance2() {
		Server server = getXml40Server("serverxml.test2");
		assertNotNull(server);
		ServerInstance si = new ServerInstance(server, "Service", "nonexistent_host");
		assertNotNull(si.getService());
		assertNotNull(si.getEngine());
		assertEquals("Engine", si.getEngine().getName());
		assertNull(si.getHost());
		assertEquals("Host \"nonexistent_host\" was not found under Engine \"Engine\" and Service \"Service\".", si.getStatus().getMessage());
		
		si = new ServerInstance(server, "Service", null);
		assertNotNull(si.getService());
		assertNotNull(si.getEngine());
		assertEquals("Engine", si.getEngine().getName());
		assertNotNull(si.getHost());
		assertEquals("localhost", si.getHost().getName());
		
		assertEquals((new Path("/Base")).append("Engine").append("localhost"), si.getContextXmlDirectory(new Path("/Base")));

		Context context = si.getContext(0);
		assertNotNull(context);
		assertEquals("/WebApp1", context.getPath());
		context = si.getContext(1);
		assertNotNull(context);
		assertEquals("/WebApp2", context.getPath());
		context = si.getContext(2);
		assertNotNull(context);
		assertEquals("/WebApp3", context.getPath());
		
		// create new context
		context = si.getContext(3);
		context.setPath("/WebApp4");
		
		Context [] contexts = si.getContexts();
		assertEquals(4, contexts.length);
		assertEquals("/WebApp1", contexts[0].getPath());
		assertEquals("/WebApp2", contexts[1].getPath());
		assertEquals("/WebApp3", contexts[2].getPath());
		assertEquals("/WebApp4", contexts[3].getPath());
		
		context = si.createContext(2);
		context.setPath("/WebApp2b");
		
		contexts = si.getContexts();
		assertEquals(5, contexts.length);
		assertEquals("/WebApp1", contexts[0].getPath());
		assertEquals("/WebApp2", contexts[1].getPath());
		assertEquals("/WebApp2b", contexts[2].getPath());
		assertEquals("/WebApp3", contexts[3].getPath());
		assertEquals("/WebApp4", contexts[4].getPath());
		
		assertTrue(si.removeContext("WebApp2b"));
		contexts = si.getContexts();
		assertEquals(4, contexts.length);
		assertEquals("/WebApp1", contexts[0].getPath());
		assertEquals("/WebApp2", contexts[1].getPath());
		assertEquals("/WebApp3", contexts[2].getPath());
		assertEquals("/WebApp4", contexts[3].getPath());

		assertTrue(si.removeContext(3));
		contexts = si.getContexts();
		assertEquals(3, contexts.length);
		assertEquals("/WebApp1", contexts[0].getPath());
		assertEquals("/WebApp2", contexts[1].getPath());
		assertEquals("/WebApp3", contexts[2].getPath());
		
		context = si.getContext("/WebApp1");
		assertNotNull(context);
		assertEquals("/WebApp1", context.getPath());
		assertEquals(new Path("/Base/work/Engine/localhost/WebApp1"), si.getContextWorkDirectory(new Path("/Base"), context));
		context = si.getContext("WebApp2");
		assertNotNull(context);
		assertEquals("/WebApp2", context.getPath());
		assertEquals(new Path("/Base/relative/workdir"), si.getContextWorkDirectory(new Path("/Base"), context));
		context = si.getContext("WebApp3");
		assertNotNull(context);
		assertEquals("/WebApp3", context.getPath());
		assertEquals(new Path("/absolute/workdir"), si.getContextWorkDirectory(new Path("/Base"), context));
		
		context = si.createContext(3);
		context.setPath("");
		
		context = si.getContext("");
		assertNotNull(context);
		assertEquals("", context.getPath());
		assertEquals(new Path("/Base/work/Engine/localhost/_"), si.getContextWorkDirectory(new Path("/Base"), context));

		assertEquals(new Path("/Base/work/Engine/localhost"), si.getHostWorkDirectory(new Path("/Base")));
		
		assertNull(si.getContext("nonexistent"));
		assertEquals("Context with path \"/nonexistent\" was not found under Service \"Service\", Engine \"Engine\", and Host \"localhost\".",
				si.getStatus().getMessage());
	}

	/**
	 * Test behavior of ServerInstance
	 */
	public void testServerInstance3() {
		Server server = getXml40Server("serverxml.test3");
		assertNotNull(server);
		ServerInstance si = new ServerInstance(server, "Service", null);
		Context context = si.getContext("/WebApp1");
		assertNotNull(context);
		assertEquals(new Path("/Base/relative/host/WebApp1"), si.getContextWorkDirectory(new Path("/Base"), context));
		context = si.getContext("WebApp2");
		assertNotNull(context);
		assertEquals(new Path("/Base/relative/workdir"), si.getContextWorkDirectory(new Path("/Base"), context));
		context = si.getContext("WebApp3");
		assertNotNull(context);
		assertEquals(new Path("/absolute/workdir"), si.getContextWorkDirectory(new Path("/Base"), context));
	}

	/**
	 * Test reading of the default server.xml provided by the
	 * current Tomcat 3.2.
	 */
	public void testDefaultServerXml32() {
		org.eclipse.jst.server.tomcat.core.internal.xml.server32.Server server = getXml32Server("default.serverxml.32");
		assertNotNull(server);
		
		ContextManager contextManager = server.getContextManager();
		assertNotNull(contextManager);
		
		assertEquals(2, contextManager.getConnectorCount());
		assertNotNull(contextManager.getConnector(0));
		Parameter parameter = contextManager.getConnector(0).getParameter(0);
		assertEquals("handler", parameter.getName());
		assertEquals("org.apache.tomcat.service.http.HttpConnectionHandler", parameter.getValue());
		parameter = contextManager.getConnector(0).getParameter(1);
		assertEquals("port", parameter.getName());
		assertEquals("8080", parameter.getValue());
		
		assertNotNull(contextManager.getConnector(1));
		parameter = contextManager.getConnector(1).getParameter(0);
		assertEquals("handler", parameter.getName());
		assertEquals("org.apache.tomcat.service.connector.Ajp12ConnectionHandler", parameter.getValue());
		parameter = contextManager.getConnector(1).getParameter(1);
		assertEquals("port", parameter.getName());
		assertEquals("8007", parameter.getValue());
		
		assertEquals(2, contextManager.getContextCount());
		assertNotNull(contextManager.getContext(0));
		assertEquals("/examples", contextManager.getContext(0).getPath());
		assertNotNull(contextManager.getContext(1));
		assertEquals("/admin", contextManager.getContext(1).getPath());
	}
	
	/**
	 * Test behavior of ServerInstance with Tomcat 3.2 default server.xml.
	 */
	public void testServerInstance32() {
		org.eclipse.jst.server.tomcat.core.internal.xml.server32.Server server = getXml32Server("default.serverxml.32");
		assertNotNull(server);
		org.eclipse.jst.server.tomcat.core.internal.xml.server32.ServerInstance si =
			new org.eclipse.jst.server.tomcat.core.internal.xml.server32.ServerInstance(server);
		
		assertNotNull(si.getContextManager());
		org.eclipse.jst.server.tomcat.core.internal.xml.server32.Connector [] connectors = si.getConnectors();
		assertNotNull(connectors);
		assertEquals(2, connectors.length);
		Parameter parameter = connectors[0].getParameter(0);
		assertEquals("handler", parameter.getName());
		assertEquals("org.apache.tomcat.service.http.HttpConnectionHandler", parameter.getValue());
		parameter = connectors[0].getParameter(1);
		assertEquals("port", parameter.getName());
		assertEquals("8080", parameter.getValue());
		
		parameter = connectors[1].getParameter(0);
		assertEquals("handler", parameter.getName());
		assertEquals("org.apache.tomcat.service.connector.Ajp12ConnectionHandler", parameter.getValue());
		parameter = connectors[1].getParameter(1);
		assertEquals("port", parameter.getName());
		assertEquals("8007", parameter.getValue());

		parameter = si.getConnector(0).getParameter(0);
		assertEquals("handler", parameter.getName());
		assertEquals("org.apache.tomcat.service.http.HttpConnectionHandler", parameter.getValue());
		parameter = si.getConnector(0).getParameter(1);
		assertEquals("port", parameter.getName());
		assertEquals("8080", parameter.getValue());
		
		parameter = si.getConnector(1).getParameter(0);
		assertEquals("handler", parameter.getName());
		assertEquals("org.apache.tomcat.service.connector.Ajp12ConnectionHandler", parameter.getValue());
		parameter = si.getConnector(1).getParameter(1);
		assertEquals("port", parameter.getName());
		assertEquals("8007", parameter.getValue());
		
		org.eclipse.jst.server.tomcat.core.internal.xml.server32.Context [] contexts = si.getContexts();
		assertNotNull(contexts);
		assertEquals(2, contexts.length);
		assertEquals("/examples", contexts[0].getPath());
		assertEquals("/admin", contexts[1].getPath());
		
		assertEquals("/examples", si.getContext(0).getPath());
		assertEquals("/admin", si.getContext(1).getPath());

		assertEquals("/examples", si.getContext("examples").getPath());
		assertEquals("/admin", si.getContext("/admin").getPath());
		
		org.eclipse.jst.server.tomcat.core.internal.xml.server32.Context context = si.createContext(2);
		context.setPath("/WebApp1");

		contexts = si.getContexts();
		assertNotNull(contexts);
		assertEquals(3, contexts.length);
		assertEquals("/examples", contexts[0].getPath());
		assertEquals("/admin", contexts[1].getPath());
		assertEquals("/WebApp1", contexts[2].getPath());
		
		assertEquals(new Path("/Base/work/localhost_8080%2Fexamples"), 
				si.getContextWorkDirectory(new Path("/Base"), si.getContext("examples")));
		assertEquals(new Path("/Base/work/localhost_8080%2Fadmin"),
				si.getContextWorkDirectory(new Path("/Base"), si.getContext("admin")));
		assertEquals(new Path("/Base/work/localhost_8080%2FWebApp1"),
				si.getContextWorkDirectory(new Path("/Base"), si.getContext("WebApp1")));
		
		assertTrue(si.removeContext(2));
		contexts = si.getContexts();
		assertNotNull(contexts);
		assertEquals(2, contexts.length);
		assertEquals("/examples", contexts[0].getPath());
		assertEquals("/admin", contexts[1].getPath());

		context = si.createContext(2);
		context.setPath("");

		context = si.getContext("");
		assertNotNull(context);
		assertEquals("", context.getPath());

		assertEquals(new Path("/Base/work/localhost_8080"),
				si.getContextWorkDirectory(new Path("/Base"), si.getContext("")));
		
		assertEquals(si.getServerWorkDirectory(new Path("/Base")), new Path("/Base/work"));
	}
	
	public void testTomcatContextComparison() {
		Context context = getXml40Context("tomcat.context.50");
		assertTrue(context.isEquivalentTest(context));

		Context context2 = getXml40Context("tomcat.context.50");
		assertTrue(context.isEquivalentTest(context2));
		assertTrue(context2.isEquivalentTest(context));
		
		String docBase = context2.getDocBase();
		context2.setDocBase(docBase + "X");
		assertTrue(!context.isEquivalentTest(context2));
		assertTrue(!context2.isEquivalentTest(context));
		context.setDocBase(docBase + "X");
		assertTrue(context.isEquivalentTest(context2));
		assertTrue(context2.isEquivalentTest(context));
		
		Element realm = context2.getSubElement("Realm");
		assertNotNull(realm);
		Node parent = realm.getParentNode();
		assertNotNull(parent);
		assertTrue(parent == context2.getElementNode());
		parent.removeChild(realm);
		assertTrue(!context.isEquivalentTest(context2));
		assertTrue(!context2.isEquivalentTest(context));

		parent.insertBefore(realm, parent.getFirstChild());
		assertTrue(!context.isEquivalentTest(context2));
		assertTrue(!context2.isEquivalentTest(context));
		
		parent.removeChild(realm);
		parent.appendChild(realm);
		assertTrue(context.isEquivalentTest(context2));
		assertTrue(context2.isEquivalentTest(context));
	}
}
