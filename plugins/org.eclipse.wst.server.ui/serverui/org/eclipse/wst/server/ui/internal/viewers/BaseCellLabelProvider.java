/*******************************************************************************
 * Copyright (c) 2003, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.viewers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
/**
 * A basic cell label provider.
 */
public abstract class BaseCellLabelProvider extends ColumnLabelProvider {
	public ILabelDecorator decorator;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ColumnLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
	 */
	public void update(ViewerCell cell){
		super.update(cell);
		Object element = cell.getElement();
		int index = cell.getColumnIndex();
		cell.setText(getColumnText(element,index));
		Image image = getColumnImage(element, index);
		cell.setImage(image);
	}

	/**
	 * Create a BaseCellLabelProvider
	 */
	public BaseCellLabelProvider() {
		super();
	}

	/**
	 * Create a BaseCellLabelProvider with a decorator at the front of the row
	 * @param decorator
	 */
	public BaseCellLabelProvider(ILabelDecorator decorator) {
		super();
		if (decorator == null)
			decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		
		this.decorator = decorator;
	} 

	public Point getToolTipShift(Object object) {
		return new Point(5, 5);
	}

	public int getToolTipDisplayDelayTime(Object object) {
		return 2000;
	}

	public int getToolTipTimeDisplayed(Object object) {	
		return 5000;
	}

	/**
	 * Extenders of this class would implement this method to provide an image to the column based on the element 
	 * being passed
	 * @param element
	 * @param index
	 * @return
	 */
	public abstract Image getColumnImage(Object element, int index);

	/**
    * Extenders of this class would implement this method to provide a text label to the column based on the element
    * being passed 
	 * @param element
	 * @param index
	 * @return
	 */
	public abstract String getColumnText(Object element, int index);
}