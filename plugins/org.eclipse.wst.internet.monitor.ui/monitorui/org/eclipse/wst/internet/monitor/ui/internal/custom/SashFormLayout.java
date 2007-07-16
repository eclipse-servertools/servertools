/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.internet.monitor.ui.internal.custom;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.*;

/**
 * This class provides the layout for SashForm
 * 
 * @see SashForm
 */
class SashFormLayout extends Layout {
	protected boolean simple;

protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
	SashForm sashForm = (SashForm)composite;
	Control[] cArray = sashForm.getControls(true);
	int width = 0;
	int height = 0;
	if (cArray.length == 0) {
		if (wHint != SWT.DEFAULT) width = wHint;
		if (hHint != SWT.DEFAULT) height = hHint;
		return new Point(width, height);
	}
	// determine control sizes
	boolean vertical = sashForm.getOrientation() == SWT.VERTICAL;
	int maxIndex = 0;
	int maxValue = 0;
	Point[] sizes = new Point[cArray.length];
	for (int i = 0; i < cArray.length; i++) {
		if (vertical) {
			sizes[i] = cArray[i].computeSize(wHint, SWT.DEFAULT, flushCache);
			if (sizes[i].y > maxValue) {
				maxIndex = i;
				maxValue = sizes[i].y;
			}
			width = Math.max(width, sizes[i].x);
		} else {
			sizes[i] = cArray[i].computeSize(SWT.DEFAULT, hHint, flushCache);
			if (sizes[i].x > maxValue) {
				maxIndex = i;
				maxValue = sizes[i].x;
			}
			height = Math.max(height, sizes[i].y);
		}
	}
	// get the ratios
	long[] ratios = new long[cArray.length];
	long total = 0;
	for (int i = 0; i < cArray.length; i++) {
		Object data = cArray[i].getLayoutData();
		if (data != null && data instanceof SashFormData) {
			ratios[i] = ((SashFormData)data).weight;
		} else {
			data = new SashFormData();
			cArray[i].setLayoutData(data);
			((SashFormData)data).weight = ratios[i] = ((200 << 16) + 999) / 1000;
			
		}
		total += ratios[i];
	}
	if (ratios[maxIndex] > 0) {
		int sashwidth = sashForm.sashes.length > 0 ? sashForm.SASH_WIDTH + sashForm.sashes [0].getBorderWidth() * 2 : sashForm.SASH_WIDTH;
		if (vertical) {
			height += (int)(total * maxValue / ratios[maxIndex]) + (cArray.length - 1) * sashwidth;
		} else {
			width += (int)(total * maxValue / ratios[maxIndex]) + (cArray.length - 1) * sashwidth;
		}
	}
	width += sashForm.getBorderWidth()*2;
	height += sashForm.getBorderWidth()*2;
	if (wHint != SWT.DEFAULT) width = wHint;
	if (hHint != SWT.DEFAULT) height = hHint;
	return new Point(width, height);
}

protected boolean flushCache(Control control) {
	return true;
}

