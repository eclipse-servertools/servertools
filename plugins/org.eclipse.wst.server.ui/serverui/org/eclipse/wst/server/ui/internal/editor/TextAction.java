/*******************************************************************************
 * Copyright (c) 2003, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.editor;

import org.eclipse.jface.action.Action;
import org.eclipse.wst.server.ui.internal.Trace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
/**
 * Text actions (cut, copy, paste) for the Web browser.
 */
public class TextAction extends Action {
	protected Display display;
	protected Clipboard clipboard;
	protected byte type;

	public static final byte CUT_ACTION = 0;
	public static final byte COPY_ACTION = 1;
	public static final byte PASTE_ACTION = 2;
	
	/**
	 * TextAction constructor comment.
	 */
	protected TextAction(Display display, byte type) {
		super("Text action: " + type);
		this.display = display;
		clipboard = new Clipboard(display);
		this.type = type;
	}

	protected Control getControl() {
		Control control = display.getFocusControl();
		return control;
	}
	
	protected Point getControlSelection() {
		Control control = getControl();
		if (control == null)
			return null;

		if (control instanceof Text) {
			Text text = (Text) control;
			return text.getSelection();
		} else if (control instanceof Combo) {
			Combo combo = (Combo) control;
			return combo.getSelection();
		} else
			return null;
	}
	
	protected void setControlSelection(Point sel) {
		Control control = getControl();
		if (control == null)
			return;

		if (control instanceof Text) {
			Text text = (Text) control;
			text.setSelection(sel);
		} else if (control instanceof Combo) {
			Combo combo = (Combo) control;
			combo.setSelection(sel);
		}
	}
	
	protected String getControlText() {
		Control control = getControl();
		if (control == null)
			return null;

		if (control instanceof Text) {
			Text text = (Text) control;
			return text.getText();
		} else if (control instanceof Combo) {
			Combo combo = (Combo) control;
			return combo.getText();
		} else
			return null;
	}
	
	protected void setControlText(String text) {
		Control control = getControl();
		if (control == null)
			return;

		if (control instanceof Text) {
			Text text2 = (Text) control;
			text2.setText(text);
		} else if (control instanceof Combo) {
			Combo combo = (Combo) control;
			combo.setText(text);
		}
	}
	
	/**
	 * Copies the selected text to the clipboard.  The text will be put in the 
	 * clipboard in plain text format.
	 */
	public void copy() {
		Point selection = getControlSelection();
		Control control = getControl();
		if (control == null)
			return;

		if (control instanceof Text) {
			Text textcontrol = (Text) control;
			if ((textcontrol.getStyle() & SWT.PASSWORD) != 0) return;
		}
		
		String text = getControlText();
		if (selection == null || text == null)
			return;
	
		int length = selection.y - selection.x;
		if (length > 0) {
			TextTransfer plainTextTransfer = TextTransfer.getInstance();
			try {
				clipboard.setContents(
					new String[] { text.substring(selection.x, selection.y) }, 
					new Transfer[] { plainTextTransfer });
			} catch (SWTError error) {
				// Copy to clipboard failed. This happens when another application 
				// is accessing the clipboard while we copy. Ignore the error.
			}
		}
	}
	
	/**
	 * Moves the selected text to the clipboard.  The text will be put in the 
	 * clipboard in plain text format and RTF format.
	 */
	public void cut(){
		Point selection = getControlSelection();
		if (selection == null)
			return;

		Control control = getControl();
		if (control == null)
			return;

		if (control instanceof Text) {
			Text textcontrol = (Text) control;
			if ((textcontrol.getStyle() & SWT.PASSWORD) != 0) return;
		}
		
		if (selection.y > selection.x) {
			copy();
			delete();
		}
	}
	
	/**
	 * Deletes the character to the right of the caret. Delete the selected text if any.
	 */
	public void delete() {
		Point selection = getControlSelection();
		String text = getControlText();
		if (selection == null || text == null)
			return;
	
		if (selection.x != selection.y) {
			text = text.substring(0, selection.x) + text.substring(selection.y);
			setControlText(text);
			setControlSelection(new Point(selection.x, selection.x));
		}
	}
	
	/** 
	 * Replaces the selection with the clipboard text or insert the text at 
	 * the current caret offset if there is no selection. 
	 * If the widget has the SWT.SINGLE style and the clipboard text contains
	 * more than one line, only the first line without line delimiters is 
	 * inserted in the widget.
	 */
	public void paste() {
		TextTransfer transfer = TextTransfer.getInstance();
		Point selection = getControlSelection();
		String text = getControlText();
		if (selection == null)
			return;
		
		String newText = (String) clipboard.getContents(transfer);
		if (newText != null && newText.length() > 0) {
			if (text == null)
				text = newText;
			else
				text = text.substring(0, selection.x) + newText + text.substring(selection.y);
			setControlText(text);
	
			// set the selection to the end of the paste
			int x = selection.x + newText.length();
			setControlSelection(new Point(x, x));
		}
	}
	
	/**
	 * Execute the action.
	 */
	public void run() {
		if (display == null)
			return;
		if (type == CUT_ACTION)
			cut();
		else if (type == COPY_ACTION)
			copy();
		else if (type == PASTE_ACTION)
			paste();
	}
	
	/**
	 * Update the actions enabled/disabled state.
	 */
	protected void update() {
		if (getControl() == null) {
			setEnabled(false);
			return;
		}
		Point selection = getControlSelection();
		String text = getControlText();

		try {
			if (type == CUT_ACTION)
				setEnabled(text != null && text.length() > 0 && selection != null && selection.x < selection.y);
			else if (type == COPY_ACTION)
				setEnabled(text != null && text.length() > 0 && selection != null && selection.x < selection.y);
			else if (type == PASTE_ACTION) {
				Control control = getControl();
				if (!control.isEnabled()) {
					setEnabled(false);
					return;
				}
				if (!(control instanceof Text)) {
					setEnabled(false);
					return;
				}

				Text text2 = (Text) control;
				if (!text2.getEditable()) {
					setEnabled(false);
					return;
				}
				TextTransfer transfer = TextTransfer.getInstance();
				String newText = (String) clipboard.getContents(transfer);
				setEnabled(newText != null && newText.length() > 0);
			}
		} catch (Exception e) {
			if (Trace.SEVERE) {
				Trace.trace(Trace.STRING_SEVERE, "Error updating text action", e);
			}
		}
	}
}
