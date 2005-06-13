/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - Initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.server.ui.internal.audio;

import java.util.*;
import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IMemento;
import org.eclipse.wst.server.ui.internal.ServerUIPlugin;
import org.eclipse.wst.server.ui.internal.Trace;
/**
 * Main audio plugin class.
 */
public class AudioCore {
	protected static AudioCore instance;

	public static final String PREF_SOUND_ENABLED = "soundEnabled";
	public static final String PREF_VOLUME = "volume";

	public static final String SOUNDS_FILE = "sounds.xml";
	public static final String DISABLED_FILE = "disabled-sounds.xml";

	// Categories - map of String id to String names
	private Map categories;

	// Sounds - map of String id to Sound
	private Map sounds;

	// specific sounds or categories that have been disabled, by id
	private List disabledSounds;
	private List disabledCategories;

	// SoundMap - map of String id to an IPath
	private Map userSoundMap;

	/**
	 * AudioCore constructor comment.
	 */
	private AudioCore() {
		super();
	
		loadExtensionPoints();
	
		loadSoundMap();
		loadDisabledLists();
		
		
	}

	/**
	 * Return the categories
	 *
	 * @return java.util.Map
	 */
	protected Map getCategories() {
		return categories;
	}

	/**
	 * Returns the audio clip.
	 *
	 * @param url java.net.URL
	 * @return javax.sound.sampled.Clip
	 */
	protected static Clip getClip(URL url) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
	
			AudioFormat format = audioInputStream.getFormat();
	
