/*******************************************************************************
 * Copyright (c) 2009,2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.server.ui.internal;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jst.server.core.internal.JavaServerPlugin;
import org.eclipse.jst.server.core.internal.ProfilerPreferences;
import org.eclipse.jst.server.core.internal.ServerProfiler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Preference page which can be used to select the server profiler
 * that is called when the server is launched in Profile mode
 */
public class ProfilePreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {
	
	private Combo comboBox;
	/* List to hold the names of the profilers */
	private ArrayList<String> nameList;
	/* List to hold the id's of the profilers */
	private ArrayList<String> idList;
	
	public void init(IWorkbench arg0) {
		nameList = new ArrayList<String>();
		idList = new ArrayList<String>();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		/* Set the layout of the composite */
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		/* Layout data for the target composite */
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalIndent = 0;
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);
		
		Label profilersLabel = new Label(composite, SWT.LEFT);
		profilersLabel.setText(Messages.profilerPrefsTitle);
		
		loadValues();
		if ( nameList == null || nameList.size() == 0 ) {
		
			/* There are no registered server profilers */
			Composite labelComposite = new Composite(composite, SWT.NONE);
			GridLayout labelLayout = new GridLayout();	
			layout.numColumns = 1;
			layout.marginLeft=10;
			labelComposite.setLayout(labelLayout);
		
			Label noRegisteredProfilersLabel = new Label(labelComposite,SWT.LEFT);
			
			GridData nrplData = new GridData();
			noRegisteredProfilersLabel.setData(nrplData);
			noRegisteredProfilersLabel.setForeground(new Color(Display.getDefault(), 255, 0, 0));
			noRegisteredProfilersLabel.setText(Messages.profilerPrefsNoneRegistered);
		} else {
			/* Create the combo box */
			comboBox = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);

			GridData comboLayout = new GridData();
			comboLayout.verticalAlignment = GridData.BEGINNING;
			comboLayout.horizontalAlignment = GridData.FILL;
			comboLayout.grabExcessHorizontalSpace = true;
			comboBox.setLayoutData(comboLayout);

			String[] strList = nameList.toArray(new String[0]);
			comboBox.setItems( strList );
			int index = findIndexOfSelectedProfiler();
			if ( index != -1 ) 
				comboBox.select(index);
			else
				comboBox.select(0);
		}
		
		Dialog.applyDialogFont(composite);
		return composite;
		
	}
	
	/**
	 * Loads the existing preference from the preference store and returns the
	 * index of that profiler in the current list of id's
	 * @return the index of the saves profiler in idList, -1 if not found
	 */
	private int findIndexOfSelectedProfiler() {
		String preference = ProfilerPreferences.getInstance().getServerProfilerId();
		if ( preference == null ) return -1;
		
		for ( int i = 0; i < idList.size(); i++ ) {
			if ( (idList.get(i)).equals(preference) ) 
				return i;			
		}
		return -1;
	}

	/**
	 * Loads the server profilers data and stores the information in a list
	 */
	private void loadValues() {
		ServerProfiler[] profilers = JavaServerPlugin.getServerProfilers();
		for (int i = 0; i < profilers.length; i++) {
			String name = profilers[i].getName();
			String id = profilers[i].getId();
			if ( name != null && id != null ) {
				nameList.add(name);
				idList.add(id);
			}
		}
	}

	@Override
	public boolean performOk() {
		
		if ( comboBox != null ) {
			/* Get the selected profiler and save it to the preferences */
			String selectedId = idList.get( comboBox.getSelectionIndex() );
			ProfilerPreferences.getInstance().setServerProfilerId( selectedId );
		}
		return super.performOk();
	}

}
