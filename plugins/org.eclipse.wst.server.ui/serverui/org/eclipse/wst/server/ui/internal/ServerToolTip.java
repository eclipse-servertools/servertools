/**********************************************************************
 * Copyright (c) 2007, 2013 IBM Corporation and others.
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

import org.eclipse.core.runtime.*;
import org.eclipse.jface.internal.text.html.HTML2TextReader;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.wst.server.core.*;
import org.eclipse.wst.server.ui.IServerModule;
import org.eclipse.wst.server.ui.internal.provisional.IServerToolTip;

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
				if (e == null)
					return;
				
				if (e.keyCode == SWT.ESC) {
					if (CURRENT_TOOLTIP != null) {
						CURRENT_TOOLTIP.dispose();
						CURRENT_TOOLTIP = null;
					}
					activate();
				}
				if (e.keyCode == SWT.F6) {
					if (CURRENT_TOOLTIP == null) {
						deactivate();
						hide();
						createFocusedTooltip(tree);
					}
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
		stickyTooltip.addShellListener(new ShellAdapter() {
			public void shellClosed(ShellEvent e) {
				if (CURRENT_TOOLTIP != null) {
					CURRENT_TOOLTIP.dispose();
					CURRENT_TOOLTIP = null;
				}
				activate();
			}
		});

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
		Object o = tree.getItem(new Point(event.x, event.y));
		if (o == null) {
			return false;
		}
		IServer server = null;
		IServerModule module = null;
		if (o instanceof TreeItem) {
			Object obj = ((TreeItem)o).getData();
			if (obj instanceof IServer) {
				server = (IServer) obj;
			}
			if (obj instanceof IServerModule) {
				module = (IServerModule) obj;
			}
		}
		// Only enable for supported objects.
		if (server == null && module == null) {
			return false;
		}
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
		parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		// set the default text for the tooltip
		StyledText sText = new StyledText(parent, SWT.NONE);
		sText.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		sText.setEditable(false);
		sText.setBackground(parent.getBackground());
		
		if (module != null) {
			IModule[] modules = module.getModule();
			IModule m = modules[modules.length - 1];
			sText.setText("<b>" + ServerUtil.getModuleDisplayName(m) + "</b>");
			//sText.setText("<b>" + m.getName() + "</b></p>" + m.getModuleType().getName());
			
			StyledText sText2 = new StyledText(parent, SWT.NONE);
			sText2.setEditable(false);
			sText2.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
			sText2.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
			
			sText2.setText(m.getModuleType().getName());
		}
		
		if (server != null) {
			sText.setText("<b>" + server.getName() + "</b>");
			
			// add adopters content
			if (server.getServerType() != null) {
				ArrayList<IServerToolTip> listOfProviders = toolTipProviders.get(server.getServerType().getId());
				
				if (listOfProviders != null) {
					for (IServerToolTip tipProvider : listOfProviders) {
						tipProvider.createContent(parent,server);
					}
				}
			}
		}
		
		// add the F3 text
		hintLabel = new Label(parent,SWT.BORDER);
		hintLabel.setAlignment(SWT.RIGHT);
		hintLabel.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		hintLabel.setText(Messages.toolTipEnableFocus);
		hintLabel.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
		
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
		if (Trace.EXTENSION_POINT) {
			Trace.trace(Trace.STRING_EXTENSION_POINT, "->- Loading serverToolTip extension point ->-");
		}
		
		// search for extension points 
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID + ".serverToolTip");
		
		IServerType[] serverTypes = ServerCore.getServerTypes();
		
		for (int i=0; i < extensions.length; i++){			
			IConfigurationElement exElement = extensions[i];
			
			try {
				// sort the extensions based on serverType
				String exServerType = exElement.getAttribute("serverTypes");
				
				for (IServerType serverType : serverTypes) {
					if ("*".equals(exServerType) || serverType.getId().matches(exServerType)) {
						IServerToolTip exTooltip = (IServerToolTip) exElement.createExecutableExtension("class");
						ArrayList<IServerToolTip> listOfProviders = new ArrayList<IServerToolTip>(); 
						if (toolTipProviders.containsKey(serverType.getId()))
							listOfProviders = toolTipProviders.get(serverType.getId());
						
						listOfProviders.add(exTooltip);
						toolTipProviders.put(serverType.getId(), listOfProviders);
						if (Trace.EXTENSION_POINT) {
							Trace.trace(Trace.STRING_EXTENSION_POINT,
									"  Loaded serverToolTip: " + exElement.getAttribute("id") + " for server type: "
											+ serverType.getId());
						}
					}
				}
			} catch (CoreException e) {
				if (Trace.SEVERE) {
					Trace.trace(Trace.STRING_SEVERE, "Tooltip failed to load" + exElement.toString(), e);
				}
			}
		}
		if (Trace.EXTENSION_POINT) {
			Trace.trace(Trace.STRING_EXTENSION_POINT, "-<- Done loading serverToolTip extension point -<-");
		}
	}

//  read the createFocusedTooltip method for information on why this is commented out
//
//	protected class StickyTipMouseListener implements MouseListener{
//
//		public void mouseDoubleClick(MouseEvent e) {
//			// Auto-generated method stub
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
//			// Auto-generated method stub
//		}
//	}	
}