			/**
			 * we can't yet open the device for ALAW/ULAW playback,
			 * convert ALAW/ULAW to PCM
			 */
			if ((format.getEncoding() == AudioFormat.Encoding.ULAW) ||
				(format.getEncoding() == AudioFormat.Encoding.ALAW)) {
				AudioFormat tmp = new AudioFormat(
					AudioFormat.Encoding.PCM_SIGNED, 
					format.getSampleRate(),
					format.getSampleSizeInBits() * 2,
					format.getChannels(),
					format.getFrameSize() * 2,
					format.getFrameRate(), true);
				audioInputStream = AudioSystem.getAudioInputStream(tmp, audioInputStream);
				format = tmp;
			}
			DataLine.Info info = new DataLine.Info(
				Clip.class, audioInputStream.getFormat(), 
				((int) audioInputStream.getFrameLength() *
				format.getFrameSize()));
		
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(audioInputStream);
			return clip;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not get clip: " + url, e);
		}
		return null;
	}
	
	/**
	 * Returns true if audio is currently available on this system.
	 *
	 * @return boolean
	 */
	protected static boolean isAudioSupported() {
		try {
			boolean sound = false;
			Mixer.Info[] info2 = AudioSystem.getMixerInfo();
			if (info2 != null) {
				int size = info2.length;
				for (int i = 0; i < size; i++) {
					//Trace.trace(" " + info2[i]);
					Mixer mixer = AudioSystem.getMixer(info2[i]);
					if (mixer != null) {
						//Trace.trace("   Mixer:" + mixer);
						//Trace.trace("   " + mixer.getLineInfo());
						try {
							Line.Info info = mixer.getLineInfo();
							Line line = mixer.getLine(info);
							//Trace.trace("   Line:" + line);
							if (line != null && line.toString().indexOf("Output") >= 0)
								sound = true;
						} catch (Exception e) {
							// ignore
						}
					}
				}
			}
			return sound;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not verify audio status", e);
		}
		return true;
	}
	
	/**
	 * Returns true if sound is enabled.
	 *
	 * @return boolean
	 */
	public boolean getDefaultSoundsEnabled() {
		return getPreferenceStore().getDefaultBoolean(PREF_SOUND_ENABLED);
	}

	/**
	 * Returns the default volume.
	 *
	 * @return int
	 */
	public int getDefaultVolume() {
		return getPreferenceStore().getDefaultInt(PREF_VOLUME);
	}

	/**
	 * Returns the singleton instance.
	 *
	 * @return org.eclipse.audio.internal.AudioCore
	 */
	public static AudioCore getInstance() {
		if (instance == null)
			instance = new AudioCore();
		return instance;
	}

	/**
	 * 
	 * @return org.eclipse.jface.preference.IPreferenceStore
	 */
	protected IPreferenceStore getPreferenceStore() {
		return ServerUIPlugin.getInstance().getPreferenceStore();
	}

	/**
	 * Returns the sound with the given id.
	 *
	 * @param id java.lang.String
	 * @return org.eclipse.audio.Sound
	 */
	protected Sound getSound(String id) {
		try {
			return (Sound) sounds.get(id);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Return the sounds.
	 *
	 * @return java.util.Map
	 */
	protected Map getSounds() {
		return sounds;
	}

	/**
	 * Returns the full user sound map.
	 *
	 * @return java.util.Map
	 */
	protected Map getUserSoundMap() {
		if (userSoundMap == null)
			loadSoundMap();
		return userSoundMap;
	}

	/**
	 * Return the current URL for this sound.
	 *
	 * @param id java.lang.String
	 * @return java.net.URL
	 */
	protected IPath getUserSoundPath(String id) {
		try {
			if (userSoundMap == null)
				loadSoundMap();
	
			IPath path = (IPath) userSoundMap.get(id);
			if (path != null)
				return path;
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not get sound URL: " + id, e);
		}
		return null;
	}

	/**
	 * Returns the preferred volume.
	 *
	 * @return int
	 */
	public int getVolume() {
		return getPreferenceStore().getInt(PREF_VOLUME);
	}

	/**
	 * Initialize the default preferences.
	 *
	 * @param store org.eclipse.jface.preference.IPreferenceStore
	 */
	public static void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(PREF_VOLUME, 18);
	}

	/**
	 * Returns true if the given category is enabled.
	 *
	 * @param id java.lang.String
	 * @return boolean
	 */
	public boolean isCategoryEnabled(String id) {
		if (id == null)
			return false;
	
		if (disabledCategories == null)
			loadDisabledLists();
	
		return (!disabledCategories.contains(id));
	}

	/**
	 * Returns true if sound is enabled.
	 *
	 * @return boolean
	 */
	public boolean isSoundEnabled() {
		return getPreferenceStore().getBoolean(PREF_SOUND_ENABLED);
	}

	/**
	 * Returns true if the given sound is enabled.
	 *
	 * @param id java.lang.String
	 * @return boolean
	 */
	public boolean isSoundEnabled(String id) {
		if (id == null)
			return false;
	
		if (disabledSounds == null)
			loadDisabledLists();
	
		return (!disabledSounds.contains(id));
	}

	/**
	 * Saves the disabled sound list.
	 */
	private void loadDisabledLists() {
		String filename = ServerUIPlugin.getInstance().getStateLocation().append(DISABLED_FILE).toOSString();
	
		FileInputStream in = null;
		disabledCategories = new ArrayList();
		disabledSounds = new ArrayList();
		try {
			in = new FileInputStream(filename);
			IMemento memento = XMLMemento.loadMemento(in);
	
			IMemento cat = memento.getChild("categories");
			IMemento[] children = cat.getChildren("category");
	
			int size = children.length;
			for (int i = 0; i < size; i++) {
				try {
					IMemento child = children[i];
					String id = child.getString("id");
	
					disabledCategories.add(id);
				} catch (Exception ex) {
					Trace.trace(Trace.SEVERE, "Error reading URL map ", ex);
				}
			}
	
			IMemento sound = memento.getChild("sounds");
			children = sound.getChildren("sound");
	
			size = children.length;
			for (int i = 0; i < size; i++) {
				try {
					IMemento child = children[i];
					String id = child.getString("id");
	
					disabledSounds.add(id);
				} catch (Exception ex) {
					Trace.trace(Trace.SEVERE, "Error reading URL map ", ex);
				}
			}
		} catch (Exception e) {
			//AudioPlugin.log(new Status(IStatus.WARNING, AudioPlugin.PLUGIN_ID, 0, "Could not load disabled sound information", e));
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Load extension point.
	 */
	private void loadExtensionPoints() {
		// load extension points
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] cf = registry.getConfigurationElementsFor(ServerUIPlugin.PLUGIN_ID, "audio");
	
		int size = cf.length;
		categories = new HashMap();
		sounds = new HashMap();
	
		for (int i = 0; i < size; i++) {
			try {
				String elementName = cf[i].getName();
				String id = cf[i].getAttribute("id");
				String name = cf[i].getAttribute("name");
				if ("category".equals(elementName)) {
					categories.put(id, name);
				} else if ("sound".equals(elementName)) {
					String category = cf[i].getAttribute("category");
					String location = cf[i].getAttribute("location");
	
					URL realURL = null;
					if (location != null && location.length() > 0) {
						String pluginId = cf[i].getDeclaringExtension().getNamespace();
						URL url = Platform.find(Platform.getBundle(pluginId), new Path(location));
						realURL = Platform.resolve(url);
					}
	
					Sound sound = new Sound(id, category, name, realURL);
					sounds.put(id, sound);
				}
			} catch (Throwable t) {
				Trace.trace(Trace.SEVERE, "Could not load audio: " + cf[i].getAttribute("id"), t);
			}
		}
	}

	/**
	 * Saves the disabled sound list.
	 */
	private void loadSoundMap() {
		String filename = ServerUIPlugin.getInstance().getStateLocation().append(SOUNDS_FILE).toOSString();
	
		InputStream in = null;
		userSoundMap = new HashMap();
		try {
			in = new FileInputStream(filename);
			IMemento memento = XMLMemento.loadMemento(in);
	
			IMemento[] children = memento.getChildren("map");
	
			int size = children.length;
			for (int i = 0; i < size; i++) {
				try {
					IMemento child = children[i];
					String id = child.getString("id");
					String pathStr = child.getString("path");
					IPath path = new Path(pathStr);
	
					userSoundMap.put(id, path);
				} catch (Exception ex) {
					Trace.trace(Trace.SEVERE, "Error reading URL map ", ex);
				}
			}
		} catch (Exception e) {
			// ignore
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Play the sound with the given id. (provided that
	 * the user has enabled the sound)
	 *
	 * @param id java.lang.String
	 */
	public void playSound(String id) {
		if (!isSoundEnabled())
			return;
	
		if (!isSoundEnabled(id))
			return;
	
		try {
			Sound sound = (Sound) sounds.get(id);
			String category = sound.getCategory();
			if (category != null && categories.containsKey(category)) {
				if (!isCategoryEnabled(category))
					return;
			}
	
			URL url = sound.getLocation();
			IPath path = getUserSoundPath(id);
			if (path != null)
				url = path.toFile().toURL();
	
			playSound(url, getVolume());
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error playing audio: " + id, e);
		}
	}

	/**
	 * Plays the sound at the given url.
	 *
	 * @param url java.net.URL
	 */
	protected static void playSound(URL url, final int volume) {
		try {
			Trace.trace(Trace.FINEST, "playSound");
			if (url == null || volume <= 0)
				return;
	
			final Clip clip = getClip(url);
			if (clip == null)
				return;
				
			Trace.trace(Trace.FINEST, "playing");
	
			Thread t = new Thread("Sound Thread") {
				public void run() {
					// set gain
					FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					double value = volume / 20.0;
					float dB = (float) (Math.log(value==0.0?0.0001:value)/Math.log(10.0)*20.0);
					gainControl.setValue(dB);
	
					Trace.trace(Trace.FINEST, "start");
					clip.start();
					try {
						sleep(99);
					} catch (Exception e) {
						// ignore
					}
	
					while (clip.isActive()) {
						try {
							sleep(99);
						} catch (Exception e) {
							break;
						}
					}
					clip.stop();
					clip.close();
					Trace.trace(Trace.FINEST, "stop");
				}
			};
			t.setDaemon(true);
			t.start();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error playing audio: " + url, e);
		}
	}

	/**
	 * Saves the disabled sounds and categories list.
	 */
	private void saveDisabledLists() {
		String filename = ServerUIPlugin.getInstance().getStateLocation().append(DISABLED_FILE).toOSString();
	
		FileOutputStream fout = null;
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("disabled");
	
			IMemento cat = memento.createChild("categories");
			Iterator iterator = disabledCategories.iterator();
			while (iterator.hasNext()) {
				IMemento child = cat.createChild("category");
				String id = (String) iterator.next();
				child.putString("id", id);
			}
	
			IMemento sound = memento.createChild("sounds");
			iterator = disabledSounds.iterator();
			while (iterator.hasNext()) {
				IMemento child = sound.createChild("sound");
				String id = (String) iterator.next();
				child.putString("id", id);
			}
	
			fout = new FileOutputStream(filename);
			memento.save(fout);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save disabled information", e);
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Saves the disabled sound list.
	 */
	private void saveSoundMap() {
		String filename = ServerUIPlugin.getInstance().getStateLocation().append(SOUNDS_FILE).toOSString();
	
		FileOutputStream fout = null;
		try {
			XMLMemento memento = XMLMemento.createWriteRoot("sound-map");
	
			Iterator iterator = userSoundMap.keySet().iterator();
			while (iterator.hasNext()) {
				IMemento child = memento.createChild("map");
				String id = (String) iterator.next();
				child.putString("id", id);
				IPath path = (IPath) userSoundMap.get(id);
				child.putString("path", path.toString());
			}
	
			fout = new FileOutputStream(filename);
			memento.save(fout);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not save URL map information", e);
		} finally {
			if (fout != null) {
				try {
					fout.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Enable or disable a specific category.
	 *
	 * @param id java.lang.String
	 * @param b boolean
	 */
	public void setCategoryEnabled(String id, boolean b) {
		if (id == null)
			return;
	
		if (disabledCategories == null)
			loadDisabledLists();
	
		if (b) {
			if (disabledCategories.contains(id)) {
				disabledCategories.remove(id);
				saveDisabledLists();
			}
		} else {
			if (!disabledCategories.contains(id)) {
				disabledCategories.add(id);
				saveDisabledLists();
			}
		}
	}

	/**
	 * Enable or disable a specific sound.
	 *
	 * @param id java.lang.String
	 * @param b boolean
	 */
	public void setSoundEnabled(String id, boolean b) {
		if (id == null)
			return;
	
		if (disabledSounds == null)
			loadDisabledLists();
	
		if (b) {
			if (disabledSounds.contains(id)) {
				disabledSounds.remove(id);
				saveDisabledLists();
			}
		} else {
			if (!disabledSounds.contains(id)) {
				disabledSounds.add(id);
				saveDisabledLists();
			}
		}
	}

	/**
	 * Sets whether sound is enabled.
	 *
	 * @param enabled
	 */
	public void setSoundsEnabled(boolean enabled) {
		getPreferenceStore().setValue(PREF_SOUND_ENABLED, enabled);
	}

	/**
	 * Sets the current URL for this sound.
	 *
	 * @param id java.lang.String
	 * @param path IPath
	 */
	protected void setSoundURL(String id, IPath path) {
		if (id == null || path == null)
			return;
	
		try {
			if (userSoundMap == null)
				loadSoundMap();
	
			userSoundMap.put(id, path);
			saveSoundMap();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Could not get sound URL: " + id, e);
		}
	}

	/**
	 * Sets the full user sound map.
	 *
	 * @param map the sound map
	 */
	protected void setUserSoundMap(Map map) {
		if (map != null) {
			userSoundMap = map;
			saveSoundMap();
		}
	}

	/**
	 * Sets the volume.
	 *
	 * @param volume the volume
	 */
	public void setVolume(int volume) {
		getPreferenceStore().setValue(PREF_VOLUME, volume);
	}
}