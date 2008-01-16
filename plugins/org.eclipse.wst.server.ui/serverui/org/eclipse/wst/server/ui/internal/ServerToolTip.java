/**********************************************************************
 * Copyright (c) 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corporation - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.internal.text.html.HTML2TextReader;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.ui.IServerModule;
import org.eclipse.wst.server.ui.IServerToolTip;
/**
 * 
 */
public class ServerToolTip extends ToolTip {	
	protected Hashtable<String,ArrayList<IServerToolTip>> toolTipProviders = new Hashtable<String,ArrayList<IServerToolTip>>();	
	protected static Shell CURRENT_TOOLTIP;
	protected Label hintLabel;
	protected Tree tree;
	protected int x;
	protected int y;

	public ServerToolTip(final Tree tree) {
		super(tree);
		
		this.tree = tree;
		
		tree.addMouseMoveListener(new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				x = e.x;
				y = e.y;
			}
		});
		
		tree.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent  e) {
				if (e.keyCode == SWT.ESC) {
					CURRENT_TOOLTIP.setVisible(false);
					CURRENT_TOOLTIP.dispose();
					activate();
				}
				if (e.keyCode == SWT.F6) {
					deactivate();
					hide();
					createFocusedTooltip(tree);					
				}
			}
			public void keyReleased(KeyEvent e){
				// nothing to do 
			}
		});
		
		loadExtensions();
	}
	
	public void createFocusedTooltip(final Control control) {
		final Shell stickyTooltip = new Shell(control.getShell(), SWT.ON_TOP | SWT.TOOL
				| SWT.NO_FOCUS);
		stickyTooltip.setLayout(new FillLayout());
		stickyTooltip.setBackground(stickyTooltip.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		control.getDisplay().asyncExec(new Runnable() {
			public void run() {
				Event event = new Event();
				event.x = x;
				event.y = y;
				event.widget = tree;
				
				createToolTipContentArea(event, stickyTooltip);
				stickyTooltip.pack();
				
				stickyTooltip.setLocation(stickyTooltip.getDisplay().getCursorLocation());				
				hintLabel.setText(Messages.toolTipDisableFocus);
				stickyTooltip.setVisible(true);
//				Eventually we want to add a listener that checks if
//              the mouseDown event is occurring outside of the bounds of the tooltip
//              if it is, then hide the tooltip
//				addListener(stickyTooltip);
			}
		});
		CURRENT_TOOLTIP = stickyTooltip;
	}

//  read the createFocusedTooltip method for information on why this is commented out
//
//	private void addListener(Control control){
//		control.addMouseListener(new StickyTipMouseListener());
//		if (control instanceof Composite){
//			Control[] childrens = ((Composite)control).getChildren();
//			for (Control child :childrens){
//				addListener(child);
//			}
//		}
//		
//	}
	
	
	@Override
	protected Object getToolTipArea(Event event) {
		Object o = tree.getItem(new Point(event.x,event.y));
		return o;
	}

	protected final boolean shouldCreateToolTip(Event event) {
		if (tree.getItem(new Point(event.x, event.y)) == null)
			return false;
		return super.shouldCreateToolTip(event);
	}

	protected Composite createToolTipContentArea(Event event, Composite parent) {
		Object o = tree.getItem(new Point(event.x, event.y));
		if (o == null)
			return null;
		
		IServer server = null;
		IServerModule module = null;
		if (o instanceof TreeItem) {
			Object obj = ((TreeItem)o).getData();
			if (obj instanceof IServer)
				server = (IServer) obj;
			if (obj instanceof IServerModule)
				module = (IServerModule) obj;
		}
		
		FillLayout layout = (FillLayout)parent.getLayout();
		layout.type = SWT.VERTICAL;
		parent.setLayout(layout);
		
		// set the default text for the tooltip
		StyledText sText = new StyledText(parent, SWT.NONE);
		sText.setEditable(false);
		sText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		if (module != null) {
			IModule[] modules = module.getModule();
			IModule m = modules[modules.length - 1];
			sText.setText("<b>" + m.getName() + "</b>");
			//sText.setText("<b>" + m.getName() + "</b></p>" + m.getModuleType().getName());
			
			StyledText sText2 = new StyledText(parent, SWT.NONE);
			sText2.setEditable(false);
			sText2.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			sText2.setText(m.getModuleType().getName());
		}
		
		if (server != null) {
			sText.setText("<b>" + server.getName() + "</b>");
			
			// add adopters content
			if (server.getServerType() != null) {
				ArrayList<IServerToolTip> listOfProviders = toolTipProviders.get(server.getServerType().getId());
				
				final Composite adoptersComposite = new Composite(parent,SWT.NONE);
				adoptersComposite.setLayout(new FillLayout());
				adoptersComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
				
				if (listOfProviders != null) {
					for (IServerToolTip tipProvider : listOfProviders) {
						tipProvider.createContent(adoptersComposite,server);
					}
				}
			}
		}
		
		// add the F3 text
		hintLabel = new Label(parent,SWT.BORDER);
		hintLabel.setAlignment(SWT.RIGHT);
		hintLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		hintLabel.setText(Messages.toolTipEnableFocus);
		hintLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
		final Font font;
		Display display = parent.getDisplay();
		FontData[] fd = parent.getFont().getFontData();
		int size2 = fd.length;
		for (int i = 0; i < size2; i++)
			fd[i].setHeight(7);
		font = new Font(display, fd);
		parent.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				font.dispose();
			}
		});
		hintLabel.setFont(font);
		
		parseText(sText.getText(),sText);
		
		return parent;
	}

	protected void parseText(String htmlText,StyledText sText) {	
		TextPresentation presentation = new TextPresentation();
		HTML2TextReader reader = new HTML2TextReader(new StringReader(htmlText), presentation);
		String text;
		
		try {
			text = reader.getString();
		} catch (IOException e) {
			text= ""; //$NON-NLS-1$
		}
		
		sText.setText(text);		
		Iterator iter = presentation.getAllStyleRangeIterator();
		while (iter.hasNext()) {
			StyleRange sr = (StyleRange)iter.next();
			sText.setStyleRange(sr);
		}
	}

	private void loadExtensions() {
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading serverToolTip extension point ->-");
		
		// search for extension points 
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID + ".serverToolTip");
		
		IServerType[] serverTypes = ServerCore.getServerTypes();
		
		for (int i=0; i < extensions.length; i++){			
			IConfigurationElement exElement = extensions[i];
			
			// Sort the extensions based on serverType
			String exServerType = exElement.getAttribute("serverTypes");
			
			for (IServerType serverType : serverTypes) {
				if (exServerType.compareTo("*") == 0 || 
						exServerType.startsWith(serverType.getId()) == true) {
					try {
						IServerToolTip exTooltip = (IServerToolTip) exElement.createExecutableExtension("class");
						ArrayList<IServerToolTip> listOfProviders = new ArrayList<IServerToolTip>(); 
						if (toolTipProviders.containsKey(serverType)) {
							listOfProviders = toolTipProviders.get(serverType);
						}
						listOfProviders.add(exTooltip);
						toolTipProviders.put(serverType.getId(), listOfProviders);
					} catch (CoreException e) {
						Trace.trace(Trace.SEVERE, "Tooltip failed to load" + extensions[i].toString(), e);
					}
					Trace.trace(Trace.EXTENSION_POINT, "  Loaded serverToolTip: " + extensions[i].getAttribute("id"));
				}
			}
		}
	}

//  read the createFocusedTooltip method for information on why this is commented out
//
//	protected class StickyTipMouseListener implements MouseListener{
//
//		public void mouseDoubleClick(MouseEvent e) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		public void mouseDown(MouseEvent e) {
//			//System.out.println("mouseDown");
//			if (CURRENT_TOOLTIP.getBounds().contains(new Point(e.x,e.y)) == true){
//				CURRENT_TOOLTIP.setVisible(false);
//				CURRENT_TOOLTIP.dispose();
//				activate();
//				CURRENT_TOOLTIP.removeMouseListener(this);
//			}
//		}
//
//		public void mouseUp(MouseEvent e) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//	}	
}