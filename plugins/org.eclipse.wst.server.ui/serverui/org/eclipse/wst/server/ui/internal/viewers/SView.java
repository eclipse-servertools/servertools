/**********************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TypedListener;
/**
 * Simple SWT widget with an image banner and a label.
 */
public class SView extends Canvas {
	protected Color focusColor;
	protected boolean mouseOver = false;
	protected boolean hasFocus = false;
	//protected boolean isOpen = true;
	
	//protected String name;
	//protected String description;
	
	protected Color one;
	protected Color two;
	protected Color three;
	
	protected Color grone;
	protected Color grtwo;
	protected Color grthree;
	
	private Cursor cursor;

	public SView(Composite parent, int style) {
		super(parent, style);
		
		cursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
		setCursor(cursor);
		
		one = new Color(getDisplay(), 224, 244, 252);
		two = new Color(getDisplay(), 178, 212, 247);
		three = new Color(getDisplay(), 138, 185, 242);
		
		grone = new Color(getDisplay(), 229, 255, 193);
		grtwo = new Color(getDisplay(), 63, 214, 16);
		grthree = new Color(getDisplay(), 21, 157, 4);

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				SView.this.paintControl(e);
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				//changeTwistie(!isOpen);
			}
			public void mouseUp(MouseEvent e) {
				// do nothing
			}
			public void mouseDoubleClick(MouseEvent e) {
				// do nothing
			}
		});
		addMouseTrackListener(new MouseTrackListener() {
			public void mouseEnter(MouseEvent e) {
				mouseOver = true;
				SView.this.redraw();
			}
			public void mouseExit(MouseEvent e) {
				mouseOver = false;
				SView.this.redraw();
			}
			public void mouseHover(MouseEvent e) {
				// do nothing
			}
		});
		addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				//changeTwistie(!isOpen);
				redraw();
			}
		});
		addListener(SWT.Traverse, new Listener () {
			public void handleEvent(Event e) {
				if (e.detail != SWT.TRAVERSE_RETURN)
					e.doit = true;
			}
		});
		addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				hasFocus = true;
				redraw();
			}
			public void focusLost(FocusEvent e) {
				hasFocus = false;
				redraw();
			}
		});
		addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.character == '\r') {
					// Activation
					notifyListeners(SWT.Selection);
				}
			}
		});
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				disposeImpl();
			}
		});
		
		getAccessible().addAccessibleListener(new AccessibleAdapter() {
			public void getName(AccessibleEvent e) {
				//e.result = name;
			}

			public void getDescription(AccessibleEvent e) {
				//e.result = description;
			}
		});

		getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
			public void getLocation(AccessibleControlEvent e) {
				Rectangle location = getBounds();
				Point pt = toDisplay(new Point(location.x, location.y));
				e.x = pt.x;
				e.y = pt.y;
				e.width = location.width;
				e.height = location.height;
			}

			public void getChildCount(AccessibleControlEvent e) {
				e.detail = 0;
			}

			public void getRole(AccessibleControlEvent e) {
				e.detail = ACC.ROLE_TREE;
			}

			public void getState(AccessibleControlEvent e) {
				//e.detail = isOpen ? ACC.STATE_EXPANDED : ACC.STATE_COLLAPSED;
			}
		});
	}
	
	protected void disposeImpl() {
		if (cursor != null) {
			cursor.dispose();
			cursor = null;
			one.dispose();
			two.dispose();
			three.dispose();
			
			grone.dispose();
			grtwo.dispose();
			grthree.dispose();
		}
	}

	public void addSelectionListener(SelectionListener listener) {
		checkWidget ();
		if (listener == null) return;
		TypedListener typedListener = new TypedListener (listener);
		addListener (SWT.Selection,typedListener);
	}
	
	public void removeSelectionListener(SelectionListener listener) {
		checkWidget ();
		if (listener == null) return;
		removeListener (SWT.Selection, listener);
	}
	
	protected void notifyListeners(int eventType) {
		Event event = new Event();
		event.type = eventType;
		event.widget = this;
		notifyListeners(eventType, event);
	}
	
	protected void paintRect(GC gc, int x, int y, Color a, Color b, Color c) {
		int[] p = new int[] { 0, 2, 2, 0, 30, 0, 32, 2, 32, 30, 30, 32, 2, 32, 0, 30};
		
		int[] q = new int[p.length];
		for (int i = 0; i < p.length / 2; i++) {
			q[i*2] = p[i*2] + x;
			q[i*2+1] = p[i*2+1] + y;
		}
		
		Region region = new Region(getDisplay());
		region.add(q);
		
		gc.setClipping(region);
		
		gc.setBackground(a);
		gc.setForeground(b);
		gc.fillGradientRectangle(x, y, 32, 32, true);
		gc.setClipping((Region)null);
		gc.setForeground(c);
		gc.drawPolygon(q);
		
		gc.setForeground(getForeground());
		gc.setBackground(getBackground());
		String st = "Tomcat Test Environment";
		Point stp = gc.stringExtent(st);
		gc.drawString(st, x+16 - stp.x / 2, y + 32 + 2);
	}

	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		
		Point s = getSize();

		gc.setBackground(getBackground());
		gc.fillRectangle(0, 0, s.x, s.y);

		/*if (mouseOver)
			gc.setBackground(getFocusColor());
		else
			gc.setBackground(getForeground());*/
		
		// one
		paintRect(gc, 60, 0, one, two, three);
		
		paintRect(gc, 140, 0, grone, grtwo, grthree);
		
		/*if (hasFocus) {
			gc.setBackground(getBackground());
	   		gc.setForeground(getFocusColor());
	   		gc.drawFocus(0, 0, 10, 11);
		}*/
	}

	/*public Color getFocusColor() {
		return focusColor;
	}

	public void setFocusColor(Color color) {
		focusColor = color;
	}*/

	public Point computeSize(int wHint, int hHint, boolean changed) {
		return new Point(200, 60);
	}
}