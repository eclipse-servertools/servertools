/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
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
package org.eclipse.wst.server.ui.internal.audio;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.*;
import org.eclipse.swt.events.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.wst.server.ui.internal.ContextIds;
import org.eclipse.wst.server.ui.internal.Messages;
import org.eclipse.wst.server.ui.internal.SWTUtil;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Audio preference page.
 */
public class AudioPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected Button enableButton;
	protected Spinner volume;

	protected Map<String, IPath> userSoundMap;

	protected CategoryTableViewer viewer;
	
	boolean soundAvailable = true;

	/**
	 * AudioPreferencePage constructor comment.
	 */
	public AudioPreferencePage() {
		super();
	
		loadUserMapInfo();
	}
	
	protected IPath chooseAudioFile() {
		FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
		dialog.setText(Messages.audioPrefSelectFile);
		dialog.setFilterExtensions(new String[] {"*.au;*.wav"});
		dialog.setFilterPath(null);
		dialog.open();
	
		String[] filenames = dialog.getFileNames();
		if (filenames != null && filenames.length > 0) {
			String filterPath = dialog.getFilterPath();
			return new Path(filterPath + java.io.File.separator + filenames[0]);
		}
		return null;
	}

	/**
	 * Creates and returns the SWT control for the customized body 
	 * of this preference page under the given parent composite.
	 * <p>
	 * This framework method must be implemented by concrete
	 * subclasses.
	 * </p>
	 *
	 * @param parent the parent composite
	 * @return the new control
	 */
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(parent, ContextIds.AUDIO_PREFERENCES);
	
		final AudioCore core = AudioCore.getInstance();
	
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(3);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);
	
		enableButton = new Button(composite, SWT.CHECK);
		enableButton.setText(Messages.audioPrefEnable);
		enableButton.setSelection(AudioCore.getInstance().isSoundEnabled());
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 3;
		enableButton.setLayoutData(data);
		whs.setHelp(enableButton, ContextIds.AUDIO_PREFERENCES_ENABLE);
		
		final Label volumeLabel = new Label(composite, SWT.NONE);
		volumeLabel.setText(Messages.audioPrefVolume);
		data = new GridData();
		data.horizontalIndent = 20;
		volumeLabel.setLayoutData(data);
		volumeLabel.setEnabled(enableButton.getSelection());
		
		volume = new Spinner(composite, SWT.BORDER);
		volume.setMinimum(0);
		volume.setMaximum(20);
		volume.setSelection(AudioCore.getInstance().getVolume());
		data = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data.horizontalSpan = 2;
		data.widthHint = 60;
		volume.setLayoutData(data);
		volume.setEnabled(enableButton.getSelection());
		whs.setHelp(volume, ContextIds.AUDIO_PREFERENCES_VOLUME);
		
		enableButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				volumeLabel.setEnabled(enableButton.getSelection());
				volume.setEnabled(enableButton.getSelection());
			}
		});
		
		Label label = new Label(composite, SWT.NONE);
		data = new GridData();
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		
		label = new Label(composite, SWT.NONE);
		label.setText(Messages.audioPrefSounds);
		data = new GridData();
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		
		final Table table = new Table(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		table.setLayoutData(data);
		whs.setHelp(table, ContextIds.AUDIO_PREFERENCES_SOUNDS_TABLE);
		
		viewer = new CategoryTableViewer(table);
		
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);
		table.setHeaderVisible(true);
		
		tableLayout.addColumnData(new ColumnPixelData(23, false));
		TableColumn col = new TableColumn(table, SWT.CENTER, 0);
		col.setResizable(false);
		
		tableLayout.addColumnData(new ColumnWeightData(11, 110, true));
		col = new TableColumn(table, SWT.NONE, 1);
		col.setText(Messages.audioPrefSound);
		col.setResizable(true);
		
		tableLayout.addColumnData(new ColumnWeightData(15, 150, true));
		col = new TableColumn(table, SWT.NONE, 2);
		col.setText(Messages.audioPrefFile);
		col.setResizable(true);
		
		viewer.setContentProvider(new AudioTableContentProvider());
		viewer.setLabelProvider(new AudioTableLabelProvider(this));
		viewer.setInput("root");
		
		setCheckState(viewer);
		
		viewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				boolean checked = event.getChecked();
				Object obj = event.getElement();
				
				if (obj instanceof String) {
					String id = (String) obj;
					core.setCategoryEnabled(id, checked);
					//viewer.refresh();
					Iterator iterator = AudioTableContentProvider.getSoundsByCategory(id).iterator();
					while (iterator.hasNext()) {
						Sound s = (Sound) iterator.next();
						viewer.setChecked(s, checked);
						core.setSoundEnabled(s.getId(), checked);
					}
				} else {
					Sound sound = (Sound) obj;
					core.setSoundEnabled(sound.getId(), checked);
				}
			}
		});
		
		Composite right = new Composite(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		right.setLayout(layout);
		data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL);
		right.setLayoutData(data);
	
		// play button and table selection listener
		final Button playButton = SWTUtil.createButton(right, Messages.audioPrefPlay);
		playButton.setEnabled(false);
		whs.setHelp(playButton, ContextIds.AUDIO_PREFERENCES_PLAY);
	
		final Button browseButton = SWTUtil.createButton(right, Messages.audioPrefBrowse);
		browseButton.setEnabled(false);
		whs.setHelp(browseButton, ContextIds.AUDIO_PREFERENCES_BROWSE);
	
		final Button resetButton = SWTUtil.createButton(right, Messages.audioPrefReset);
		resetButton.setEnabled(false);
		whs.setHelp(resetButton, ContextIds.AUDIO_PREFERENCES_RESET);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				try {
					IStructuredSelection sel = (IStructuredSelection) event.getSelection();
					Object obj = sel.getFirstElement();
					if (obj instanceof Sound) {
						Sound sound = (Sound) obj;
						URL url = getSoundURL(sound.getId());
						if (url != null && soundAvailable)
							playButton.setEnabled(true);
						else
							playButton.setEnabled(false);
						browseButton.setEnabled(true);
						
						if (getUserSoundPath(sound.getId()) != null)
							resetButton.setEnabled(true);
						else
							resetButton.setEnabled(false);
					} else {
						playButton.setEnabled(false);
						browseButton.setEnabled(false);
						resetButton.setEnabled(false);
					}
				} catch (Exception ex) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Error in table selection", ex);
					}
				}
			}
		});
	
		soundAvailable = AudioCore.isAudioSupported();
		if (soundAvailable) {
			playButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					try {
						int sel = table.getSelectionIndex();
						Sound sound = (Sound) table.getItem(sel).getData();
						AudioCore.playSound(getSoundURL(sound.getId()), volume.getSelection());
					} catch (Exception ex) {
						if (Trace.SEVERE) {
							Trace.trace(Trace.STRING_SEVERE, "Error in table selection", ex);
						}
					}
				}
			});
		} else
			playButton.setEnabled(false);
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					IPath path = chooseAudioFile();
					if (path != null) {
						int sel = table.getSelectionIndex();
						Sound sound = (Sound) table.getItem(sel).getData();
						setUserSoundPath(sound.getId(), path);
						viewer.refresh(sound);
						playButton.setEnabled(true);
					}
				} catch (Exception ex) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Error browsing", ex);
					}
				}
			}
		});
		
		resetButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					int sel = table.getSelectionIndex();
					Sound sound = (Sound) table.getItem(sel).getData();
					removeUserSoundPath(sound.getId());
					viewer.refresh(sound);
					//playButton.setEnabled(true);
				} catch (Exception ex) {
					if (Trace.SEVERE) {
						Trace.trace(Trace.STRING_SEVERE, "Error reseting sound", ex);
					}
				}
			}
		});
		
		Dialog.applyDialogFont(composite);
		
		return composite;
	}

	protected void setCheckState(CheckboxTableViewer viewer) {
		AudioCore core = AudioCore.getInstance();
		
		Map<String, String> categories = core.getCategories();
		
		// first, find all the categories and sort
		List<String> cats = new ArrayList<String>();
		Iterator iterator = categories.keySet().iterator();
		while (iterator.hasNext())
			cats.add((String)iterator.next());
		
		// list them, ignoring empty ones
		iterator = categories.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			List l = AudioTableContentProvider.getSoundsByCategory(id);
			if (!l.isEmpty()) {
				if (core.isCategoryEnabled(id))
					viewer.setChecked(id, true);
				
				int size = l.size();
				for (int i = 0; i < size; i++) {
					Sound s = (Sound) l.get(i);
					if (core.isSoundEnabled(s.getId()))
						viewer.setChecked(s, true);
				}
			}
		}
		
		// finally, list the "misc" sounds
		List l = AudioTableContentProvider.getSoundsByCategory(null);
		if (!l.isEmpty()) {
			int size = l.size();
			for (int i = 0; i < size; i++) {
				Sound s = (Sound) l.get(i);
				if (core.isSoundEnabled(s.getId()))
					viewer.setChecked(s, true);
			}
		}
	}

	/**
	 * 
	 * @return org.eclipse.core.runtime.IPath
	 * @param id java.lang.String
	 */
	protected URL getSoundURL(String id) {
		try {
			IPath path = userSoundMap.get(id);
			if (path != null)
				return path.toFile().toURL();
		} catch (Exception e) {
			// ignore
		}
	
		return AudioCore.getInstance().getSound(id).getLocation();
	}

	/**
	 * 
	 * @return org.eclipse.core.runtime.IPath
	 * @param id java.lang.String
	 */
	protected IPath getUserSoundPath(String id) {
		try {
			IPath path = userSoundMap.get(id);
			if (path != null)
				return path;
		} catch (Exception e) {
			// ignore
		}
		return null;
	}

	/**
	 * Initializes this preference page for the given workbench.
	 * <p>
	 * This method is called automatically as the preference page is being created
	 * and initialized. Clients must not call this method.
	 * </p>
	 *
	 * @param workbench the workbench
	 */
	public void init(IWorkbench workbench) {
		// do nothing
	}

	/**
	 * 
	 */
	protected void loadUserMapInfo() {
		// create a copy of the user sound map
		Map<String, IPath> map = AudioCore.getInstance().getUserSoundMap();
		userSoundMap = new HashMap<String, IPath>(map.size());
		
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			IPath path = map.get(id);
			userSoundMap.put(id, path);
		}
	}

	/**
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		AudioCore core = AudioCore.getInstance();
		
		enableButton.setSelection(core.getDefaultSoundsEnabled());
		volume.setSelection(core.getDefaultVolume());
	
		userSoundMap = new HashMap<String, IPath>();
		viewer.refresh();
	
		super.performDefaults();
	}

	/** 
	 * @see PreferencePage#performOk()
	 */
	public boolean performOk() {
		AudioCore core = AudioCore.getInstance();
		core.setSoundsEnabled(enableButton.getSelection());
		core.setVolume(volume.getSelection());
	
		core.setUserSoundMap(userSoundMap);
		viewer.refresh();
	
		return super.performOk();
	}

	/**
	 * 
	 */
	protected void removeUserSoundPath(String id) {
		if (userSoundMap.containsKey(id))
			userSoundMap.remove(id);
	}

	/**
	 * 
	 */
	protected void saveUserMapInfo() {
		// create a copy of the user sound map
		Map map = AudioCore.getInstance().getUserSoundMap();
		userSoundMap = new HashMap<String, IPath>(map.size());
	
		Iterator iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String id = (String) iterator.next();
			IPath path = (IPath) map.get(id);
			userSoundMap.put(id, path);
		}
	}

	/**
	 * 
	 * @param path org.eclipse.core.runtime.IPath
	 */
	protected void setUserSoundPath(String id, IPath path) {
		userSoundMap.put(id, path);
	}
}