protected void layout(Composite composite, boolean flushCache) {
	SashForm sashForm = (SashForm)composite;
	Rectangle area = sashForm.getClientArea();
	if (area.width <= 1 || area.height <= 1) return;
	
	Control[] newControls = sashForm.getControls(true);
	if (sashForm.controls.length == 0 && newControls.length == 0)
		return;
	sashForm.controls = newControls;
	
	Control[] controls = sashForm.controls;
	
	if (sashForm.maxControl != null && !sashForm.maxControl.isDisposed()) {
		for (int i= 0; i < controls.length; i++){
			if (controls[i] != sashForm.maxControl) {
				controls[i].setBounds(-200, -200, 0, 0);
			} else {
				controls[i].setBounds(area);
			}
		}
		return;
	}
	
	// simple layout
	if (simple) {
		// hide sashes
		for (int i = 0; i < sashForm.sashes.length; i++) {
			sashForm.sashes[i].setEnabled(false);
			sashForm.sashes[i].setVisible(false);
		}
		
		Point p = controls[0].computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		controls[0].setBounds(0, 0, area.width, p.y);
		controls[1].setBounds(0, p.y + 2, area.width, area.height - p.y - 2);
		
		return;
	}
	
	// keep just the right number of sashes
	if (sashForm.sashes.length < controls.length - 1) {
		Sash[] newSashes = new Sash[controls.length - 1];
		System.arraycopy(sashForm.sashes, 0, newSashes, 0, sashForm.sashes.length);
		for (int i = sashForm.sashes.length; i < newSashes.length; i++) {
			newSashes[i] = new Sash(sashForm, sashForm.sashStyle);
			newSashes[i].setBackground(sashForm.background);
			newSashes[i].setForeground(sashForm.foreground);
			newSashes[i].addListener(SWT.Selection, sashForm.sashListener);
		}
		sashForm.sashes = newSashes;
	}
	if (sashForm.sashes.length > controls.length - 1) {
		if (controls.length == 0) {
			for (int i = 0; i < sashForm.sashes.length; i++) {
				sashForm.sashes[i].dispose();
			}
			sashForm.sashes = new Sash[0];
		} else {
			Sash[] newSashes = new Sash[controls.length - 1];
			System.arraycopy(sashForm.sashes, 0, newSashes, 0, newSashes.length);
			for (int i = controls.length - 1; i < sashForm.sashes.length; i++) {
				sashForm.sashes[i].dispose();
			}
			sashForm.sashes = newSashes;
		}
	}
	if (controls.length == 0) return;
	Sash[] sashes = sashForm.sashes;
	
	for (int i = 0; i < sashForm.sashes.length; i++) {
		sashes[i].setEnabled(true);
		sashes[i].setVisible(true);
	}
	
	if (sashForm.resize) {
		Control c1 = controls[0];
		Control c2 = controls[1];
		
		int h1 = area.height / 3;
		int h2 = area.height - h1 - sashes[0].getBounds().height;
		
		Object data1 = c1.getLayoutData();
		if (data1 == null || !(data1 instanceof SashFormData)) {
			data1 = new SashFormData();
			c1.setLayoutData(data1);
		}
		Object data2 = c2.getLayoutData();
		if (data2 == null || !(data2 instanceof SashFormData)) {
			data2 = new SashFormData();
			c2.setLayoutData(data2);
		}
		((SashFormData)data1).weight = ((200 << 16) + 999) / 1000 * h1 / h2;
		((SashFormData)data2).weight = ((200 << 16) + 999) / 1000;
		sashForm.resize = false;
	}
	
	// get the ratios
	long[] ratios = new long[controls.length];
	long total = 0;
	for (int i = 0; i < controls.length; i++) {
		Object data = controls[i].getLayoutData();
		if (data != null && data instanceof SashFormData) {
			ratios[i] = ((SashFormData)data).weight;
		} else {
			data = new SashFormData();
			controls[i].setLayoutData(data);
			((SashFormData)data).weight = ratios[i] = ((200 << 16) + 999) / 1000;
		}
		total += ratios[i];
	}
	
	int sashwidth = sashes.length > 0 ? sashForm.SASH_WIDTH + sashes [0].getBorderWidth() * 2 : sashForm.SASH_WIDTH;
	if (sashForm.getOrientation() == SWT.HORIZONTAL) {
		int width = (int)(ratios[0] * (area.width - sashes.length * sashwidth) / total);
		int x = area.x;
		controls[0].setBounds(x, area.y, width, area.height);
		x += width;
		for (int i = 1; i < controls.length - 1; i++) {
			sashes[i - 1].setBounds(x, area.y, sashwidth, area.height);
			x += sashwidth;
			width = (int)(ratios[i] * (area.width - sashes.length * sashwidth) / total);
			controls[i].setBounds(x, area.y, width, area.height);
			x += width;
		}
		if (controls.length > 1) {
			sashes[sashes.length - 1].setBounds(x, area.y, sashwidth, area.height);
			x += sashwidth;
			width = area.width - x;
			controls[controls.length - 1].setBounds(x, area.y, width, area.height);
		}
	} else {
		int height = (int)(ratios[0] * (area.height - sashes.length * sashwidth) / total);
		int y = area.y;
		controls[0].setBounds(area.x, y, area.width, height);
		y += height;
		for (int i = 1; i < controls.length - 1; i++) {
			sashes[i - 1].setBounds(area.x, y, area.width, sashwidth);
			y += sashwidth;
			height = (int)(ratios[i] * (area.height - sashes.length * sashwidth) / total);
			controls[i].setBounds(area.x, y, area.width, height);
			y += height;
		}
		if (controls.length > 1) {
			sashes[sashes.length - 1].setBounds(area.x, y, area.width, sashwidth);
			y += sashwidth;
			height = area.height - y;
			controls[controls.length - 1].setBounds(area.x, y, area.width, height);
		}
	}
}
}