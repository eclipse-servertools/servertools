/*******************************************************************************
 * Copyright (c) 2003, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.core.internal.ServerPreferences;
import org.eclipse.wst.server.discovery.Discovery;
import org.eclipse.wst.server.ui.ICacheUpdateListener;
import org.eclipse.wst.server.ui.ServerUIUtil;
/**
 * The preference page that holds server properties.
 */
public class ServerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected ServerPreferences preferences;
	protected ServerUIPreferences uiPreferences;

	protected Button showOnActivity;
	protected Button refreshNow;
	protected Text updateTime;
	protected Combo updateCacheFrequencyCombo;
	
	public static final String CACHE_LAST_UPDATED_DATE_FORMAT = "EEE MMM dd yyyy kk:mm:ss zzz";

	/**
	 * ServerPreferencesPage constructor comment.
	 */
	public ServerPreferencePage() {
		super();
	
		preferences = ServerPreferences.getInstance();
		uiPreferences = ServerUIPlugin.getPreferences();
	}
	
	/**
	 * Create the preference options.
	 *
	 * @param parent org.eclipse.swt.widgets.Composite
	 * @return org.eclipse.swt.widgets.Control
	 */
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(parent, ContextIds.PREF_GENERAL);
		
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);
		
		showOnActivity = new Button(composite, SWT.CHECK);
		showOnActivity.setText(Messages.prefShowOnActivity);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		showOnActivity.setLayoutData(data);
		showOnActivity.setSelection(uiPreferences.getShowOnActivity());
		whs.setHelp(showOnActivity, ContextIds.PREF_GENERAL_SHOW_ON_ACTIVITY);
		
		Label label = new Label(composite, SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 3;
		label.setLayoutData(data);
				
		Group cacheGroup = new Group(composite, SWT.NONE);
		cacheGroup.setText(Messages.cacheUpdate_boxTitle);
		layout = new GridLayout();
		layout.numColumns = 3;
		cacheGroup.setLayout(layout);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		cacheGroup.setLayoutData(data);
		Label updateCacheLabel = new Label(cacheGroup, SWT.NONE);
		updateCacheLabel.setText(Messages.cacheUpdate_frequencyLabel);

		
		updateCacheFrequencyCombo = new Combo(cacheGroup, SWT.READ_ONLY);
		String[] frequency = new String[5];
		frequency[0] = Messages.cacheFrequency_manual;
		frequency[1] = Messages.cacheFrequency_daily;
		frequency[2] = Messages.cacheFrequency_weekly;
		frequency[3] = Messages.cacheFrequency_monthly;
		frequency[4] = Messages.cacheFrequency_quarterly;
		
		updateCacheFrequencyCombo.setItems(frequency);
			
		int cacheFrequency = uiPreferences.getCacheFrequency();
		updateCacheFrequencyCombo.select(cacheFrequency);
		
		refreshNow = new Button(cacheGroup, SWT.PUSH);
		data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		refreshNow.setLayoutData(data);
		String refreshButtonText = ServerUIUtil.refreshButtonText;
		ServerUIUtil.setListener(new UpdateJobChangeListener());
		 // Manual refresh is allowed if Downloadable adapters are shown in server/runtime wizard and refresh not active
		if (refreshButtonText.equals(Messages.cacheUpdate_refreshNow) && ServerUIPlugin.getPreferences().getExtAdapter())
			refreshNow.setEnabled(true);
		else
			refreshNow.setEnabled(false);
		refreshNow.setText(refreshButtonText);
		
		refreshNow.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				refreshNow.setEnabled(false);
				updateRefreshText(Messages.cacheUpdate_refreshing);
				updateJob = ServerUIUtil.refreshServerNode(true);
			}
		});
		
		Label lastUpdatedLabel = new Label(cacheGroup, SWT.NONE);
		lastUpdatedLabel.setText(Messages.cacheUpdate_lastUpdatedOn);
		updateTime = new Text(cacheGroup, SWT.READ_ONLY);
		updateTime.setText(getLastUpdateDate());
		
		Dialog.applyDialogFont(composite);
		
		
		return composite;
	}
	Job updateJob = null;
	
	private class UpdateJobChangeListener implements ICacheUpdateListener {
		public UpdateJobChangeListener() {
		}
		public void start(){
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					 if (refreshNow != null && !refreshNow.isDisposed()){
						 refreshNow.setText(Messages.cacheUpdate_refreshing);
						 refreshNow.setEnabled(false);
					 }
				}
			});
		}

		public void done() {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					 if (refreshNow != null && !refreshNow.isDisposed()){
						 refreshNow.setEnabled(true);
						 refreshNow.setText(Messages.cacheUpdate_refreshNow);
						 updateTime.setText(getLastUpdateDate());
					 }
				}
			});

		}

	}
	public void updateRefreshText(String refreshText){
		refreshNow.setText(refreshText);
	}

	/**
	 * Initializes this preference page using the passed workbench.
	 *
	 * @param workbench the current workbench
	 */
	public void init(IWorkbench workbench) {
		// do nothing
	}

	/**
	 * Performs special processing when this page's Defaults button has been pressed.
	 */
	protected void performDefaults() {
		showOnActivity.setSelection(uiPreferences.getDefaultShowOnActivity());
		updateCacheFrequencyCombo.select(/*Weekly*/2);
		
		super.performDefaults();
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		uiPreferences.setShowOnActivity(showOnActivity.getSelection());
		uiPreferences.setCacheFrequency(updateCacheFrequencyCombo.getSelectionIndex());
		return true;
	}
	
	public void dispose(){
		ServerUIUtil.setListener(null);
	}
	
	public String getLastUpdateDate() {
		String lastUpdatedDate = Discovery.getLastUpdatedDate();
		lastUpdatedDate = lastUpdatedDate.trim();
		SimpleDateFormat format = new SimpleDateFormat(CACHE_LAST_UPDATED_DATE_FORMAT, Locale.ENGLISH);
		Date oldDate;
		try {
			oldDate = format.parse(lastUpdatedDate);
			Date now = new Date();
			long diffInMillies = now.getTime() - oldDate.getTime();
			
			long msPerMinute = 60 * 1000;
		    long msPerHour = msPerMinute * 60;
		    long msPerDay = msPerHour * 24;
		    long msPerWeek = msPerDay * 7;
		    long msPerMonth = msPerDay * 30;
		    long msPerYear = msPerDay * 365;
		    int value;
		    if (diffInMillies < msPerMinute) {
	    	    value = (int)diffInMillies/1000;
	    	    if (value > 1) {
	    	    	lastUpdatedDate = NLS.bind(Messages.secondsAgo, value);
	    	    } else {
	    	    	lastUpdatedDate = Messages.secondAgo;
	    	    }
		    }else if (diffInMillies < msPerHour) {
			    	value =(int)(diffInMillies/msPerMinute);
			    	if (value > 1) {
		    	    	lastUpdatedDate = NLS.bind(Messages.minutesAgo, value);
		    	    } else {
		    	    	lastUpdatedDate = Messages.minuteAgo;
		    	    }
		    } else if (diffInMillies < msPerDay ) {
			    	value = (int)(diffInMillies/msPerHour);
			    	if (value > 1) {
		    	    	lastUpdatedDate = NLS.bind(Messages.hoursAgo, value);
		    	    } else {
		    	    	lastUpdatedDate = Messages.hourAgo;
		    	    }
		    } else if (diffInMillies < msPerWeek) {
			    	value = (int)(diffInMillies/msPerDay);
			    	if (value > 1) {
		    	    	lastUpdatedDate = NLS.bind(Messages.daysAgo, value);
		    	    } else {
		    	    	lastUpdatedDate = Messages.dayAgo;
		    	    }
		    } else if (diffInMillies < msPerMonth) {
			    	value = (int)(diffInMillies/msPerWeek);
			    	if (value > 1) {
		    	    	lastUpdatedDate = NLS.bind(Messages.weeksAgo, value);
		    	    } else {
		    	    	lastUpdatedDate = Messages.weekAgo;
		    	    }
		    } else if (diffInMillies < msPerYear) {
			    	value = (int)(diffInMillies/msPerMonth);
			    	if (value > 1) {
		    	    	lastUpdatedDate = NLS.bind(Messages.monthsAgo, value);
		    	    } else {
		    	    	lastUpdatedDate = Messages.monthAgo;
		    	    }
		    }else {
		    	value = (int)(diffInMillies/msPerYear);
		    	if (value > 1) {
	    	    	lastUpdatedDate = NLS.bind(Messages.yearsAgo, value);
	    	    } else {
	    	    	lastUpdatedDate =Messages.yearAgo;
	    	    }
	    	    
		    }

		} catch (ParseException e1) {
			// In case of failure, display what was cached, so do nothing.
		}

		return lastUpdatedDate;
	}
}