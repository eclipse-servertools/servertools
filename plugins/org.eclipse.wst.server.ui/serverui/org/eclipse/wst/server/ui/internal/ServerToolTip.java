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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.server.core.IServerType;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.Trace;
import org.eclipse.wst.server.ui.IServerToolTip;

public class ServerToolTip extends ToolTip {	
	protected Server server;
	protected Hashtable<String,ArrayList> toolTipProviders = new Hashtable<String,ArrayList>();
	protected boolean shouldHide = true;
	protected Tree tree;
	protected final static int MARGIN = 5;
//	final ServerToolTip instance;
//	Shell tip;

	public ServerToolTip(final Control control) {
		super(control);
		if (control instanceof Tree) {
			tree =(Tree)control;
		}
// 		This is and the rest of the commented code is a hack to see if I can try to stop the hiding of the tooltip when the mouse exits the area 		
//		instance=this;
//		control.addKeyListener(new KeyListener(){
//
//			public void keyPressed(KeyEvent e) {
//				System.out.println(e.character);
//				if (e.keyCode == SWT.F3){
//					shouldHide = false;
//					deactivate();
//				}
//				if (e.keyCode == SWT.ESC){
//					show(new Point(0,0));
//					hide();
//				}
//				
//			}
//
//			public void deactivate(){
//				 try{
//		            final Method method = ToolTip.class.getDeclaredMethod( "toolTipHookByTypeRecursively", Control.class,boolean.class,int.class); //$NON-NLS-1$
//		            method.setAccessible( true );
//		            method.invoke( instance, tip, false,SWT.MouseExit );
//		            System.out.println("hello");
//		         }
//				 catch (Exception e){
//					 e.printStackTrace();
//				 }
//			}
//			public void keyReleased(KeyEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
		loadExtensions();
	}

	protected Composite createToolTipContentArea(Event event, Composite parent) {
//		tip = parent.getShell();
		Object o = tree.getItem(new Point(event.x,event.y));
		if (o == null) {
			hide();
			return null;
		}
		if (o instanceof TreeItem) {
			server = (Server)((TreeItem)o).getData();
		}		
		
		FillLayout layout = (FillLayout)parent.getLayout();
		layout.type = SWT.VERTICAL;		
		parent.setLayout(layout);
		
		// Set the default text for the tooltip
		StyledText sText = new StyledText(parent,SWT.NONE);
		sText.setEditable(false);
		sText.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		sText.setText("<b>"+server.getName()+"</b>");
		
		// Add adopters content		
		ArrayList<IServerToolTip> listOfProviders = toolTipProviders.get(server.getServerType().getId());
		
		final Composite adoptersComposite = new Composite(parent,SWT.NONE);
		adoptersComposite.setLayout(new FillLayout());
		adoptersComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		
		if (listOfProviders != null){
			for (IServerToolTip tipProvider : listOfProviders){
				tipProvider.createContent(adoptersComposite,server);
			}
		}
				
		// Add the F3 text
		Label label = new Label(parent,SWT.BORDER);
		label.setAlignment(SWT.RIGHT);	
		label.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		label.setText(Messages.toolTipEnableFocus);
		label.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		
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
		label.setFont(font);

		parseText(sText.getText(),sText);
		
		return parent;
	}

	public void parseText(String htmlText,StyledText sText){	
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
		Trace.trace(Trace.EXTENSION_POINT, "->- Loading ServerToolTip extension point ->-");

		// Search for extension points 
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = reg.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID + ".serverToolTip");
			
		IServerType[] serverTypes = ServerCore.getServerTypes();
		
		for (int i=0; i < extensions.length; i++){			
			IConfigurationElement exElement = extensions[i];
			
			// Sort the extensions based on ServerType
			String exServerType = exElement.getAttribute("serverTypes");
			
			for (IServerType serverType : serverTypes) {
				
				if (exServerType.compareTo("*") == 0 || 
						exServerType.startsWith(serverType.getId()) == true) {
					try {
						IServerToolTip exTooltip = (IServerToolTip) exElement.createExecutableExtension("class");
						ArrayList<IServerToolTip> listOfProviders = new ArrayList<IServerToolTip>(); 
						if (toolTipProviders.containsKey(serverType)){
							listOfProviders = toolTipProviders.get(serverType);
						}						
						listOfProviders.add(exTooltip);
						toolTipProviders.put(serverType.getId(), listOfProviders);
					} catch (CoreException e){
						Trace.trace(Trace.SEVERE, "Tooltip failed to load" + extensions[i].toString(), e);
					}
					Trace.trace(Trace.EXTENSION_POINT, "  Loaded startup: " + extensions[i].getAttribute("id"));
				}
			}
	
		}
	}

//	@Override
//	public void hide() {
//		if (shouldHide == true || !sText.isFocusControl()){
//			super.hide();
//		}
//	}	
}